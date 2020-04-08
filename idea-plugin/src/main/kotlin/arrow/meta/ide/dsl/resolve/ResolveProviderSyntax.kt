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
     * import arrow.meta.Plugin
     * import com.intellij.openapi.project.Project
     * import com.intellij.openapi.vfs.VirtualFile
     * import com.intellij.psi.PsiManager
     * import com.intellij.psi.search.GlobalSearchScope
     * import org.jetbrains.kotlin.idea.KotlinFileType
     * import org.jetbrains.kotlin.idea.core.script.ScriptConfigurationManager
     * import org.jetbrains.kotlin.idea.core.script.StandardIdeScriptDefinition
     * import org.jetbrains.kotlin.psi.KtFile
     * import org.jetbrains.kotlin.scripting.definitions.ScriptDefinition
     * import org.jetbrains.kotlin.scripting.definitions.findScriptDefinition
     * import org.jetbrains.kotlin.scripting.resolve.KotlinScriptDefinitionFromAnnotatedTemplate
     *
     * IdeMetaPlugin().addResolveScopeProvider { file: VirtualFile, project: Project ->
     *  if (file.fileType != KotlinFileType.INSTANCE) return@addResolveScopeProvider null
     *
     *  val ktFile = PsiManager.getInstance(project).findFile(file) as? KtFile ?: return@addResolveScopeProvider null
     *  val scriptDefinition = ktFile.findScriptDefinition()
     *  when {
     *      scriptDefinition == null -> null
     *      scriptDefinition.baseClassType.fromClass == Any::class -> null
     *      scriptDefinition.asLegacyOrNull<StandardIdeScriptDefinition>() != null -> null
     *      scriptDefinition is ScriptDefinition.FromConfigurations ||
     *              scriptDefinition.asLegacyOrNull<KotlinScriptDefinitionFromAnnotatedTemplate>() != null -> {
     *           GlobalSearchScope.fileScope(project, file)
     *          .union(ScriptConfigurationManager.getInstance(project).getScriptDependenciesClassFilesScope(file))
     *     }
     *     else -> null
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