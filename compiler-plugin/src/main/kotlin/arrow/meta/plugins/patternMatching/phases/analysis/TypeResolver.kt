package arrow.meta.plugins.patternMatching.phases.analysis

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

import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo

fun BindingTrace.resolveTypesFor(resolution: (BindingTrace) -> Unit) =
  resolution(this)

fun wildcards(bindingTrace: BindingTrace) =
  bindingTrace.wildcardTypeInfoEntries.forEach { entry ->
    constructorArgTypeInfo(bindingTrace).let { replacementType ->
      bindingTrace.recordType(entry.key, replacementType)
    }
  }

val BindingTrace.wildcardTypeInfoEntries: List<MutableMap.MutableEntry<KtExpression, KotlinTypeInfo>>
  get() = bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO).entries
    .filter { it.value.type == null && it.key.text == "_" }

private fun constructorArgTypeInfo(bindingTrace: BindingTrace) =
  bindingTrace.bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO)
    .entries
    .find { it.key.textMatches(""""Matt"""") }
    ?.value
    ?.type
