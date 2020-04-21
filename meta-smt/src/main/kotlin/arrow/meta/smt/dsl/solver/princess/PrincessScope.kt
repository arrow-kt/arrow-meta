package arrow.meta.smt.dsl.solver.princess

import arrow.meta.smt.dsl.scope.array.ArrayCtx
import arrow.meta.smt.dsl.scope.int.IntCtx
import arrow.meta.smt.dsl.scope.quantify.QuantifyCtx
import arrow.meta.smt.dsl.scope.vector.VectorCtx
import arrow.meta.smt.dsl.util.float
import arrow.meta.smt.dsl.util.rational
import arrow.meta.smt.dsl.util.sLogic
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

interface PrincessScope : IntCtx, QuantifyCtx, ArrayCtx, VectorCtx {
  companion object {
    fun scope(ctx: SolverContext): PrincessScope =
      object : PrincessScope {
        override val ctx: SolverContext = ctx
        override val formula: FormulaManager = ctx.formulaManager
        override val uf: UFManager = formula.ufManager
        override val bool: BooleanFormulaManager = formula.booleanFormulaManager
        override val int: IntegerFormulaManager = formula.integerFormulaManager
        override val array: ArrayFormulaManager = formula.arrayFormulaManager
        override val float: FloatingPointFormulaManager? = formula.float // null at runtime
        override val rational: RationalFormulaManager? = formula.rational // null at runtime
        override val vector: BitvectorFormulaManager = formula.bitvectorFormulaManager
        override val quantify: QuantifiedFormulaManager = formula.quantifiedFormulaManager
        override val sLogic: SLFormulaManager? = formula.sLogic // null at runtime
      }
  }
}



