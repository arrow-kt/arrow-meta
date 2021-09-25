package arrow.meta.plugins.liquid.phases.analysis.solver.check.model

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.liquid.smt.ObjectFormula

data class CheckData(
  val context: ResolutionContext,
  val returnPoints: ReturnPoints,
  val varInfo: CurrentVarInfo
) {
  fun addReturnPoint(scope: String, variableName: ObjectFormula) =
    CheckData(context, returnPoints.addAndReplaceTopMost(scope, variableName), varInfo)
}
