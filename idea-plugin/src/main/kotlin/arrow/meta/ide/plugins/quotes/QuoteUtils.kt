package arrow.meta.ide.plugins.quotes

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.DumbProgressIndicator
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.idea.search.projectScope
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode

/**
 * complements the quote service with a cache strategy
 * It aggregates the configs of caching quote transformations
 */
interface CacheStrategy {
  val resetCache: Boolean
  val indicator: ProgressIndicator
}

/**
 * The default strategy resets the quote cache and uses the DumbProgressIndicator
 * @param resetCache defines if all previous transformations should be removed or not. Pass false for incremental updates.
 */
fun cacheStrategy(
  resetCache: Boolean = true,
  indicator: ProgressIndicator = DumbProgressIndicator.INSTANCE
): CacheStrategy =
  object : CacheStrategy {
    override val resetCache: Boolean = resetCache
    override val indicator: ProgressIndicator = indicator
  }

// fixme use ViewProvider's files instead?
@Suppress("UNCHECKED_CAST")
fun <F : PsiFile> List<VirtualFile>.files(project: Project): List<F> =
  mapNotNull { PsiManager.getInstance(project).findFile(it) as? F }

fun Project.ktFiles(): List<VirtualFile> =
  FileTypeIndex.getFiles(KotlinFileType.INSTANCE, projectScope()).filterNotNull()

fun VirtualFile.quoteRelevantFile(): Boolean =
  isValid &&
    this.fileType is KotlinFileType &&
    (isInLocalFileSystem || ApplicationManager.getApplication().isUnitTestMode)

/**
 * Collects all Kotlin files of the current project which are source files for Meta transformations.
 */
fun Project.quoteRelevantFiles(): List<KtFile> =
  ktFiles()
    .filter { it.quoteRelevantFile() && it.isInLocalFileSystem }
    .files(this)

/**
 * returns the [DeclarationDescriptor]s of each File
 */
fun KtFile.resolve(facade: ResolutionFacade, resolveMode: BodyResolveMode = BodyResolveMode.PARTIAL): Pair<KtFile, List<DeclarationDescriptor>> =
  this to declarations.map { facade.resolveToDescriptor(it, resolveMode) }