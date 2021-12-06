package arrow.meta.plugins.analysis.phases.analysis.solver.check

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.meta.continuations.ContSeq
import arrow.meta.continuations.asContSeq
import arrow.meta.continuations.cont
import arrow.meta.continuations.doOnlyWhenNotNull
import arrow.meta.continuations.nested
import arrow.meta.plugins.analysis.phases.analysis.solver.ArgumentExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.RESULT_VAR_NAME
import arrow.meta.plugins.analysis.phases.analysis.solver.SpecialKind
import arrow.meta.plugins.analysis.phases.analysis.solver.THIS_VAR_NAME
import arrow.meta.plugins.analysis.phases.analysis.solver.arg
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableMemberDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.LocalVariableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PropertyDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnnotatedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AssignmentExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BinaryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BreakExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CallableReferenceExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CatchClause
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ConstantExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ContinueExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DeclarationWithBody
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DeclarationWithInitializer
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DoWhileExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DoubleColonExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ExpressionWithLabel
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ForExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Function
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.IfExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.IsExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LabeledExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LambdaExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LoopExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NamedDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NullExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ParenthesizedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.QualifiedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ReturnExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SafeQualifiedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SynchronizedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ThisExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ThreePieceForExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ThrowExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.VariableDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenConditionIsPattern
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenConditionWithExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhileExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.CheckData
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.Condition
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.ControlFlowFn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.ExplicitBlockReturn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.ExplicitLoopReturn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.ExplicitReturn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.ExplicitThrowReturn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.LoopPlace
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.NoReturn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.SimpleCondition
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.StateAfter
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.SubjectCondition
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.VarInfo
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.noReturn
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.DeclarationConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.topLevelExpressionToFormula
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorIds
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages
import arrow.meta.plugins.analysis.phases.analysis.solver.getReceiverOrThisNamedArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.hasReceiver
import arrow.meta.plugins.analysis.phases.analysis.solver.inTrustedEnvironment
import arrow.meta.plugins.analysis.phases.analysis.solver.isElvisOperator
import arrow.meta.plugins.analysis.phases.analysis.solver.isField
import arrow.meta.plugins.analysis.phases.analysis.solver.referencedArg
import arrow.meta.plugins.analysis.phases.analysis.solver.search.getConstraintsFor
import arrow.meta.plugins.analysis.phases.analysis.solver.search.primitiveConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.search.typeInvariants
import arrow.meta.plugins.analysis.phases.analysis.solver.specialKind
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkCallPostConditionsInconsistencies
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkCallPreConditionsImplication
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkConditionsInconsistencies
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkInvariant
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkInvariantConsistency
import arrow.meta.plugins.analysis.phases.analysis.solver.valueArgumentExpressions
import arrow.meta.plugins.analysis.smt.ObjectFormula
import arrow.meta.plugins.analysis.smt.renameObjectVariables
import arrow.meta.plugins.analysis.smt.substituteDeclarationConstraints
import arrow.meta.plugins.analysis.types.PrimitiveType
import arrow.meta.plugins.analysis.types.asFloatingLiteral
import arrow.meta.plugins.analysis.types.asIntegerLiteral
import arrow.meta.plugins.analysis.types.primitiveType
import arrow.meta.plugins.analysis.types.unwrapIfNullable
import org.apache.commons.text.StringEscapeUtils
import org.sosy_lab.java_smt.api.BooleanFormula

// 2.2: expressions
// ----------------

internal fun SolverState.checkExpressionConstraints(
  associatedVarName: String,
  expression: Expression?,
  data: CheckData
): ContSeq<StateAfter> =
  checkExpressionConstraints(solver.makeObjectVariable(associatedVarName), expression, data)

internal fun SolverState.checkExpressionConstraintsWithNewName(
  prefix: String,
  expression: Expression?,
  data: CheckData
): ContSeq<StateAfter> =
  checkExpressionConstraints(newName(data.context, prefix, expression), expression, data)

/**
 * Produces a continuation that when invoked recursively checks an [expression] set of constraints
 */
internal fun SolverState.checkExpressionConstraints(
  associatedVarName: ObjectFormula,
  expression: Expression?,
  data: CheckData
): ContSeq<StateAfter> =
  ContSeq.unit
    .map {
      if (expression != null)
        solverTrace.add("BEGIN ${expression.text.take(50)} ${expression.javaClass.name}")
    }
    .flatMap {
      when (expression) {
        // these two simply recur into their underlying expressions
        is ParenthesizedExpression ->
          checkExpressionConstraints(associatedVarName, expression.expression, data)
        is AnnotatedExpression ->
          checkExpressionConstraints(associatedVarName, expression.baseExpression, data)
        is SynchronizedExpression ->
          checkExpressionConstraintsWithNewName("subject", expression.subject, data)
            .checkReturnInfo { stateAfter ->
              checkExpressionConstraints(associatedVarName, expression.block, stateAfter.data)
            }
        is BlockExpression ->
          inScope(data) { // new variables are local to that block
            checkBlockExpression(
              associatedVarName,
              expression.statements,
              expression.implicitReturnFromLast,
              data
            )
          }
        is ReturnExpression -> checkReturnConstraints(expression, data)
        is BreakExpression, is ContinueExpression -> {
          val withLabel = expression as ExpressionWithLabel
          cont { StateAfter(ExplicitLoopReturn(withLabel.getLabelName()), data) }
        }
        is CallableReferenceExpression ->
          data.noReturn {
            // for now we do nothing with function references
          }
        is LambdaExpression -> checkLambda(expression, data)
        is ThrowExpression -> checkThrowConstraints(expression, data)
        is NullExpression -> checkNullExpression(associatedVarName, data)
        is ConstantExpression -> checkConstantExpression(associatedVarName, expression, data)
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
            else -> data.noReturn {} // this should not happen
          }
        }
        is LabeledExpression -> checkLabeledExpression(associatedVarName, expression, data)
        is IfExpression ->
          checkConditional(associatedVarName, null, expression.computeConditions(), data)
        is WhenExpression ->
          checkConditional(
            associatedVarName,
            expression.subjectExpression,
            expression.computeConditions(),
            data
          )
        is LoopExpression -> checkLoopExpression(expression, data)
        is TryExpression -> checkTryExpression(associatedVarName, expression, data)
        is IsExpression -> {
          val subject = expression.leftHandSide
          val subjectName = solver.makeObjectVariable(newName(data.context, "is", subject))
          checkExpressionConstraints(subjectName, subject, data).checkReturnInfo { stateAfter ->
            cont {
              checkIsExpression(
                associatedVarName,
                expression.isNegated,
                expression.typeReference,
                subjectName,
                data
              )
              stateAfter
            }
          }
        }
        is AssignmentExpression -> checkAssignmentExpression(expression, data)
        is BinaryExpression -> checkBinaryExpression(associatedVarName, expression, data)
        is Function -> checkFunctionDeclarationExpression(expression, data)
        is Declaration ->
          // we get additional info about the subject, but it's irrelevant here
          checkNonFunctionDeclarationExpression(expression, data).map { it.second }
        is Expression -> fallThrough(associatedVarName, expression, data)
        else -> data.noReturn {}
      }
    }
    .onEach {
      if (expression != null)
        solverTrace.add("END ${expression.text.take(50)} ${expression.javaClass.name}")
    }

