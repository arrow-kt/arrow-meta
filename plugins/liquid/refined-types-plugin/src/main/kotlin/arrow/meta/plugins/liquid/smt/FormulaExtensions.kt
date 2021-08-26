package arrow.meta.plugins.liquid.smt

import arrow.meta.plugins.liquid.phases.analysis.solver.DeclarationConstraints
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FormulaManager
import org.sosy_lab.java_smt.api.FunctionDeclaration
import org.sosy_lab.java_smt.api.visitors.DefaultFormulaVisitor
import org.sosy_lab.java_smt.api.visitors.FormulaTransformationVisitor
import org.sosy_lab.java_smt.api.visitors.TraversalProcess

fun <T : Formula> Solver.substituteFormulae(formula: T, subst: Map<Formula, Formula>): T =
  formulae {
    this.substitute(formula, subst)
  }

fun <T : Formula> Solver.substituteVariable(formula: T, mapping: Map<String, Formula>): T =
  formulae {
    val subst = mapping.mapKeys { entry ->
      makeVariable(getFormulaType(entry.value), entry.key)
    }
    substituteFormulae(formula, subst)
  }

fun <T : Formula> Solver.rename(formula: T, mapping: Map<String, String>): T =
  formulae {
    val renamer = object : FormulaTransformationVisitor(this) {
      override fun visitFreeVariable(f: Formula, name: String): Formula {
        return when {
          mapping.containsKey(name) -> makeVariable(getFormulaType(f), mapping[name])
          else -> f
        }
      }
    }
    transformRecursively(formula, renamer)
  }

fun Solver.renameDeclarationConstraints(
  decl: DeclarationConstraints,
  mapping: Map<String, String>
): DeclarationConstraints =
  DeclarationConstraints(
    decl.descriptor,
    decl.pre.map { rename(it, mapping) },
    decl.post.map { rename(it, mapping) }
  )

fun FormulaManager.fieldNames(f: Formula): Set<String> {
  val names = mutableSetOf<String>()
  val visitor = object : DefaultFormulaVisitor<TraversalProcess>() {
    override fun visitDefault(f: Formula?): TraversalProcess = TraversalProcess.CONTINUE
    override fun visitFunction(f: Formula?, args: MutableList<Formula>?, fn: FunctionDeclaration<*>?): TraversalProcess {
      if (fn?.name == "field") {
        args?.get(0)?.let {
          names.addAll(extractVariables(it).keys)
        }
      }
      return TraversalProcess.CONTINUE
    }
  }
  visitRecursively(f, visitor)
  return names
}

fun FormulaManager.fieldNames(f: Iterable<Formula>): Set<String> =
  f.flatMap { fieldNames(it) }.toSet()

fun FormulaManager.isSingleVariable(f: Formula): Boolean {
  val visitor = object : DefaultFormulaVisitor<Boolean>() {
    override fun visitDefault(f: Formula?): Boolean = false
    override fun visitFreeVariable(f: Formula?, name: String?): Boolean = true
  }
  return visit(f, visitor)
}

fun Solver.isFieldCall(f: Formula): Boolean {
  val visitor = object : DefaultFormulaVisitor<Boolean>() {
    override fun visitDefault(f: Formula?): Boolean = false
    override fun visitFunction(f: Formula?, args: MutableList<Formula>?, functionDeclaration: FunctionDeclaration<*>?): Boolean =
      functionDeclaration?.name == Solver.FIELD_FUNCTION_NAME
  }
  return visit(f, visitor)
}
