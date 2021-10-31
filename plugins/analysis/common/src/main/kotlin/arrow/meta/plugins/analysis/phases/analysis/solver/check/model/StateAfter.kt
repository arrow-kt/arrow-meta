package arrow.meta.plugins.analysis.phases.analysis.solver.check.model

/**
 * Describes the state of the analysis after a check:
 * - whether the computation should end and how
 * - the new information about variables and branches
 */
data class StateAfter(val returnInfo: Return, val data: CheckData) {
  fun withReturn(newReturnInfo: Return): StateAfter = this.copy(returnInfo = newReturnInfo)

  fun withData(oldData: CheckData): StateAfter = this.copy(data = oldData)
}

fun CheckData.noReturn() = StateAfter(NoReturn, this)
