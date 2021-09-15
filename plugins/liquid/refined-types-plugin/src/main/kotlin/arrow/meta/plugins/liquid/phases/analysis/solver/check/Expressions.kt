package arrow.meta.plugins.liquid.phases.analysis.solver.check

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.meta.continuations.ContSeq
import arrow.meta.continuations.asContSeq
import arrow.meta.continuations.cont
import arrow.meta.continuations.doOnlyWhen
import arrow.meta.continuations.doOnlyWhenNotNull
import arrow.meta.continuations.sequence
import arrow.meta.internal.mapNotNull
import arrow.meta.phases.analysis.body
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.CheckData
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.Condition
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.ControlFlowFn
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.ExplicitBlockReturn
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.ExplicitLoopReturn
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.ExplicitReturn
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.ExplicitThrowReturn
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.LoopPlace
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.NoReturn
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.Return
import arrow.meta.plugins.liquid.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.arg
import arrow.meta.plugins.liquid.phases.analysis.solver.state.checkCallPostConditionsInconsistencies
import arrow.meta.plugins.liquid.phases.analysis.solver.state.checkCallPreConditionsImplication
import arrow.meta.plugins.liquid.phases.analysis.solver.state.checkConditionsInconsistencies
import arrow.meta.plugins.liquid.phases.analysis.solver.state.checkInvariant
import arrow.meta.plugins.liquid.phases.analysis.solver.state.checkInvariantConsistency
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.constraintsFromSolverState
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.expressionToFormula
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.invariantCall
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.isField
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.postCall
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.preCall
import arrow.meta.plugins.liquid.phases.analysis.solver.state.specialCasingForResolvedCalls
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.valueArgumentExpressions
import arrow.meta.plugins.liquid.smt.renameDeclarationConstraints
import arrow.meta.plugins.liquid.smt.renameObjectVariables
import arrow.meta.plugins.liquid.smt.utils.ReferencedElement
import org.jetbrains.kotlin.codegen.kotlinType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtDeclarationWithInitializer
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtExpressionWithLabel
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtLabeledExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import org.jetbrains.kotlin.psi.KtWhenConditionWithExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.KtWhileExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getReceiverExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.sosy_lab.java_smt.api.BooleanFormula

// 2.2: expressions
// ----------------

/**
 * Produces a continuation that when invoked
 * recursively checks an [expression] set of constraints
 */
internal fun SolverState.checkExpressionConstraints(
  associatedVarName: String,
  expression: KtExpression?,
  data: CheckData
): ContSeq<Return> =
  when (expression) {
    // these two simply recur into their underlying expressions
    is KtParenthesizedExpression ->
      checkExpressionConstraints(associatedVarName, expression.expression, data)
    is KtAnnotatedExpression ->
      checkExpressionConstraints(associatedVarName, expression.baseExpression, data)
    is KtBlockExpression ->
      data.varInfo.bracket().flatMap { // new variables are local to that block
        checkBlockExpression(associatedVarName, expression.statements, data)
      }
    is KtReturnExpression ->
      checkReturnConstraints(expression, data)
    is KtBreakExpression, is KtContinueExpression -> {
      val withLabel = expression as KtExpressionWithLabel
      cont { ExplicitLoopReturn(withLabel.getLabelName()) }
    }
    is KtThrowExpression ->
      checkThrowConstraints(expression, data)
    is KtConstantExpression ->
      checkConstantExpression(associatedVarName, expression)
    is KtThisExpression ->
      // both 'this' and 'this@name' are available in the variable info
      checkNameExpression(associatedVarName, expression.text, data)
    is KtSimpleNameExpression ->
      checkNameExpression(associatedVarName, expression.getReferencedName(), data)
    is KtLabeledExpression ->
      checkLabeledExpression(associatedVarName, expression, data)
    is KtIfExpression ->
      checkSimpleConditional(associatedVarName, expression.computeSimpleConditions(), data)
    is KtWhenExpression ->
      if (expression.subjectExpression != null) {
        cont { NoReturn } // TODO: handle `when` with subject
      } else {
        checkSimpleConditional(associatedVarName, expression.computeSimpleConditions(), data)
      }
    is KtLoopExpression ->
      checkLoopExpression(expression, data)
    is KtTryExpression ->
      checkTryExpression(associatedVarName, expression, data)
    is KtIsExpression ->
      checkIsExpression(associatedVarName, expression, data)
    is KtBinaryExpression ->
      checkBinaryExpression(associatedVarName, expression, data)
    is KtDeclaration ->
      checkDeclarationExpression(expression, data)
    is KtExpression ->
      fallThrough(associatedVarName, expression, data)
    else ->
      cont { NoReturn }
  }

private fun SolverState.fallThrough(
  associatedVarName: String,
  expression: KtExpression,
  data: CheckData
): ContSeq<Return> =
// fall-through case
  // try to treat it as a function call (for +, -, and so on)
  doOnlyWhenNotNull(expression.getResolvedCall(data.context.trace.bindingContext), NoReturn) { resolvedCall ->
    checkCallExpression(associatedVarName, expression, resolvedCall, data)
  }

