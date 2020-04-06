package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.typeProjections
import arrow.meta.ide.dsl.utils.typeReferences
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import org.jetbrains.kotlin.psi.KtElement

val IdeMetaPlugin.codeFoldingOnTuples: ExtensionPhase
  get() = registerFoldingBuilder(
    foldingBuilder(
      placeHolderText = { node: ASTNode ->
        (node.psi as KtElement).typeProjections
          .filter { !it.text.startsWith("Tuple") }
          .map { it.text }
          .toString()
          .replace("[", "(")
          .replace("]", ")")
      },
      foldRegions = { node: ASTNode, _: Document ->
        (node.psi as KtElement).typeReferences
          .filter { it.text.startsWith("Tuple") }
          .map { FoldingDescriptor(it, it.textRange) }
          .toTypedArray()
      },
      isCollapsedByDefault = {
        true
      }))
