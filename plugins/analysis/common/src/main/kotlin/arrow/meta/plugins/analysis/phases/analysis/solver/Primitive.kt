package arrow.meta.plugins.analysis.phases.analysis.solver

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BinaryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.allArgumentExpressions
import arrow.meta.plugins.analysis.smt.Solver
import arrow.meta.plugins.analysis.smt.boolAnd
import arrow.meta.plugins.analysis.smt.boolEquivalence
import arrow.meta.plugins.analysis.smt.boolNot
import arrow.meta.plugins.analysis.smt.boolOr
import arrow.meta.plugins.analysis.smt.boolXor
import arrow.meta.plugins.analysis.smt.intEquals
import arrow.meta.plugins.analysis.smt.intGreaterThan
import arrow.meta.plugins.analysis.smt.intGreaterThanOrEquals
import arrow.meta.plugins.analysis.smt.intLessThan
import arrow.meta.plugins.analysis.smt.intLessThanOrEquals
import arrow.meta.plugins.analysis.smt.intMinus
import arrow.meta.plugins.analysis.smt.intMultiply
import arrow.meta.plugins.analysis.smt.intNegate
import arrow.meta.plugins.analysis.smt.intPlus
import arrow.meta.plugins.analysis.smt.rationalEquals
import arrow.meta.plugins.analysis.smt.rationalGreaterThan
import arrow.meta.plugins.analysis.smt.rationalGreaterThanOrEquals
import arrow.meta.plugins.analysis.smt.rationalLessThan
import arrow.meta.plugins.analysis.smt.rationalLessThanOrEquals
import arrow.meta.plugins.analysis.smt.rationalMinus
import arrow.meta.plugins.analysis.smt.rationalMultiply
import arrow.meta.plugins.analysis.smt.rationalNegate
import arrow.meta.plugins.analysis.smt.rationalPlus
import arrow.meta.plugins.analysis.types.PrimitiveType
import arrow.meta.plugins.analysis.types.primitiveType
import arrow.meta.plugins.analysis.types.unwrapIfNullable
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.NumeralFormula

/*
 * Most of the information in this file refers to
 * https://kotlinlang.org/docs/basic-types.html
 */

fun Solver.primitiveFormula(
  context: ResolutionContext,
  resolvedCall: ResolvedCall,
  args: List<Formula>
): Formula? {
  val descriptor = resolvedCall.resultingDescriptor
  val returnTy = descriptor.returnType?.primitiveType()
  val argTys = descriptor.allParameters.map { param -> param.type.primitiveType() }
  return when {
    descriptor.isComparison() ->
      comparisonFormula(context, resolvedCall, args)
    returnTy == PrimitiveType.BOOLEAN && argTys.all { it == PrimitiveType.BOOLEAN } ->
      booleanFormula(descriptor, args)
    returnTy == PrimitiveType.INTEGRAL && argTys.all { it == PrimitiveType.INTEGRAL } ->
      integralFormula(descriptor, args)
    returnTy == PrimitiveType.RATIONAL && argTys.all { it == PrimitiveType.RATIONAL } ->
      rationalFormula(descriptor, args)
    else -> null
  }
}

internal fun CallableDescriptor.isComparison() =
  overriddenDescriptors.any {
    it.fqNameSafe == FqName("kotlin.Any.equals") ||
      it.fqNameSafe == FqName("kotlin.Comparable.equals") ||
      it.fqNameSafe == FqName("kotlin.Comparable.compareTo")
  }

private fun Solver.comparisonFormula(
  context: ResolutionContext,
  resolvedCall: ResolvedCall,
  args: List<Formula>
): BooleanFormula? =
  resolvedCall.allArgumentExpressions(context)
    .takeIf { it.size == 2 }
    ?.let {
      val ty1 = it[0].second.unwrapIfNullable().primitiveType()
      val ty2 = it[1].second.unwrapIfNullable().primitiveType()
      val op = (resolvedCall.callElement as? BinaryExpression)?.operationToken
      when {
        ty1 == PrimitiveType.BOOLEAN && ty2 == PrimitiveType.BOOLEAN ->
          when (op) {
            "==" -> boolEquivalence(args)
            "!=" -> boolEquivalence(args)?.let { f -> not(f) }
            else -> null
          }
        ty1 == PrimitiveType.INTEGRAL && ty2 == PrimitiveType.INTEGRAL ->
          when (op) {
            "==" -> intEquals(args)
            "!=" -> intEquals(args)?.let { f -> not(f) }
            ">" -> intGreaterThan(args)
            ">=" -> intGreaterThanOrEquals(args)
            "<" -> intLessThan(args)
            "<=" -> intLessThanOrEquals(args)
            else -> null
          }
        ty1 == PrimitiveType.RATIONAL && ty2 == PrimitiveType.RATIONAL ->
          when (op) {
            "==" -> rationalEquals(args)
            "!=" -> rationalEquals(args)?.let { f -> not(f) }
            ">" -> rationalGreaterThan(args)
            ">=" -> rationalGreaterThanOrEquals(args)
            "<" -> rationalLessThan(args)
            "<=" -> rationalLessThanOrEquals(args)
            else -> null
          }
        else -> null
      }
    }

private fun Solver.booleanFormula(
  descriptor: CallableDescriptor,
  args: List<Formula>
): BooleanFormula? = when (descriptor.fqNameSafe) {
  FqName("kotlin.Boolean.not") -> boolNot(args)
  FqName("kotlin.Boolean.and") -> boolAnd(args)
  FqName("kotlin.Boolean.or") -> boolOr(args)
  FqName("kotlin.Boolean.xor") -> boolXor(args)
  else -> null
}

private fun Solver.integralFormula(
  descriptor: CallableDescriptor,
  args: List<Formula>
): NumeralFormula.IntegerFormula? = when (descriptor.name.value) {
  "plus" -> intPlus(args)
  "minus" -> intMinus(args)
  "times" -> intMultiply(args)
  // "div" -> intDivide(args) // not all SMT solvers support div
  "inc" -> intPlus(args + listOf(integerFormulaManager.makeNumber(1)))
  "dec" -> intMinus(args + listOf(integerFormulaManager.makeNumber(1)))
  "unaryMinus" -> intNegate(args)
  "unaryPlus" -> (args.getOrNull(0) as? NumeralFormula.IntegerFormula)
  else -> null
}

private fun Solver.rationalFormula(
  descriptor: CallableDescriptor,
  args: List<Formula>
): NumeralFormula.RationalFormula? = when (descriptor.name.value) {
  "plus" -> rationalPlus(args)
  "minus" -> rationalMinus(args)
  "times" -> rationalMultiply(args)
  // "div" -> rationalDivide(args) // not all SMT solvers support div
  "inc" -> rationalPlus(args + listOf(integerFormulaManager.makeNumber(1)))
  "dec" -> rationalMinus(args + listOf(integerFormulaManager.makeNumber(1)))
  "unaryMinus" -> rationalNegate(args)
  "unaryPlus" -> (args.getOrNull(0) as? NumeralFormula.RationalFormula)
  else -> null
}
