package arrow.meta.ide.phases.resolve

import arrow.meta.ide.dsl.application.projectLifecycleListener
import arrow.meta.quotes.AnalysisDefinition
import arrow.meta.quotes.analysisIdeExtensions
import arrow.meta.quotes.processKtFile
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.progress.PerformInBackgroundOption
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.impl.ProjectLifecycleListener
import com.intellij.openapi.roots.FileIndexFacade
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.psi.PsiDocumentManager
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.idea.debugger.readAction
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.resolve.lazy.LazyEntity
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

val quoteSystem: ProjectLifecycleListener
  get() = projectLifecycleListener(
    initialize = { project ->
      project.getService(QuoteSystemService::class.java)?.let { service ->
        // register an async file listener.
        // We need to update the transformations of .kt files as soon as they were modified.
        VirtualFileManager.getInstance().addAsyncFileListener(
          AsyncFileListener { events: MutableList<out VFileEvent> ->
            // fixme properly handle remove events
            // fixme properly handle copy event: file is the source file, transform new file, too
            val files: List<KtFile> =
              events
                .filter { vfile ->
                  (vfile is VFileContentChangeEvent || vfile is VFileMoveEvent || vfile is VFileCopyEvent)
                    && vfile.file?.run { quoteRelevantFile() && isInLocalFileSystem } ?: false
                }
                .mapNotNull { it.file }
                .files(project)

            object : AsyncFileListener.ChangeApplier {
              override fun afterVfsChange(): Unit {
                LOG.info("afterVfsChange")
                // fixme data may have changed between prepareChange and afterVfsChange, take care of this
                // the docs of afterVfsChange states: "The implementations should be as fast as possible"
                // therefore we're moving this operation into the background
                service.refreshCache(files, cacheStrategy(resetCache = false, backgroundTask = false))
              }
            }
          }, project) // because there is no need to dispose the service

        EditorFactory.getInstance().eventMulticaster.addDocumentListener(object : DocumentListener {
          override fun documentChanged(event: DocumentEvent) {
            val doc: Document = event.document
            PsiDocumentManager.getInstance(project)?.getPsiFile(doc)
              ?.safeAs<KtFile>()
              ?.takeIf {
                it.isPhysical && !it.isCompiled &&
                  it.virtualFile?.run {
                    quoteRelevantFile() && FileIndexFacade.getInstance(project).isInSourceContent(this)
                  } ?: false
              }
              ?.let { file ->
                LOG.info("transforming ${file.name} after change in editor")
                // fixme avoid this, this slows down the editor.
                //  it would be better to take the text and send the text content to the quote system
                // fixme this breaks in a live ide with "com.intellij.util.IncorrectOperationException: Must not modify PSI inside save listener"
                //   but doesn't fail in tests
                if (ApplicationManager.getApplication().isWriteAccessAllowed) {
                  PsiDocumentManager.getInstance(project).commitDocument(doc)
                }
                // move this into the background to avoid blocking the editor
                // document listeners should be as fast as possible
                service.refreshCache(listOf(file), cacheStrategy(resetCache = false, backgroundTask = true))
              }
          }
        }, project)
      }
    },
    postStartupActivitiesPassed = { project ->
      // add a startup activity to populate the cache with a transformation of all project files
      // fixme sometimes initially opened files still show errors.
      //  This seems to be a timing issue between cache update and initial update.
      project.getService(QuoteSystemService::class.java)?.let { service ->
        StartupManager.getInstance(project).runWhenProjectIsInitialized {
          // TODO: Check reference
          ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Initializing arrow-meta...", false, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
            override fun run(indicator: ProgressIndicator) {
              LOG.info("Initializing quote system cache...")
              val files = readAction {
                project.quoteRelevantFiles()
              }
              service.refreshCache(files, cacheStrategy(resetCache = true, backgroundTask = false))
            }
          })
        }
      }
    },
    afterProjectClosed = { project ->
      project.getService(QuoteSystemService::class.java)?.let { service ->
        try {
          service.exec.shutdown()
        } catch (e: Exception) {
          LOG.warn("error shutting down pool", e)
        }
        service.cache.clearCache()
      }
    }
  )

/**
 * TODO: remove this and express this as in Meta DSL along with [quoteSystem] and `metaProjectRegistrar`
 */
class QuoteService(val project: Project) : QuoteSystemService {
  override val cache: QuoteCache = QuoteCache.default()
  // pool where the quote system transformations are executed.
  // this is a single thread pool to avoid concurrent updates to the cache.
  // we keep a pool per project, so that we're able to shut it down when the project is closed
  override val exec: ExecutorService = Executors.newFixedThreadPool(1) { runnable ->
    Thread(runnable, "arrow worker, ${project.name}")
  }

