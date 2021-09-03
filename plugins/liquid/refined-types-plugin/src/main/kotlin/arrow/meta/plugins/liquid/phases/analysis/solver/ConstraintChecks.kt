package arrow.meta.plugins.liquid.phases.analysis.solver

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
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.body
import arrow.meta.plugins.liquid.smt.renameDeclarationConstraints
import org.jetbrains.kotlin.backend.common.descriptors.allParameters
import arrow.meta.plugins.liquid.smt.renameObjectVariables
import org.jetbrains.kotlin.codegen.kotlinType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtDeclarationWithInitializer
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLabeledExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamed
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import org.jetbrains.kotlin.psi.KtWhenConditionWithExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.calls.callUtil.getReceiverExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.sosy_lab.java_smt.api.BooleanFormula

// PHASE 2: CHECKING OF CONSTRAINTS
// ================================

/* [NOTE: which do we use continuations?]
 * It might look odd that we create continuations when checking
 * the body, instead of simply performing the steps.
 *
 * The reason to do so is to have the ability to decide whether
 * and how to execute the "remainder of the analysis". For example:
 * - if we find a `return`, we ought to stop, since no further
 *   statement is executed;
 * - if we are in a condition, the "remainder of the analysis"
 *   ought to be executed more than once; in fact once per possible
 *   execution flow.
 *
 * What makes the problem complicated, and brings in continuations,
 * is that these decisions may have to be made within an argument:
 *
 * ```
 * f(if (x > 0) 3 else 2)
 * ```
 *
 * yet they must affect the global ongoing computations. Continuations
 * allow us to do so by saying that checking `if (x > 0) 3 else 2`
 * is the current computation, and checking `f(...)` the "remainder".
 * Thus, the conditional `if` can duplicate the check of the "remainder".
 */

internal const val RESULT_VAR_NAME = "${'$'}result"

// 2.0: entry point
/**
 * When the solver is in the prover state
 * check this [declaration] body constraints
 */
internal fun CompilerContext.checkDeclarationConstraints(
  context: DeclarationCheckerContext,
  declaration: KtDeclaration,
  descriptor: DeclarationDescriptor
) {
  val solverState = get<SolverState>(SolverState.key(context.moduleDescriptor))
  if (solverState != null &&
    solverState.isIn(SolverState.Stage.Prove) &&
    !solverState.hadParseErrors() &&
    declaration.shouldBeAnalyzed()
  ) {
    // bring the constraints in (if there are any)
    val constraints = solverState.constraintsFromSolverState(descriptor)
    // choose a good name for the result
    // should we change it for 'val' declarations?
    val resultVarName = RESULT_VAR_NAME
    // trace
    solverState.solverTrace.add("CHECKING ${descriptor.fqNameSafe.asString()}")
    // now go on and check the body
    solverState.checkTopLevelDeclaration(
      constraints, context, descriptor,
      resultVarName, declaration
    ).drain()
    // trace
    solverState.solverTrace.add("FINISH ${descriptor.fqNameSafe.asString()}")
  }
}

/**
 * Only elements which are not inside another "callable declaration"
 * (function, property, etc) should be analyzed
 */
fun KtDeclaration.shouldBeAnalyzed() =
  !(this.parents.any { it is KtCallableDeclaration })

// 2.0: data for the checks
// ------------------------

data class CheckData(
  val context: DeclarationCheckerContext,
  val returnPoints: ReturnPoints,
  val varInfo: CurrentVarInfo
) {
  fun addReturnPoint(scope: String, variableName: String) =
    CheckData(context, returnPoints.addAndReplaceTopMost(scope, variableName), varInfo)
}

/**
 * Maps return points to the SMT variables representing that place.
 */
