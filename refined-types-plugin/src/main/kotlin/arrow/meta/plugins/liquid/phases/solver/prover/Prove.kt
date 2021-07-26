package arrow.meta.plugins.liquid.phases.solver.prover

import arrow.meta.plugins.liquid.phases.solver.collector.DeclarationConstraints
import arrow.meta.plugins.liquid.phases.solver.collector.argsFormulae
import arrow.meta.plugins.liquid.phases.solver.state.SolverState
import arrow.meta.plugins.liquid.phases.solver.state.constraintsFromSolverState
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.types.typeUtil.isInt
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.NumeralFormula

fun SolverState.resolvedCallProveFormula(resolvedCall: ResolvedCall<out CallableDescriptor>, bindingContext: BindingContext): BooleanFormula? =
  solver.run {
    val argsFormulae = argsFormulae(resolvedCall, bindingContext)
    val result = argsFormulae.fold(makeTrue()) { acc, (type, name, formula) ->
      if (type.isInt() && formula is NumeralFormula.IntegerFormula) {
        ints {
          and(acc, equal(makeVariable(name), formula))
        }
      } else acc
    }
    val declarationConstraints: DeclarationConstraints? = constraintsFromSolverState(resolvedCall)
    val check = if (declarationConstraints != null) {
      and(listOf(result) + declarationConstraints.pre.filterIsInstance<BooleanFormula>())
    } else result
    return check
  }