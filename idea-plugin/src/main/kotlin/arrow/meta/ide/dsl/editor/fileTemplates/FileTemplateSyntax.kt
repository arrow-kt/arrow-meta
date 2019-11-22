package arrow.meta.ide.dsl.editor.fileTemplates

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.ide.fileTemplates.InternalTemplateBean
import com.intellij.openapi.extensions.LoadingOrder

interface FileTemplateSyntax {
  // TODO: Add ways to add new FileTemplates with costume preset Text
  fun IdeMetaPlugin.addFileTemplate(name: String): ExtensionPhase =
    extensionProvider(
      InternalTemplateBean.EP_NAME,
      object : InternalTemplateBean() {
        val name: String = name
        // val subject: String = subject
      },
      LoadingOrder.FIRST
    )
}