package arrow.meta.ide.dsl.resolve

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.ResolveScopeProvider
import com.intellij.psi.search.GlobalSearchScope

interface ResolveProviderSyntax {
    fun IdeMetaPlugin.addResolveScopeProvider(
        getResolveScope: (file: VirtualFile, project: Project) -> GlobalSearchScope?
    ): ExtensionPhase =
            extensionProvider(
                    ResolveScopeProvider.EP_NAME,
                    object : ResolveScopeProvider(){
                        override fun getResolveScope(file: VirtualFile, project: Project?): GlobalSearchScope? =
                                getResolveScope(file, project)
                    }
            )

    fun IdeMetaPlugin.globalSearchScope(
        containsFile: (file: VirtualFile) -> Boolean,
        isSearchInModuleContent: (module: Module) -> Boolean,
        isSearchInLibraries: () -> Boolean
    ): GlobalSearchScope =
            object :  GlobalSearchScope(){
                override fun contains(file: VirtualFile): Boolean = containsFile(file)

                override fun isSearchInModuleContent(aModule: Module): Boolean = isSearchInModuleContent(aModule)

                override fun isSearchInLibraries(): Boolean = isSearchInLibraries()

            }
}
