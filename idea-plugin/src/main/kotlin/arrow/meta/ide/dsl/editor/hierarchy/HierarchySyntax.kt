package arrow.meta.ide.dsl.editor.hierarchy

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.ui.toolwindow.ToolWindowSyntax
import arrow.meta.phases.ExtensionPhase
import com.intellij.ide.hierarchy.HierarchyBrowser
import com.intellij.ide.hierarchy.HierarchyProvider
import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.lang.LanguageExtensionPoint
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.PsiElement

interface HierarchySyntax {
  fun IdeMetaPlugin.hierarchyProvider(

  ): ExtensionPhase =
    extensionProvider(
      HierarchyProvider.METHOD_EP_NAME,
      object : LanguageExtensionPoint<HierarchyProvider>() {

         fun getTarget(dataContext: DataContext): PsiElement? {
          TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        fun createHierarchyBrowser(target: PsiElement): HierarchyBrowser {
          TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        fun browserActivated(hierarchyBrowser: HierarchyBrowser) {
          TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
      }
    )

  fun ToolWindowSyntax.hierarchyBrowseBase(

  )
}