package arrow.meta.plugins.analysis.phases.analysis.solver.collect

import arrow.meta.plugins.analysis.phases.analysis.solver.RESULT_VAR_NAME
import arrow.meta.plugins.analysis.phases.analysis.solver.SpecialKind
import arrow.meta.plugins.analysis.phases.analysis.solver.allArgumentExpressions
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ExpressionValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.LocalVariableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnnotatedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BinaryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ConstantExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ExpressionLambdaArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ExpressionResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.IfExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LambdaExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NameReferenceExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NullExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ParenthesizedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ThisExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenConditionWithExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorIds
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages
import arrow.meta.plugins.analysis.phases.analysis.solver.isField
import arrow.meta.plugins.analysis.phases.analysis.solver.primitiveFormula
import arrow.meta.plugins.analysis.phases.analysis.solver.resolvedArg
import arrow.meta.plugins.analysis.phases.analysis.solver.specialKind
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.smt.ObjectFormula
import arrow.meta.plugins.analysis.smt.Solver
import arrow.meta.plugins.analysis.smt.boolAnd
import arrow.meta.plugins.analysis.smt.boolAndList
import arrow.meta.plugins.analysis.smt.boolOr
import arrow.meta.plugins.analysis.smt.boolOrList
import arrow.meta.plugins.analysis.smt.isFieldCall
import arrow.meta.plugins.analysis.smt.isSingleVariable
import arrow.meta.plugins.analysis.types.PrimitiveType
import arrow.meta.plugins.analysis.types.asFloatingLiteral
import arrow.meta.plugins.analysis.types.asIntegerLiteral
import arrow.meta.plugins.analysis.types.primitiveType
import arrow.meta.plugins.analysis.types.unwrapIfNullable
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FormulaType

/**
 * Transform a [Expression] into a [Formula], wrapping with additional 'bool' fields when required
 *
 * This is the function to be used when turning an expression in a formula anywhere in the analysis
 */
internal fun SolverState.topLevelExpressionToFormula(
  ex: Expression?,
  context: ResolutionContext,
  parameters: List<Parameter>,
  allowAnyReference: Boolean
): BooleanFormula? =
  expressionToFormula(ex, context, parameters, allowAnyReference)?.let {
    when (it) {
      is BooleanFormula -> it
      is ObjectFormula -> solver.boolValue(it)
      else -> null
    }
  }

/** Transform a [Expression] into a [Formula] */
private fun SolverState.expressionToFormula(
  ex: Expression?,
  context: ResolutionContext,
  parameters: List<Parameter>,
  allowAnyReference: Boolean
): Formula? {
  val argCall = ex?.getResolvedCall(context)
  val recur = { v: Expression? -> expressionToFormula(v, context, parameters, allowAnyReference) }
  return when {
    // just recur
    ex is ParenthesizedExpression -> recur(ex.expression)
    ex is AnnotatedExpression -> recur(ex.baseExpression)
    ex is LambdaExpression -> recur(ex.bodyExpression)
    // basic blocks
    ex is BlockExpression ->
      ex.statements.mapNotNull { recur(it) as? BooleanFormula }.let { conditions ->
        solver.boolAndList(conditions)
      }
    ex is ConstantExpression -> ex.type(context)?.let { ty -> solver.makeConstant(ty, ex) }
    ex is ThisExpression -> // reference to this
    solver.makeObjectVariable("this")
    ex is NameReferenceExpression && ex.isResultReference(context) ->
      solver.makeObjectVariable(RESULT_VAR_NAME)
    ex is NameReferenceExpression && argCall?.resultingDescriptor is ParameterDescriptor ->
      if (allowAnyReference || parameters.any { it.nameAsName == ex.getReferencedNameAsName() }) {
        solver.makeObjectVariable(ex.getReferencedName())
      } else {
        val msg = ErrorMessages.Parsing.unexpectedReference(ex.getReferencedName())
        context.handleError(ErrorIds.Parsing.UnexpectedReference, ex, msg)
        null
      }
    ex is NameReferenceExpression && argCall?.resultingDescriptor is LocalVariableDescriptor ->
      if (allowAnyReference) {
        solver.makeObjectVariable(ex.getReferencedName())
      } else {
        val msg = ErrorMessages.Parsing.unexpectedReference(ex.getReferencedName())
        context.handleError(ErrorIds.Parsing.UnexpectedReference, ex, msg)
        null
      }
    ex is IfExpression -> {
      val cond = recur(ex.condition) as? BooleanFormula
      val thenBranch = recur(ex.thenExpression)
      val elseBranch = recur(ex.elseExpression)
      if (cond != null && thenBranch != null && elseBranch != null) {
        solver.ifThenElse(cond, thenBranch, elseBranch)
      } else {
        null
      }
    }
    ex is WhenExpression && ex.subjectExpression == null ->
      ex.entries.foldRight<WhenEntry, Formula?>(null) { entry, acc ->
        val conditions: List<BooleanFormula?> =
          when {
            entry.isElse -> listOf(solver.booleanFormulaManager.makeTrue())
            else ->
              entry.conditions.map { cond ->
                when (cond) {
                  is WhenConditionWithExpression -> recur(cond.expression) as? BooleanFormula
                  else -> null
                }
              }
          }
        val body = recur(entry.expression)
        when {
          body == null || conditions.any { it == null } -> return@foldRight null // error case
          acc != null -> solver.ifThenElse(solver.boolOrList(conditions.filterNotNull()), body, acc)
          entry.isElse -> body
          else -> null
        }
      }
    // special cases which do not always resolve well
    ex is BinaryExpression && ex.operationTokenRpr == "EQEQ" && ex.right is NullExpression ->
      ex.left?.let { recur(it) as? ObjectFormula }?.let { solver.isNull(it) }
    ex is BinaryExpression && ex.operationTokenRpr == "EXCLEQ" && ex.right is NullExpression ->
      ex.left?.let { recur(it) as? ObjectFormula }?.let { solver.isNotNull(it) }
    ex is BinaryExpression && ex.operationTokenRpr == "ANDAND" ->
      recur(ex.left)?.let { leftFormula ->
        recur(ex.right)?.let { rightFormula -> solver.boolAnd(listOf(leftFormula, rightFormula)) }
      }
    ex is BinaryExpression && ex.operationTokenRpr == "OROR" ->
      recur(ex.left)?.let { leftFormula ->
        recur(ex.right)?.let { rightFormula -> solver.boolOr(listOf(leftFormula, rightFormula)) }
      }
    // fall-through case
    argCall != null -> {
      val args =
        argCall.allArgumentExpressions(context).flatMap { (_, ty, _, _, e) ->
          e.map { Pair(ty, recur(it)) }
        }
      val wrappedArgs =
        args.takeIf { args.all { it.second != null } }?.map { (ty, e) -> solver.wrap(e!!, ty) }
      wrappedArgs?.let { solver.primitiveFormula(context, argCall, it) }
        ?: fieldFormula(argCall.resultingDescriptor, args)
    }
    else -> null
  }
}

