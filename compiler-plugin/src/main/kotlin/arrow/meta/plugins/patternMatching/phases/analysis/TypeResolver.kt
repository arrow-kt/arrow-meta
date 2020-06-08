/* TODO:
    This demonstrates getting past type-checking phase but is hard-coded and needs to be cleaned up.
    Right now I'm still just hardcoding a lot to experiment with the concept.
    While we're past the type-checking phase I still need to properly search for the right parameter type.
    Since we're past the type-checking phase we'll have a codegen failure.
    This is a good thing. Codegen is the next piece to figure out.
    In codegen we'll need to transform _ to the property that goes along with the constructor for the class.
    We also need to see about adding @Suppress("UNRESOLVED_REFERENCE") during the desugaring process,
      so that _ doesn't cause an unresolved reference. I'm doing this in the test manually.
 */

package arrow.meta.plugins.patternMatching.phases.analysis

import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo

/* TODO:
    This can go away or be rewritten for codegen at some point.
    I'm leaving it here to demonstrate a naive example
    of what we're trying to do with transforms.
 */
//val KtCallExpression.desugar: String
//  get() =
//    firstChild.nextSibling
//      .firstChild.nextSibling
//      .text.replace("_", "person.firstName")

fun resolvePatternTypes(project: Project, bindingTrace: BindingTrace) {
  val underscoreTypeInfo = bindingTrace.bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO).entries
    .filter { it.value.type == null && it.key.text == "_" }

  val replacementType = constructorFieldTypeInfo(bindingTrace)
  if (replacementType != null) {
    val replacementTypeInfo = KotlinTypeInfo(
      type = replacementType,
      dataFlowInfo = underscoreTypeInfo.first().value.dataFlowInfo,
      jumpOutPossible = underscoreTypeInfo.first().value.jumpOutPossible,
      jumpFlowInfo = underscoreTypeInfo.first().value.jumpFlowInfo
    )
    bindingTrace.record(BindingContext.EXPRESSION_TYPE_INFO, underscoreTypeInfo.first().key, replacementTypeInfo)
  }
}

private fun constructorFieldTypeInfo(bindingTrace: BindingTrace) =
  bindingTrace.bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO)
    .entries
    .find { it.key.textMatches(""""Matt"""") }
    ?.value
    ?.type
