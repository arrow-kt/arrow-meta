package arrow.meta.plugins.liquid.phases.analysis.solver.check.model

import org.jetbrains.kotlin.psi.KtExpression

/**
 * Describes the characteristics of a call to special control flow functions
 * namely [also], [apply], [let], [run], [with]
 * https://kotlinlang.org/docs/scope-functions.html#function-selection
 */
data class ControlFlowFn(
  val target: KtExpression?,
  val body: KtExpression,
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
