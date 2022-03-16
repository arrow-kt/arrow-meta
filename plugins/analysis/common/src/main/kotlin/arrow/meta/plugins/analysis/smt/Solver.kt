package arrow.meta.plugins.analysis.smt

import arrow.meta.plugins.analysis.phases.analysis.solver.RESULT_VAR_NAME
import arrow.meta.plugins.analysis.phases.analysis.solver.THIS_VAR_NAME
import arrow.meta.plugins.analysis.smt.utils.DefaultKotlinPrinter
import arrow.meta.plugins.analysis.smt.utils.KotlinPrinter
import arrow.meta.plugins.analysis.smt.utils.NameProvider
import org.sosy_lab.java_smt.SolverContextFactory
import org.sosy_lab.java_smt.api.ArrayFormulaManager
import org.sosy_lab.java_smt.api.BitvectorFormulaManager
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.BooleanFormulaManager
import org.sosy_lab.java_smt.api.FloatingPointFormulaManager
import org.sosy_lab.java_smt.api.FormulaManager
import org.sosy_lab.java_smt.api.FormulaType
import org.sosy_lab.java_smt.api.FunctionDeclaration
import org.sosy_lab.java_smt.api.IntegerFormulaManager
import org.sosy_lab.java_smt.api.NumeralFormula
import org.sosy_lab.java_smt.api.QuantifiedFormulaManager
import org.sosy_lab.java_smt.api.RationalFormulaManager
import org.sosy_lab.java_smt.api.SLFormulaManager
import org.sosy_lab.java_smt.api.SolverContext
import org.sosy_lab.java_smt.api.UFManager

typealias ObjectFormula = NumeralFormula.IntegerFormula

typealias FieldFormula = NumeralFormula.IntegerFormula

val ObjectFormulaType = FormulaType.IntegerType
val FieldFormulaType = FormulaType.IntegerType

