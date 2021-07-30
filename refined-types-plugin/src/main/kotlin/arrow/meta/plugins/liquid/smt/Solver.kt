package arrow.meta.plugins.liquid.smt

import org.sosy_lab.common.ShutdownManager
import org.sosy_lab.common.configuration.Configuration
import org.sosy_lab.common.log.BasicLogManager
import org.sosy_lab.common.log.LogManager
import org.sosy_lab.java_smt.SolverContextFactory
import org.sosy_lab.java_smt.api.*

typealias ObjectFormula = NumeralFormula.IntegerFormula
val ObjectFormulaType = FormulaType.IntegerType

class Solver(context: SolverContext) :
  SolverContext by context,
  FormulaManager by context.formulaManager,
  BooleanFormulaManager by context.formulaManager.booleanFormulaManager {

  fun <A> ints(f: IntegerFormulaManager.() -> A): A =
    f(integerFormulaManager)

  fun <A> objects(f: IntegerFormulaManager.() -> A): A =
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

  val intValueFun: FunctionDeclaration<ObjectFormula> =
    ufManager.declareUF(INT_VALUE_NAME, FormulaType.IntegerType, ObjectFormulaType)
  val boolValueFun: FunctionDeclaration<BooleanFormula> =
    ufManager.declareUF(BOOL_VALUE_NAME, FormulaType.BooleanType, ObjectFormulaType)
  val decimalValueFun: FunctionDeclaration<NumeralFormula.RationalFormula> =
    ufManager.declareUF(DECIMAL_VALUE_NAME, FormulaType.RationalType, ObjectFormulaType)

  fun intValue(formula: ObjectFormula): NumeralFormula.IntegerFormula =
    uninterpretedFunctions { callUF(intValueFun, formula) }

  fun boolValue(formula: ObjectFormula): BooleanFormula =
    uninterpretedFunctions { callUF(boolValueFun, formula) }

  fun decimalValue(formula: ObjectFormula): NumeralFormula.RationalFormula =
    uninterpretedFunctions { callUF(decimalValueFun, formula) }

  fun makeObjectVariable(varName: String): ObjectFormula =
    objects { this.makeVariable(varName) }

  fun makeBooleanObjectVariable(varName: String): BooleanFormula =
    boolValue(makeObjectVariable(varName))

  fun makeIntegerObjectVariable(varName: String): NumeralFormula.IntegerFormula =
    intValue(makeObjectVariable(varName))

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

    val INT_VALUE_NAME = "int"
    val BOOL_VALUE_NAME = "bool"
    val DECIMAL_VALUE_NAME = "dec"
  }
}