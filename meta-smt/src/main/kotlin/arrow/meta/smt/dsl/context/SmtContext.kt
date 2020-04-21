package arrow.meta.smt.dsl.context

import arrow.meta.smt.dsl.quantify.QuantifySyntax
import arrow.meta.smt.util.orNull
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

interface SmtContext {
  val ctx: SolverContext
  val formula: FormulaManager
  val uf: UFManager
  val bool: BooleanFormulaManager
  val int: IntegerFormulaManager?
  val array: ArrayFormulaManager?
  val float: FloatingPointFormulaManager?
  val rational: RationalFormulaManager?
  val vector: BitvectorFormulaManager?
  val quantify: QuantifiedFormulaManager?
  val slogic: SLFormulaManager?
}

interface ArrayCtx : SmtContext {
  override val array: ArrayFormulaManager
}



interface RationalCtx : SmtContext {
  override val rational: RationalFormulaManager
}

interface VectorCtx : SmtContext {
  override val vector: BitvectorFormulaManager
}

interface QuantifyCtx : SmtContext, QuantifySyntax {
  override val quantify: QuantifiedFormulaManager
}

interface SLogic : SmtContext {
  override val slogic: SLFormulaManager
}

fun default(ctx: SolverContext): SmtContext =
  object : SmtContext {
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
    override val slogic: SLFormulaManager? = formula.slogic
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

val FormulaManager.slogic: SLFormulaManager?
  get() = orNull { slFormulaManager }