package arrow.meta.smt.dsl.util

import org.sosy_lab.java_smt.api.ArrayFormulaManager
import org.sosy_lab.java_smt.api.BitvectorFormulaManager
import org.sosy_lab.java_smt.api.FloatingPointFormulaManager
import org.sosy_lab.java_smt.api.FormulaManager
import org.sosy_lab.java_smt.api.IntegerFormulaManager
import org.sosy_lab.java_smt.api.QuantifiedFormulaManager
import org.sosy_lab.java_smt.api.RationalFormulaManager
import org.sosy_lab.java_smt.api.SLFormulaManager

fun <A> orNull(a: () -> A): A? =
  try {
    a()
  } catch (e: Exception) {
    null
  }

val FormulaManager.int: IntegerFormulaManager?
  get() = orNull { integerFormulaManager }
val FormulaManager.array: ArrayFormulaManager?
  get() = orNull { arrayFormulaManager }
val FormulaManager.rational: RationalFormulaManager?
  get() = orNull { rationalFormulaManager }
val FormulaManager.float: FloatingPointFormulaManager?
  get() = orNull { floatingPointFormulaManager }
val FormulaManager.vector: BitvectorFormulaManager?
  get() = orNull { bitvectorFormulaManager }
val FormulaManager.quantify: QuantifiedFormulaManager?
  get() = orNull { quantifiedFormulaManager }
val FormulaManager.sLogic: SLFormulaManager?
  get() = orNull { slFormulaManager }