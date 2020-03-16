package arrow.meta.ide.dsl.resolve

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.ResolveScopeEnlarger
import com.intellij.psi.search.SearchScope

interface ResolveScopeSyntax {
    fun IdeMetaPlugin.resolveScope(
        getAdditionalResolveScope: (file: VirtualFile, project: Project) -> SearchScope
    ): ExtensionPhase =
      extensionProvider(
        ResolveScopeEnlarger.EP_NAME,
        object : ResolveScopeEnlarger() {
            override fun getAdditionalResolveScope(file: VirtualFile, project: Project?): SearchScope? =
                getAdditionalResolveScope(file, project)
        },
        LoadingOrder.FIRST
      )
}