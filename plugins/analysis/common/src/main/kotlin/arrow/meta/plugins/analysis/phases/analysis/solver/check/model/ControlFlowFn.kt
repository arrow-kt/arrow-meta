package arrow.meta.plugins.analysis.phases.analysis.solver.check.model

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression

/**
 * Describes the characteristics of a call to special control flow functions
 * namely [also], [apply], [let], [run], [with]
 * https://kotlinlang.org/docs/scope-functions.html#function-selection
 */
data class ControlFlowFn(
  val target: Expression?,
  val body: Expression,
  val argumentName: String,
  val returnBehavior: ReturnBehavior
) {
  /**
   * Describes whether functions return their argument
   * or whatever is done in a block
   */
  enum class ReturnBehavior {
    /**
     * Return what was given as argument, usually after applying a function T -> Unit
     */
    RETURNS_ARGUMENT,

    /**
     * Return whatever the enclosing block returns
     */
    RETURNS_BLOCK_RESULT
  }
}
