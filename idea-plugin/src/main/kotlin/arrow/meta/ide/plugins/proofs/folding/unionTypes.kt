package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.getType
import arrow.meta.ide.dsl.utils.typeProjections
import arrow.meta.ide.dsl.utils.typeReferences
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType

val IdeMetaPlugin.codeFoldingOnUnions: ExtensionPhase
  get() = registerFoldingBuilder(
    foldingBuilder(
      placeHolderText = { node: ASTNode ->
        (node.psi as KtElement).typeProjections
          .filter { it.typeReference?.getType()?.isUnionType() == false }
          .map { it.text }
          .toString()
          .replace("[", "")
          .replace("]", "")
          .replace(", ", " | ")
      },
      foldRegions = { node: ASTNode, _: Document ->
        (node.psi as KtElement).typeReferences
          .filter { it.getType()?.isUnionType() == true }
          .map { FoldingDescriptor(it, it.textRange) }
          .toTypedArray()
      },
      isCollapsedByDefault = {
        true
      }))

private fun KotlinType.isUnionType() =
  constructor.declarationDescriptor?.fqNameSafe?.asString() == "arrow.Union22"