private fun KtDeclaration.isVar(): Boolean = when (this) {
  is KtVariableDeclaration -> this.isVar
  else -> false
}

/**
 * Checks each statement in a block expression in order.
 * We need our own function because only the *last* statement
 * is the one assigned as the "return value" of the block.
 */
private fun SolverState.checkBlockExpression(
  associatedVarName: String,
  expressions: List<KtExpression>,
  data: CheckData
): ContSeq<Return> =
  when (expressions.size) {
    0 -> cont { NoReturn }
    1 -> // this is the last element, so it's the return value of the expression
      checkExpressionConstraints(associatedVarName, expressions[0], data)
    else -> {
      val first = expressions[0]
      val remainder = expressions.drop(1)
      val inventedName = names.newName("stmt", ReferencedElement(first, null))
      checkExpressionConstraints(inventedName, first, data).checkReturnInfo {
        checkBlockExpression(associatedVarName, remainder, data)
      }
    }
  }

/**
 * Checks a block which introduces a label or scope.
 */
private fun SolverState.checkLabeledExpression(
  associatedVarName: String,
  expression: KtLabeledExpression,
  data: CheckData
): ContSeq<Return> {
  val labelName = expression.name!!
  // add the return point to the list and recur
  val updatedData = data.addReturnPoint(labelName, associatedVarName)
  return checkExpressionConstraints(associatedVarName, expression.baseExpression, updatedData).map { r ->
    // if we have reached the point where the label was introduced,
    // then we are done with the block, and we can keep going
    if (r is ExplicitBlockReturn && r.returnPoint == labelName) {
      NoReturn
    } else {
      r
    }
  }
}

/**
 * Checks a 'return' or 'return@label' statement.
 * At the end it aborts the current computation, because
 * after a return there's nothing else to be checked.
 */
private fun SolverState.checkReturnConstraints(
  expression: KtReturnExpression,
  data: CheckData
): ContSeq<Return> {
  // figure out the right variable to assign
  // - if 'return@label', find the label in the recorded return points
  // - otherwise, it should be the top-most one
  val label = expression.getLabelName()
  val returnVarName = label.let {
    data.returnPoints.namedReturnPointVariableNames[it]
  } ?: data.returnPoints.topMostReturnPointVariableName.second
  // assign it, and signal that we explicitly return
  return checkExpressionConstraints(returnVarName, expression.returnedExpression, data)
    .map { ExplicitBlockReturn(label) }
}

private fun SolverState.checkThrowConstraints(
  expression: KtThrowExpression,
  data: CheckData
): ContSeq<Return> {
  val throwName = names.newName("throw", ReferencedElement(expression, null))
  return checkExpressionConstraints(throwName, expression.thrownExpression, data)
    .map {
      expression.thrownExpression?.kotlinType(data.context.trace.bindingContext)?.let { ty ->
        ExplicitThrowReturn(ty)
      } ?: ExplicitThrowReturn(null)
    }
}

/**
 * Produces a continuation that when invoked
 * recursively checks the call [resolvedCall]
 * starting from its arguments
 */
private fun SolverState.checkCallExpression(
  associatedVarName: String,
  expression: KtExpression,
  resolvedCall: ResolvedCall<out CallableDescriptor>,
  data: CheckData
): ContSeq<Return> {
  val specialCase = solver.specialCasingForResolvedCalls(resolvedCall)
  val specialControlFlow = controlFlowAnyFunction(resolvedCall)
  val fqName = resolvedCall.resultingDescriptor.fqNameSafe
  return when {
    resolvedCall.preCall() -> // ignore calls to 'pre'
      cont { NoReturn }
    resolvedCall.postCall() -> // ignore post arguments
      checkExpressionConstraints(associatedVarName, resolvedCall.getReceiverExpression(), data)
    resolvedCall.invariantCall() -> // ignore invariant arguments
      checkExpressionConstraints(associatedVarName, resolvedCall.getReceiverExpression(), data)
    specialControlFlow != null ->
      checkControlFlowFunctionCall(associatedVarName, expression, specialControlFlow, data)
    fqName == FqName("<SPECIAL-FUNCTION-FOR-ELVIS-RESOLVE>") ->
      doOnlyWhenNotNull(resolvedCall.arg("left"), NoReturn) { left ->
        doOnlyWhenNotNull(resolvedCall.arg("right"), NoReturn) { right ->
          checkElvisOperator(associatedVarName, left, right, data)
        }
      }
    specialCase != null -> { // this should eventually go away
      val receiverExpr = resolvedCall.getReceiverExpression()
      val referencedArg = resolvedCall.referencedArg(receiverExpr)
      val receiverName = names.newName("this", referencedArg)
      checkExpressionConstraints(receiverName, receiverExpr, data).checkReturnInfo {
        checkCallArguments(resolvedCall, data).map {
          it.fold(
            { r -> r },
            { valueArgVars ->
              val argVars = listOf("this" to receiverName) + valueArgVars
              val result =
                if (expression.kotlinType(data.context.trace.bindingContext)?.isBoolean() == true)
                  solver.makeBooleanObjectVariable(associatedVarName)
                else
                  solver.makeIntegerObjectVariable(associatedVarName)
              val arg1 = solver.makeIntegerObjectVariable(argVars[0].second)
              val arg2 = solver.makeIntegerObjectVariable(argVars[1].second)
              specialCase(result, arg1, arg2)?.let { formula ->
                addConstraint(
                  NamedConstraint(
                    "${expression.text}, checkCallArguments(${resolvedCall.resultingDescriptor.fqNameSafe}) [$result, $arg1, $arg2]",
                    formula
                  )
                )
              }
              NoReturn
            }
          )
        }
      }
    }
    else -> checkRegularFunctionCall(associatedVarName, resolvedCall, expression, data)
  }
}

