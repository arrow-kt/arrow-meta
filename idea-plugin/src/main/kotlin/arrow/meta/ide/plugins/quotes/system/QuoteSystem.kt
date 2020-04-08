package arrow.meta.ide.plugins.quotes.system

import arrow.meta.ide.dsl.utils.resolve
import arrow.meta.ide.phases.resolve.LOG
import arrow.meta.ide.plugins.quotes.cache.QuoteCache
import arrow.meta.ide.plugins.quotes.resolve.isMetaSynthetic
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.AnalysisDefinition
import arrow.meta.quotes.processKtFile
import arrow.meta.quotes.updateFiles
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.resolve.lazy.LazyEntity
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.util.concurrent.atomic.AtomicLong

private class QuoteSystem(project: Project) : QuoteSystemService {
  override val context: QuoteSystemService.Ctx = QuoteSystemService.defaultCtx(project)

  /**
   * Applies the Quote system's transformations on the input files and returns a mapping of
   * originalFile->transformedFile if the transformation changed the original file.
   */
  override fun transform(project: Project, files: List<KtFile>, extensions: List<AnalysisDefinition>): List<Pair<KtFile, KtFile>> {
    ApplicationManager.getApplication().assertReadAccessAllowed()
    LOG.assertTrue(ProgressManager.getInstance().hasProgressIndicator())
    val resultFiles = arrayListOf<KtFile>()
    resultFiles.addAll(files)

    // fixme: remove debugging code before it's used in production
    val allDuration = AtomicLong(0)
    extensions.forEach { ext ->
      ProgressManager.checkCanceled()

      val mutations = resultFiles.map {
        ProgressManager.checkCanceled()

        val start = System.currentTimeMillis()
        try {
          // fixme add checkCancelled to processKtFile? The API should be available
          processKtFile(it, ext.type, ext.quoteFactory, ext.match, ext.map)
        } finally {
          val fileDuration = System.currentTimeMillis() - start
          allDuration.addAndGet(fileDuration)
          LOG.warn("transformation: file %s, duration %d".format(it.name, fileDuration))
        }
      }
      LOG.warn("created transformations for all quotes: duration $allDuration ms")

      ProgressManager.checkCanceled()

      val start = System.currentTimeMillis()
      try {
        // this replaces the entries of resultFiles with transformed files, if transformations apply.
        // a file may be transformed multiple times
        // fixme add checkCancelled to updateFiles? The API should be available
        CompilerContext(project, messages).updateFiles(resultFiles, mutations, ext.match)
      } finally {
        val updateDuration = System.currentTimeMillis() - start
        LOG.warn("update of ${resultFiles.size} files with ${mutations.size} mutations: duration $updateDuration ms")
        allDuration.addAndGet(updateDuration)
      }
    }

    LOG.warn("transformation and update of all quotes and all files: duration $allDuration")

    // now, restore the association of sourceFile to transformed file
    // don't keep files which were not transformed
    ProgressManager.checkCanceled()
    return files.zip(resultFiles).filter { it.first != it.second }
  }

  /**
   * The transformations are executed in the background to avoid blocking the IDE.
   */
  override fun refreshCache(cache: QuoteCache, project: Project, files: List<KtFile>, extensions: List<AnalysisDefinition>, strategy: CacheStrategy) {
    LOG.assertTrue(strategy.indicator.isRunning)
    LOG.info("refreshCache(): updating/adding ${files.size} files, currently cached ${cache.size} files")

    if (files.isEmpty()) {
      return
    }

    // non–blocking read action mode may execute multiple times until the action finished without being cancelled
    // writes cancel non–blocking read actions, e.g. typing in the editor is triggering a write action
    // a matching progress indicator is passed by the caller to decide when the transformation must not be repeated anymore
    // a blocking read action can lead to very bad editor experience, especially we're doing a lot with the PsiFiles
    // in the transformation
    ReadAction.nonBlocking<List<Pair<KtFile, KtFile>>> {
      val start = System.currentTimeMillis()
      try {
        transform(project, files, extensions)
      } finally {
        val duration = System.currentTimeMillis() - start
        LOG.warn("kt file transformation: %d files, duration %d ms".format(files.size, duration))
      }
    }
      .cancelWith(strategy.indicator)
      .expireWhen { strategy.indicator.isCanceled }
      .submit(context.docExec)
      .then { transformed ->
        // limit to one pool to avoid cache corruption
        ProgressManager.getInstance().runProcess({
          performRefresh(cache, files, transformed, strategy, project)
        }, strategy.indicator)
      }.onError { e ->
        // fixme atm a transformation of a .kt file with syntax errors also returns an empty list of transformations
        //    we probably need to handle this, otherwise files with errors will always have unresolved references
        //    best would be partial transformation results for the valid parts of a file (Quote system changes needed)

        // IllegalStateExceptions are usually caused by syntax errors in the source files, thrown by quote system
        if (LOG.isDebugEnabled) {
          LOG.debug("error transforming files $files", e)
        }
      }
  }
}

