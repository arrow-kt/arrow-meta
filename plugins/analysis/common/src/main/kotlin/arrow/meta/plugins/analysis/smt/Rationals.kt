package arrow.meta.plugins.analysis.smt

import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.NumeralFormula

private fun List<Formula>.rational(): List<NumeralFormula.RationalFormula> =
  filterIsInstance<NumeralFormula.RationalFormula>()

internal fun Solver.rationalEquals(args: List<Formula>): BooleanFormula? = rationals {
  if (args.size == 2) {
    val (left, right) = args.rational()
    equal(left, right)
  } else null
}

internal fun Solver.rationalPlus(args: List<Formula>): NumeralFormula.RationalFormula? = rationals {
  if (args.size == 2) {
    val (left, right) = args.rational()
    add(left, right)
  } else null
}

internal fun Solver.rationalNegate(args: List<Formula>): NumeralFormula.RationalFormula? =
    rationals {
  if (args.size == 1) {
    negate(args.rational().first())
  } else null
}

internal fun Solver.rationalMinus(args: List<Formula>): NumeralFormula.RationalFormula? =
    rationals {
  if (args.size == 2) {
    val (left, right) = args.rational()
    subtract(left, right)
  } else null
}

internal fun Solver.rationalDivide(args: List<Formula>): NumeralFormula.RationalFormula? =
    rationals {
  if (args.size == 2) {
    val (left, right) = args.rational()
    divide(left, right)
  } else null
}

internal fun Solver.rationalMultiply(args: List<Formula>): NumeralFormula.RationalFormula? =
    rationals {
  if (args.size == 2) {
    val (left, right) = args.rational()
    multiply(left, right)
  } else null
}

internal fun Solver.rationalGreaterThan(args: List<Formula>): BooleanFormula? = rationals {
  if (args.size == 2) {
    val (left, right) = args.rational()
    greaterThan(left, right)
  } else null
}

internal fun Solver.rationalGreaterThanOrEquals(args: List<Formula>): BooleanFormula? = rationals {
  if (args.size == 2) {
    val (left, right) = args.rational()
    greaterOrEquals(left, right)
  } else null
}

internal fun Solver.rationalLessThan(args: List<Formula>): BooleanFormula? = rationals {
  if (args.size == 2) {
    val (left, right) = args.rational()
    lessThan(left, right)
  } else null
}

internal fun Solver.rationalLessThanOrEquals(args: List<Formula>): BooleanFormula? = rationals {
  if (args.size == 2) {
    val (left, right) = args.rational()
    lessOrEquals(left, right)
  } else null
}
