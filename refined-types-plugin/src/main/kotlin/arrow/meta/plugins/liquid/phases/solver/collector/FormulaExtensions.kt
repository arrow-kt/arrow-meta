package arrow.meta.plugins.liquid.phases.solver.collector

import arrow.meta.plugins.liquid.phases.solver.Solver
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FormulaManager
import org.sosy_lab.java_smt.api.visitors.FormulaTransformationVisitor

fun <T: Formula> Solver.substituteFormulae(formula: T, subst: Map<Formula, Formula>): T =
  formulae {
    this.substitute(formula, subst)
  }

fun <T: Formula> Solver.substituteVariable(formula: T, mapping: Map<String, Formula>): T =
  formulae {
    val subst = mapping.mapKeys { entry ->
      makeVariable(getFormulaType(entry.value), entry.key)
    }
    substituteFormulae(formula, subst)
  }

fun <T: Formula> Solver.rename(formula: T, mapping: Map<String, String>): T =
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
  mapping: Map<String, String>): DeclarationConstraints =
  DeclarationConstraints(
    decl.descriptor,
    decl.element,
    decl.pre.map { rename(it, mapping) },
    decl.post.map { rename(it, mapping) }
  )