package arrow.meta.plugins.liquid.phases.analysis.solver.check.model

import arrow.meta.plugins.liquid.smt.ObjectFormula
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

data class CheckData(
  val context: DeclarationCheckerContext,
  val returnPoints: ReturnPoints,
  val varInfo: CurrentVarInfo
) {
  fun addReturnPoint(scope: String, variableName: ObjectFormula) =
    CheckData(context, returnPoints.addAndReplaceTopMost(scope, variableName), varInfo)
}