/**
 * Update the cache with the transformed data as soon as index access is available.
 * The execution of the cache update may be delayed.
 * This method takes care that only one cache update may happen at the same time by using a single-bounded executor.
 */
private fun QuoteSystemService.performRefresh(cache: QuoteCache, files: List<KtFile>, transformed: List<Pair<KtFile, KtFile>>, strategy: CacheStrategy, project: Project): Unit {
  LOG.assertTrue(strategy.indicator.isRunning)

  ReadAction.nonBlocking {
    ProgressManager.getInstance().runProcess({
      LOG.assertTrue(strategy.indicator.isRunning)
      LOG.info("resolving descriptors of transformed files: ${transformed.size} files")

      if (strategy.resetCache) {
        cache.clear()
      }

      LOG.info("resolving descriptors of transformed files: ${cache.size} files")
      if (files.isNotEmpty()) {
        // clear descriptors of all updatedFiles, which don't have a transformation result
        // e.g. because meta-code isn't used anymore

        // fixme this lookup is slow (exponential?), optimize when necessary
        for (source in files) {
          if (!transformed.any { it.first == source }) {
            cache.removeQuotedFile(source)
          }
        }

        // the kotlin facade needs files with source and target elements
        val facade: ResolutionFacade = KotlinCacheService.getInstance(project).getResolutionFacade(files + transformed.map { it.second })

        // fixme: remove descriptors which belong to the newly transformed files
        transformed.forEach { (sourceFile, transformedFile) ->
          // fixme this triggers a resolve which already queries our synthetic resolve extensions
          val synthDescriptors = transformedFile.resolve(facade, BodyResolveMode.FULL)
            .second.filter { it.isMetaSynthetic() }

          synthDescriptors
            .mapNotNull { descriptor -> descriptor.safeAs<LazyEntity>()?.let { descriptor to it } }
            .forEach { (descriptor, entity) ->
              try {
                // fixme this triggers a call to the .resolved()
                // via MetaSyntheticPackageFragmentProvider.BuildCachePackageFragmentDescriptor.Scope.getContributedClassifier
                entity.forceResolveAllContents()
              } catch (e: IndexNotReadyException) {
                LOG.warn("Index wasn't ready to resolve: ${descriptor.name}")
              }
            }
          cache.update(sourceFile, transformedFile.packageFqName to synthDescriptors)
        }

        // refresh the highlighting of editors of modified files, using the new cache
        for ((origin, _) in transformed) {
          DaemonCodeAnalyzer.getInstance(project).restart(origin)
        }
      }
    }, strategy.indicator)
  }.cancelWith(strategy.indicator)
    .expireWhen { strategy.indicator.isCanceled }
    .expireWith(project)
    .inSmartMode(project)
    .submit(context.cacheExec)
}

/**
 * messages used to report messages generated by quote system via IntelliJ's log.
 */
private val messages: MessageCollector = object : MessageCollector {
  override fun clear() {}

  override fun hasErrors(): Boolean = false

  override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?) {
    when {
      severity.isError -> LOG.error(message)
      severity.isWarning -> LOG.warn(message)
      else -> LOG.debug(message)
    }
  }
}