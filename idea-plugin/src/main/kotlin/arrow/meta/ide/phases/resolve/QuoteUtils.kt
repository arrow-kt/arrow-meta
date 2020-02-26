package arrow.meta.ide.phases.resolve

import arrow.meta.quotes.AnalysisDefinition
import com.intellij.openapi.application.ApplicationManager
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
import java.util.concurrent.ExecutorService


/**
 * returns the [DeclarationDescriptor]s of each File
 */
fun KtFile.resolve(facade: ResolutionFacade, resolveMode: BodyResolveMode = BodyResolveMode.PARTIAL): Pair<KtFile, List<DeclarationDescriptor>> =
  this to declarations.mapNotNull { facade.resolveToDescriptor(it, resolveMode) }

/**
 * project level service for quote transformations
 */
interface QuoteSystemService {

  /**
   * [ExecutorService] is the computational context if [CacheStrategy.backgroundTask] == true
   * @see computeRefreshCache default implementation
   */
  val exec: ExecutorService

  /**
   * transforms all [files] with registered [extensions]
   * @returns a List of transformed files (OldFile, NewFile)
   * @param extensions registered for quotes
   */
  fun transform(files: List<KtFile>, extensions: List<AnalysisDefinition>): List<Pair<KtFile, KtFile>>

  /**
   * transforms [files] and repopulates the [cache] based on the [strategy]
   */
  fun refreshCache(project: Project, cache: QuoteCache, files: List<KtFile>, strategy: CacheStrategy = cacheStrategy(resetCache = true, backgroundTask = true)): Unit

  /**
   * refreshes [cache] with an computational context for instance [ExecutorService] if [CacheStrategy.backgroundTask] == true
   */
  fun computeRefreshCache(strategy: CacheStrategy = cacheStrategy(resetCache = true, backgroundTask = true), refresh: () -> Unit): Unit {
    if (strategy.backgroundTask) exec.submit(refresh) else refresh()
  }
}

/**
 * complements the quote service with a cache strategy
 */
interface CacheStrategy {
  val resetCache: Boolean
  val backgroundTask: Boolean
}

fun cacheStrategy(
  resetCache: Boolean = true,
  backgroundTask: Boolean = true
): CacheStrategy =
  object : CacheStrategy {
    override val resetCache: Boolean = resetCache
    override val backgroundTask: Boolean = backgroundTask
  }

@Suppress("UNCHECKED_CAST")
fun <F : PsiFile> List<VirtualFile>.files(project: Project): List<F> =
  mapNotNull { PsiManager.getInstance(project).findFile(it) as? F }

fun Project.ktFiles(): List<VirtualFile> =
  FileTypeIndex.getFiles(KotlinFileType.INSTANCE, projectScope()).filterNotNull()

fun VirtualFile.quoteRelevantFile(): Boolean {
  return isValid &&
    this.fileType is KotlinFileType &&
    (isInLocalFileSystem || ApplicationManager.getApplication().isUnitTestMode)
}

/**
 * Collects all Kotlin files of the current project which are source files for Meta transformations.
 */
fun Project.quoteRelevantFiles(): List<KtFile> =
  ktFiles()
    .filter { it.quoteRelevantFile() && it.isInLocalFileSystem }
    .files(this)