private fun ResolvedCall<out CallableDescriptor>.referencedArg(
  arg: KtExpression?
): ReferencedElement? = valueArguments.toList().firstOrNull { (paramDescriptor, resolvedArg) ->
  resolvedArg.arguments.any { valueArg ->
    valueArg.getArgumentExpression() == arg
  }
}?.let { arg?.let { a -> ReferencedElement(a, it) }}

/**
 * Special treatment for special control flow functions ([also], [apply], [let], [run], [with])
 * https://kotlinlang.org/docs/scope-functions.html#function-selection
 */
private fun controlFlowAnyFunction(
  resolvedCall: ResolvedCall<out CallableDescriptor>
): ControlFlowFn? {
  val thisElement = resolvedCall.arg("this") ?: resolvedCall.arg("receiver")
  val blockElement = resolvedCall.arg("block") as? KtLambdaExpression
  val bodyElement = blockElement?.bodyExpression
  return if (blockElement != null && bodyElement != null) {
    if (thisElement != null) {
      when (resolvedCall.resultingDescriptor.fqNameSafe) {
        FqName("kotlin.also") -> {
          val argumentName = blockElement.valueParameters.getOrNull(0)?.name ?: "it"
          ControlFlowFn(thisElement, bodyElement, argumentName, ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT)
        }
        FqName("kotlin.apply") ->
          ControlFlowFn(thisElement, bodyElement, "this", ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT)
        FqName("kotlin.let") -> {
          val argumentName = blockElement.valueParameters.getOrNull(0)?.name ?: "it"
          ControlFlowFn(thisElement, bodyElement, argumentName, ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT)
        }
        FqName("kotlin.run") ->
          ControlFlowFn(thisElement, bodyElement, "this", ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT)
        FqName("kotlin.with") ->
          ControlFlowFn(thisElement, bodyElement, "this", ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT)
        else -> null
      }
    } else {
      when (resolvedCall.resultingDescriptor.fqNameSafe) {
        // 'run' can also be called without a receiver
        // https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/run.html
        FqName("kotlin.run") ->
          ControlFlowFn(thisElement, bodyElement, "this", ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT)
        else -> null
      }
    }
  } else {
    null
  }
}

/**
 * Checks special control flow functions ([also], [apply], [let], [run])
 */
private fun SolverState.checkControlFlowFunctionCall(
  associatedVarName: String,
  wholeExpr: KtExpression,
  info: ControlFlowFn,
  data: CheckData
): ContSeq<Return> {
  val referencedElement = info.target?.let { ReferencedElement(it, null) }
  val thisName = when (info.returnBehavior) {
    ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT -> associatedVarName
    ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT -> names.newName("this", referencedElement)
  }
  val returnName = when (info.returnBehavior) {
    ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT -> names.newName("ret", referencedElement)
    ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT -> associatedVarName
  }
  return checkReceiverWithPossibleSafeDot(associatedVarName, wholeExpr, thisName, info.target, data) {
    data.varInfo.bracket().flatMap {
      // add the name to the context,
      // being careful not overriding any existing name
      info.target?.let {
        val smtName = names.newName(info.argumentName, referencedElement)
        data.varInfo.add(info.argumentName, smtName, info.target, null)
        // add the constraint to make the parameter equal
        val formula = solver.objects { equal(solver.makeObjectVariable(smtName), solver.makeObjectVariable(thisName)) }
        addConstraint(NamedConstraint("introduce argument for lambda", formula))
      }
      // check the body in this new context
      checkExpressionConstraints(returnName, info.body, data)
    }
  }
}

/**
 * Checks any function call which is not a special case
 */
