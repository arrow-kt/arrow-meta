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
  get() = registerFoldingBuilder(
    foldingBuilder(
      placeHolderText = { node: ASTNode ->
        (node.psi as KtElement).typeProjections
          .filter { !it.text.startsWith("Kind") }
          .map { it.text }
          .toString()
          .replace("[", "")
          .replaceFirst(", ", "<")
          .replace("]", ">")
      },
      foldRegions = { node: ASTNode, _: Document ->
        (node.psi as KtElement).typeReferences
          .filter { it.text.startsWith("Kind") }
          .map { FoldingDescriptor(it, it.textRange) }
          .toTypedArray()
      },
      isCollapsedByDefault = {
        true
      }))
