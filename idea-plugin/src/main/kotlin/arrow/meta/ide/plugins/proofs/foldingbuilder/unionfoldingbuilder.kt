package arrow.meta.ide.plugins.proofs.foldingbuilder

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document

fun IdeMetaPlugin.addUnionFoldingBuilder(): ExtensionPhase =
  addFoldingBuilder(
    placeHolderText = { node: ASTNode ->
      "String | Int"
    },
    foldRegions = { node: ASTNode, document: Document ->
      val result = arrayListOf<FoldingDescriptor>()
      println("document=$document")
      println("node=$node")
      result.toTypedArray()
    },
    isCollapsedByDefault = { node: ASTNode ->
      true
    }
  )