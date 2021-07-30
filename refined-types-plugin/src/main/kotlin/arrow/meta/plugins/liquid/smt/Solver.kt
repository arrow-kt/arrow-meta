package arrow.meta.plugins.liquid.smt

import org.sosy_lab.common.ShutdownManager
import org.sosy_lab.common.configuration.Configuration
import org.sosy_lab.common.log.BasicLogManager
import org.sosy_lab.common.log.LogManager
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

class Solver(context: SolverContext) :
  SolverContext by context,
  FormulaManager by context.formulaManager,
  BooleanFormulaManager by context.formulaManager.booleanFormulaManager {

  fun <A> ints(f: IntegerFormulaManager.() -> A): A =
    f(integerFormulaManager)

  fun <A> booleans(f: BooleanFormulaManager.() -> A): A =
    f(booleanFormulaManager)

  fun <A> rationals(f: RationalFormulaManager.() -> A): A =
    f(rationalFormulaManager)

  fun <A> floatingPoint(f: FloatingPointFormulaManager.() -> A): A =
    f(floatingPointFormulaManager)

  fun <A> bitvectors(f: BitvectorFormulaManager.() -> A): A =
    f(bitvectorFormulaManager)

  fun <A> arrays(f: ArrayFormulaManager.() -> A): A =
    f(arrayFormulaManager)

  fun <A> quantified(f: QuantifiedFormulaManager.() -> A): A =
    f(quantifiedFormulaManager)

  fun <A> separationLogic(f: SLFormulaManager.() -> A): A =
    f(slFormulaManager)

  fun <A> uninterpretedFunctions(f: UFManager.() -> A): A =
    f(ufManager)

  fun <A> formulae(f: FormulaManager.() -> A): A =
    f(formulaManager)

  companion object {
    operator fun invoke(): Solver {
      val config: Configuration = Configuration.defaultConfiguration()
      val logger: LogManager = BasicLogManager.create(config)
      val shutdown = ShutdownManager.create()
      val context = SolverContextFactory.createSolverContext(
        config, logger, shutdown.notifier, SolverContextFactory.Solvers.SMTINTERPOL
      )
      return Solver(context)
    }
  }
}