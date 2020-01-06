package arrow.meta.ide.dsl.editor.fileTemplates

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase

interface FileTemplateSyntax {
  fun IdeMetaPlugin.addFileTemplate(name: String): ExtensionPhase =
    TODO(" Add ways to add new FileTemplates with costume preset Text use com.intellij.ide.fileTemplates.InternalTemplateBean with [name] and [subject]")
}