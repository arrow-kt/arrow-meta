package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.psi.PsiElement
import com.intellij.psi.util.strictParents
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.codeFoldingOnTuples: ExtensionPhase
  get() = addFoldingBuilder(
    match = KtTypeReference::tupleTypeMatches,
    hint = KtTypeReference::foldString
  )

fun KtTypeReference.tupleTypeMatches(): Boolean =
  getType().isTypeMatching() &&
    strictParents().all { psiElement ->
      !psiElement.safeAs<KtTypeReference>()?.getType().isTypeMatching()
    }

private val tuplesFqName = FqName("arrow.tuples")

private fun KotlinType?.isTypeMatching() =
  this?.constructor?.declarationDescriptor?.fqNameSafe?.parent() == tuplesFqName

private fun KtTypeReference.foldString(): String =
  firstChild.safeAs<KtUserType>()?.typeArgumentList?.let { ktTypeArgList: KtTypeArgumentList ->
    ktTypeArgList.children.joinToString(
      separator = ",",
      transform = { psi: PsiElement ->
        psi.safeAs<KtTypeProjection>()?.typeReference?.let { ktTypeReference ->
          if (ktTypeReference.getType().isTypeMatching()) {
            ktTypeReference.foldString()
          } else {
            ktTypeReference.text
          }
        } ?: psi.text
      })
      .split(",")
      .joinToString(
        prefix = "(",
        postfix = ")",
        separator = ", ",
        limit = 5,
        truncated = "..."
      )
  } ?: text
