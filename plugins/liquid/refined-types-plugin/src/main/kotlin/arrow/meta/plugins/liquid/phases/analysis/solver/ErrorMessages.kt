package arrow.meta.plugins.liquid.phases.analysis.solver

import arrow.meta.plugins.liquid.smt.utils.KotlinPrinter
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Model

internal fun KotlinPrinter.errorUnsatInvariants(
  expression: KtElement,
  constraint: NamedConstraint,
  model: Model
): String =
  "`${expression.text}` invariants are not satisfied: ${constraint.formula.dumpKotlinLike()} counter examples: ${model.template()}"

internal fun KotlinPrinter.errorInconsistentCallPost(unsatCore: List<BooleanFormula>): String =
  "unreachable code due to post-conditions: ${unsatCore.joinToString { it.dumpKotlinLike() }}"

internal fun KotlinPrinter.errorInconsistentInvariants(it: List<BooleanFormula>): String =
  "invariants are inconsistent: ${it.joinToString { it.dumpKotlinLike() }}"

internal fun KotlinPrinter.errorInconsistentConditions(unsatCore: List<BooleanFormula>): String =
  "unreachable code due to conflicting conditions: ${unsatCore.joinToString { it.dumpKotlinLike() }}"

internal fun KotlinPrinter.errorInconsistentBodyPre(
  declaration: KtDeclaration,
  unsatCore: List<BooleanFormula>
): String = "${declaration.name} has inconsistent pre-conditions: ${unsatCore.joinToString { it.dumpKotlinLike() }}"

internal fun KotlinPrinter.errorUnsatCallPre(
  callPreCondition: NamedConstraint,
  resolvedCall: ResolvedCall<out CallableDescriptor>,
  model: Model
): String =
  "call to `${resolvedCall.call.callElement.text}` resulting in `${model.template()}` fails to satisfy pre-conditions: ${callPreCondition.formula.dumpKotlinLike()}"

internal fun Model.template(): String = filter { it.argumentsInterpretation.isNotEmpty() }.joinToString { valueAssignment ->
  valueAssignment.argumentsInterpretation.joinToString { it.toString() }
}

internal fun KotlinPrinter.errorUnsatBodyPost(
  declaration: KtDeclaration,
  postCondition: NamedConstraint
): String = "declaration `${declaration.name}` fails to satisfy the post-condition: ${postCondition.formula.dumpKotlinLike()}"
