package arrow.meta.plugins.analysis.smt

import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.DeclarationConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FormulaManager
import org.sosy_lab.java_smt.api.FunctionDeclaration
import org.sosy_lab.java_smt.api.visitors.DefaultFormulaVisitor
import org.sosy_lab.java_smt.api.visitors.FormulaTransformationVisitor
import org.sosy_lab.java_smt.api.visitors.TraversalProcess
import org.sosy_lab.java_smt.basicimpl.FunctionDeclarationImpl

fun <T : Formula> Solver.substituteVariable(formula: T, mapping: Map<String, Formula>): T =
  try {
    formulae {
      val subst = mapping.mapKeys { entry -> makeVariable(getFormulaType(entry.value), entry.key) }
      substituteWithFix(formula, subst)
    }
  } catch (e: Exception) {
    throw java.lang.IllegalArgumentException("substituting $formula with $mapping", e)
  }

internal fun <T : Formula> Solver.renameObjectVariables(
  formula: T,
  mapping: Map<String, String>
): T =
  try {
    formulae {
      val subst =
        mapping.map { (k, v) -> Pair(makeObjectVariable(k), makeObjectVariable(v)) }.toMap()
      substituteWithFix(formula, subst)
    }
  } catch (e: Exception) {
    throw java.lang.IllegalArgumentException("renaming $formula with $mapping", e)
  }

internal fun <T : Formula> Solver.substituteObjectVariables(
  formula: T,
  mapping: Map<String, ObjectFormula>
): T =
  try {
    formulae {
      val subst = mapping.map { (k, v) -> Pair(makeObjectVariable(k), v) }.toMap()
      substituteWithFix(formula, subst)
    }
  } catch (e: Exception) {
    throw java.lang.IllegalArgumentException("substituting $formula with $mapping", e)
  }

fun Solver.renameDeclarationConstraints(
  decl: DeclarationConstraints,
  mapping: Map<String, String>
): DeclarationConstraints {
  fun go(c: NamedConstraint) = NamedConstraint(c.msg, renameObjectVariables(c.formula, mapping))
  return DeclarationConstraints(
    decl.descriptor,
    decl.pre.map(::go),
    decl.post.map(::go),
    decl.doNotLookAtArgumentsWhen.map(::go)
  )
}

fun Solver.substituteDeclarationConstraints(
  decl: DeclarationConstraints,
  mapping: Map<String, ObjectFormula>
): DeclarationConstraints {
  fun go(c: NamedConstraint) = NamedConstraint(c.msg, substituteObjectVariables(c.formula, mapping))
  return DeclarationConstraints(
    decl.descriptor,
    decl.pre.map(::go),
    decl.post.map(::go),
    decl.doNotLookAtArgumentsWhen.map(::go)
  )
}

fun FormulaManager.fieldNames(f: Formula): Set<Pair<String, ObjectFormula>> {
  val names = mutableSetOf<Pair<String, ObjectFormula>>()
  val visitor =
    object : DefaultFormulaVisitor<TraversalProcess>() {
      override fun visitDefault(f: Formula?): TraversalProcess = TraversalProcess.CONTINUE
      override fun visitFunction(
        f: Formula?,
        args: MutableList<Formula>?,
        fn: FunctionDeclaration<*>?
      ): TraversalProcess {
        val secondArg = args?.getOrNull(1) as? ObjectFormula
        if (fn?.name == Solver.FIELD_FUNCTION_NAME && secondArg != null) {
          args.getOrNull(0)?.let { fieldNames ->
            names.addAll(extractVariables(fieldNames).keys.map { Pair(it, secondArg) })
          }
        }
        return TraversalProcess.CONTINUE
      }
    }
  visitRecursively(f, visitor)
  return names
}

fun FormulaManager.fieldNames(f: Iterable<Formula>): Set<Pair<String, ObjectFormula>> =
  f.flatMap { fieldNames(it) }.toSet()

fun FormulaManager.isSingleVariable(f: Formula): Boolean {
  val visitor =
    object : DefaultFormulaVisitor<Boolean>() {
      override fun visitDefault(f: Formula?): Boolean = false
      override fun visitFreeVariable(f: Formula?, name: String?): Boolean = true
    }
  return visit(f, visitor)
}

fun Solver.isFieldCall(f: Formula): Boolean {
  val visitor =
    object : DefaultFormulaVisitor<Boolean>() {
      override fun visitDefault(f: Formula?): Boolean = false
      override fun visitFunction(
        f: Formula?,
        args: MutableList<Formula>?,
        functionDeclaration: FunctionDeclaration<*>?
      ): Boolean = functionDeclaration?.name == Solver.FIELD_FUNCTION_NAME
    }
  return visit(f, visitor)
}

fun FormulaManager.extractSingleVariable(formula: Formula): String? =
  extractVariables(formula).takeIf { it.size == 1 }?.toList()?.getOrNull(0)?.first

// our version of substitute, needed to work around
// a bug with SMTInterpol
fun <T : Formula> Solver.substituteWithFix(pF: T, pFromToMapping: Map<out Formula, Formula>): T =
  transformRecursively(
    pF,
    object : FormulaTransformationVisitor(this) {
      override fun visitFreeVariable(f: Formula, name: String): Formula = replace(f)

      override fun visitFunction(
        f: Formula,
        newArgs: List<Formula>,
        functionDeclaration: FunctionDeclaration<*>
      ): Formula = pFromToMapping[f] ?: makeApplicationSafe(functionDeclaration, newArgs)

      private fun replace(f: Formula): Formula = pFromToMapping[f] ?: f

      private fun makeApplicationSafe(fn: FunctionDeclaration<*>, args: List<Formula>) =
        try {
          makeApplication(fn, args)
        } catch (e: IndexOutOfBoundsException) {
          fn as FunctionDeclarationImpl<*, *>
          val newFn =
            FunctionDeclarationImpl.of(
              fn.name,
              fn.kind,
              fn.argumentTypes.take(2),
              fn.type,
              fn.solverDeclaration
            )
          // this happens sometimes in SMTInterpol,
          // so we try to fix it by using the function with each 2 arguments
          args.reduce { acc, f -> makeApplication(newFn, listOf(acc, f)) }
        }
    }
  )
