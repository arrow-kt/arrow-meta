package arrow.meta.plugins.analysis.phases.analysis.solver.check

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
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableMemberDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnnotatedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BinaryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BreakExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CatchClause
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ConstantExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ContinueExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DeclarationWithBody
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DeclarationWithInitializer
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DoWhileExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ExpressionWithLabel
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ForExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.IfExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.IsExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LabeledExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LambdaExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LoopExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NameReferenceExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NamedDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NullExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ParenthesizedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ReturnExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SafeQualifiedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ThisExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ThrowExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.VariableDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenConditionIsPattern
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenConditionWithExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhileExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.CheckData
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.Condition
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.ControlFlowFn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.ExplicitBlockReturn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.ExplicitLoopReturn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.ExplicitReturn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.ExplicitThrowReturn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.LoopPlace
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.NoReturn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.Return
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.SimpleCondition
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.SubjectCondition
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.arg
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.constraintsFromSolverState
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.expressionToFormula
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.invariantCall
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.isField
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.postCall
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.preCall
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.primitiveConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.typeInvariants
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.valueArgumentExpressions
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkCallPostConditionsInconsistencies
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkCallPreConditionsImplication
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkConditionsInconsistencies
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkInvariant
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkInvariantConsistency
import arrow.meta.plugins.analysis.smt.ObjectFormula
import arrow.meta.plugins.analysis.smt.renameObjectVariables
import arrow.meta.plugins.analysis.smt.substituteDeclarationConstraints
import arrow.meta.plugins.analysis.types.PrimitiveType
import arrow.meta.plugins.analysis.types.asFloatingLiteral
import arrow.meta.plugins.analysis.types.asIntegerLiteral
import arrow.meta.plugins.analysis.types.primitiveType
import arrow.meta.plugins.analysis.types.unwrapIfNullable
import org.sosy_lab.java_smt.api.BooleanFormula

// 2.2: expressions
// ----------------

internal fun SolverState.checkExpressionConstraints(
  associatedVarName: String,
  expression: Expression?,
  data: CheckData
): ContSeq<Return> =
  checkExpressionConstraints(solver.makeObjectVariable(associatedVarName), expression, data)

internal fun SolverState.checkExpressionConstraintsWithNewName(
  prefix: String,
  expression: Expression?,
  data: CheckData
): ContSeq<Return> =
  checkExpressionConstraints(newName(data.context, prefix, expression), expression, data)

/**
 * Produces a continuation that when invoked
 * recursively checks an [expression] set of constraints
 */