private fun SolverState.checkRegularFunctionCall(
  associatedVarName: String,
  resolvedCall: ResolvedCall<out CallableDescriptor>,
  expression: KtExpression,
  data: CheckData
): ContSeq<Return> {
  val receiverExpr = resolvedCall.getReceiverExpression()
  val referencedElement = resolvedCall.referencedArg(receiverExpr)
  val receiverName = names.newName("this", referencedElement)
  return checkReceiverWithPossibleSafeDot(associatedVarName, expression, receiverName, receiverExpr, data) {
    checkCallArguments(resolvedCall, data).map {
      it.fold(
        { r -> r },
        { argVars ->
          val callConstraints = constraintsFromSolverState(resolvedCall)?.let { declInfo ->
            val completeRenaming = argVars.toMap() + (RESULT_VAR_NAME to associatedVarName) + ("this" to receiverName)
            solver.renameDeclarationConstraints(declInfo, completeRenaming)
          }
          // check pre-conditions and post-conditions
          checkCallPreConditionsImplication(callConstraints, data.context, expression, resolvedCall)
          // add a constraint for fields: result == field(name, value)
          val descriptor = resolvedCall.resultingDescriptor
          if (descriptor.isField()) {
            val fieldConstraint = solver.ints {
              val typeName = descriptor.fqNameSafe.asString()
              val argName = when (receiverExpr) {
                null -> argVars[0].second
                else -> receiverName
              }
              NamedConstraint(
                "${expression.text} == $typeName($argName)",
                equal(
                  solver.makeObjectVariable(associatedVarName),
                  solver.field(typeName, solver.makeObjectVariable(argName))
                )
              )
            }
            addConstraint(fieldConstraint)
          }
          // if the result is not null
          if (!resolvedCall.getReturnType().isMarkedNullable) {
            addConstraint(
              NamedConstraint(
              "$associatedVarName is not null",
              solver.isNotNull(solver.makeObjectVariable(associatedVarName)))
            )
          }
          // there's no point in continuing if we are in an inconsistent position
          val inconsistentPostConditions =
            checkCallPostConditionsInconsistencies(callConstraints, data.context, expression, resolvedCall)
          ensure(!inconsistentPostConditions)
          // and we continue as normal
          NoReturn
        }
      )
    }
  }
}

/**
 * Handles the possibility of a function call being done with ?.
 */
private fun SolverState.checkReceiverWithPossibleSafeDot(
  associatedVarName: String,
  wholeExpr: KtExpression,
  receiverName: String,
  receiverExpr: KtExpression?,
  data: CheckData,
  block: () -> ContSeq<Return>
): ContSeq<Return> =
  checkExpressionConstraints(receiverName, receiverExpr, data).checkReturnInfo {
    // here comes a trick: when the method is access with the "safe dot" ?.
    // we need to create two different "branches",
    // one for the case in which the value is null, one for when it isn't
    //   x?.f(..)  <=>  if (x == null) null else x.f(..)
    // we do so by yielding 'true' and 'false' in that case,
    // and only 'true' when we use a "regular dot" .
    ContSeq {
      if (wholeExpr is KtSafeQualifiedExpression)
        yield(false)
      yield(true)
    }.flatMap { r ->
      continuationBracket.map { r }
    }.flatMap { definitelyNotNull ->
      if (!definitelyNotNull) { // the null case of ?.
        ContSeq.unit.map {
          val nullReceiver = NamedConstraint("$receiverName is null", solver.isNull(solver.makeObjectVariable(receiverName)))
          val nullResult = NamedConstraint("$associatedVarName is null", solver.isNull(solver.makeObjectVariable(associatedVarName)))
          val inconsistent = checkConditionsInconsistencies(listOf(nullReceiver, nullResult), data.context, receiverExpr!!)
          ensure(!inconsistent)
          NoReturn
        }
      } else { // the non-null case of ?., or simply regular .
        doOnlyWhenNotNull(receiverExpr, NoReturn) { rcv ->
          ContSeq.unit.map {
            val notNullCstr = NamedConstraint("$receiverName is not null", solver.isNotNull(solver.makeObjectVariable(receiverName)))
            val inconsistent = checkConditionsInconsistencies(listOf(notNullCstr), data.context, rcv)
            ensure(!inconsistent)
            NoReturn
          }
        }.flatMap { block() }
      }
    }
  }

/**
 * Checks leftExpr ?: rightExpr
 * This is very similar to [checkReceiverWithPossibleSafeDot],
 * but it's hard to abstract between both due to small details
 */
private fun SolverState.checkElvisOperator(
  associatedVarName: String,
  leftExpr: KtExpression,
  rightExpr: KtExpression,
  data: CheckData
): ContSeq<Return> {
  val leftName = names.newName("left", ReferencedElement(leftExpr, null))
  return checkExpressionConstraints(leftName, leftExpr, data).checkReturnInfo {
    ContSeq {
      yield(false)
      yield(true)
    }.flatMap { r ->
      continuationBracket.map { r }
    }.flatMap { definitelyNotNull ->
      if (!definitelyNotNull) { // the null case of ?:
        ContSeq.unit.map {
          val nullLeft = NamedConstraint("$leftName is null", solver.isNull(solver.makeObjectVariable(leftName)))
          val inconsistent = checkConditionsInconsistencies(listOf(nullLeft), data.context, leftExpr)
          ensure(!inconsistent)
          NoReturn
        }.flatMap {
          // then the result is whatever we get from the right
          checkExpressionConstraints(associatedVarName, rightExpr, data)
        }
      } else { // the non-null case of ?:
        ContSeq.unit.map {
          val notNullLeft = NamedConstraint("$leftName is not null", solver.isNotNull(solver.makeObjectVariable(leftName)))
          val resultIsLeft = NamedConstraint("$leftName is result of ?:",
            solver.objects { equal(solver.makeObjectVariable(leftName), solver.makeObjectVariable(associatedVarName)) })
          val inconsistent = checkConditionsInconsistencies(listOf(notNullLeft, resultIsLeft), data.context, leftExpr)
          ensure(!inconsistent)
          NoReturn
        }
      }
    }
  }
}