data class ReturnPoints(
  val topMostReturnPointVariableName: Pair<String?, String>,
  val namedReturnPointVariableNames: Map<String, String>
) {

  fun addAndReplaceTopMost(newScopeName: String, newVariableName: String) =
    this
      .replaceTopMost(newScopeName, newVariableName)
      .add(newScopeName, newVariableName)

  private fun replaceTopMost(newScopeName: String, newVariableName: String) =
    ReturnPoints(Pair(newScopeName, newVariableName), namedReturnPointVariableNames)

  private fun add(returnPoint: String, variableName: String) =
    ReturnPoints(
      topMostReturnPointVariableName,
      namedReturnPointVariableNames + (returnPoint to variableName)
    )

  companion object {
    private fun new(scope: String?, variableName: String): ReturnPoints =
      when (scope) {
        null -> ReturnPoints(Pair(scope, variableName), emptyMap())
        else -> ReturnPoints(Pair(scope, variableName), mapOf(scope to variableName))
      }

    fun new(scope: KtElement, variableName: String): ReturnPoints =
      when (scope) {
        is KtNamed -> new(scope.nameAsName!!.asString(), variableName)
        else -> new(null, variableName)
      }
  }
}

data class CurrentVarInfo(val varInfo: MutableList<VarInfo>) {

  fun get(name: String): VarInfo? =
    varInfo.firstOrNull { it.name == name }

  fun get(name: FqName): VarInfo? =
    this.get(name.asString())

  fun add(name: String, smtName: String, origin: KtElement, invariant: BooleanFormula?) {
    varInfo.add(0, VarInfo(name, smtName, origin, invariant))
  }

  fun bracket(): ContSeq<Unit> = ContSeq {
    val currentVarInfo = varInfo.toList()
    yield(Unit)
    varInfo.clear()
    varInfo.addAll(currentVarInfo)
  }
}

/**
 * For each variable, we keep two pieces of data:
 * - the name it was declared with
 * - the element it came from
 * - invariants which may have been declared
 */
data class VarInfo(
  val name: String,
  val smtName: String,
  val origin: KtElement,
  val invariant: BooleanFormula? = null
)

/**
 * Ways to return from a block.
 */
sealed class Return
object NoReturn : Return()
data class ExplicitReturn(val returnPoint: String?) : Return()

// 2.1: declarations
// -----------------
/**
 * When the solver is in the prover state
 * check this [declaration] body and constraints for
 * - pre-condition inconsistencies,
 * - whether the body satisfy all the pre-conditions in calls,
 * - whether the post-condition really holds.
 */
private fun SolverState.checkTopLevelDeclaration(
  constraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  descriptor: DeclarationDescriptor,
  resultVarName: String,
  declaration: KtDeclaration
): ContSeq<Return> {
  val maybeBody = declaration.stableBody()
  return doOnlyWhenNotNull(maybeBody, NoReturn) { body ->
    continuationBracket.map {
      // introduce non-nullability of parameters
      if (descriptor is CallableDescriptor) {
        descriptor.allParameters.forEach { param ->
          if (!param.type.isMarkedNullable) {
            val paramName = param.name.asString()
            val notNullFormula = solver.isNotNull(solver.makeObjectVariable(paramName))
            addConstraint(NamedConstraint("$paramName is not null", notNullFormula))
          }
        }
      }
      val inconsistentPreconditions =
        checkPreconditionsInconsistencies(constraints, context, declaration)
      ensure(!inconsistentPreconditions)
    }.flatMap {
      // only check body when we are not in a @Law
      doOnlyWhen(!descriptor.hasLawAnnotation(), NoReturn) {
        val data = CheckData(context, ReturnPoints.new(declaration, resultVarName), initializeVarInfo(declaration))
        checkExpressionConstraints(resultVarName, body, data).onEach {
          checkPostConditionsImplication(constraints, context, declaration)
        }
      }
    }
  }
}

/**
 * Initialize the names of the variables,
 * the SMT name is initialized to themselves.
 */
