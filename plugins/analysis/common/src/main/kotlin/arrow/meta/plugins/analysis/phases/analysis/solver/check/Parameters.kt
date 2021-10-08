package arrow.meta.plugins.analysis.phases.analysis.solver.check

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.CheckData
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.typeInvariants
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState

data class ParamInfo(val name: String, val smtName: String, val type: Type?, val element: Element?)

/**
 * Record the information about parameters
 * and introduce the corresponding invariants
 */
internal fun SolverState.initializeParameters(
  thisParam: ParamInfo?,
  valueParams: List<ParamInfo>,
  result: ParamInfo?,
  data: CheckData
) {
  val things = listOfNotNull(thisParam) + valueParams + listOfNotNull(result)
  things.forEach { param ->
    param.element?.let { element ->
      data.varInfo.add(param.name, param.smtName, element)
    }
    param.type?.let { ty ->
      typeInvariants(data.context, ty, param.smtName).forEach { addConstraint(it) }
    }
  }
}
