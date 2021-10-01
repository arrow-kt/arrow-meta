package arrow.meta.plugins.analysis.smt

import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.NumeralFormula

private fun List<Formula>.numeric(): List<NumeralFormula.IntegerFormula> =
  filterIsInstance<NumeralFormula.IntegerFormula>()

internal fun Solver.intEquals(args: List<Formula>): BooleanFormula? =
  ints {
    if (args.size == 2) {
      val (left, right) = args.numeric()
      equal(left, right)
    } else null
  }

internal fun Solver.intPlus(args: List<Formula>): NumeralFormula.IntegerFormula? =
  ints {
    if (args.size == 2) {
      val (left, right) = args.numeric()
      add(left, right)
    } else null
  }

internal fun Solver.intNegate(args: List<Formula>): NumeralFormula.IntegerFormula? =
  ints {
    if (args.size == 1) {
      negate(args.numeric().first())
    } else null
  }

internal fun Solver.intMinus(args: List<Formula>): NumeralFormula.IntegerFormula? =
  ints {
    if (args.size == 2) {
      val (left, right) = args.numeric()
      subtract(left, right)
    } else null
  }

internal fun Solver.intDivide(args: List<Formula>): NumeralFormula.IntegerFormula? =
  ints {
    if (args.size == 2) {
      val (left, right) = args.numeric()
      divide(left, right)
    } else null
  }

internal fun Solver.intMultiply(args: List<Formula>): NumeralFormula.IntegerFormula? =
  ints {
    if (args.size == 2) {
      val (left, right) = args.numeric()
      multiply(left, right)
    } else null
  }

internal fun Solver.intGreaterThan(args: List<Formula>): BooleanFormula? =
  ints {
    if (args.size == 2) {
      val (left, right) = args.numeric()
      greaterThan(left, right)
    } else null
  }

internal fun Solver.intGreaterThanOrEquals(args: List<Formula>): BooleanFormula? =
  ints {
    if (args.size == 2) {
      val (left, right) = args.numeric()
      greaterOrEquals(left, right)
    } else null
  }

internal fun Solver.intLessThan(args: List<Formula>): BooleanFormula? =
  ints {
    if (args.size == 2) {
      val (left, right) = args.numeric()
      lessThan(left, right)
    } else null
  }

internal fun Solver.intLessThanOrEquals(args: List<Formula>): BooleanFormula? =
  ints {
    if (args.size == 2) {
      val (left, right) = args.numeric()
      lessOrEquals(left, right)
    } else null
  }
