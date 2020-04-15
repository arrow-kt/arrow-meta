package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.psi.util.strictParents
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.codeFoldingOnUnions: ExtensionPhase
  get() = addFoldingBuilder(
    match = ::parentTypeMatches,
    hint = KtTypeReference::foldString
  )

private fun parentTypeMatches(typeReference: KtTypeReference): Boolean =
  typeReference.getType().isTypeMatching() &&
    typeReference.strictParents().all { psiElement ->
      !psiElement.safeAs<KtTypeReference>()?.getType().isTypeMatching()
    }

private fun KotlinType?.isTypeMatching() =
  this?.constructor?.declarationDescriptor?.fqNameSafe?.asString() == "arrow.Union22"

private fun KtTypeReference.foldString(): String =
  firstChild.safeAs<KtUserType>()?.typeArgumentList?.children.orEmpty().joinToString(
    separator = " | ",
    transform = {
      it.safeAs<KtTypeProjection>()?.typeReference?.let { ktTypeReference ->
        if (ktTypeReference.getType().isTypeMatching()) {
          ktTypeReference.foldString()
        } else {
          ktTypeReference.text
        }
      } ?: it.text
    })