private fun SolverState.fallThrough(
  associatedVarName: ObjectFormula,
  expression: Expression,
  data: CheckData
): ContSeq<StateAfter> =
  when (val call = expression.getResolvedCall(data.context)) {
    // fall-through: treat as a call
    is ResolvedCall -> checkCallExpression(associatedVarName, expression, call, data)
    // otherwise, report as unsupported
    else ->
      when (val variable = expression.getVariableDescriptor(data.context)) {
        is VariableDescriptor -> {
          val receiverExpr =
            when (expression) {
              is QualifiedExpression -> expression.receiverExpression
              is DoubleColonExpression -> expression.receiverExpression
              else -> null
            }
          checkVariableAsFunctionCall(associatedVarName, variable, receiverExpr, expression, data)
        }
        else ->
          data.noReturn {
            val msg = ErrorMessages.Unsupported.unsupportedExpression(expression)
            data.context.handleError(ErrorIds.Unsupported.UnsupportedExpression, expression, msg)
          }
      }
  }

private fun Declaration.isVar(): Boolean =
  when (this) {
    is VariableDeclaration -> this.isVar
    else -> false
  }

/**
 * Checks each statement in a block expression in order. We need our own function because only the
 * *last* statement is the one assigned as the "return value" of the block.
 */
private fun SolverState.checkBlockExpression(
  associatedVarName: ObjectFormula,
  expressions: List<Expression>,
  implicitReturnFromLast: Boolean,
  data: CheckData
): ContSeq<StateAfter> =
  when {
    expressions.isEmpty() -> data.noReturn {}
    expressions.size == 1 &&
      implicitReturnFromLast -> // this is the last element, so it's the return value of the
      // expression
      checkExpressionConstraints(associatedVarName, expressions[0], data)
    else -> {
      val first = expressions[0]
      val remainder = expressions.drop(1)
      // we need to carefully thread the data in the state,
      // since it holds the information about variables
      checkExpressionConstraintsWithNewName("stmt", first, data).checkReturnInfo { stateAfter ->
        checkBlockExpression(associatedVarName, remainder, implicitReturnFromLast, stateAfter.data)
      }
    }
  }

/** Checks a block which introduces a label or scope. */
private fun SolverState.checkLabeledExpression(
  associatedVarName: ObjectFormula,
  expression: LabeledExpression,
  data: CheckData
): ContSeq<StateAfter> {
  val labelName = expression.getLabelName()!!
  // add the return point to the list and recur
  val updatedData = data.addReturnPoint(labelName, associatedVarName)
  return checkExpressionConstraints(associatedVarName, expression.baseExpression, updatedData)
    .map { stateAfter ->
      // if we have reached the point where the label was introduced,
      // then we are done with the block, and we can keep going
      val r = stateAfter.returnInfo
      if (r is ExplicitBlockReturn && r.returnPoint == labelName) {
        stateAfter.withReturn(NoReturn)
      } else {
        stateAfter
      }
    }
}

/**
 * Checks a 'return' or 'return@label' statement. At the end it aborts the current computation,
 * because after a return there's nothing else to be checked.
 */
private fun SolverState.checkReturnConstraints(
  expression: ReturnExpression,
  data: CheckData
): ContSeq<StateAfter> {
  // figure out the right variable to assign
  // - if 'return@label', find the label in the recorded return points
  // - otherwise, it should be the top-most one
  val label = expression.getLabelName()
  val returnVarName =
    label.let { data.returnPoints.namedReturnPointVariableNames[it] }
      ?: data.returnPoints.topMostReturnPointVariableName.second
  // assign it, and signal that we explicitly return
  return checkExpressionConstraints(returnVarName, expression.returnedExpression, data).map {
    stateAfter ->
    stateAfter.withReturn(ExplicitBlockReturn(label))
  }
}

/** Checks a 'throw', by simply returning the type of the exception */
private fun SolverState.checkThrowConstraints(
  expression: ThrowExpression,
  data: CheckData
): ContSeq<StateAfter> {
  return checkExpressionConstraintsWithNewName("throw", expression.thrownExpression, data).map {
    stateAfter ->
    stateAfter.withReturn(
      expression.thrownExpression?.type(data.context)?.let { ty -> ExplicitThrowReturn(ty) }
        ?: ExplicitThrowReturn(null)
    )
  }
}

/**
 * Produces a continuation that when invoked recursively checks the call [resolvedCall] starting
 * from its arguments
 */
private fun SolverState.checkCallExpression(
  associatedVarName: ObjectFormula,
  expression: Expression,
  resolvedCall: ResolvedCall,
  data: CheckData
): ContSeq<StateAfter> {
  val specialKind = resolvedCall.specialKind
  val specialControlFlow = controlFlowAnyFunction(data.context, resolvedCall)
  return when {
    specialKind == SpecialKind.Pre -> // ignore calls to 'pre'
    data.noReturn {}
    specialKind == SpecialKind.Post -> // ignore post arguments
    checkExpressionConstraints(
        associatedVarName,
        resolvedCall.getReceiverOrThisNamedArgument(),
        data
      )
    specialKind == SpecialKind.Invariant -> // ignore invariant arguments
    checkExpressionConstraints(
        associatedVarName,
        resolvedCall.getReceiverOrThisNamedArgument(),
        data
      )
    specialKind == SpecialKind.TrustCall || specialKind == SpecialKind.TrustBlock -> {
      val arg = resolvedCall.valueArgumentExpressions(data.context).getOrNull(0)
      checkExpressionConstraints(associatedVarName, arg?.expression, data)
    }
    specialControlFlow != null ->
      checkControlFlowFunctionCall(associatedVarName, expression, specialControlFlow, data)
    resolvedCall.isElvisOperator() ->
      doOnlyWhenNotNull(resolvedCall.arg("left", data.context), data.noReturn()) { left ->
        doOnlyWhenNotNull(resolvedCall.arg("right", data.context), data.noReturn()) { right ->
          checkElvisOperator(associatedVarName, left, right, data)
        }
      }
    else -> checkRegularFunctionCall(associatedVarName, resolvedCall, expression, data)
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
          ControlFlowFn(
            thisElement,
            bodyElement,
            argumentName,
            ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT
          )
        }
        FqName("kotlin.apply") ->
          ControlFlowFn(
            thisElement,
            bodyElement,
            "this",
            ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT
          )
        FqName("kotlin.let") -> {
          val argumentName = blockElement.valueParameters.getOrNull(0)?.nameAsName?.value ?: "it"
          ControlFlowFn(
            thisElement,
            bodyElement,
            argumentName,
            ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT
          )
        }
        FqName("kotlin.run") ->
          ControlFlowFn(
            thisElement,
            bodyElement,
            "this",
            ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT
          )
        FqName("kotlin.with") ->
          ControlFlowFn(
            thisElement,
            bodyElement,
            "this",
            ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT
          )
        else -> null
      }
    } else {
      when (resolvedCall.resultingDescriptor.fqNameSafe) {
        // 'run' can also be called without a receiver
        // https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/run.html
        FqName("kotlin.run") ->
          ControlFlowFn(
            null /* thisElement == null */,
            bodyElement,
            "this",
            ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT
          )
        else -> null
      }
    }
  } else {
    null
  }
}

