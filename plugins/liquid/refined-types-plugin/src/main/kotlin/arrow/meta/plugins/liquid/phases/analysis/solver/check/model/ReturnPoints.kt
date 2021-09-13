package arrow.meta.plugins.liquid.phases.analysis.solver.check.model

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamed

/**
 * Maps return points to the SMT variables representing that place.
 */
data class ReturnPoints(
  val topMostReturnPointVariableName: Pair<String?, String>,
  val namedReturnPointVariableNames: Map<String, String>
) {

  fun addAndReplaceTopMost(newScopeName: String, newVariableName: String) =
    this
      .replaceTopMost(newScopeName, newVariableName)
      .add(newScopeName, newVariableName)

  private fun replaceTopMost(newScopeName: String, newVariableName: String) =
    ReturnPoints(Pair(newScopeName, newVariableName), namedReturnPointVariableNames)

  private fun add(returnPoint: String, variableName: String) =
    ReturnPoints(
      topMostReturnPointVariableName,
      namedReturnPointVariableNames + (returnPoint to variableName)
    )

  companion object {
    private fun new(scope: String?, variableName: String): ReturnPoints =
      when (scope) {
        null -> ReturnPoints(Pair(scope, variableName), emptyMap())
        else -> ReturnPoints(Pair(scope, variableName), mapOf(scope to variableName))
      }

    fun new(scope: KtElement, variableName: String): ReturnPoints =
      when (scope) {
        is KtNamed -> new(scope.nameAsName?.asString(), variableName)
        else -> new(null, variableName)
      }
  }
}