/**
 * Recursively perform check on arguments,
 * including extension receiver and dispatch receiver
 *
 * [NOTE: argument renaming]
 *   this function creates a new name for each argument,
 *   based on the formal parameter name;
 *   this creates a renaming for the original constraints
 */
private fun SolverState.checkCallArguments(
  resolvedCall: ResolvedCall<out CallableDescriptor>,
  data: CheckData
): ContSeq<Either<ExplicitReturn, List<Pair<String, String>>>> {
  // why is this so complicated?
  //   in theory, we just need to run checkExpressionConstraints over each argument
  //   (in fact, the original implementation just did that, and then called .sequence())
  //   alas, Kotlin allows arguments to include 'return' (yes, really!)
  //   so we need to check after each step whether a ExplicitReturn has been generated
  //   and in that case we stop the check of any more arguments
  //   (stopping there is important, since introducing additional constraints from
  //    other arguments may not be right in the general case)
  fun <A> acc(
    upToNow: ContSeq<Either<ExplicitReturn, List<Pair<String, String>>>>,
    current: Triple<String, A, KtExpression?>
  ): ContSeq<Either<ExplicitReturn, List<Pair<String, String>>>> =
    upToNow.flatMap {
      it.fold(
        { r -> cont { r.left() } },
        { argsUpToNow ->
          val (name, _, expr) = current
          val referencedElement = resolvedCall.referencedArg(expr)
          val argUniqueName = names.newName(name, referencedElement)
          checkExpressionConstraints(argUniqueName, expr, data).checkReturnInfo({ r -> r.left() }) {
            cont { (argsUpToNow + listOf(name to argUniqueName)).right() }
          }
        }
      )
    }
  return resolvedCall.valueArgumentExpressions()
    .fold(cont { emptyList<Pair<String, String>>().right() }, ::acc)
}

/**
 * This function produces a constraint that makes the desired variable name
 * equal to the value encoded in the constant and adds it to the
 * [SolverState.prover] constraints.
 */
private fun SolverState.checkConstantExpression(
  associatedVarName: String,
  expression: KtConstantExpression
): ContSeq<Return> = cont {
  if (expression.text == "null") {
    val isNullFormula = solver.isNull(solver.makeObjectVariable(associatedVarName))
    addConstraint(NamedConstraint("$associatedVarName is null", isNullFormula))
  } else {
    val mayBoolean = expression.text.toBooleanStrictOrNull()
    val mayInteger = expression.text.toBigIntegerOrNull()
    val mayRational = expression.text.toBigDecimalOrNull()
    when {
      mayBoolean == true ->
        solver.makeBooleanObjectVariable(associatedVarName)
      mayBoolean == false ->
        solver.booleans { not(solver.makeBooleanObjectVariable(associatedVarName)) }
      mayInteger != null ->
        solver.ints {
          equal(
            solver.makeIntegerObjectVariable(associatedVarName),
            makeNumber(mayInteger)
          )
        }
      mayRational != null ->
        solver.rationals {
          equal(
            solver.decimalValue(solver.makeObjectVariable(associatedVarName)),
            makeNumber(mayRational)
          )
        }
      else -> null
    }?.let {
      addConstraint(
        NamedConstraint(
          "${expression.text} checkConstantExpression $associatedVarName ${expression.text}",
          it
        )
      )
      addConstraint(
        NamedConstraint(
          "${expression.text} is not null",
          solver.isNotNull(solver.makeObjectVariable(associatedVarName))
        )
      )
    }
  }
  NoReturn
}

/**
 * Check special binary cases, and make the other fall-through
 */
private fun SolverState.checkBinaryExpression(
  associatedVarName: String,
  expression: KtBinaryExpression,
  data: CheckData
): ContSeq<Return> {
  val operator = expression.operationToken.toString()
  val left = expression.left
  val right = expression.right
  val referencedElement = left?.let { ReferencedElement(it, null) }
  return when {
    // this is an assignment to a mutable variable
    operator == "EQ" && left is KtNameReferenceExpression -> {
      // we introduce a new name because we don't want to introduce
      // any additional information about the variable,
      // we should only have that declared in the invariant
      val newName = names.newName(left.getReferencedName(), referencedElement)
      val invariant = data.varInfo.get(left.getReferencedName())?.invariant
      checkBodyAgainstInvariants(expression, newName, invariant, expression.right, data)
        .map { it.second } // forget about the temporary name
    }
    // this is x == null, or x != null
    (operator == "EQEQ" || operator == "EXCLEQ") && right is KtConstantExpression && right.text == "null" -> {
      val newName = names.newName("checkNull", referencedElement)
      checkExpressionConstraints(newName, left, data).checkReturnInfo {
        cont {
          when (operator) {
            "EQEQ" -> solver.isNull(solver.makeObjectVariable(newName))
            "EXCLEQ" -> solver.isNotNull(solver.makeObjectVariable(newName))
            else -> null
          }?.let {
            val cstr = solver.booleans { equivalence(solver.makeBooleanObjectVariable(associatedVarName), it) }
            addConstraint(NamedConstraint("$associatedVarName is null?", cstr))
          }
          NoReturn
        }
      }
    }
    else -> fallThrough(associatedVarName, expression, data)
  }
}