/** Checks special control flow functions ([also], [apply], [let], [run]) */
private fun SolverState.checkControlFlowFunctionCall(
  associatedVarName: ObjectFormula,
  wholeExpr: Expression,
  info: ControlFlowFn,
  data: CheckData
): ContSeq<StateAfter> {
  val thisName =
    when (info.returnBehavior) {
      ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT -> associatedVarName
      ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT ->
        solver.makeObjectVariable(newName(data.context, THIS_VAR_NAME, info.target))
    }
  val returnName =
    when (info.returnBehavior) {
      ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT ->
        solver.makeObjectVariable(newName(data.context, "ret", info.target))
      ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT -> associatedVarName
    }
  return checkReceiverWithPossibleSafeDot(
    associatedVarName,
    wholeExpr,
    null,
    thisName,
    info.target,
    data
  ) { newData ->
    inScope(newData) {
      // add the name to the context,
      // being careful not overriding any existing name
      val newVarInfo: VarInfo? =
        info.target?.let {
          val smtName = newName(newData.context, info.argumentName, info.target)
          // add the constraint to make the parameter equal
          val formula = solver.objects { equal(solver.makeObjectVariable(smtName), thisName) }
          addConstraint(NamedConstraint("introduce argument for lambda", formula), data.context)
          VarInfo(info.argumentName, smtName, info.target, null)
        }
      // check the body in this new context
      checkExpressionConstraints(
        returnName,
        info.body,
        newData.addVarInfos(listOfNotNull(newVarInfo))
      )
    }
  }
}

/** Checks any function call which is not a special case */
internal fun SolverState.checkRegularFunctionCall(
  associatedVarName: ObjectFormula,
  resolvedCall: ResolvedCall,
  expression: Expression,
  data: CheckData
): ContSeq<StateAfter> {
  val receiverExpr = resolvedCall.getReceiverExpression()
  val referencedArg = resolvedCall.referencedArg(receiverExpr)
  val receiverName =
    solver.makeObjectVariable(newName(data.context, THIS_VAR_NAME, receiverExpr, referencedArg))
  return checkReceiverWithPossibleSafeDot(
    associatedVarName,
    expression,
    resolvedCall,
    receiverName,
    receiverExpr, //
    data
  ) { dataAfterReceiver ->
    checkCallArguments(resolvedCall, dataAfterReceiver).flatMap { (returnOrContinue, dataAfterArgs)
      ->
      returnOrContinue.fold(
        { r -> cont { StateAfter(r, dataAfterArgs) } },
        { argVars ->
          val callConstraints =
            getConstraintsFor(resolvedCall) ?: primitiveConstraints(data.context, resolvedCall)
          checkCallableDescriptor(
            associatedVarName,
            resolvedCall.resultingDescriptor,
            callConstraints,
            argVars,
            preconditionsCheck = { reworkedConstraints ->
              whenNotTrusted(expression, data) {
                checkCallPreConditionsImplication(
                  reworkedConstraints,
                  dataAfterArgs.context,
                  expression,
                  resolvedCall,
                  dataAfterArgs.branch.get()
                )
              }
            },
            resolvedCall.hasReceiver(),
            receiverName,
            resolvedCall.getReturnType(),
            expression,
            dataAfterArgs
          )
        }
      )
    }
  }
}

internal fun SolverState.checkVariableAsFunctionCall(
  associatedVarName: ObjectFormula,
  descriptor: VariableDescriptor,
  receiverExpr: Expression?,
  expression: Expression,
  data: CheckData
): ContSeq<StateAfter> {
  val receiverName = solver.makeObjectVariable(newName(data.context, THIS_VAR_NAME, receiverExpr))
  val callConstraints = getConstraintsFor(descriptor)
  return checkCallableDescriptor(
    associatedVarName,
    descriptor,
    callConstraints,
    emptyList(),
    preconditionsCheck = {},
    receiverExpr != null,
    receiverName,
    descriptor.type,
    expression,
    data
  )
}

/** Checks something similar to a function call, which could also be a variable reference */
private fun SolverState.checkCallableDescriptor(
  associatedVarName: ObjectFormula,
  descriptor: CallableDescriptor,
  obtainedConstraints: DeclarationConstraints?,
  argVars: List<CallArgumentVariable>,
  preconditionsCheck: (DeclarationConstraints?) -> Unit,
  hasReceiver: Boolean,
  receiverName: ObjectFormula,
  returnType: Type,
  expression: Expression,
  data: CheckData
): ContSeq<StateAfter> =
  ContSeq.unit.map {
    // rename with the arguments
    val callConstraints =
      obtainedConstraints?.let { declInfo ->
        val completeRenaming =
          argVars.toMap() + (RESULT_VAR_NAME to associatedVarName) + (THIS_VAR_NAME to receiverName)
        solver.substituteDeclarationConstraints(declInfo, completeRenaming)
      }
    // check any preconditions
    preconditionsCheck(callConstraints)
    // add a constraint for fields: result == field(name, value)
    if (descriptor.isField()) {
      val fieldConstraint =
        solver.ints {
          val argName = if (hasReceiver) receiverName else argVars[0].assignedSmtVariable
          NamedConstraint(
            "${expression.text} == ${descriptor.fqNameSafe.name}($argName)",
            equal(associatedVarName, field(descriptor, argName))
          )
        }
      addConstraint(fieldConstraint, data.context)
    }
    // if the result is not null
    if (!returnType.isNullable()) {
      addConstraint(
        NamedConstraint("$associatedVarName is not null", solver.isNotNull(associatedVarName)),
        data.context
      )
    }
    // there's no point in continuing if we are in an inconsistent position
    val inconsistentPostConditions =
      checkCallPostConditionsInconsistencies(
        callConstraints,
        data.context,
        expression,
        data.branch.get()
      )
    ensure(!inconsistentPostConditions)
    data.noReturn()
  }

