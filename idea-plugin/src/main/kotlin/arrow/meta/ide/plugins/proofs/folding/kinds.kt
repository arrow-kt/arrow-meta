package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.dsl.utils.typeProjections
import arrow.meta.ide.dsl.utils.typeReferences
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.SourceTreeToPsiMap
import org.jetbrains.kotlin.psi.KtElement

class KindFoldingBuilder : FoldingBuilderEx() {
  override fun getPlaceholderText(node: ASTNode): String? =
    SourceTreeToPsiMap.treeElementToPsi(node)?.let { psiElement ->
      (psiElement as KtElement).typeProjections
        .filter { !it.text.startsWith("Kind") }
        .map { it.text }
        .toString()
        .replace("[", "")
        .replaceFirst(", ", "<")
        .replace("]", ">")
    }

  override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> =
    (root as KtElement).typeReferences
      .filter { it.text.startsWith("Kind") }
      .map { FoldingDescriptor(it, it.textRange) }
      .toTypedArray()

  override fun isCollapsedByDefault(node: ASTNode): Boolean =
    true
}

// prototype
/*
val IdeMetaPlugin.codeFoldingOnKinds: ExtensionPhase
  get() = addFoldingBuilder(
    lang = KotlinLanguage.INSTANCE,
    foldRegions = { root: PsiElement, document: Document, quick: Boolean ->
      root.safeAs<KtElement>()?.typeReferences
        ?.filter { it.text.startsWith("Kind") }
        ?.map { FoldingDescriptor(it, it.textRange) }
        .orEmpty()
    },
    isCollapsedByDefault = { true },
    placeHolderText = { node: ASTNode ->
      SourceTreeToPsiMap.treeElementToPsi(node)?.let { psiElement ->
        psiElement.safeAs<KtElement>()?.typeProjections
          ?.filter { !it.text.startsWith("Kind") }
          ?.map { it.text }
          ?.toString()
          ?.replace("[", "")
          ?.replaceFirst(", ", "<")
          ?.replace("]", ">")
      }
    }
  )*/