/**
 * Checks (x is Something) expressions
 * We do not track types, so in theory this should not affect our analysis
 * However, in the specific case in which 'Something' is not nullable
 * we can also ensure that 'x' is not null
 * This is different from 'x != null' in that we do not generate
 *   associatedVarName <=> not (null x)
 * But rather
 *   associatedVarName ==> not (null x)
 */
private fun SolverState.checkIsExpression(
  associatedVarName: String,
  expression: KtIsExpression,
  data: CheckData
): ContSeq<Return> = doOnlyWhen(!expression.isNegated, NoReturn) {
  val referencedElement = ReferencedElement(expression.leftHandSide, null)
  val newName = names.newName("is", referencedElement)
  checkExpressionConstraints(newName, expression.leftHandSide, data).checkReturnInfo {
    cont {
      val cstr = solver.booleans {
        implication(
          solver.makeBooleanObjectVariable(associatedVarName),
          solver.isNotNull(solver.makeObjectVariable(newName))
        )
      }
      addConstraint(NamedConstraint("$associatedVarName => not null", cstr))
      NoReturn
    }
  }
}

/**
 * This function produces a continuation that makes the desired variable name
 * equal to the value encoded in the named expression.
 */
private fun SolverState.checkDeclarationExpression(
  declaration: KtDeclaration,
  data: CheckData
): ContSeq<Return> {
  val referencedElement = ReferencedElement(declaration, null)
  val declName = when (declaration) {
    // use the given name if available
    is KtNamedDeclaration -> declaration.nameAsSafeName.asString()
    else -> names.newName("decl", referencedElement)
  }
  // we need to create a new one to prevent shadowing
  val smtName = names.newName(declName, referencedElement)
  // find out whether we have an invariant
  val body = declaration.stableBody()
  val invariant = obtainInvariant(body, data)
  // assert the invariant if found and check its consistency
  return doOnlyWhenNotNull(invariant, Unit) { (invBody, invFormula: BooleanFormula) ->
    ContSeq.unit.map {
      val renamed = solver.renameObjectVariables(invFormula, mapOf(RESULT_VAR_NAME to smtName))
      val inconsistentInvariant = checkInvariantConsistency(
        NamedConstraint("invariant in $declName", renamed),
        data.context,
        invBody
      )
      ensure(!inconsistentInvariant)
    }
  }.flatMap {
    // this gives back a new temporary name for the body
    checkBodyAgainstInvariants(declaration, declName, invariant?.second, body, data)
  }.map { (newVarName, r) ->
    // if it's not a var, we state it's equal to the one
    // we've introduced while checking the invariants
    // this means the solver can use everything it may
    // gather about it
    if (!declaration.isVar()) {
      solver.objects {
        addConstraint(
          NamedConstraint(
            "$declName $smtName = $newVarName",
            equal(solver.makeObjectVariable(smtName), solver.makeObjectVariable(newVarName))
          )
        )
      }
    }
    // update the list of variables in scope
    data.varInfo.add(declName, smtName, declaration, invariant?.second)
    // and then keep going
    r
  }
}

/**
 * Checks the possible invariants of a declaration, and its body.
 */
private fun SolverState.checkBodyAgainstInvariants(
  element: KtElement,
  declName: String,
  invariant: BooleanFormula?,
  body: KtExpression?,
  data: CheckData
): ContSeq<Pair<String, Return>> {
  val referencedElement = ReferencedElement(element, null)
  val newName = names.newName(declName, referencedElement)
  return checkExpressionConstraints(newName, body, data).onEach {
    invariant?.let {
      val renamed = solver.renameObjectVariables(it, mapOf(RESULT_VAR_NAME to newName))
      checkInvariant(
        NamedConstraint("assignment to `${element.text}`", renamed),
        data.context,
        element
      )
    }
  }.map { r -> Pair(newName, r) }
}

private fun SolverState.obtainInvariant(
  expression: KtExpression?,
  data: CheckData
): Pair<KtExpression, BooleanFormula>? =
  expression?.getResolvedCall(data.context.trace.bindingContext)
    ?.takeIf { it.invariantCall() }
    ?.arg("predicate")
    ?.let { expr: KtExpression ->
      solver.expressionToFormula(expr, data.context.trace.bindingContext)
        ?.let { it as? BooleanFormula }
        ?.let { formula -> expr to formula }
    }

/**
 * This function produces a continuation that makes the desired variable name
 * equal to the value encoded in the named expression and adds the resulting boolean formula
 * to the [SolverState.prover] constraints.
 */
private fun SolverState.checkNameExpression(
  associatedVarName: String,
  referencedName: String,
  data: CheckData
): ContSeq<Return> = cont {
  // use the SMT name recorded in the variable info
  data.varInfo.get(referencedName)?.let {
    val constraint = solver.objects {
      equal(
        solver.makeObjectVariable(associatedVarName),
        solver.makeObjectVariable(it.smtName)
      )
    }
    addConstraint(NamedConstraint("$associatedVarName = ${it.smtName}", constraint))
  }
  NoReturn
}

