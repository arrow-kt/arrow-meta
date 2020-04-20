package arrow.meta.internal.smt

import org.sosy_lab.common.ShutdownManager
import org.sosy_lab.common.configuration.Configuration
import org.sosy_lab.common.log.BasicLogManager
import org.sosy_lab.java_smt.SolverContextFactory
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
import test

data class MetaSmt<R>(
  val name: String,
  val solver: SolverContextFactory.Solvers,
  val smt: (SolverContext, Managers) -> R
)

fun FormulaManager.managers(): Managers =
  Managers(
    this,
    ufManager,
    booleanFormulaManager,
    orNull { integerFormulaManager },
    orNull { arrayFormulaManager },
    orNull { floatingPointFormulaManager },
    orNull { rationalFormulaManager },
    orNull { bitvectorFormulaManager },
    orNull { quantifiedFormulaManager },
    orNull { slFormulaManager }
  )

data class Managers(
  val formula: FormulaManager,
  val uf: UFManager,
  val bool: BooleanFormulaManager,
  val int: IntegerFormulaManager?,
  val array: ArrayFormulaManager?,
  val float: FloatingPointFormulaManager?,
  val rational: RationalFormulaManager?,
  val vector: BitvectorFormulaManager?,
  val quantify: QuantifiedFormulaManager?,
  val slogic: SLFormulaManager?
)

fun ctx(solver: SolverContextFactory.Solvers): SolverContext =
  Configuration.defaultConfiguration().let {
    SolverContextFactory.createSolverContext(it, BasicLogManager.create(it), ShutdownManager.create().notifier, solver)
  }

fun <A> orNull(a: () -> A): A? =
  try {
    a()
  } catch (e: Exception) {
    null
  }

fun <R> register(smt: MetaSmt<R>) {
  println("Initialize SMT: ${smt.name}")
  val ctx: SolverContext = ctx(smt.solver)
  smt.smt(ctx, ctx.formulaManager.managers())
}

fun main() {
  //println(ctx(SolverContextFactory.Solvers.Z3).version)
  register(test)
}