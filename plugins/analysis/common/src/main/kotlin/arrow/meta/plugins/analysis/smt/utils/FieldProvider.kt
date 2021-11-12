package arrow.meta.plugins.analysis.smt.utils

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.overriddenDescriptors
import arrow.meta.plugins.analysis.phases.analysis.solver.state.asField
import arrow.meta.plugins.analysis.smt.Solver
import org.sosy_lab.java_smt.api.ProverEnvironment

class FieldProvider(
  private val solver: Solver,
  private val prover: ProverEnvironment,
  private val basicFields: MutableMap<String, Long>,
  private var current: Long
) {

  fun introduce(descriptor: DeclarationDescriptor): Long {
    val name = descriptor.fqNameSafe.asField

    // shortcut, if it's there, just return it
    if (basicFields.containsKey(name)) return basicName(name)

    val overridden = descriptor.overriddenDescriptors().orEmpty()
    val topOverriden = overridden.filter { it.overriddenDescriptors().isNullOrEmpty() }
    return when (topOverriden.size) {
      0 -> {
        // it's basic!
        // it's OK to save since otherwise it would have
        // been found in the initial check
        save(name)
        basicName(name)
      }
      1 -> {
        // first introduce all of the upper descriptors
        overridden.reversed().forEach { introduce(it) }
        // now the map contains the basic one
        // assert that they are equal
        derivedName(name, topOverriden[0].fqNameSafe.asField)
      }
      else -> {
        // weird case:
        // we have more than one "top most" elements
        // in this case, we just override *none*
        // and take it as basic
        save(name)
        basicName(name)
      }
    }
  }

  private fun basicName(name: String): Long {
    val index = basicFields.getValue(name)
    val constraint = solver.ints { equal(makeVariable(name), makeNumber(index)) }
    prover.addConstraint(constraint)
    return index
  }

  private fun derivedName(name: String, topName: String): Long {
    val index = basicFields.getValue(topName)
    val constraint = solver.ints { equal(makeVariable(name), makeVariable(topName)) }
    prover.addConstraint(constraint)
    return index
  }

  private fun save(name: String): Long {
    current += 1
    basicFields[name] = current
    return current
  }

  companion object {
    operator fun invoke(solver: Solver, prover: ProverEnvironment): FieldProvider =
      FieldProvider(solver, prover, mutableMapOf(), 0)
  }
}