internal fun SolverState.checkExpressionConstraints(
  associatedVarName: ObjectFormula,
  expression: Expression?,
  data: CheckData
): ContSeq<Return> =
  when (expression) {
    // these two simply recur into their underlying expressions
    is ParenthesizedExpression ->
      checkExpressionConstraints(associatedVarName, expression.expression, data)
    is AnnotatedExpression ->
      checkExpressionConstraints(associatedVarName, expression.baseExpression, data)
    is BlockExpression ->
      data.varInfo.bracket().flatMap { // new variables are local to that block
        checkBlockExpression(associatedVarName, expression.statements, data)
      }
    is ReturnExpression ->
      checkReturnConstraints(expression, data)
    is BreakExpression, is ContinueExpression -> {
      val withLabel = expression as ExpressionWithLabel
      cont { ExplicitLoopReturn(withLabel.getLabelName()) }
    }
    is ThrowExpression ->
      checkThrowConstraints(expression, data)
    is NullExpression ->
      checkNullExpression(associatedVarName)
    is ConstantExpression ->
      checkConstantExpression(associatedVarName, expression, data)
    is ThisExpression ->
      // both 'this' and 'this@name' are available in the variable info
      checkNameExpression(associatedVarName, expression.text, data)
    is SimpleNameExpression -> {
      val resolvedCall = expression.getResolvedCall(data.context)
      when (resolvedCall?.resultingDescriptor) {
        is CallableMemberDescriptor ->
          checkCallExpression(associatedVarName, expression, resolvedCall, data)
        is ValueDescriptor ->
          checkNameExpression(associatedVarName, expression.getReferencedName(), data)
        else -> cont { NoReturn } // this should not happen
      }
    }
    is LabeledExpression ->
      checkLabeledExpression(associatedVarName, expression, data)
    is IfExpression ->
      checkConditional(associatedVarName, null, expression.computeConditions(), data)
    is WhenExpression ->
      checkConditional(associatedVarName, expression.subjectExpression, expression.computeConditions(), data)
    is LoopExpression ->
      checkLoopExpression(expression, data)
    is TryExpression ->
      checkTryExpression(associatedVarName, expression, data)
    is IsExpression -> {
      val subject = expression.leftHandSide
      val subjectName = solver.makeObjectVariable(newName(data.context, "is", subject))
      checkExpressionConstraints(subjectName, subject, data).checkReturnInfo {
        checkIsExpression(associatedVarName, expression.isNegated, expression.typeReference, subjectName, data)
      }
    }
    is BinaryExpression ->
      checkBinaryExpression(associatedVarName, expression, data)
    is Declaration ->
      // we get additional info about the subject, but it's irrelevant here
      checkDeclarationExpression(expression, data).map { it.second }
    is Expression ->
      fallThrough(associatedVarName, expression, data)
    else ->
      cont { NoReturn }
  }

private fun SolverState.fallThrough(
  associatedVarName: ObjectFormula,
  expression: Expression,
  data: CheckData
): ContSeq<Return> = when (val call = expression.getResolvedCall(data.context)) {
  // fall-through: treat as a call
  is ResolvedCall -> checkCallExpression(associatedVarName, expression, call, data)
  // otherwise, report as unsupported
  else -> cont {
    data.context.reportUnsupported(expression, ErrorMessages.Unsupported.unsupportedExpression())
    NoReturn
  }
}

private fun Declaration.isVar(): Boolean = when (this) {
  is VariableDeclaration -> this.isVar
  else -> false
}

/**
 * Checks each statement in a block expression in order.
 * We need our own function because only the *last* statement
 * is the one assigned as the "return value" of the block.
 */
private fun SolverState.checkBlockExpression(
  associatedVarName: ObjectFormula,
  expressions: List<Expression>,
  data: CheckData
): ContSeq<Return> =
  when (expressions.size) {
    0 -> cont { NoReturn }
    1 -> // this is the last element, so it's the return value of the expression
      checkExpressionConstraints(associatedVarName, expressions[0], data)
    else -> {
      val first = expressions[0]
      val remainder = expressions.drop(1)
      checkExpressionConstraintsWithNewName("stmt", first, data).checkReturnInfo {
        checkBlockExpression(associatedVarName, remainder, data)
      }
    }
  }

/**
 * Checks a block which introduces a label or scope.
 */
