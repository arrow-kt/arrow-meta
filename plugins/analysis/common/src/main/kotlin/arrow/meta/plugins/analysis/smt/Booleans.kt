package arrow.meta.plugins.analysis.smt

import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula

internal fun Solver.boolNot(args: List<Formula>): BooleanFormula? = booleans {
  if (args.size == 1 && args[0] is BooleanFormula) {
    val theOne = args[0] as BooleanFormula
    not(theOne)
  } else null
}

internal fun Solver.boolEquivalence(args: List<Formula>): BooleanFormula? = booleans {
  if (args.size == 2) {
    val (left, right) = args
    if (left is BooleanFormula && right is BooleanFormula) {
      equivalence(left, right)
    } else null
  } else null
}

internal fun Solver.boolImplication(args: List<Formula>): BooleanFormula? = booleans {
  if (args.size == 2) {
    val (left, right) = args
    if (left is BooleanFormula && right is BooleanFormula) {
      implication(left, right)
    } else null
  } else null
}

internal fun Solver.boolAnd(args: List<Formula>): BooleanFormula? = booleans {
  if (args.size == 2) {
    val (left, right) = args
    if (left is BooleanFormula && right is BooleanFormula) {
      and(left, right)
    } else null
  } else null
}

internal fun Solver.boolAndList(args: List<BooleanFormula>): BooleanFormula? =
  when (args.size) {
    0 -> null
    1 -> args[0]
    else -> booleans { and(args) }
  }

internal fun Solver.implication(args: List<Formula>): BooleanFormula? = booleans {
  if (args.size == 2) {
    val (left, right) = args
    if (left is BooleanFormula && right is BooleanFormula) {
      implication(left, right)
    } else null
  } else null
}

internal fun Solver.boolOr(args: List<Formula>): BooleanFormula? = booleans {
  if (args.size == 2) {
    val (left, right) = args
    if (left is BooleanFormula && right is BooleanFormula) {
      or(left, right)
    } else null
  } else null
}

internal fun Solver.boolOrList(args: List<BooleanFormula>): BooleanFormula =
  when (args.size) {
    0 -> booleanFormulaManager.makeFalse()
    1 -> args[0]
    else -> booleans { or(args) }
  }

internal fun Solver.boolXor(args: List<Formula>): BooleanFormula? = booleans {
  if (args.size == 2) {
    val (left, right) = args
    if (left is BooleanFormula && right is BooleanFormula) {
      xor(left, right)
    } else null
  } else null
}

internal fun Solver.ifThenElse(args: List<Formula>): Formula? = booleans {
  if (args.size == 3) {
    val (cond, f1, f2) = args
    if (cond is BooleanFormula && f1 is BooleanFormula && f2 is BooleanFormula) {
      ifThenElse(cond, f1, f2)
    } else null
  } else null
}

internal fun Solver.isTrue(args: List<Formula>): Boolean? = booleans {
  if (args.size == 1) {
    val cond = args.first()
    if (cond is BooleanFormula) {
      isTrue(cond)
    } else null
  } else null
}

internal fun Solver.isFalse(args: List<Formula>): Boolean? = booleans {
  if (args.size == 1) {
    val cond = args.first()
    if (cond is BooleanFormula) {
      isFalse(cond)
    } else null
  } else null
}