/** Handles the possibility of a function call being done with ?. */
private fun SolverState.checkReceiverWithPossibleSafeDot(
  associatedVarName: ObjectFormula,
  wholeExpr: Expression,
  resolvedCall: ResolvedCall?,
  receiverName: ObjectFormula,
  receiverExpr: Expression?,
  data: CheckData,
  block: (CheckData) -> ContSeq<StateAfter>
): ContSeq<StateAfter> =
  when {
    (receiverExpr != null) && (receiverExpr.impl() == wholeExpr.impl()) -> {
      // this happens in some weird cases, just keep going
      solverTrace.add("weird case")
      block(data)
    }
    (receiverExpr == null) && (resolvedCall?.hasReceiver() == true) ->
      // special case, no receiver, but implicitly it's 'this'
      checkNameExpression(receiverName, "this", data).flatMap { stateAfter ->
        block(stateAfter.data)
      }
    else ->
      checkExpressionConstraints(receiverName, receiverExpr, data).checkReturnInfo {
        stateAfterReceiver ->
        val dataAfterReceiver = stateAfterReceiver.data
        // here comes a trick: when the method is access with the "safe dot" ?.
        // we need to create two different "branches",
        // one for the case in which the value is null, one for when it isn't
        //   x?.f(..)  <=>  if (x == null) null else x.f(..)
        // we do so by yielding 'true' and 'false' in that case,
        // and only 'true' when we use a "regular dot" .
        ContSeq {
          if (wholeExpr is SafeQualifiedExpression) yield(false)
          yield(true)
        }
          .flatMap { r -> continuationBracket.map { r } }
          .flatMap { definitelyNotNull ->
            solverTrace.add("?. case $definitelyNotNull")
            if (!definitelyNotNull) { // the null case of ?.
              ContSeq.unit.map {
                val nullReceiver =
                  NamedConstraint("$receiverName is null (?.)", solver.isNull(receiverName))
                val nullResult =
                  NamedConstraint(
                    "$associatedVarName is null (?.)",
                    solver.isNull(associatedVarName)
                  )
                val inconsistent =
                  checkConditionsInconsistencies(
                    listOf(nullReceiver, nullResult),
                    dataAfterReceiver.context,
                    receiverExpr!!,
                    dataAfterReceiver.branch.get()
                  )
                ensure(!inconsistent)
                dataAfterReceiver.addBranch(solver.isNull(receiverName)).noReturn()
              }
            } else { // the non-null case of ?., or simply regular .
              doOnlyWhenNotNull(receiverExpr, dataAfterReceiver.noReturn()) { rcv ->
                ContSeq.unit.map {
                  val notNullCstr =
                    NamedConstraint(
                      "$receiverName is not null (?.)",
                      solver.isNotNull(receiverName)
                    )
                  val inconsistent =
                    checkConditionsInconsistencies(
                      listOf(notNullCstr),
                      data.context,
                      rcv,
                      dataAfterReceiver.branch.get()
                    )
                  ensure(!inconsistent)
                  dataAfterReceiver.addBranch(solver.isNotNull(receiverName)).noReturn()
                }
              }
                .flatMap { stateAfterNotNullReceiver -> block(stateAfterNotNullReceiver.data) }
            }
          }
      }
  }

/**
 * Checks leftExpr ?: rightExpr This is very similar to [checkReceiverWithPossibleSafeDot], but it's
 * hard to abstract between both due to small details
 */
private fun SolverState.checkElvisOperator(
  associatedVarName: ObjectFormula,
  leftExpr: Expression,
  rightExpr: Expression,
  data: CheckData
): ContSeq<StateAfter> {
  val leftName = newName(data.context, "left", leftExpr)
  val left = solver.makeObjectVariable(leftName)
  return checkExpressionConstraints(leftName, leftExpr, data).checkReturnInfo { stateAfterLeft ->
    ContSeq {
      yield(false)
      yield(true)
    }
      .flatMap { r -> continuationBracket.map { r } }
      .flatMap { definitelyNotNull ->
        if (!definitelyNotNull) { // the null case of ?:
          ContSeq.unit
            .map {
              val nullLeft = NamedConstraint("$leftName is null (?:)", solver.isNull(left))
              val inconsistent =
                checkConditionsInconsistencies(
                  listOf(nullLeft),
                  data.context,
                  leftExpr,
                  data.branch.get()
                )
              ensure(!inconsistent)
            }
            .flatMap {
              // then the result is whatever we get from the right
              checkExpressionConstraints(
                associatedVarName,
                rightExpr,
                stateAfterLeft.data.addBranch(solver.isNull(left))
              )
            }
        } else { // the non-null case of ?:
          ContSeq.unit
            .map {
              val notNullLeft = NamedConstraint("$leftName is not null", solver.isNotNull(left))
              val resultIsLeft =
                NamedConstraint(
                  "$leftName is result of ?:",
                  solver.objects { equal(left, associatedVarName) }
                )
              val inconsistent =
                checkConditionsInconsistencies(
                  listOf(notNullLeft, resultIsLeft),
                  data.context,
                  leftExpr,
                  data.branch.get()
                )
              ensure(!inconsistent)
            }
            .map { stateAfterLeft.data.addBranch(solver.isNotNull(left)).noReturn() }
        }
      }
  }
}

internal data class CallArgumentsInfo(
  val returnOrVariables: Either<ExplicitReturn, List<CallArgumentVariable>>,
  val data: CheckData
) {
  internal companion object {
    fun init(data: CheckData) = CallArgumentsInfo(emptyList<CallArgumentVariable>().right(), data)
  }
}

internal data class CallArgumentVariable(
  val parameterName: String,
  val assignedSmtVariable: ObjectFormula
)

private fun List<CallArgumentVariable>.toMap() = associate { (name, smt) -> name to smt }

/**
 * Recursively perform check on arguments, including extension receiver and dispatch receiver
 *
 * [NOTE: argument renaming] this function creates a new name for each argument, based on the formal
 * parameter name; this creates a renaming for the original constraints
 */