private fun KtExpression.computeSimpleConditions(): List<Condition> = when (this) {
  is KtIfExpression ->
    listOf(
      Condition(condition!!, then!!, then!!),
      Condition(null, `else`!!, `else`!!)
    )
  is KtWhenExpression ->
    entries.flatMap { entry ->
      if (entry.conditions.isEmpty()) {
        listOf(Condition(null, entry.expression!!, entry))
      } else {
        entry.conditions.toList().mapNotNull { cond ->
          when (cond) {
            is KtWhenConditionWithExpression ->
              Condition(cond.expression!!, entry.expression!!, entry)
            else -> null
          }
        }
      }
    }
  else -> emptyList()
}

/**
 * Check `if` and `when` expressions without subject.
 */
private fun SolverState.checkSimpleConditional(
  associatedVarName: String,
  branches: List<Condition>,
  data: CheckData
): ContSeq<Return> =
  branches.map { cond ->
    val referencedElement = cond.condition?.let { ReferencedElement(it, null) }
    val conditionVar = names.newName("cond", referencedElement)
    // introduce the condition
    (cond.condition?.let {
      checkExpressionConstraints(conditionVar, it, data)
    } ?: cont {
      // if we have no condition, it's equivalent to true
      addConstraint(
        NamedConstraint(
          "check condition branch $conditionVar",
          solver.makeBooleanObjectVariable(conditionVar)
        )
      )
      NoReturn
    }).map { returnInfo -> Pair(Pair(returnInfo, cond), conditionVar) }
  }.sequence().flatMap { conditionInformation ->
    yesNo(conditionInformation)
      .asContSeq()
      .flatMap { (returnAndCond, correspondingVars) ->
        val (returnInfo, cond) = returnAndCond
        when (returnInfo) {
          is ExplicitReturn -> // weird case: a return in a condition
            cont { returnInfo }
          else ->
            continuationBracket.map {
              // assert the variables and check that we are consistent
              val inconsistentEnvironment =
                checkConditionsInconsistencies(correspondingVars, data.context, cond.whole)
              // it only makes sense to continue if we are not consistent
              ensure(!inconsistentEnvironment)
            }.flatMap {
              // check the body
              checkExpressionConstraints(associatedVarName, cond.body, data)
            }
        }
      }
  }

/**
 * Given a list of names for condition variables,
 * create the boolean conditions for each branch.
 *
 * For example, given [a, b, c], it generates:
 * - a
 * - not a, b
 * - not a, not b, c
 */
private fun <A> SolverState.yesNo(conditionVars: List<Pair<A, String>>): List<Pair<A, List<NamedConstraint>>> {
  fun go(currents: List<Pair<A, String>>, acc: List<NamedConstraint>): List<Pair<A, List<NamedConstraint>>> =
    if (currents.isEmpty()) {
      emptyList()
    } else {
      solver.booleans {
        val varName = solver.makeBooleanObjectVariable(currents[0].second)
        val nextValue = acc + listOf(NamedConstraint("$varName", varName))
        val nextAcc = acc + listOf(NamedConstraint("!($varName)", not(varName)))
        listOf(Pair(currents[0].first, nextValue)) + go(currents.drop(1), nextAcc)
      }
    }
  return go(conditionVars, emptyList())
}

private fun SolverState.checkLoopExpression(
  expression: KtLoopExpression,
  data: CheckData
): ContSeq<Return> = when (expression) {
  is KtForExpression ->
    checkForExpression(expression.loopParameter, expression.body, data)
  is KtWhileExpression ->
    doOnlyWhenNotNull(expression.condition, NoReturn) {
      checkWhileExpression(it, expression.body, data)
    }
  is KtDoWhileExpression -> {
    // remember that do { t } while (condition)
    // is equivalent to { t }; while (condition) { t }
    val uselessName = names.newName("firstIter", null)
    checkExpressionConstraints(uselessName, expression.body, data).flatMap {
      doOnlyWhenNotNull(expression.condition, NoReturn) {
        checkWhileExpression(it, expression.body, data)
      }
    }
  }
  else -> ContSeq.unit.map { NoReturn } // this should not happen
}

private fun SolverState.checkForExpression(
  loopParameter: KtParameter?,
  body: KtExpression?,
  data: CheckData
): ContSeq<Return> = ContSeq {
  yield(LoopPlace.INSIDE_LOOP)
  yield(LoopPlace.AFTER_LOOP)
}.flatMap {
  when (it) {
    LoopPlace.INSIDE_LOOP ->
      continuationBracket.flatMap {
        data.varInfo.bracket()
      }.map {
        val paramName = loopParameter?.name
        if (loopParameter != null && paramName != null) {
          val referencedElement = ReferencedElement(loopParameter, null)
          val smtName = names.newName(paramName, referencedElement)
          data.varInfo.add(paramName, smtName, loopParameter, null)
        }
      }.flatMap {
        checkLoopBody(body, data)
      }
    // in this case we know nothing
    // after the loop finishes
    LoopPlace.AFTER_LOOP ->
      cont { NoReturn }
  }
}

