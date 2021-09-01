package arrow.meta.plugins.liquid.phases.analysis.solver.errors

import arrow.meta.plugins.liquid.phases.analysis.solver.NamedConstraint
import arrow.meta.plugins.liquid.smt.utils.KotlinPrinter
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Model

/**
 * # Errors in Arrow Analysis
 *
 * There are broadly three kinds of errors that may arise from Arrow Analysis.
 * This files gives an overview of the information tracked in each case,
 * which shall form the basis for top-quality error messages.
 */
object ErrorMessages {

  /**
   * These errors arise from `pre`, `post`, or `invariant` blocks which cannot be
   * translated into SMT formulae.
   *
   * For example, we cannot translate method calls to SMT:
   *
   * ```kotlin
   * fun f(xs: List[String]): String {
   *   pre({ !xs.get(0).isEmpty() }) { ... }
   *   ...
   *   }
   * ```
   *
   * The Kotlin compiler won't catch these errors in its own analysis phase
   * (like it would do with a type error), since this is perfectly good Kotlin code.
   * However, it seems desirable for the programmer to know that a particular language
   * feature cannot be used in these blocks.
   */
  object Parsing {
    internal fun errorParsingPredicate(predicateArg: KtExpression?): String =
      "Could not parse predicate: ${predicateArg?.text}"
  }

  /**
   * These errors embody the idea that "something should have been true, but it is not."
   * There are three cases in which this may arise.
   *
   * ### Information available
   *
   * See [arrow.meta.plugins.liquid.phases.analysis.solver.checkImplicationOf] for the code which produces the errors.
   *
   * - The _one_ constraint name and Boolean formula which could not be satisfied.
   * - A _counter-example_ (also called a _model_), which is an assignment of values to variableswhich show a specific instance in which the constraint is false.
   * - In the `f` function above in the `UnsatBodyPost` epigraph, one such counter-example is `x == 0`, since in that case `0 + 0 > 1` is false.
   * - By looking at the values of the model for the arguments, we can derive one specific trace for which the function fails.
   *
   */
  object Unsatisfiability {

    /**
     * `UnsatCallPre` (attached to the argument):
     * The required pre-conditions for a (method, property, function) call
     * are not satisfied.
     *
     * For example:
     *
     * ```kotlin
     *   val wrong = 1 / 0  // does not satisfy '0 != 0' in Int.div law
     * ```
     */
    internal fun KotlinPrinter.unsatCallPre(
      callPreCondition: NamedConstraint,
      resolvedCall: ResolvedCall<out CallableDescriptor>,
      model: Model
    ): String =
      "call to `${resolvedCall.call.callElement.text}` resulting in `${model.template()}` fails to satisfy pre-conditions: ${callPreCondition.formula.dumpKotlinLike()}"

    /**
     * (attached to the return value)
     *
     * the post-condition declared in a function body is not true.
     *
     * For example:
     *
     * ```kotlin
     * fun f(x: Int): Int {
     *   pre(x >= 0) { "non-negative" }
     *   val r = x + x
     *   return r.post({ it > 1 }) { "greater than 1" }
     *   // does not satisfy 'x + x > 1'
     * }
     * ```
     */
    internal fun KotlinPrinter.unsatBodyPost(
      declaration: KtDeclaration,
      postCondition: NamedConstraint
    ): String =
      "declaration `${declaration.name}` fails to satisfy the post-condition: ${postCondition.formula.dumpKotlinLike()}"

    /**
     * (attached to the new value): the invariant declared for a mutable variable
     * is not satisfied by the new value.
     *
     * For example:
     *
     *  ```kotlin
     *  fun g(): Int {
     *    var r = 1.invariant({ it > 0 }) { "it > 0" }
     *    r = 0 // does not satisfy '0 > 0'
     *    ...
     *  }
     *  ```
     *
     */
    internal fun KotlinPrinter.unsatInvariants(
      expression: KtElement,
      constraint: NamedConstraint,
      model: Model
    ): String =
      "`${expression.text}` invariants are not satisfied: ${constraint.formula.dumpKotlinLike()} counter examples: ${model.template()}"
  }

  /**
   * These errors embody the idea that "there's no possible way in which
   * we may end up in this situation."
   * Usually this means that the code is somehow unreachable.
   * There are four cases in which this may arise.
   */
  object Inconsistency {

    /**
     *  The set of pre-conditions given to the function leaves no possible way
     *  to call the function.
     *
     *  For example:
     *
     *  ```kotlin
     *  fun h(x: Int): Int {
     *    pre({ x > 0 }) { "greater than 0" }
     *    pre({ x < 0 }) { "smaller than 0" }
     *    // no value can be both < 0 and > 0
     *    ...
     *  }
     *  ```
     */
    internal fun KotlinPrinter.inconsistentBodyPre(
      declaration: KtDeclaration,
      unsatCore: List<BooleanFormula>
    ): String = "${declaration.name} has inconsistent pre-conditions: ${unsatCore.joinToString { it.dumpKotlinLike() }}"

    /**
     * (attached to a particular condition):
     *  the body of a branch is never executed, because the condition it hangs upon
     *  conflicts with the rest of the information about the function.
     *
     *  For example, if a condition goes against a pre-condition:
     *
     *  ```kotlin
     *   fun i(x: Int): Int {
     *     pre({ x > 0 }) { "greater than 0" }
     *     if (x == 0) {
     *       // 'x > 0' and 'x == 0' are incompatible
     *       // so this branch is unreachable
     *     } else {
     *       ...
     *     }
     *   }
     *   ```
     */
    internal fun KotlinPrinter.inconsistentConditions(unsatCore: List<BooleanFormula>): String =
      "unreachable code due to conflicting conditions: ${unsatCore.joinToString { it.dumpKotlinLike() }}"

    /**
     * (attached to the function call): the post-conditions gathered after calling
     * a function imply that this function could not be called at all.
     * _This is really uncommon in practice_.
     */
    internal fun KotlinPrinter.inconsistentCallPost(unsatCore: List<BooleanFormula>): String =
      "unreachable code due to post-conditions: ${unsatCore.joinToString { it.dumpKotlinLike() }}"

    /**
     * (attached to a local declaration):
     * there is no way in which the invariant attached to a declaration may be satisfied.
     *
     * For example:
     *
     *  ```kotlin
     *  fun j(x: Int): Int {
     *    pre({ x > 0 }) { "greater than 0" }
     *    var v = 3 invariant { v > x && v < 0 }
     *  }
     *  ```
     */
    internal fun KotlinPrinter.inconsistentInvariants(it: List<BooleanFormula>): String =
      "invariants are inconsistent: ${it.joinToString { it.dumpKotlinLike() }}"
  }

  internal fun Model.template(): String =
    filter { it.argumentsInterpretation.isNotEmpty() }.joinToString { valueAssignment ->
      valueAssignment.argumentsInterpretation.joinToString { it.toString() }
    }
}