private fun SolverState.checkCallArguments(
  resolvedCall: ResolvedCall,
  data: CheckData
): ContSeq<CallArgumentsInfo> {
  // why is this so complicated?
  //   in theory, we just need to run checkExpressionConstraints over each argument
  //   (in fact, the original implementation just did that, and then called .sequence())
  //   alas, Kotlin allows arguments to include 'return' (yes, really!)
  //   so we need to check after each step whether a ExplicitReturn has been generated
  //   and in that case we stop the check of any more arguments
  //   (stopping there is important, since introducing additional constraints from
  //    other arguments may not be right in the general case)
  fun acc(
    upToNow: ContSeq<CallArgumentsInfo>,
    current: ArgumentExpression
  ): ContSeq<CallArgumentsInfo> =
    upToNow.flatMap { (result, data) ->
      result.fold(
        { r -> cont { CallArgumentsInfo(r.left(), data) } },
        { argsUpToNow ->
          val (name, _, expr) = current
          val referencedElement = resolvedCall.referencedArg(expr)
          val argUniqueName =
            solver.makeObjectVariable(newName(data.context, name, expr, referencedElement))
          checkExpressionConstraints(argUniqueName, expr, data).checkReturnInfo({ r, s ->
            CallArgumentsInfo(r.left(), s.data)
          }) { s ->
            cont {
              CallArgumentsInfo(
                (argsUpToNow + listOf(CallArgumentVariable(name, argUniqueName))).right(),
                s.data
              )
            }
          }
        }
      )
    }
  return resolvedCall
    .valueArgumentExpressions(data.context)
    .fold(cont { CallArgumentsInfo.init(data) }, ::acc)
}

private fun SolverState.checkNullExpression(
  associatedVarName: ObjectFormula,
  data: CheckData
): ContSeq<StateAfter> =
  data.noReturn {
    addConstraint(
      NamedConstraint("$associatedVarName is null (== null)", solver.isNull(associatedVarName)),
      data.context
    )
  }

/**
 * This function produces a constraint that makes the desired variable name equal to the value
 * encoded in the constant and adds it to the [SolverState.prover] constraints.
 */
private fun SolverState.checkConstantExpression(
  associatedVarName: ObjectFormula,
  expression: ConstantExpression,
  data: CheckData
): ContSeq<StateAfter> =
  data.noReturn {
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
          solver.ints { equal(solver.intValue(associatedVarName), makeNumber(it)) }
        }
      PrimitiveType.RATIONAL ->
        expression.text.asFloatingLiteral()?.let {
          solver.rationals { equal(solver.decimalValue(associatedVarName), makeNumber(it)) }
        }
      PrimitiveType.STRING -> {
        // record the length of the string
        StringEscapeUtils.unescapeJava(expression.text.trim('"'))?.let { stringLiteral ->
          type
            .descriptor
            ?.unsubstitutedMemberScope
            ?.getContributedDescriptors { it == "length" }
            ?.singleOrNull { it.name.value == "length" && it.isField() }
            ?.let { lengthDecl ->
              solver.ints {
                equal(
                  solver.intValue(field(lengthDecl, associatedVarName)),
                  makeNumber(stringLiteral.length.toLong())
                )
              }
            }
        }
      }
      else -> null
    }?.let {
      addConstraint(
        NamedConstraint(
          "${expression.text} checkConstantExpression $associatedVarName ${expression.text}",
          it
        ),
        data.context
      )
      addConstraint(
        NamedConstraint("${expression.text} is not null", solver.isNotNull(associatedVarName)),
        data.context
      )
    }
  }

private fun SolverState.checkAssignmentExpression(
  expression: AssignmentExpression,
  data: CheckData
): ContSeq<StateAfter> {

  // in cases in which we don't do anything special
  // we simply look at the rhs to check for problems
  // there, but without assigning it at all
  fun onlyRhs(): ContSeq<StateAfter> = checkExpressionConstraints("rhs", expression.right, data)

  // early termination if we cannot resolve the left side
  val left = expression.left ?: return onlyRhs()
  val leftCall = left.getResolvedCall(data.context)
  val leftReceiver = leftCall?.getReceiverExpression()
  return when (val leftDescr = leftCall?.resultingDescriptor) {
    is LocalVariableDescriptor -> {
      when {
        leftDescr.isVar -> { // assignment to mutable variable
          // we introduce a new name because we don't want to introduce
          // any additional information about the variable,
          // we should only have that declared in the invariant
          val refName = leftDescr.name.toString()
          val newName = newName(data.context, refName, left)
          val invariant = data.varInfo.get(refName)?.invariant
          checkBodyAgainstInvariants(expression, newName, invariant, expression.right, data).map {
            it.second
          } // and then we forget about the new name
        }
        // no more cases, since by the Kotlin and Java rules
        // 'val's / 'final var's must be initialized at declaration time
        else -> null
      }
    }
    is PropertyDescriptor -> {
      when {
        // nothing for variables from properties or fields
        leftDescr.isVar -> null
        // the interesting case is when we are initializing
        // a variable in a constructor
        leftDescr.dispatchReceiverParameter != null &&
          leftDescr.isField() &&
          (leftReceiver == null || leftReceiver is ThisExpression) -> {
          // here we are
          data.varInfo.get(THIS_VAR_NAME)?.let { info ->
            checkExpressionConstraints(
              field(leftDescr, solver.makeObjectVariable(info.smtName)),
              expression.right,
              data
            )
          }
        }
        else -> null
      }
    }
    else -> null
  }
    ?: onlyRhs()
}

/** Check special binary cases, and make the other fall-through */
private fun SolverState.checkBinaryExpression(
  associatedVarName: ObjectFormula,
  expression: BinaryExpression,
  data: CheckData
): ContSeq<StateAfter> {
  val operator = expression.operationTokenRpr
  val left = expression.left
  val right = expression.right
  return when {
    // this is x == null, or x != null
    (operator == "EQEQ" || operator == "EXCLEQ") && right is NullExpression -> {
      val newName = solver.makeObjectVariable(newName(data.context, "checkNull", left))
      checkExpressionConstraints(newName, left, data).checkReturnInfo { stateAfter ->
        cont {
          when (operator) {
            "EQEQ" -> solver.isNull(newName)
            "EXCLEQ" -> solver.isNotNull(newName)
            else -> null
          }?.let {
            val cstr = solver.booleans { equivalence(solver.boolValue(associatedVarName), it) }
            addConstraint(
              NamedConstraint("$associatedVarName is null?", cstr),
              stateAfter.data.context
            )
          }
          stateAfter
        }
      }
    }
    else -> fallThrough(associatedVarName, expression, data)
  }
}

/**
 * Checks (x is Something) expressions We do not track types, so in theory this should not affect
 * our analysis However, in the specific case in which 'Something' is not nullable we can also
 * ensure that 'x' is not null This is different from 'x != null' in that we do not generate
 * associatedVarName <=> not (null x) But rather associatedVarName ==> not (null x)
 */
private fun SolverState.checkIsExpression(
  associatedVarName: ObjectFormula,
  isNegated: Boolean,
  typeReference: TypeReference?,
  subjectName: ObjectFormula,
  data: CheckData
) {
  if (!isNegated) {
    val invariants =
      (data.context.type(typeReference)?.let { typeInvariants(it, subjectName, data.context) })
      // in the worst case, we know that it is not null
      ?: listOf(NamedConstraint("$associatedVarName is not null", solver.isNotNull(subjectName)))
    invariants.forEach { cstr ->
      val constraint =
        NamedConstraint(
          "$associatedVarName => ${cstr.msg}",
          solver.booleanFormulaManager.implication(
            solver.boolValue(associatedVarName),
            cstr.formula
          )
        )
      addConstraint(constraint, data.context)
    }
  }
}

