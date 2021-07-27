package arrow.meta.plugins.liquid.phases.solver.collector

import arrow.meta.plugins.liquid.smt.Solver
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula

internal fun Solver.boolEquivalence(args: List<Formula>): Formula? =
  booleans {
    if (args.size == 2) {
      val (left, right) = args
      if (left is BooleanFormula && right is BooleanFormula) {
        equivalence(left, right)
      } else null
    } else null
  }

internal fun Solver.boolImplication(args: List<Formula>): Formula? =
  booleans {
    if (args.size == 2) {
      val (left, right) = args
      if (left is BooleanFormula && right is BooleanFormula) {
        implication(left, right)
      } else null
    } else null
  }

internal fun Solver.boolAnd(args: List<Formula>): BooleanFormula? =
  booleans {
    if (args.size == 2) {
      val (left, right) = args
      if (left is BooleanFormula && right is BooleanFormula) {
        and(left, right)
      } else null
    } else null
  }

internal fun Solver.implication(args: List<Formula>): BooleanFormula? =
  booleans {
    if (args.size == 2) {
      val (left, right) = args
      if (left is BooleanFormula && right is BooleanFormula) {
        implication(left, right)
      } else null
    } else null
  }


internal fun Solver.boolOr(args: List<Formula>): Formula? =
  booleans {
    if (args.size == 2) {
      val (left, right) = args
      if (left is BooleanFormula && right is BooleanFormula) {
        or(left, right)
      } else null
    } else null
  }

internal fun Solver.boolXor(args: List<Formula>): Formula? =
  booleans {
    if (args.size == 2) {
      val (left, right) = args
      if (left is BooleanFormula && right is BooleanFormula) {
        xor(left, right)
      } else null
    } else null
  }

internal fun Solver.ifThenElse(args: List<Formula>): Formula? =
  booleans {
    if (args.size == 3) {
      val (cond, f1, f2) = args
      if (cond is BooleanFormula && f1 is BooleanFormula && f2 is BooleanFormula) {
        ifThenElse(cond, f1, f2)
      } else null
    } else null
  }

internal fun Solver.isTrue(args: List<Formula>): Boolean? =
  booleans {
    if (args.size == 1) {
      val cond = args.first()
      if (cond is BooleanFormula) {
        isTrue(cond)
      } else null
    } else null
  }

internal fun Solver.isFalse(args: List<Formula>): Boolean? =
  booleans {
    if (args.size == 1) {
      val cond = args.first()
      if (cond is BooleanFormula) {
        isFalse(cond)
      } else null
    } else null
  }


