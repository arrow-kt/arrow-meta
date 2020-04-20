package arrow.meta.ide.dsl.resolve

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.ResolveScopeProvider
import com.intellij.psi.search.GlobalSearchScope

/**
 * [GlobalSearchScope] search for {@code VirtualFile}s in global scope.
 * [ResolveProviderSyntax] provides APIs to create GlobalSearchScope.
 */
interface ResolveProviderSyntax {


    /**
     * registers an [GlobalSearchScope].
     * One minimal example from [KotlinScriptResolveScopeProvider], may look like this:
     * ```kotlin
     * import arrow.meta.ide.IdeMetaPlugin
     * import arrow.meta.phases.ExtensionPhase
     * import com.intellij.openapi.project.Project
     * import com.intellij.openapi.vfs.VirtualFile
     * import com.intellij.psi.PsiManager
     * import com.intellij.psi.search.GlobalSearchScope
     * import org.jetbrains.kotlin.idea.KotlinFileType
     * import org.jetbrains.kotlin.idea.core.script.ScriptConfigurationManager
     * import org.jetbrains.kotlin.scripting.definitions.ScriptDefinition
     * import org.jetbrains.kotlin.scripting.definitions.findScriptDefinition
     * import org.jetbrains.kotlin.scripting.resolve.KotlinScriptDefinitionFromAnnotatedTemplate
     * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
     *
     * val IdeMetaPlugin.scopeProvider: ExtensionPhase
     *    get() = addResolveScopeProvider { file: VirtualFile, project: Project ->
     *      file.takeIf {
     *          it.fileType == KotlinFileType.INSTANCE &&
     *                  PsiManager.getInstance(project)
     *                          .findFile(it)
     *                          ?.findScriptDefinition()
     *                          ?.safeAs<ScriptDefinition.FromConfigurations>()
     *                          ?.asLegacyOrNull<KotlinScriptDefinitionFromAnnotatedTemplate>() != null
     *      }.let { file ->
     *          GlobalSearchScope
     *                  .fileScope(project, file)
     *                  .union(
     *                          ScriptConfigurationManager
     *                                 .getInstance(project)
     *                                 .getScriptDependenciesClassFilesScope(file)
     *                  )
     *  }
     * }
     *
     *
     * @param getResolveScope defines where the [VirtualFile] is in the [Project] return the [GlobalSearchScope] if there is one.
     */
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

    /**
     * @see com.intellij.psi.search.GlobalSearchScope
     */
    fun ResolveProviderSyntax.globalSearchScope(
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