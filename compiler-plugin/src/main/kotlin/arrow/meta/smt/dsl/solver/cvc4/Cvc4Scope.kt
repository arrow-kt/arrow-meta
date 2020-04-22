package arrow.meta.smt.dsl.solver.cvc4

import arrow.meta.smt.dsl.scope.SmtScope
import arrow.meta.smt.dsl.util.array
import arrow.meta.smt.dsl.util.float
import arrow.meta.smt.dsl.util.int
import arrow.meta.smt.dsl.util.quantify
import arrow.meta.smt.dsl.util.rational
import arrow.meta.smt.dsl.util.sLogic
import arrow.meta.smt.dsl.util.vector
import org.sosy_lab.java_smt.api.ArrayFormulaManager
import org.sosy_lab.java_smt.api.BitvectorFormulaManager
import org.sosy_lab.java_smt.api.BooleanFormulaManager
import org.sosy_lab.java_smt.api.FloatingPointFormulaManager
import org.sosy_lab.java_smt.api.FormulaManager
import org.sosy_lab.java_smt.api.IntegerFormulaManager
import org.sosy_lab.java_smt.api.QuantifiedFormulaManager
import org.sosy_lab.java_smt.api.RationalFormulaManager
import org.sosy_lab.java_smt.api.SLFormulaManager
import org.sosy_lab.java_smt.api.SolverContext
import org.sosy_lab.java_smt.api.UFManager

/**
 * TODO: populate
 */
interface Cvc4Scope : SmtScope {

  companion object {
    // default
    fun scope(ctx: SolverContext): Cvc4Scope =
      object : Cvc4Scope {
        override val ctx: SolverContext = ctx
        override val formula: FormulaManager = ctx.formulaManager
        override val uf: UFManager = formula.ufManager
        override val bool: BooleanFormulaManager = formula.booleanFormulaManager
        override val int: IntegerFormulaManager? = formula.int
        override val array: ArrayFormulaManager? = formula.array
        override val float: FloatingPointFormulaManager? = formula.float
        override val rational: RationalFormulaManager? = formula.rational
        override val vector: BitvectorFormulaManager? = formula.vector
        override val quantify: QuantifiedFormulaManager? = formula.quantify
        override val sLogic: SLFormulaManager? = formula.sLogic
      }
  }
}