private fun SolverState.checkWhileExpression(
  condition: KtExpression,
  body: KtExpression?,
  data: CheckData
): ContSeq<Return> {
  val referencedElement = ReferencedElement(condition, null)
  val condName = names.newName("cond", referencedElement)
  // TODO: check the return info, just in case
  return checkExpressionConstraints(condName, body, data).flatMap {
    ContSeq {
      yield(LoopPlace.INSIDE_LOOP)
      yield(LoopPlace.AFTER_LOOP)
    }
  }.flatMap {
    when (it) {
      LoopPlace.INSIDE_LOOP ->
        continuationBracket.flatMap {
          data.varInfo.bracket()
        }.onEach {
          // inside the loop the condition is true
          checkConditionsInconsistencies(listOf(
            NamedConstraint("inside the loop, condition is tue",
              solver.makeBooleanObjectVariable(condName))
          ), data.context, condition)
        }.flatMap {
          checkLoopBody(body, data)
        }
      // after the loop the condition is false
      LoopPlace.AFTER_LOOP -> cont {
        checkConditionsInconsistencies(listOf(
          NamedConstraint("loop is finished, condition is false",
            solver.booleans { not(solver.makeBooleanObjectVariable(condName)) })
        ), data.context, condition)
        NoReturn
      }
    }
  }
}

private fun SolverState.checkLoopBody(
  body: KtExpression?,
  data: CheckData
): ContSeq<Return> {
  val uselessName = names.newName("loop", null)
  return checkExpressionConstraints(uselessName, body, data).map { returnInfo ->
    // only keep working on this branch
    // if we had a 'return' inside
    // otherwise the other branch is enough
    when (returnInfo) {
      is ExplicitLoopReturn -> abort()
      is ExplicitBlockReturn -> returnInfo
      else -> abort()
    }
  }
}

/**
 * Check try/catch/finally blocks
 * This is a very rough check,
 * in which we assume the worst-case scenario:
 * - when you get to a 'catch' you have *no* information about
 *   the 'try' at all
 * - all the 'catch' blocks may potentially execute
 */
private fun SolverState.checkTryExpression(
  associatedVarName: String,
  expression: KtTryExpression,
  data: CheckData
): ContSeq<Return> =
  ContSeq {
    yield(expression.tryBlock)
    yieldAll(expression.catchClauses)
  }.flatMap { r ->
    continuationBracket.flatMap { data.varInfo.bracket() }.map { r }
  }.flatMap<Return> {
    when (it) {
      is KtBlockExpression -> // the try
        checkExpressionConstraints(associatedVarName, it, data).flatMap { returnInfo ->
          when (returnInfo) {
            // if we had a throw, this will eventually end in a catch
            is ExplicitThrowReturn ->
              // is the thrown exception something in our own catch?
              if (doesAnyCatchMatch(returnInfo.exceptionType, expression.catchClauses, data))
                ContSeq { abort() } // then there's no point in keep looking here
              else
                cont { returnInfo } // otherwise, bubble up the exception
            else -> cont { returnInfo }
          }
        }
      is KtCatchClause -> { // the catch
        doOnlyWhenNotNull(it.catchParameter, NoReturn) { param ->
          doOnlyWhenNotNull(param.name, NoReturn) { paramName ->
            // introduce the name of the parameter, which is never null
            val smtName = names.newName(paramName, ReferencedElement(param, null))
            data.varInfo.add(paramName, smtName, param, null)
            addConstraint(NamedConstraint(
              "$paramName (from catch) is not null", solver.isNotNull(solver.makeObjectVariable(smtName))
            ))
            // and then go on and check the body
            checkExpressionConstraints(associatedVarName, it.catchBody, data)
          }
        }
      }
      else -> ContSeq { abort() }
    }
  }.onEach { returnInfo ->
    doOnlyWhenNotNull(expression.finallyBlock, returnInfo) { finally ->
      val finallyName = names.newName("finally", ReferencedElement(finally, null))
      // override the return of the finally with the return of the try or catch
      checkExpressionConstraints(finallyName, finally.finalExpression, data).map { returnInfo }
    }
  }

/**
 * Checks whether the type obtain from an explicit 'throw'
 * matches any of the types in the 'catch' clauses
 */
fun doesAnyCatchMatch(
  throwType: KotlinType?,
  clauses: List<KtCatchClause>,
  data: CheckData
): Boolean = clauses.any { clause ->
  val catchType = clause.catchParameter?.kotlinType(data.context.trace.bindingContext)
  if (throwType != null && catchType != null) {
    throwType.isSubtypeOf(catchType)
  } else {
    false
  }
}

/**
 * Find the corresponding "body" of a declaration
 */
internal fun KtDeclaration.stableBody(): KtExpression? = when (this) {
  is KtVariableDeclaration -> initializer
  is KtDeclarationWithBody -> body()
  is KtDeclarationWithInitializer -> initializer
  else -> null
}

private fun <A> ContSeq<Return>.checkReturnInfo(r: (r: ExplicitReturn) -> A, f: () -> ContSeq<A>): ContSeq<A> =
  this.flatMap { returnInfo ->
    when (returnInfo) {
      is ExplicitReturn -> cont { r(returnInfo) }
      else -> f()
    }
  }

private fun ContSeq<Return>.checkReturnInfo(f: () -> ContSeq<Return>): ContSeq<Return> =
  checkReturnInfo({ it }, f)