fun initializeVarInfo(declaration: KtDeclaration): CurrentVarInfo {
  val initialVarInfo = mutableListOf<VarInfo>()
  initialVarInfo.add(VarInfo("this", "this", declaration))
  if (declaration is KtNamedDeclaration) {
    // Add 'this@functionName'
    declaration.name?.let { name ->
      initialVarInfo.add(VarInfo("this@$name", "this", declaration))
    }
  }
  if (declaration is KtDeclarationWithBody) {
    declaration.valueParameters.forEach { param ->
      param.name?.let { name ->
        initialVarInfo.add(VarInfo(name, name, param))
      }
    }
  }
  return CurrentVarInfo(initialVarInfo)
}

// 2.2: expressions
// ----------------

/**
 * Produces a continuation that when invoked
 * recursively checks an [expression] set of constraints
 */
private fun SolverState.checkExpressionConstraints(
  associatedVarName: String,
  expression: KtExpression?,
  data: CheckData
): ContSeq<Return> =
  when (expression) {
    is KtParenthesizedExpression ->
      checkExpressionConstraints(associatedVarName, expression.expression, data)
    is KtBlockExpression ->
      data.varInfo.bracket().flatMap { // new variables are local to that block
        checkBlockExpression(associatedVarName, expression.statements, data)
      }
    is KtReturnExpression ->
      checkReturnConstraints(expression, data)
    is KtConstantExpression ->
      checkConstantExpression(associatedVarName, expression)
    is KtThisExpression ->
      // both 'this' and 'this@name' are available in the variable info
      checkNameExpression(associatedVarName, expression.text, data)
    is KtSimpleNameExpression ->
      checkNameExpression(associatedVarName, expression.getReferencedName(), data)
    is KtLabeledExpression ->
      checkLabeledExpression(associatedVarName, expression, data)
    is KtDeclaration ->
      checkDeclarationExpression(expression, data)
    is KtIfExpression ->
      checkSimpleConditional(associatedVarName, expression.computeSimpleConditions(), data)
    is KtWhenExpression ->
      if (expression.subjectExpression != null) {
        cont { NoReturn } // TODO: handle `when` with subject
      } else {
        checkSimpleConditional(associatedVarName, expression.computeSimpleConditions(), data)
      }
    is KtBinaryExpression ->
      checkBinaryExpression(associatedVarName, expression, data)
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
      val inventedName = names.newName("stmt", first)
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
    if (r is ExplicitReturn && r.returnPoint == labelName) {
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
    .map { ExplicitReturn(label) }
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
    fqName == FqName("arrow.refinement.pre") -> // ignore calls to 'pre'
      cont { NoReturn }
    fqName == FqName("arrow.refinement.post") -> // ignore post arguments
      checkExpressionConstraints(associatedVarName, resolvedCall.getReceiverExpression(), data)
    fqName == FqName("arrow.refinement.invariant") -> // ignore invariant arguments
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
      val receiverName = names.newName("this", receiverExpr)
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

/**
 * Describes the characteristics of a call to special control flow functions
 * namely [also], [apply], [let], and [run]
 */
data class ControlFlowFn(
  val target: KtExpression,
  val body: KtExpression,
  val argumentName: String,
  val returnBehavior: ReturnBehavior
) {
  /**
   * Describes whether functions return their argument
   * or whatever is done in a block
   */
  enum class ReturnBehavior {
    /**
     * Return what was given as argument, usually after applying a function T -> Unit
     */
    RETURNS_ARGUMENT,

    /**
     * Return whatever the enclosing block returns
     */
    RETURNS_BLOCK_RESULT
  }
}

/**
 * Special treatment for special control flow functions ([also], [apply], [let], [run])
 */
private fun controlFlowAnyFunction(
  resolvedCall: ResolvedCall<out CallableDescriptor>
): ControlFlowFn? {
  val thisElement = resolvedCall.arg("this")
  val blockElement = resolvedCall.arg("block") as? KtLambdaExpression
  val bodyElement = blockElement?.bodyExpression
  return if (thisElement != null && blockElement != null && bodyElement != null) {
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
      else -> null
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
  val thisName = when (info.returnBehavior) {
    ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT -> associatedVarName
    ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT -> names.newName("this", info.target)
  }
  val returnName = when (info.returnBehavior) {
    ControlFlowFn.ReturnBehavior.RETURNS_ARGUMENT -> names.newName("ret", info.target)
    ControlFlowFn.ReturnBehavior.RETURNS_BLOCK_RESULT -> associatedVarName
  }
  return checkReceiverWithPossibleSafeDot(associatedVarName, wholeExpr, thisName, info.target, data) {
    data.varInfo.bracket().flatMap {
      // add the name to the context,
      // being careful not overriding any existing name
      val smtName = when (data.varInfo.get(info.argumentName)) {
        null -> info.argumentName
        else -> names.newName(info.argumentName, info.target)
      }
      data.varInfo.add(info.argumentName, smtName, info.target, null)
      // add the constraint to make the parameter equal
      val formula = solver.objects { equal(solver.makeObjectVariable(smtName), solver.makeObjectVariable(thisName)) }
      addConstraint(NamedConstraint("introduce argument for lambda", formula))
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
  val receiverName = names.newName("this", receiverExpr)
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
              val argName = argVars[0].second
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
            addConstraint(NamedConstraint(
              "$associatedVarName is not null",
              solver.isNotNull(solver.makeObjectVariable(associatedVarName))))
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
  val leftName = names.newName("left", leftExpr)
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
          val argUniqueName = names.newName(name, expr)
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
  return when {
    // this is an assignment to a mutable variable
    operator == "EQ" && left is KtNameReferenceExpression -> {
      // we introduce a new name because we don't want to introduce
      // any additional information about the variable,
      // we should only have that declared in the invariant
      val newName = names.newName(left.getReferencedName(), left)
      val invariant = data.varInfo.get(left.getReferencedName())?.invariant
      checkBodyAgainstInvariants(expression, newName, invariant, expression.right, data)
        .map { it.second } // forget about the temporary name
    }
    // this is x == null, or x != null
    (operator == "EQEQ" || operator == "EXCLEQ") && right is KtConstantExpression && right.text == "null" -> {
      val newName = names.newName("checkNull", left)
      checkExpressionConstraints(newName, left, data).map {
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
    else -> fallThrough(associatedVarName, expression, data)
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
  val declName = when (declaration) {
    // use the given name if available
    is KtNamedDeclaration -> declaration.nameAsSafeName.asString()
    else -> names.newName("decl", declaration)
  }
  // if we are shadowing the name,
  // we need to create a new one
  val smtName = when (data.varInfo.get(declName)) {
    null -> declName
    else -> names.newName(declName, declaration)
  }
  // find out whether we have an invariant
  val body = declaration.stableBody()
  val invariant = obtainInvariant(body, data)
  // assert the invariant if found and check its consistency
  return doOnlyWhenNotNull(invariant, Unit) { (invBody, invFormula: BooleanFormula) ->
    ContSeq.unit.map {
      val renamed = solver.renameObjectVariables(invFormula, mapOf(RESULT_VAR_NAME to smtName))
      val inconsistentInvariant = checkInvariantConsistency(
        NamedConstraint("$declName $RESULT_VAR_NAME renamed $smtName", renamed),
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
  val newName = names.newName(declName, element)
  return checkExpressionConstraints(newName, body, data).onEach {
    invariant?.let {
      val renamed = solver.renameObjectVariables(it, mapOf(RESULT_VAR_NAME to newName))
      checkInvariant(
        NamedConstraint("checkBodyInvariants: $RESULT_VAR_NAME renamed $newName", renamed),
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

/**
 * Data type used to handle `if` and `when` without subject uniformly.
 */
data class Condition(val condition: KtExpression?, val body: KtExpression, val whole: KtElement)

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
    val conditionVar = names.newName("cond", cond.condition)
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

/**
 * Find the corresponding "body" of a declaration
 */
private fun KtDeclaration.stableBody(): KtExpression? = when (this) {
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
