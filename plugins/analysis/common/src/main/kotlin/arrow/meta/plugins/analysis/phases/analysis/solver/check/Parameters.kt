package arrow.meta.plugins.analysis.phases.analysis.solver.check

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.VarInfo
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.search.fieldEqualitiesInvariants
import arrow.meta.plugins.analysis.phases.analysis.solver.search.typeInvariants
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.smt.Solver

data class ParamInfo(
  val name: String,
  val smtName: String,
  val type: Type?,
  val element: Element?,
  val thisFromConstructor: Boolean = false
) {
  companion object {
    public operator fun invoke(
      solver: Solver,
      name: String,
      smtName: String,
      type: Type?,
      element: Element?,
      thisFromConstructor: Boolean = false
    ): ParamInfo = ParamInfo(name, solver.escape(smtName), type, element, thisFromConstructor)
  }
}

/** Record the information about parameters and introduce the corresponding invariants */
internal fun SolverState.initialParameters(
  thisParam: ParamInfo?,
  valueParams: List<ParamInfo>,
  result: ParamInfo?,
  context: ResolutionContext
): List<VarInfo> {
  val things = listOfNotNull(thisParam) + valueParams + listOfNotNull(result)
  return things.mapNotNull { param ->
    param.type?.let { ty ->
      val invariants: (Type, String, ResolutionContext) -> List<NamedConstraint> =
        if (param.thisFromConstructor) this::fieldEqualitiesInvariants else this::typeInvariants
      invariants(ty, param.smtName, context).forEach { addConstraint(it, context) }
    }
    param.element?.let { element -> VarInfo.unsafeCreate(param.name, param.smtName, element) }
  }
}
