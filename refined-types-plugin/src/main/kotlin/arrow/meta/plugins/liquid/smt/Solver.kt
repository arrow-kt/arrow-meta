package arrow.meta.plugins.liquid.smt

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

    operator fun invoke(log: (String) -> Unit): Solver =
      Solver(SolverContextFactory.createSolverContext(SolverContextFactory.Solvers.SMTINTERPOL))

    val INT_VALUE_NAME = "int"
    val BOOL_VALUE_NAME = "bool"
    val DECIMAL_VALUE_NAME = "dec"
  }
}

//val initZ3: Unit = //initializes first the turnkey distribution to enable z3 dynamic loading
//
////tricks java smt lib by injecting the turnkey z3 unpackaded location as part of the java libs
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