/** Checks the body of a lambda expression, but does nothing in particular with it */
internal fun SolverState.checkLambda(expr: LambdaExpression, data: CheckData): ContSeq<StateAfter> {
  val itParam =
    expr.type(data.context)?.takeIf { expr.valueParameters.isEmpty() }?.singleFunctionArgument()
  return checkFunctionBody(
    expr,
    null,
    expr.valueParameters,
    itParam,
    expr.functionLiteral.typeReference,
    expr.bodyExpression,
    data
  )
}

/** Checks whether we have a type (x: A) -> B, and returns the A */
internal fun Type.singleFunctionArgument() =
  this.takeIf { it.descriptor?.fqNameSafe == FqName("kotlin.Function1") }
    ?.arguments
    ?.getOrNull(0)
    ?.type

/** Checks the body of a local function, but does nothing in particular with it */
private fun SolverState.checkFunctionDeclarationExpression(
  declaration: Function,
  data: CheckData
): ContSeq<StateAfter> =
  checkFunctionBody(
    declaration,
    declaration.receiverTypeReference,
    declaration.valueParameters,
    null,
    declaration.typeReference,
    declaration.stableBody(),
    data
  )

/** Shared code between lambda expressions and local function declarations */
internal fun SolverState.checkFunctionBody(
  wholeExpr: Expression,
  receiverType: TypeReference?,
  valueParameters: List<Parameter>,
  itType: Type?,
  resultType: TypeReference?,
  body: Expression?,
  data: CheckData
): ContSeq<StateAfter> =
  // We need to introduce new arguments
  // and a new return point
  inScope(data) {
    scopedBracket {
      // add information about parameters
      val thisParam =
        receiverType?.let {
          val thisSmtName = newName(data.context, THIS_VAR_NAME, wholeExpr)
          ParamInfo(THIS_VAR_NAME, thisSmtName, data.context.type(it), wholeExpr)
        }
      val itParam =
        itType?.let {
          val smtName = newName(data.context, "it", wholeExpr)
          ParamInfo("it", smtName, itType, wholeExpr)
        }
      val valueParams =
        valueParameters.mapNotNull { param ->
          param.name?.let { name ->
            val smtName = newName(data.context, name, param)
            ParamInfo(name, smtName, param.type(data.context), param)
          }
        } + listOfNotNull(itParam)
      val resultSmtName = newName(data.context, RESULT_VAR_NAME, wholeExpr)
      val resultParam =
        resultType?.let {
          ParamInfo(RESULT_VAR_NAME, resultSmtName, data.context.type(it), wholeExpr)
        }
      val newParams = initialParameters(thisParam, valueParams, resultParam, data.context)
      val newData =
        data
          .addVarInfos(newParams) // add new names from arguments
          .replaceTopMostReturnPoint(
            null,
            solver.makeObjectVariable(resultSmtName)
          ) // add the new return point
      data.noReturn {
        // and now go and check the body
        checkExpressionConstraints(resultSmtName, body, newData)
          .drain() // execute until the end, so any bracket is closed
      }
    }
  }

/**
 * This function produces a continuation that makes the desired variable name equal to the value
 * encoded in the named expression.
 */
private fun SolverState.checkNonFunctionDeclarationExpression(
  declaration: Declaration,
  data: CheckData
): ContSeq<Pair<String?, StateAfter>> =
  doOnlyWhenNotNull(declaration.stableBody(), Pair(null, data.noReturn())) { body ->
    val declName =
      when (declaration) {
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
        val inconsistentInvariant =
          checkInvariantConsistency(
            NamedConstraint("invariant in $declName", renamed),
            data.context,
            invBody,
            data.branch.get()
          )
        ensure(!inconsistentInvariant)
      }
    }
      .flatMap {
        // this gives back a new temporary name for the body
        checkBodyAgainstInvariants(declaration, declName, invariant?.second, body, data)
      }
      .map { (newVarName, stateAfter) ->
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
              ),
              data.context
            )
          }
        }
        // update the list of variables in scope
        val newData = stateAfter.data.addVarInfo(declName, smtName, declaration, invariant?.second)
        // and then keep going
        Pair(newVarName, stateAfter.withData(newData))
      }
  }

/** Checks the possible invariants of a declaration, and its body. */
private fun SolverState.checkBodyAgainstInvariants(
  element: Element,
  declName: String,
  invariant: BooleanFormula?,
  body: Expression?,
  data: CheckData
): ContSeq<Pair<String, StateAfter>> {
  val newName = newName(data.context, declName, body)
  return checkExpressionConstraints(newName, body, data)
    .onEach {
      whenNotTrusted(body, data) {
        invariant?.let {
          val renamed = solver.renameObjectVariables(it, mapOf(RESULT_VAR_NAME to newName))
          checkInvariant(
            NamedConstraint("assignment to `${element.text}`", renamed),
            data.context,
            element,
            data.branch.get()
          )
        }
      }
    }
    .map { r -> Pair(newName, r) }
}

private fun SolverState.obtainInvariant(
  expression: Expression,
  data: CheckData
): Pair<Expression, BooleanFormula>? =
  expression
    .getResolvedCall(data.context)
    ?.takeIf { it.specialKind == SpecialKind.Invariant }
    ?.arg("predicate", data.context)
    ?.let { expr: Expression ->
      topLevelExpressionToFormula(expr, data.context, emptyList(), true)?.let { formula ->
        expr to formula
      }
    }

/**
 * This function produces a continuation that makes the desired variable name equal to the value
 * encoded in the named expression and adds the resulting boolean formula to the
 * [SolverState.prover] constraints.
 */
private fun SolverState.checkNameExpression(
  associatedVarName: ObjectFormula,
  referencedName: String,
  data: CheckData
): ContSeq<StateAfter> =
  data.noReturn {
    // use the SMT name recorded in the variable info
    data.varInfo.get(referencedName)?.let {
      val constraint =
        solver.objects { equal(associatedVarName, solver.makeObjectVariable(it.smtName)) }
      addConstraint(
        NamedConstraint("$associatedVarName = ${it.smtName} (name)", constraint),
        data.context
      )
    }
  }