private fun SolverState.checkLabeledExpression(
  associatedVarName: ObjectFormula,
  expression: LabeledExpression,
  data: CheckData
): ContSeq<Return> {
  val labelName = expression.getLabelName()!!
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
  expression: ReturnExpression,
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
  expression: ThrowExpression,
  data: CheckData
): ContSeq<Return> {
  return checkExpressionConstraintsWithNewName("throw", expression.thrownExpression, data)
    .map {
      expression.thrownExpression?.type(data.context)?.let { ty ->
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
  associatedVarName: ObjectFormula,
  expression: Expression,
  resolvedCall: ResolvedCall,
  data: CheckData
): ContSeq<Return> {
  val specialControlFlow = controlFlowAnyFunction(data.context, resolvedCall)
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
      doOnlyWhenNotNull(resolvedCall.arg("left", data.context), NoReturn) { left ->
        doOnlyWhenNotNull(resolvedCall.arg("right", data.context), NoReturn) { right ->
          checkElvisOperator(associatedVarName, left, right, data)
        }
      }
    else -> checkRegularFunctionCall(associatedVarName, resolvedCall, expression, data)
  }
}

private fun ResolvedCall.referencedArg(
  arg: Expression?
): Pair<ValueParameterDescriptor, ResolvedValueArgument>? = valueArguments.toList().firstOrNull { (_, resolvedArg) ->
  resolvedArg.arguments.any { valueArg ->
    valueArg.argumentExpression == arg
  }
}

/**
 * Special treatment for special control flow functions ([also], [apply], [let], [run], [with])
 * https://kotlinlang.org/docs/scope-functions.html#function-selection
 */
private fun controlFlowAnyFunction(
  context: ResolutionContext,
  resolvedCall: ResolvedCall
): ControlFlowFn? {
  val thisElement = resolvedCall.arg("this", context) ?: resolvedCall.arg("receiver", context)
  val blockElement = resolvedCall.arg("block", context) as? LambdaExpression
  val bodyElement = blockElement?.bodyExpression
  return if (blockElement != null && bodyElement != null) {
    if (thisElement != null) {
      when (resolvedCall.resultingDescriptor.fqNameSafe) {
        FqName("kotlin.also") -> {
          val argumentName = blockElement.valueParameters.getOrNull(0)?.nameAsName?.value ?: "it"
          ControlFlowFn(thisElement, bodyElement, argumentName, ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT)
        }
        FqName("kotlin.apply") ->
          ControlFlowFn(thisElement, bodyElement, "this", ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT)
        FqName("kotlin.let") -> {
          val argumentName = blockElement.valueParameters.getOrNull(0)?.nameAsName?.value ?: "it"
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
  associatedVarName: ObjectFormula,
  wholeExpr: Expression,
  info: ControlFlowFn,
  data: CheckData
): ContSeq<Return> {
  val thisName = when (info.returnBehavior) {
    ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT ->
      associatedVarName
    ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT ->
      solver.makeObjectVariable(newName(data.context, THIS_VAR_NAME, info.target))
  }
  val returnName = when (info.returnBehavior) {
    ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT ->
      solver.makeObjectVariable(newName(data.context, "ret", info.target))
    ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT ->
      associatedVarName
  }
  return checkReceiverWithPossibleSafeDot(associatedVarName, wholeExpr, null, thisName, info.target, data) {
    data.varInfo.bracket().flatMap {
      // add the name to the context,
      // being careful not overriding any existing name
      info.target?.let {
        val smtName = newName(data.context, info.argumentName, info.target)
        data.varInfo.add(info.argumentName, smtName, info.target, null)
        // add the constraint to make the parameter equal
        val formula = solver.objects {
          equal(solver.makeObjectVariable(smtName), thisName)
        }
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
internal fun SolverState.checkRegularFunctionCall(
  associatedVarName: ObjectFormula,
  resolvedCall: ResolvedCall,
  expression: Expression,
  data: CheckData
): ContSeq<Return> {
  val receiverExpr = resolvedCall.getReceiverExpression()
  val referencedArg = resolvedCall.referencedArg(receiverExpr)
  val receiverName = solver.makeObjectVariable(newName(data.context, THIS_VAR_NAME, receiverExpr, referencedArg))
  return checkReceiverWithPossibleSafeDot(associatedVarName, expression, resolvedCall, receiverName, receiverExpr, data) {
    checkCallArguments(resolvedCall, data).map {
      it.fold(
        { r -> r },
        { argVars ->
          val callConstraints = (constraintsFromSolverState(resolvedCall)
            ?: primitiveConstraints(data.context, resolvedCall))?.let { declInfo ->
            val completeRenaming =
              argVars.toMap() + (RESULT_VAR_NAME to associatedVarName) + (THIS_VAR_NAME to receiverName)
            solver.substituteDeclarationConstraints(declInfo, completeRenaming)
          }
          // check pre-conditions and post-conditions
          checkCallPreConditionsImplication(callConstraints, data.context, expression, resolvedCall)
          // add a constraint for fields: result == field(name, value)
          val descriptor = resolvedCall.resultingDescriptor
          if (descriptor.isField()) {
            val fieldConstraint = solver.ints {
              val typeName = descriptor.fqNameSafe.name
              val argName = if (resolvedCall.hasReceiver()) receiverName else argVars[0].second
              NamedConstraint(
                "${expression.text} == $typeName($argName)",
                equal(
                  associatedVarName,
                  solver.field(typeName, argName))
              )
            }
            addConstraint(fieldConstraint)
          }
          // if the result is not null
          if (!resolvedCall.getReturnType().isNullable()) {
            addConstraint(
              NamedConstraint(
                "$associatedVarName is not null",
                solver.isNotNull(associatedVarName))
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
  associatedVarName: ObjectFormula,
  wholeExpr: Expression,
  resolvedCall: ResolvedCall?,
  receiverName: ObjectFormula,
  receiverExpr: Expression?,
  data: CheckData,
  block: () -> ContSeq<Return>
): ContSeq<Return> = when {
  (receiverExpr != null) && (receiverExpr.impl() == wholeExpr.impl()) ->
    // this happens in some weird cases, just keep going
    block()
  (receiverExpr == null) && (resolvedCall?.hasReceiver() == true) ->
    // special case, no receiver, but implicitly it's 'this'
    checkNameExpression(receiverName, "this", data)
      .flatMap { block() }
  else ->
    checkExpressionConstraints(receiverName, receiverExpr, data).checkReturnInfo {
      // here comes a trick: when the method is access with the "safe dot" ?.
      // we need to create two different "branches",
      // one for the case in which the value is null, one for when it isn't
      //   x?.f(..)  <=>  if (x == null) null else x.f(..)
      // we do so by yielding 'true' and 'false' in that case,
      // and only 'true' when we use a "regular dot" .
      ContSeq {
        if (wholeExpr is SafeQualifiedExpression)
          yield(false)
        yield(true)
      }.flatMap { r ->
        continuationBracket.map { r }
      }.flatMap { definitelyNotNull ->
        if (!definitelyNotNull) { // the null case of ?.
          ContSeq.unit.map {
            val nullReceiver = NamedConstraint("$receiverName is null", solver.isNull(receiverName))
            val nullResult = NamedConstraint("$associatedVarName is null", solver.isNull(associatedVarName))
            val inconsistent = checkConditionsInconsistencies(listOf(nullReceiver, nullResult), data.context, receiverExpr!!)
            ensure(!inconsistent)
            NoReturn
          }
        } else { // the non-null case of ?., or simply regular .
          doOnlyWhenNotNull(receiverExpr, NoReturn) { rcv ->
            ContSeq.unit.map {
              val notNullCstr = NamedConstraint("$receiverName is not null", solver.isNotNull(receiverName))
              val inconsistent = checkConditionsInconsistencies(listOf(notNullCstr), data.context, rcv)
              ensure(!inconsistent)
              NoReturn
            }
          }.flatMap { block() }
        }
      }
    }
}

private fun ResolvedCall.hasReceiver() =
  this.resultingDescriptor.dispatchReceiverParameter != null ||
    this.resultingDescriptor.extensionReceiverParameter != null

/**
 * Checks leftExpr ?: rightExpr
 * This is very similar to [checkReceiverWithPossibleSafeDot],
 * but it's hard to abstract between both due to small details
 */
private fun SolverState.checkElvisOperator(
  associatedVarName: ObjectFormula,
  leftExpr: Expression,
  rightExpr: Expression,
  data: CheckData
): ContSeq<Return> {
  val leftName = newName(data.context, "left", leftExpr)
  val left = solver.makeObjectVariable(leftName)
  return checkExpressionConstraints(leftName, leftExpr, data).checkReturnInfo {
    ContSeq {
      yield(false)
      yield(true)
    }.flatMap { r ->
      continuationBracket.map { r }
    }.flatMap { definitelyNotNull ->
      if (!definitelyNotNull) { // the null case of ?:
        ContSeq.unit.map {
          val nullLeft = NamedConstraint("$leftName is null", solver.isNull(left))
          val inconsistent = checkConditionsInconsistencies(listOf(nullLeft), data.context, leftExpr)
          ensure(!inconsistent)
          NoReturn
        }.flatMap {
          // then the result is whatever we get from the right
          checkExpressionConstraints(associatedVarName, rightExpr, data)
        }
      } else { // the non-null case of ?:
        ContSeq.unit.map {
          val notNullLeft = NamedConstraint("$leftName is not null", solver.isNotNull(left))
          val resultIsLeft = NamedConstraint("$leftName is result of ?:",
            solver.objects { equal(left, associatedVarName) })
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
  resolvedCall: ResolvedCall,
  data: CheckData
): ContSeq<Either<ExplicitReturn, List<Pair<String, ObjectFormula>>>> {
  // why is this so complicated?
  //   in theory, we just need to run checkExpressionConstraints over each argument
  //   (in fact, the original implementation just did that, and then called .sequence())
  //   alas, Kotlin allows arguments to include 'return' (yes, really!)
  //   so we need to check after each step whether a ExplicitReturn has been generated
  //   and in that case we stop the check of any more arguments
  //   (stopping there is important, since introducing additional constraints from
  //    other arguments may not be right in the general case)
  fun <A> acc(
    upToNow: ContSeq<Either<ExplicitReturn, List<Pair<String, ObjectFormula>>>>,
    current: Triple<String, A, Expression?>
  ): ContSeq<Either<ExplicitReturn, List<Pair<String, ObjectFormula>>>> =
    upToNow.flatMap {
      it.fold(
        { r -> cont { r.left() } },
        { argsUpToNow ->
          val (name, _, expr) = current
          val referencedElement = resolvedCall.referencedArg(expr)
          val argUniqueName = solver.makeObjectVariable(newName(data.context, name, expr, referencedElement))
          checkExpressionConstraints(argUniqueName, expr, data).checkReturnInfo({ r -> r.left() }) {
            cont { (argsUpToNow + listOf(name to argUniqueName)).right() }
          }
        }
      )
    }
  return resolvedCall.valueArgumentExpressions(data.context)
    .fold(cont { emptyList<Pair<String, ObjectFormula>>().right() }, ::acc)
}

private fun SolverState.checkNullExpression(
  associatedVarName: ObjectFormula
): ContSeq<Return> = cont {
  addConstraint(NamedConstraint("$associatedVarName is null", solver.isNull(associatedVarName)))
  NoReturn
}

/**
 * This function produces a constraint that makes the desired variable name
 * equal to the value encoded in the constant and adds it to the
 * [SolverState.prover] constraints.
 */
private fun SolverState.checkConstantExpression(
  associatedVarName: ObjectFormula,
  expression: ConstantExpression,
  data: CheckData
): ContSeq<Return> = cont {
  val type = expression.type(data.context)?.unwrapIfNullable()
  when (type?.primitiveType()) {
    PrimitiveType.BOOLEAN ->
      expression.text.toBooleanStrictOrNull()?.let {
        solver.booleans {
          if (it) solver.boolValue(associatedVarName)
          else not(solver.boolValue(associatedVarName))
        }
      }
    PrimitiveType.INTEGRAL ->
      expression.text.asIntegerLiteral()?.let {
        solver.ints {
          equal(
            solver.intValue(associatedVarName),
            makeNumber(it)
          )
        }
      }
    PrimitiveType.RATIONAL ->
      expression.text.asFloatingLiteral()?.let {
        solver.rationals {
          equal(
            solver.decimalValue(associatedVarName),
            makeNumber(it)
          )
        }
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
        solver.isNotNull(associatedVarName)
      )
    )
  }
  NoReturn
}

/**
 * Check special binary cases, and make the other fall-through
 */
private fun SolverState.checkBinaryExpression(
  associatedVarName: ObjectFormula,
  expression: BinaryExpression,
  data: CheckData
): ContSeq<Return> {
  val operator = expression.operationTokenRpr
  val left = expression.left
  val right = expression.right
  return when {
    // this is an assignment to a mutable variable
    operator == "EQ" && left is NameReferenceExpression -> {
      // we introduce a new name because we don't want to introduce
      // any additional information about the variable,
      // we should only have that declared in the invariant
      val newName = newName(data.context, left.getReferencedName(), left)
      val invariant = data.varInfo.get(left.getReferencedName())?.invariant
      checkBodyAgainstInvariants(expression, newName, invariant, expression.right, data)
        .map { it.second } // forget about the temporary name
    }
    // this is x == null, or x != null
    (operator == "EQEQ" || operator == "EXCLEQ") && right is NullExpression -> {
      val newName = solver.makeObjectVariable(newName(data.context, "checkNull", left))
      checkExpressionConstraints(newName, left, data).checkReturnInfo {
        cont {
          when (operator) {
            "EQEQ" -> solver.isNull(newName)
            "EXCLEQ" -> solver.isNotNull(newName)
            else -> null
          }?.let {
            val cstr = solver.booleans { equivalence(solver.boolValue(associatedVarName), it) }
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
  associatedVarName: ObjectFormula,
  isNegated: Boolean,
  typeReference: TypeReference?,
  subjectName: ObjectFormula,
  data: CheckData
): ContSeq<Return> = doOnlyWhen(!isNegated, NoReturn) {
  cont {
    val invariants =
      (data.context.type(typeReference)
        ?.let { typeInvariants(data.context, it, subjectName) })
      // in the worst case, we know that it is not null
        ?: listOf(NamedConstraint("$associatedVarName is not null", solver.isNotNull(subjectName)))
    invariants.forEach { cstr ->
      val constraint = NamedConstraint(
        "$associatedVarName => ${cstr.msg}",
        solver.booleanFormulaManager.implication(
          solver.boolValue(associatedVarName),
          cstr.formula
        )
      )
      addConstraint(constraint)
    }
    NoReturn
  }
}

/**
 * This function produces a continuation that makes the desired variable name
 * equal to the value encoded in the named expression.
 */
private fun SolverState.checkDeclarationExpression(
  declaration: Declaration,
  data: CheckData
): ContSeq<Pair<String?, Return>> =
  doOnlyWhenNotNull(declaration.stableBody(), Pair(null, NoReturn)) { body ->
    val declName = when (declaration) {
      // use the given name if available
      is NamedDeclaration -> declaration.nameAsSafeName.value
      else -> newName(data.context, "decl", body)
    }
    // we need to create a new one to prevent shadowing
    val smtName = newName(data.context, declName, body)
    // find out whether we have an invariant
    val invariant = obtainInvariant(body, data)
    // assert the invariant if found and check its consistency
    doOnlyWhenNotNull(invariant, Unit) { (invBody, invFormula: BooleanFormula) ->
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
      Pair(newVarName, r)
    }
  }

/**
 * Checks the possible invariants of a declaration, and its body.
 */
private fun SolverState.checkBodyAgainstInvariants(
  element: Element,
  declName: String,
  invariant: BooleanFormula?,
  body: Expression?,
  data: CheckData
): ContSeq<Pair<String, Return>> {
  val newName = newName(data.context, declName, body)
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
  expression: Expression,
  data: CheckData
): Pair<Expression, BooleanFormula>? =
  expression.getResolvedCall(data.context)
    ?.takeIf { it.invariantCall() }
    ?.arg("predicate", data.context)
    ?.let { expr: Expression ->
      solver.expressionToFormula(expr, data.context, emptyList(), true)
        ?.let { it as? BooleanFormula }
        ?.let { formula -> expr to formula }
    }

/**
 * This function produces a continuation that makes the desired variable name
 * equal to the value encoded in the named expression and adds the resulting boolean formula
 * to the [SolverState.prover] constraints.
 */
private fun SolverState.checkNameExpression(
  associatedVarName: ObjectFormula,
  referencedName: String,
  data: CheckData
): ContSeq<Return> = cont {
  // use the SMT name recorded in the variable info
  data.varInfo.get(referencedName)?.let {
    val constraint = solver.objects {
      equal(associatedVarName, solver.makeObjectVariable(it.smtName))
    }
    addConstraint(NamedConstraint("$associatedVarName = ${it.smtName}", constraint))
  }
  NoReturn
}

private fun Expression.computeConditions(): List<Condition> = when (this) {
  is IfExpression ->
    listOf(
      SimpleCondition(condition!!, thenExpression!!, thenExpression!!),
      SimpleCondition(null, elseExpression!!, elseExpression!!)
    )
  is WhenExpression -> {
    val subject = subjectExpression
    entries.flatMap { entry ->
      if (entry.conditions.isEmpty()) {
        listOf(SimpleCondition(null, entry.expression!!, entry))
      } else {
        entry.conditions.toList().mapNotNull { cond ->
          when {
            subject != null ->
              SubjectCondition(cond, entry.expression!!, entry)
            cond is WhenConditionWithExpression ->
              SimpleCondition(cond.expression!!, entry.expression!!, entry)
            else -> null
          }
        }
      }
    }
  }
  else -> emptyList()
}

/**
 * Check `if` and `when` expressions.
 */
private fun SolverState.checkConditional(
  associatedVarName: ObjectFormula,
  subject: Expression?,
  branches: List<Condition>,
  data: CheckData
): ContSeq<Return> {
  val newSubjectVar = solver.makeObjectVariable(newName(data.context, "subject", subject))
  // this handles the cases of when with a subject, and with 'val x = subject'
  return when (subject) {
    is Declaration -> checkDeclarationExpression(subject, data).map { (actualSubjectVar, _) ->
      actualSubjectVar?.let { solver.makeObjectVariable(it) } ?: newSubjectVar
    }
    else -> checkExpressionConstraints(newSubjectVar, subject, data).map { newSubjectVar }
  }.flatMap { subjectVar ->
    branches.map { cond ->
      val conditionVar = newName(data.context, "cond", cond.condition)
      // introduce the condition
      (cond.condition?.let {
        introduceCondition(solver.makeObjectVariable(conditionVar), subjectVar, cond, data)
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
    }.sequence()
  }.flatMap { conditionInformation ->
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
}

private fun SolverState.introduceCondition(
  conditionVar: ObjectFormula,
  subjectVar: ObjectFormula,
  cond: Condition,
  data: CheckData
): ContSeq<Return> = when (cond) {
  is SimpleCondition -> checkExpressionConstraints(conditionVar, cond.predicate, data)
  is SubjectCondition -> when (val check = cond.check) {
    is WhenConditionWithExpression ->
      if (check.expression is NullExpression) {
        cont {
          val complete = solver.booleans {
            equivalence(solver.boolValue(conditionVar), solver.isNull(subjectVar))
          }
          addConstraint(NamedConstraint("$subjectVar is null", complete))
          NoReturn
        }
      } else {
        val patternName = newName(data.context, "pattern", check.expression)
        checkExpressionConstraints(patternName, check.expression, data).map {
          when (check.expression?.type(data.context)?.primitiveType()) {
            PrimitiveType.BOOLEAN -> solver.booleans {
              equivalence(solver.boolValue(subjectVar), solver.makeBooleanObjectVariable(patternName))
            }
            PrimitiveType.INTEGRAL -> solver.ints {
              equal(solver.intValue(subjectVar), solver.makeIntegerObjectVariable(patternName))
            }
            PrimitiveType.RATIONAL -> solver.rationals {
              equal(solver.decimalValue(subjectVar), solver.makeDecimalObjectVariable(patternName))
            }
            else -> null
          }?.let {
            val complete = solver.booleans {
              equivalence(solver.boolValue(conditionVar), it)
            }
            addConstraint(NamedConstraint("$subjectVar equals $patternName", complete))
          }
          NoReturn
        }
      }
    is WhenConditionIsPattern ->
      checkIsExpression(conditionVar, check.isNegated, check.typeReference, subjectVar, data)
    else -> cont { NoReturn }
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
  expression: LoopExpression,
  data: CheckData
): ContSeq<Return> = when (expression) {
  is ForExpression ->
    checkForExpression(expression.loopParameter, expression.body, data)
  is WhileExpression ->
    doOnlyWhenNotNull(expression.condition, NoReturn) {
      checkWhileExpression(it, expression.body, data)
    }
  is DoWhileExpression -> {
    // remember that do { t } while (condition)
    // is equivalent to { t }; while (condition) { t }
    checkExpressionConstraintsWithNewName("firstIter", expression.body, data).flatMap {
      doOnlyWhenNotNull(expression.condition, NoReturn) {
        checkWhileExpression(it, expression.body, data)
      }
    }
  }
  else -> ContSeq.unit.map { NoReturn } // this should not happen
}

private fun SolverState.checkForExpression(
  loopParameter: Parameter?,
  body: Expression?,
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
        val paramName = loopParameter?.nameAsName?.value
        if (loopParameter != null && paramName != null) {
          val smtName = newName(data.context, paramName, loopParameter)
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
  condition: Expression,
  body: Expression?,
  data: CheckData
): ContSeq<Return> {
  val condName = newName(data.context, "cond", condition)
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
  body: Expression?,
  data: CheckData
): ContSeq<Return> {
  return checkExpressionConstraintsWithNewName("loop", body, data).map { returnInfo ->
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
  associatedVarName: ObjectFormula,
  expression: TryExpression,
  data: CheckData
): ContSeq<Return> =
  ContSeq {
    yield(expression.tryBlock)
    yieldAll(expression.catchClauses)
  }.flatMap { r ->
    continuationBracket.flatMap { data.varInfo.bracket() }.map { r }
  }.flatMap {
    when (it) {
      is BlockExpression -> // the try
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
      is CatchClause -> { // the catch
        doOnlyWhenNotNull(it.catchParameter, NoReturn) { param ->
          doOnlyWhenNotNull(param.nameAsName?.value, NoReturn) { paramName ->
            // introduce the name of the parameter
            val smtName = newName(data.context, paramName, param)
            data.varInfo.add(paramName, smtName, param, null)
            // and then go on and check the body
            checkExpressionConstraints(associatedVarName, it.catchBody, data)
          }
        }
      }
      else -> ContSeq { abort() }
    }
  }.onEach { returnInfo ->
    doOnlyWhenNotNull(expression.finallyBlock, returnInfo) { finally ->
      // override the return of the finally with the return of the try or catch
      checkExpressionConstraintsWithNewName("finally", finally.finalExpression, data)
        .map { returnInfo }
    }
  }

/**
 * Checks whether the type obtain from an explicit 'throw'
 * matches any of the types in the 'catch' clauses
 */
fun doesAnyCatchMatch(
  throwType: Type?,
  clauses: List<CatchClause>,
  data: CheckData
): Boolean = clauses.any { clause ->
  val catchType = clause.catchParameter?.type(data.context)
  if (throwType != null && catchType != null) {
    throwType.isSubtypeOf(catchType)
  } else {
    false
  }
}

/**
 * Find the corresponding "body" of a declaration
 */
internal fun Declaration.stableBody(): Expression? = when (this) {
  is VariableDeclaration -> initializer
  is DeclarationWithBody -> bodyExpression ?: bodyBlockExpression
  is DeclarationWithInitializer -> initializer
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
