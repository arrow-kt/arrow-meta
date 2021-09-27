package arrow.meta.plugins.liquid.phases.analysis.solver.collect

import arrow.meta.plugins.liquid.smt.Solver
import arrow.meta.plugins.liquid.smt.boolAnd
import arrow.meta.plugins.liquid.smt.boolEquivalence
import arrow.meta.plugins.liquid.smt.boolNot
import arrow.meta.plugins.liquid.smt.boolOr
import arrow.meta.plugins.liquid.smt.boolXor
import arrow.meta.plugins.liquid.smt.intDivide
import arrow.meta.plugins.liquid.smt.intEquals
import arrow.meta.plugins.liquid.smt.intGreaterThan
import arrow.meta.plugins.liquid.smt.intGreaterThanOrEquals
import arrow.meta.plugins.liquid.smt.intLessThan
import arrow.meta.plugins.liquid.smt.intLessThanOrEquals
import arrow.meta.plugins.liquid.smt.intMinus
import arrow.meta.plugins.liquid.smt.intMultiply
import arrow.meta.plugins.liquid.smt.intNegate
import arrow.meta.plugins.liquid.smt.intPlus
import arrow.meta.plugins.liquid.smt.rationalDivide
import arrow.meta.plugins.liquid.smt.rationalEquals
import arrow.meta.plugins.liquid.smt.rationalGreaterThan
import arrow.meta.plugins.liquid.smt.rationalGreaterThanOrEquals
import arrow.meta.plugins.liquid.smt.rationalLessThan
import arrow.meta.plugins.liquid.smt.rationalLessThanOrEquals
import arrow.meta.plugins.liquid.smt.rationalMinus
import arrow.meta.plugins.liquid.smt.rationalMultiply
import arrow.meta.plugins.liquid.smt.rationalNegate
import arrow.meta.plugins.liquid.smt.rationalPlus
import arrow.meta.plugins.liquid.types.PrimitiveType
import arrow.meta.plugins.liquid.types.primitiveType
import arrow.meta.plugins.liquid.types.unwrapIfNullable
import org.jetbrains.kotlin.backend.common.descriptors.allParameters
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.fir.builder.toFirOperation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.NumeralFormula

/*
 * Most of the information in this file refers to
 * https://kotlinlang.org/docs/basic-types.html
 */

fun Solver.primitiveFormula(
  resolvedCall: ResolvedCall<out CallableDescriptor>,
  args: List<Formula>
): Formula? {
  val descriptor = resolvedCall.resultingDescriptor
  val returnTy = descriptor.returnType?.primitiveType()
  val argTys = descriptor.allParameters.map { param -> param.type.primitiveType() }
  return when {
    descriptor.isComparison() ->
      comparisonFormula(resolvedCall, args)
    returnTy == PrimitiveType.BOOLEAN && argTys.all { it == PrimitiveType.BOOLEAN } ->
      booleanFormula(descriptor, args)
    returnTy == PrimitiveType.INTEGRAL && argTys.all { it == PrimitiveType.INTEGRAL } ->
      integralFormula(descriptor, args)
    returnTy == PrimitiveType.RATIONAL && argTys.all { it == PrimitiveType.RATIONAL } ->
      rationalFormula(descriptor, args)
    else -> null
  }
}

private fun CallableDescriptor.isComparison() =
  overriddenDescriptors.any {
    it.fqNameSafe == FqName("kotlin.Any.equals") ||
      it.fqNameSafe == FqName("kotlin.Comparable.equals") ||
      it.fqNameSafe == FqName("kotlin.Comparable.compareTo")
  }

private fun Solver.comparisonFormula(
  resolvedCall: ResolvedCall<out CallableDescriptor>,
  args: List<Formula>
): BooleanFormula? =
  resolvedCall.allArgumentExpressions()
    .takeIf { it.size == 2 }
    ?.let {
      val ty1 = it[0].second.unwrapIfNullable().primitiveType()
      val ty2 = it[1].second.unwrapIfNullable().primitiveType()
      val op = (resolvedCall.call.callElement as? KtBinaryExpression)?.operationToken?.toFirOperation()?.operator
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
): NumeralFormula.IntegerFormula? = when (descriptor.name.asString()) {
  "plus" -> intPlus(args)
  "minus" -> intMinus(args)
  "times" -> intMultiply(args)
  "div" -> intDivide(args)
  "inc" -> intPlus(args + listOf(integerFormulaManager.makeNumber(1)))
  "dec" -> intMinus(args + listOf(integerFormulaManager.makeNumber(1)))
  "unaryMinus" -> intNegate(args)
  "unaryPlus" -> (args.getOrNull(0) as? NumeralFormula.IntegerFormula)
  else -> null
}

private fun Solver.rationalFormula(
  descriptor: CallableDescriptor,
  args: List<Formula>
): NumeralFormula.RationalFormula? = when (descriptor.name.asString()) {
  "plus" -> rationalPlus(args)
  "minus" -> rationalMinus(args)
  "times" -> rationalMultiply(args)
  "div" -> rationalDivide(args)
  "inc" -> rationalPlus(args + listOf(integerFormulaManager.makeNumber(1)))
  "dec" -> rationalMinus(args + listOf(integerFormulaManager.makeNumber(1)))
  "unaryMinus" -> rationalNegate(args)
  "unaryPlus" -> (args.getOrNull(0) as? NumeralFormula.RationalFormula)
  else -> null
}
