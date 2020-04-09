package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.getType
import arrow.meta.ide.dsl.utils.typeReferences
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.psi.util.strictParents
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType

val IdeMetaPlugin.codeFoldingOnKinds: ExtensionPhase
  get() = addFoldingBuilder(
    isTypeMatching = ::parentTypeMatches,
    toFoldString = ::foldString
  )

private fun parentTypeMatches(typeReference: KtTypeReference): Boolean =
  typeReference.getType()?.isTypeMatching() == true &&
    typeReference.strictParents().all { psiElement ->
      (psiElement as? KtTypeReference)?.getType()?.isTypeMatching() != true
    }

private fun KotlinType.isTypeMatching() =
  constructor.declarationDescriptor?.fqNameSafe?.asString() == "arrowx.Kind22"

private fun foldString(typeReferenceParent: KtTypeReference): String =
  (typeReferenceParent.firstChild as? KtUserType)?.typeArgumentList?.children?.toList()?.let { typeProjections ->
    if (typeProjections.isEmpty()) ""
    else {
      val list = typeProjections.subList(1, typeProjections.size)
        .map {
          val typeReference = (it as? KtTypeProjection)?.typeReference
          if (typeReference?.getType()?.isTypeMatching() == true) {
            foldString(typeReference)
          } else {
            it.text
          }
        }
      typeProjections[0].text + list
        .toString()
        .replace("[", "<")
        .replace("]", ">")
    }
  } ?: ""

