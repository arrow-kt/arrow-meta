package arrow.meta.plugins.liquid.smt

import arrow.meta.plugins.liquid.phases.analysis.solver.DeclarationConstraints
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FormulaManager
import org.sosy_lab.java_smt.api.FunctionDeclaration
import org.sosy_lab.java_smt.api.visitors.DefaultFormulaVisitor
import org.sosy_lab.java_smt.api.visitors.TraversalProcess

fun <T : Formula> Solver.substituteVariable(formula: T, mapping: Map<String, Formula>): T =
  formulae {
    val subst = mapping.mapKeys { entry ->
      makeVariable(getFormulaType(entry.value), entry.key)
    }
    substitute(formula, subst)
  }

fun <T : Formula> Solver.renameObjectVariables(formula: T, mapping: Map<String, String>): T =
  formulae {
    val subst = mapping.map { (k, v) ->
      Pair(makeObjectVariable(k), makeObjectVariable(v))
    }.toMap()
    substitute(formula, subst)
  }

fun Solver.renameDeclarationConstraints(
  decl: DeclarationConstraints,
  mapping: Map<String, String>
): DeclarationConstraints =
  DeclarationConstraints(
    decl.descriptor,
    decl.pre.map { renameObjectVariables(it, mapping) },
    decl.post.map { renameObjectVariables(it, mapping) }
  )

fun FormulaManager.fieldNames(f: Formula): Set<Pair<String, ObjectFormula>> {
  val names = mutableSetOf<Pair<String, ObjectFormula>>()
  val visitor = object : DefaultFormulaVisitor<TraversalProcess>() {
    override fun visitDefault(f: Formula?): TraversalProcess = TraversalProcess.CONTINUE
    override fun visitFunction(f: Formula?, args: MutableList<Formula>?, fn: FunctionDeclaration<*>?): TraversalProcess {
      val secondArg = args?.getOrNull(1) as? ObjectFormula
      if (fn?.name == "field" && secondArg != null) {
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
