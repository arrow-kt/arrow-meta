package arrow.meta.plugins.analysis.phases.analysis.solver.check

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.VarInfo
import arrow.meta.plugins.analysis.phases.analysis.solver.search.typeInvariants
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState

data class ParamInfo(val name: String, val smtName: String, val type: Type?, val element: Element?)

/** Record the information about parameters and introduce the corresponding invariants */
internal fun SolverState.initialParameters(
  context: ResolutionContext,
  thisParam: ParamInfo?,
  valueParams: List<ParamInfo>,
  result: ParamInfo?
): List<VarInfo> {
  val things = listOfNotNull(thisParam) + valueParams + listOfNotNull(result)
  return things.mapNotNull { param ->
    param.type?.let { ty ->
      typeInvariants(context, ty, param.smtName).forEach { addConstraint(it) }
    }
    param.element?.let { element -> VarInfo(param.name, param.smtName, element) }
  }
}
