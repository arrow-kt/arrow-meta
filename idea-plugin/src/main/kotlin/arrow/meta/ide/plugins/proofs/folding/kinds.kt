package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.getType
import arrow.meta.ide.dsl.utils.typeReferences
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.util.strictParents
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType

val IdeMetaPlugin.codeFoldingOnKinds: ExtensionPhase
  get() = registerFoldingBuilder(
    foldingBuilder(
      placeHolderText = { ktElement: KtElement ->
        ktElement.typeReferences
          .filter { typeReference ->
            typeReference.getType()?.isKindType() == true &&
              typeReference.strictParents().all { psiElement ->
                (psiElement as? KtTypeReference)?.getType()?.isKindType() != true
              }
          }
          .map { it.toKindString() }
          .toString()
          .replace("[", "")
          .replace("]", "")
      },
      foldRegions = { ktElement: KtElement, _: Document ->
        ktElement.typeReferences
          .filter { typeReference ->
            typeReference.getType()?.isKindType() == true &&
              typeReference.strictParents().all { psiElement ->
                (psiElement as? KtTypeReference)?.getType()?.isKindType() != true
              }
          }
          .map { FoldingDescriptor(it, it.textRange) }
      },
      isCollapsedByDefault = {
        true
      }))

private fun KtTypeReference.toKindString(): String =
  (firstChild as? KtUserType)?.typeArgumentList?.children?.toList()?.let { typeProjections ->
    if (typeProjections.isEmpty()) ""
    else {
      val list = typeProjections.subList(1, typeProjections.size)
        .map {
          val typeReference = (it as? KtTypeProjection)?.typeReference
          if (typeReference?.getType()?.isKindType() == true) {
            typeReference.toKindString()
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

private fun KotlinType.isKindType() =
  constructor.declarationDescriptor?.fqNameSafe?.asString() == "arrowx.Kind22"