private fun Solver.wrap(formula: Formula, type: Type): Formula =
  when {
    // only wrap variables and 'field(name, thing)'
    !formulaManager.isSingleVariable(formula) && !isFieldCall(formula) -> formula
    formula is ObjectFormula -> {
      val unwrapped = if (type.isMarkedNullable) type.unwrappedNotNullableType else type
      when (unwrapped.primitiveType()) {
        PrimitiveType.INTEGRAL -> intValue(formula)
        PrimitiveType.RATIONAL -> decimalValue(formula)
        PrimitiveType.BOOLEAN -> boolValue(formula)
        else -> formula
      }
    }
    else -> formula
  }

private fun SolverState.fieldFormula(
  descriptor: CallableDescriptor,
  args: List<Pair<Type, Formula?>>
): ObjectFormula? =
  descriptor.takeIf { it.isField() }?.let {
    // create a field, the 'this' may be missing
    val thisExpression =
      (args.getOrNull(0)?.second as? ObjectFormula) ?: solver.makeObjectVariable("this")
    field(descriptor, thisExpression)
  }

/**
 * Turns a named constant expression into a smt [Formula] represented as a constant declared in the
 * correct theory given this [type].
 *
 * For example if [type] refers to [Int] the constant smt value will have as formula type
 * [FormulaType.IntegerType]
 */
private fun Solver.makeConstant(type: Type, ex: ConstantExpression): Formula? =
  when (type.unwrapIfNullable().primitiveType()) {
    PrimitiveType.INTEGRAL ->
      ex.text.asIntegerLiteral()?.let { integerFormulaManager.makeNumber(it) }
    PrimitiveType.RATIONAL ->
      ex.text.asFloatingLiteral()?.let { rationalFormulaManager.makeNumber(it) }
    PrimitiveType.BOOLEAN -> booleanFormulaManager.makeBoolean(ex.text.toBooleanStrict())
    else -> null
  }

internal fun Element.isResultReference(bindingContext: ResolutionContext): Boolean =
  getPostOrInvariantParent(bindingContext)?.let { parent ->
    val expArg = parent.resolvedArg("predicate") as? ExpressionValueArgument
    val lambdaArg =
      (expArg?.valueArgument as? ExpressionLambdaArgument)?.getLambdaExpression()
        ?: (expArg?.valueArgument as? ExpressionResolvedValueArgument)?.argumentExpression as?
          LambdaExpression
    val params =
      lambdaArg?.functionLiteral?.valueParameters?.map { it.text }.orEmpty() + listOf("it")
    this.text in params.distinct()
  }
    ?: false

internal fun Element.getPostOrInvariantParent(bindingContext: ResolutionContext): ResolvedCall? =
  this.parents().mapNotNull { it.getResolvedCall(bindingContext) }.firstOrNull { call ->
    val kind = call.specialKind
    kind == SpecialKind.Post || kind == SpecialKind.Invariant
  }