  override fun transform(files: List<KtFile>, extensions: List<AnalysisDefinition>): List<Pair<KtFile, KtFile>> {
    ApplicationManager.getApplication().assertReadAccessAllowed()
    // fixme is scope correct here? Unsure what CompilerContext is expecting here
    // fixme do we need to set more properties of the compiler context?
    //val context = CompilerContext(project, messages, ElementScope.default(project))
    //context.files = sourceFiles
    val resultFiles = arrayListOf<KtFile>()
    resultFiles.addAll(files)

    // fixme: remove debugging code before it's used in production
    val allDuration = AtomicLong(0)
    extensions.forEach { ext ->
      val mutations = resultFiles.map {
        val start = System.currentTimeMillis()
        try {
          processKtFile(it, ext.type, ext.quoteFactory, ext.match, ext.map)
        } finally {
          val fileDuration = System.currentTimeMillis() - start
          allDuration.addAndGet(fileDuration)
          LOG.warn("transformation: file %s, duration %d".format(it.name, fileDuration))
        }
      }
      LOG.warn("created transformations for all quotes: duration $allDuration ms")

      val start = System.currentTimeMillis()
      try {
        // this replaces the entries of resultFiles with transformed files, if transformations apply.
        // a file may be transformed multiple times
        //context.updateFiles(resultFiles, mutations, ext.match)
      } finally {
        val updateDuration = System.currentTimeMillis() - start
        LOG.warn("update of ${resultFiles.size} files with ${mutations.size} mutations: duration $updateDuration ms")
        allDuration.addAndGet(updateDuration)
      }
    }
    LOG.warn("transformation and update of all quotes and all files: duration $allDuration")

    // now, restore the association of sourceFile to transformed file
    // don't keep files which were not transformed
    return files.zip(resultFiles).filter { it.first != it.second }
  }

  /**
   * TODO: remove implicit dependency of [arrow.meta.quotes.analysisIdeExtensions]
   */
  override fun refreshCache(files: List<KtFile>, strategy: CacheStrategy): Unit =
    computeRefreshCache(strategy) {
      // fixme execute under progressManager?
      files.takeIf { it.isNotEmpty() }?.let { files ->
        val transformed: List<Pair<KtFile, KtFile>> = ReadAction.compute<List<Pair<KtFile, KtFile>>, Exception> {
          val start = System.currentTimeMillis()
          try {
            transform(files, analysisIdeExtensions) // analysisIdeExtensions are at runtime collected quote transformations
          } catch (e: Exception) {
            LOG.warn("error transforming files $files. Falling back to empty transformation.", e)
            emptyList()
          } finally {
            val duration = System.currentTimeMillis() - start
            LOG.warn("kt file transformation: %d files, duration %d ms".format(files.size, duration))
          }
        }

        // debugging code to temporarily write transformed data to disk, next to the source file
        /*if (transformed.isNotEmpty()) {
          if (!ApplicationManager.getApplication().isUnitTestMode) {
            ApplicationManager.getApplication().invokeLater {
              WriteAction.run<Exception> {
                transformed.forEach { (original, transformed) ->
                  val metaFile = original.virtualFile.parent.findOrCreateChildData(this, transformed.name + ".txt")
                  metaFile.setBinaryContent(transformed.text.toByteArray())
                }
              }
            }
          }
        }*/

        // update the resolved data as soon as index access is possible
        // fixme protect against multiple transformations at once
        // fixme this might be delayed, make sure we're handling this correctly with a test
        DumbService.getInstance(project).runReadActionInSmartMode {
          if (strategy.resetCache) {
            cache.clearCache()
          }
          LOG.info("resolving descriptors of transformed files: ${cache.cache.size} files")
          // clear descriptors of all updatedFiles, which don't have a transformation result
          // e.g. because no meta-code is used anymore
          // fixme atm a transformation of a .kt file with syntax errors also returns an empty list of transformations
          //    we probably need to handle this, otherwise files with errors will always have unresolved references
          //    best would be partial transformation results for the valid parts of a file (Quote system changes needed)

          // fixme this lookup is slow (exponential?), optimize when necesary
          val removedQuotes: List<QuotedFile> =
            files
              .filter { src -> !transformed.any { it.first == src } }
              .mapNotNull { src -> cache.removeQuotedFile(src) }
          val facade: ResolutionFacade = KotlinCacheService.getInstance(project).getResolutionFacade(files + transformed.map { it.second })
          val resolvedFiles: List<Pair<KtFile, QuotedFile>> =
            transformed
              .map { (origin, quoted) ->
                origin to
                  quoted.resolve(facade, BodyResolveMode.FULL).run { copy(second = second.filter { it.isMetaSynthetic() }) }
              }

          // fixme this triggers a call to the .resolved()
          // via MetaSyntheticPackageFragmentProvider.BuildCachePackageFragmentDescriptor.Scope.getContributedClassifier
          resolvedFiles
            .map { (_, quoted) -> quoted.second }
            .flatten()
            .forEach { descriptor ->
              try {
                descriptor.safeAs<LazyEntity>()?.forceResolveAllContents()
              } catch (e: IndexNotReadyException) {
                LOG.warn("Index wasn't ready to resolve: ${descriptor.name}")
              }
            }

          transformed.forEach { // refresh the highlighting of editors of modified files, using the new cache
            (src, _) ->
            DaemonCodeAnalyzer.getInstance(project).restart(src)
          }
          LOG.info("refreshCache(): updating/adding ${files.size} files, currently cached ${cache.cache.size} files")
        }
      } ?: Unit
    }
}