private fun Expression.computeConditions(): List<Condition> =
  when (this) {
    is IfExpression ->
      listOf(
        SimpleCondition(condition!!, false, thenExpression!!, thenExpression!!),
        SimpleCondition(null, true, elseExpression!!, elseExpression!!)
      )
    is WhenExpression -> {
      val subject = subjectExpression
      entries.flatMap { entry ->
        if (entry.conditions.isEmpty()) {
          listOf(SimpleCondition(null, entry.isElse, entry.expression!!, entry))
        } else {
          entry.conditions.toList().mapNotNull { cond ->
            when {
              subject != null -> SubjectCondition(cond, entry.isElse, entry.expression!!, entry)
              cond is WhenConditionWithExpression ->
                SimpleCondition(cond.expression!!, entry.isElse, entry.expression!!, entry)
              else -> null
            }
          }
        }
      }
    }
    else -> emptyList()
  }

/** Check `if` and `when` expressions. */
private fun SolverState.checkConditional(
  associatedVarName: ObjectFormula,
  subject: Expression?,
  branches: List<Condition>,
  data: CheckData
): ContSeq<StateAfter> {
  val newSubjectVar = solver.makeObjectVariable(newName(data.context, "subject", subject))
  // this handles the cases of when with a subject, and with 'val x = subject'
  return when (subject) {
      is Declaration ->
        checkNonFunctionDeclarationExpression(subject, data).map { (actualSubjectVar, stateAfter) ->
          Pair(
            actualSubjectVar?.let { solver.makeObjectVariable(it) } ?: newSubjectVar,
            stateAfter.data
          )
        }
      else ->
        checkExpressionConstraints(newSubjectVar, subject, data).map { stateAfter ->
          Pair(newSubjectVar, stateAfter.data)
        }
    }
    .flatMap { (subjectVar, newData) ->
      branches
        .map { cond ->
          val conditionVar = newName(data.context, "cond", cond.condition)
          // introduce the condition
          (cond.condition?.let {
              introduceCondition(solver.makeObjectVariable(conditionVar), subjectVar, cond, newData)
            }
              ?: newData.noReturn {
                // if we have no condition, it's equivalent to true
                addConstraint(
                  NamedConstraint(
                    "check condition branch $conditionVar",
                    solver.makeBooleanObjectVariable(conditionVar)
                  ),
                  newData.context
                )
              })
            .map { returnInfo -> Pair(Pair(returnInfo, cond), conditionVar) }
        }
        .nested()
    }
    .flatMap { conditionInformation ->
      yesNo(conditionInformation).asContSeq().flatMap { (returnAndCond, correspondingVars) ->
        val (stateAfter, cond) = returnAndCond
        when (stateAfter.returnInfo) {
          is ExplicitReturn -> // weird case: a return in a condition
          cont { stateAfter }
          else ->
            continuationBracket
              .map {
                // assert the variables and check that we are consistent
                val inconsistentEnvironment =
                  checkConditionsInconsistencies(
                    correspondingVars,
                    data.context,
                    cond.whole,
                    data.branch.get()
                  )
                // it only makes sense to continue if we are not consistent
                ensure(!inconsistentEnvironment)
              }
              .flatMap {
                val newData = stateAfter.data.addBranch(correspondingVars.map { it.formula })
                // check the body
                checkExpressionConstraints(associatedVarName, cond.body, newData)
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
): ContSeq<StateAfter> =
  when (cond) {
    is SimpleCondition -> checkExpressionConstraints(conditionVar, cond.predicate, data)
    is SubjectCondition ->
      when (val check = cond.check) {
        is WhenConditionWithExpression ->
          if (check.expression is NullExpression) {
            data.noReturn {
              val complete =
                solver.booleans {
                  equivalence(solver.boolValue(conditionVar), solver.isNull(subjectVar))
                }
              addConstraint(
                NamedConstraint("$subjectVar is null (condition)", complete),
                data.context
              )
            }
          } else {
            val patternName = newName(data.context, "pattern", check.expression)
            checkExpressionConstraints(patternName, check.expression, data).map {
              when (check.expression?.type(data.context)?.primitiveType()) {
                PrimitiveType.BOOLEAN ->
                  solver.booleans {
                    equivalence(
                      solver.boolValue(subjectVar),
                      solver.makeBooleanObjectVariable(patternName)
                    )
                  }
                PrimitiveType.INTEGRAL ->
                  solver.ints {
                    equal(
                      solver.intValue(subjectVar),
                      solver.makeIntegerObjectVariable(patternName)
                    )
                  }
                PrimitiveType.RATIONAL ->
                  solver.rationals {
                    equal(
                      solver.decimalValue(subjectVar),
                      solver.makeDecimalObjectVariable(patternName)
                    )
                  }
                else -> null
              }?.let {
                val complete = solver.booleans { equivalence(solver.boolValue(conditionVar), it) }
                addConstraint(
                  NamedConstraint("$subjectVar equals $patternName (condition)", complete),
                  data.context
                )
              }
              data.noReturn()
            }
          }
        is WhenConditionIsPattern ->
          data.noReturn {
            checkIsExpression(conditionVar, check.isNegated, check.typeReference, subjectVar, data)
          }
        else -> data.noReturn {}
      }
  }

/**
 * Given a list of names for condition variables, create the boolean conditions for each branch.
 *
 * For example, given [a, b, c], it generates:
 * - a
 * - not a, b
 * - not a, not b, c
 */
private fun <A> SolverState.yesNo(
  conditionVars: List<Pair<A, String>>
): List<Pair<A, List<NamedConstraint>>> {
  fun go(
    currents: List<Pair<A, String>>,
    acc: List<NamedConstraint>
  ): List<Pair<A, List<NamedConstraint>>> =
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
): ContSeq<StateAfter> =
  when (expression) {
    is ForExpression -> checkForExpression(expression.loopParameter, expression.body, data)
    is ThreePieceForExpression ->
      inScope(data) {
        val initVar = solver.makeObjectVariable(newName(data.context, "initializer", expression))
        checkBlockExpression(initVar, expression.initializer, false, data).flatMap { after ->
          doOnlyWhenNotNull(expression.condition, data.noReturn()) {
            checkWhileExpression(it, expression.body, emptyList(), after.data)
          }
        }
      }
    is WhileExpression ->
      doOnlyWhenNotNull(expression.condition, data.noReturn()) {
        checkWhileExpression(it, expression.body, emptyList(), data)
      }
    is DoWhileExpression -> {
      // remember that do { t } while (condition)
      // is equivalent to { t }; while (condition) { t }
      checkExpressionConstraintsWithNewName("firstIter", expression.body, data).flatMap {
        doOnlyWhenNotNull(expression.condition, data.noReturn()) {
          // do not change the data, since the block goes out of scope
          checkWhileExpression(it, expression.body, emptyList(), data)
        }
      }
    }
    else -> data.noReturn {} // this should not happen
  }

private fun SolverState.checkForExpression(
  loopParameter: Parameter?,
  body: Expression?,
  data: CheckData
): ContSeq<StateAfter> =
  ContSeq {
    yield(LoopPlace.INSIDE_LOOP)
    yield(LoopPlace.AFTER_LOOP)
  }
    .flatMap {
      when (it) {
        LoopPlace.INSIDE_LOOP ->
          inScope(data) {
            continuationBracket.flatMap {
              val paramName = loopParameter?.nameAsName?.value
              val newData =
                if (loopParameter != null && paramName != null) {
                  val smtName = newName(data.context, paramName, loopParameter)
                  data.addVarInfo(paramName, smtName, loopParameter, null)
                } else data
              checkLoopBody(body, emptyList(), newData)
            }
          }
        // in this case we know nothing
        // after the loop finishes
        LoopPlace.AFTER_LOOP -> data.noReturn {}
      }
    }

private fun SolverState.checkWhileExpression(
  condition: Expression,
  body: Expression?,
  afterBody: List<Expression>,
  data: CheckData
): ContSeq<StateAfter> {
  val condName = newName(data.context, "cond", condition)
  return checkExpressionConstraints(condName, body, data)
    .flatMap {
      ContSeq {
        yield(LoopPlace.INSIDE_LOOP)
        yield(LoopPlace.AFTER_LOOP)
      }
    }
    .flatMap {
      val objVar = solver.makeBooleanObjectVariable(condName)
      when (it) {
        LoopPlace.INSIDE_LOOP ->
          inScope(data) {
            continuationBracket.flatMap {
              // inside the loop the condition is true
              checkConditionsInconsistencies(
                listOf(NamedConstraint("inside the loop, condition is true", objVar)),
                data.context,
                condition,
                data.branch.get()
              )
              checkLoopBody(body, afterBody, data.addBranch(objVar))
            }
          }
        // after the loop the condition is false
        LoopPlace.AFTER_LOOP ->
          cont {
            val notVar = solver.booleanFormulaManager.not(objVar)
            checkConditionsInconsistencies(
              listOf(NamedConstraint("loop is finished, condition is false", notVar)),
              data.context,
              condition,
              data.branch.get()
            )
            // add (not condition) to the data
            data.addBranch(notVar).noReturn()
          }
      }
    }
}

private fun SolverState.checkLoopBody(
  body: Expression?,
  afterBody: List<Expression>,
  data: CheckData
): ContSeq<StateAfter> {
  return checkExpressionConstraintsWithNewName("loop", body, data).flatMap { stateAfter ->
    // check the additional updates (used when describing "three-piece" for loops)
    val afterBodyVar = solver.makeObjectVariable(newName(data.context, "afterBody", body))
    checkBlockExpression(afterBodyVar, afterBody, false, stateAfter.data).map {
      // only keep working on this branch
      // if we had a 'return' inside
      // otherwise the other branch is enough
      // if we decide to abort we need to 'pop',
      // because the one from 'bracket' won't run
      when (stateAfter.returnInfo) {
        is ExplicitBlockReturn -> stateAfter
        is ExplicitReturn -> abort()
        is NoReturn -> abort()
      }
    }
  }
}

/**
 * Check try/catch/finally blocks This is a very rough check, in which we assume the worst-case
 * scenario:
 * - when you get to a 'catch' you have *no* information about the 'try' at all
 * - all the 'catch' blocks may potentially execute
 */
private fun SolverState.checkTryExpression(
  associatedVarName: ObjectFormula,
  expression: TryExpression,
  data: CheckData
): ContSeq<StateAfter> =
  inScope(data) {
    ContSeq {
      yield(expression.tryBlock)
      yieldAll(expression.catchClauses)
    }
      .flatMap { r -> continuationBracket.map { r } }
      .flatMap {
        when (it) {
          is BlockExpression -> // the try
          checkExpressionConstraints(associatedVarName, it, data).flatMap { stateAfter ->
              when (stateAfter.returnInfo) {
                // if we had a throw, this will eventually end in a catch
                is ExplicitThrowReturn ->
                  // is the thrown exception something in our own catch?
                  if (doesAnyCatchMatch(
                      stateAfter.returnInfo.exceptionType,
                      expression.catchClauses,
                      data
                    )
                  )
                    ContSeq { abort() } // then there's no point in keep looking here
                  else cont { stateAfter } // otherwise, bubble up the exception
                else -> cont { stateAfter }
              }
            }
          is CatchClause -> { // the catch
            doOnlyWhenNotNull(it.catchParameter, data.noReturn()) { param ->
              doOnlyWhenNotNull(param.nameAsName?.value, data.noReturn()) { paramName ->
                // introduce the name of the parameter
                val smtName = newName(data.context, paramName, param)
                // and then go on and check the body
                checkExpressionConstraints(
                  associatedVarName,
                  it.catchBody,
                  data.addVarInfo(paramName, smtName, param)
                )
              }
            }
          }
          else -> ContSeq { abort() }
        }
      }
      .onEach { stateAfterTryOrCatch ->
        // override the return of the finally with the return of the try or catch
        doOnlyWhenNotNull(expression.finallyBlock, data) { finally ->
          checkExpressionConstraintsWithNewName("finally", finally.finalExpression, data).map {
            stateAfterTryOrCatch
          }
        }
      }
  }

/**
 * Checks whether the type obtain from an explicit 'throw' matches any of the types in the 'catch'
 * clauses
 */
internal fun doesAnyCatchMatch(
  throwType: Type?,
  clauses: List<CatchClause>,
  data: CheckData
): Boolean =
  clauses.any { clause ->
    val catchType = clause.catchParameter?.type(data.context)
    if (throwType != null && catchType != null) {
      throwType.isSubtypeOf(catchType)
    } else {
      false
    }
  }

/** Find the corresponding "body" of a declaration */
internal fun Declaration.stableBody(): Expression? =
  when (this) {
    is VariableDeclaration -> initializer
    is DeclarationWithBody -> bodyExpression ?: bodyBlockExpression
    is DeclarationWithInitializer -> initializer
    else -> null
  }

private fun <A> ContSeq<StateAfter>.checkReturnInfo(
  r: (r: ExplicitReturn, s: StateAfter) -> A,
  f: (StateAfter) -> ContSeq<A>
): ContSeq<A> =
  this.flatMap { stateAfter ->
    when (stateAfter.returnInfo) {
      is ExplicitReturn -> cont { r(stateAfter.returnInfo, stateAfter) }
      else -> f(stateAfter)
    }
  }

private fun ContSeq<StateAfter>.checkReturnInfo(
  f: (StateAfter) -> ContSeq<StateAfter>
): ContSeq<StateAfter> = this.checkReturnInfo({ _, stateAfter -> stateAfter }, f)

private fun inScope(data: CheckData, f: () -> ContSeq<StateAfter>): ContSeq<StateAfter> =
  f().map { it.withData(data) }

private fun whenNotTrusted(expression: Expression?, data: CheckData, toDo: () -> Unit) {
  if (expression == null || !expression.inTrustedEnvironment(data.context)) {
    toDo()
  }
}
