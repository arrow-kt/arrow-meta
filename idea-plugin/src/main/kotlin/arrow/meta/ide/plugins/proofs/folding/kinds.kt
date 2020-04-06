package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.typeProjections
import arrow.meta.ide.dsl.utils.typeReferences
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.impl.source.SourceTreeToPsiMap
import org.jetbrains.kotlin.psi.KtElement

val IdeMetaPlugin.codeFoldingOnKinds: ExtensionPhase
  get() = addFoldingBuilder(
    foldingBuilder = createFoldingBuilder(
      placeHolderText = { node: ASTNode ->
        SourceTreeToPsiMap.treeElementToPsi(node)?.let { psiElement ->
          (psiElement as KtElement).typeProjections
            .filter { !it.text.startsWith("Kind") }
            .map { it.text }
            .toString()
            .replace("[", "")
            .replaceFirst(", ", "<")
            .replace("]", ">")
        }
      },
      buildFoldRegions = { node: ASTNode, _: Document ->
        SourceTreeToPsiMap.treeElementToPsi(node)?.let { psiElement ->
          (psiElement as KtElement).typeReferences
            .filter { it.text.startsWith("Kind") }
            .map { FoldingDescriptor(it, it.textRange) }
            .toTypedArray()
        } ?: emptyArray()
      },
      isCollapsedByDefault = {
        true
      }))
