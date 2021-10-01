package arrow.meta.plugins.analysis.phases.analysis.solver.check.model

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.smt.ObjectFormula

data class CheckData(
  val context: ResolutionContext,
  val returnPoints: ReturnPoints,
  val varInfo: CurrentVarInfo
) {
  fun addReturnPoint(scope: String, variableName: ObjectFormula) =
    CheckData(context, returnPoints.addAndReplaceTopMost(scope, variableName), varInfo)
}
