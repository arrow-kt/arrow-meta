package arrow.meta.smt.dsl.solverscope.princess

import arrow.meta.smt.dsl.context.QuantifyCtx
import arrow.meta.smt.dsl.context.array
import arrow.meta.smt.dsl.context.float
import arrow.meta.smt.dsl.context.int.IntCtx
import arrow.meta.smt.dsl.context.rational
import arrow.meta.smt.dsl.context.slogic
import arrow.meta.smt.dsl.context.vector
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

interface PrincessScope : IntCtx, QuantifyCtx

fun princess(formula: FormulaManager, ctx: SolverContext): PrincessScope =
  object : PrincessScope {
    override val ctx: SolverContext = ctx
    override val formula: FormulaManager = formula
    override val uf: UFManager = formula.ufManager
    override val bool: BooleanFormulaManager = formula.booleanFormulaManager
    override val int: IntegerFormulaManager = formula.integerFormulaManager
    override val array: ArrayFormulaManager? = formula.array
    override val float: FloatingPointFormulaManager? = formula.float
    override val rational: RationalFormulaManager? = formula.rational
    override val vector: BitvectorFormulaManager? = formula.vector
    override val quantify: QuantifiedFormulaManager = formula.quantifiedFormulaManager
    override val slogic: SLFormulaManager? = formula.slogic
  }