class Solver(context: SolverContext, nameProvider: NameProvider) :
  SolverContext by context,
  FormulaManager by context.formulaManager,
  BooleanFormulaManager by context.formulaManager.booleanFormulaManager,
  KotlinPrinter by DefaultKotlinPrinter(context.formulaManager, nameProvider) {

  fun <A> ints(f: IntegerFormulaManager.() -> A): A = f(integerFormulaManager)

  fun <A> objects(f: IntegerFormulaManager.() -> A): A = f(integerFormulaManager)

  fun <A> booleans(f: BooleanFormulaManager.() -> A): A = f(booleanFormulaManager)

  fun <A> rationals(f: RationalFormulaManager.() -> A): A = f(rationalFormulaManager)

  fun <A> floatingPoint(f: FloatingPointFormulaManager.() -> A): A = f(floatingPointFormulaManager)

  fun <A> bitvectors(f: BitvectorFormulaManager.() -> A): A = f(bitvectorFormulaManager)

  fun <A> arrays(f: ArrayFormulaManager.() -> A): A = f(arrayFormulaManager)

  fun <A> quantified(f: QuantifiedFormulaManager.() -> A): A = f(quantifiedFormulaManager)

  fun <A> separationLogic(f: SLFormulaManager.() -> A): A = f(slFormulaManager)

  fun <A> uninterpretedFunctions(f: UFManager.() -> A): A = f(ufManager)

  fun <A> formulae(f: FormulaManager.() -> A): A = f(formulaManager)

  val intValueFun: FunctionDeclaration<ObjectFormula> =
    ufManager.declareUF(INT_VALUE_NAME, FormulaType.IntegerType, ObjectFormulaType)

  val boolValueFun: FunctionDeclaration<BooleanFormula> =
    ufManager.declareUF(BOOL_VALUE_NAME, FormulaType.BooleanType, ObjectFormulaType)

  val decimalValueFun: FunctionDeclaration<NumeralFormula.RationalFormula> =
    ufManager.declareUF(DECIMAL_VALUE_NAME, FormulaType.RationalType, ObjectFormulaType)

  val fieldFun: FunctionDeclaration<ObjectFormula> =
    ufManager.declareUF(FIELD_FUNCTION_NAME, ObjectFormulaType, FieldFormulaType, ObjectFormulaType)

  val isNullFn: FunctionDeclaration<BooleanFormula> =
    ufManager.declareUF(IS_NULL_FUNCTION_NAME, FormulaType.BooleanType, ObjectFormulaType)

  fun intValue(formula: ObjectFormula): NumeralFormula.IntegerFormula = uninterpretedFunctions {
    callUF(intValueFun, formula)
  }

  fun boolValue(formula: ObjectFormula): BooleanFormula = uninterpretedFunctions {
    callUF(boolValueFun, formula)
  }

  fun decimalValue(formula: ObjectFormula): NumeralFormula.RationalFormula =
      uninterpretedFunctions {
    callUF(decimalValueFun, formula)
  }

  fun field(fieldName: String, formula: ObjectFormula): ObjectFormula = uninterpretedFunctions {
    callUF(fieldFun, integerFormulaManager.makeVariable(fieldName), formula)
  }

  fun isNull(formula: ObjectFormula): BooleanFormula = uninterpretedFunctions {
    callUF(isNullFn, formula)
  }

  fun isNotNull(formula: ObjectFormula): BooleanFormula = booleans { not(isNull(formula)) }

  fun makeObjectVariable(varName: String): ObjectFormula = objects { this.makeVariable(varName) }

  fun makeBooleanObjectVariable(varName: String): BooleanFormula =
    boolValue(makeObjectVariable(varName))

  fun makeIntegerObjectVariable(varName: String): NumeralFormula.IntegerFormula =
    intValue(makeObjectVariable(varName))

  fun makeDecimalObjectVariable(varName: String): NumeralFormula.RationalFormula =
    decimalValue(makeObjectVariable(varName))

  val resultVariable = makeObjectVariable(RESULT_VAR_NAME)
  val thisVariable = makeObjectVariable(THIS_VAR_NAME)

  private val forbiddenNames: List<String> =
    listOf(
      // From SMTLIB docs
      "div",
      "mod",
      "abs",
      "select",
      "store",
      "true",
      "false",
      "not",
      "and",
      "or",
      "xor",
      "distinct",
      "ite",
      // from our own internal names
      INT_VALUE_NAME,
      BOOL_VALUE_NAME,
      DECIMAL_VALUE_NAME,
      FIELD_FUNCTION_NAME,
      IS_NULL_FUNCTION_NAME
    )

  override fun escape(name: String): String =
    when {
      name in forbiddenNames -> "$name##"
      else -> name
    }.let { formulaManager.escape(it) }

  companion object {

    operator fun invoke(nameProvider: NameProvider): Solver =
      Solver(
        SolverContextFactory.createSolverContext(SolverContextFactory.Solvers.SMTINTERPOL),
        nameProvider
      )

    val INT_VALUE_NAME = "int"
    val BOOL_VALUE_NAME = "bool"
    val DECIMAL_VALUE_NAME = "dec"
    val FIELD_FUNCTION_NAME = "field"
    val IS_NULL_FUNCTION_NAME = "null"
  }
}

// val initZ3: Unit = //initializes first the turnkey distribution to enable z3 dynamic loading
//
// //tricks java smt lib by injecting the turnkey z3 unpackaded location as part of the java libs
//  //before java smt loads it
//  run {
//    //initializes first the turnkey distribution to enable z3 dynamic loading
//    val z3 = Native.getFullVersion()
//    println(z3)
//    //tricks java smt lib by injecting the turnkey z3 unpackaded location as part of the java libs
//    //before java smt loads it
//    val z3TurnKeyDistribution: File? = System.getProperty("java.io.tmpdir")?.let {
//      File(it).listFiles().orEmpty().filter {
//        it.name.contains("z3-turnkey")
//      }.firstOrNull()
//    }
//    val nativePath = NativeLibraries.getNativeLibraryPath().toFile()
//    if (nativePath.exists()) {
//      nativePath.mkdirs()
//    }
//    z3TurnKeyDistribution?.listFiles()?.orEmpty()?.forEach {
//      try {
//        if (it.exists()) {
//       //   it.copyTo(File(nativePath, it.name), true)
//        }
//      } catch (ex: IOException) {
//        println(ex.message)
//      }
//    }
//  }
