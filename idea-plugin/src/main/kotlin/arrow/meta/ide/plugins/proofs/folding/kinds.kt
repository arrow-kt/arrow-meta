package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.getType
import arrow.meta.phases.ExtensionPhase
import com.intellij.psi.util.strictParents
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.codeFoldingOnKinds: ExtensionPhase
  get() = addFoldingBuilder(
    match = KtTypeReference::kindTypeMatches,
    hint = KtTypeReference::foldString
  )

fun KtTypeReference.kindTypeMatches(): Boolean =
  getType().isTypeMatching() &&
    strictParents().all { psiElement ->
      !psiElement.safeAs<KtTypeReference>()?.getType().isTypeMatching()
    }

private fun KotlinType?.isTypeMatching() =
  this?.constructor?.declarationDescriptor?.fqNameSafe?.asString() == "arrowx.Kind22"

private fun KtTypeReference.foldString(): String {
  val children = firstChild.safeAs<KtUserType>()?.typeArgumentList?.children.orEmpty().toList()
  return if (children.isEmpty()) ""
  else children[0].text + children.subList(1, children.size).joinToString(
    prefix = "<",
    postfix = ">",
    transform = {
      it.safeAs<KtTypeProjection>()?.typeReference?.let { ktTypeReference ->
        if (ktTypeReference.getType().isTypeMatching()) {
          ktTypeReference.foldString()
        } else {
          ktTypeReference.text
        }
      } ?: it.text
    })
}
