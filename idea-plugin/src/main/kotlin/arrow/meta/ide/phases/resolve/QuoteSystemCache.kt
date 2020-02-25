package arrow.meta.ide.phases.resolve

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.analysisIdeExtensions
import arrow.meta.quotes.processKtFile
import arrow.meta.quotes.updateFiles
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.ProjectComponent
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
import com.intellij.openapi.roots.FileIndexFacade
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.debugger.readAction
import org.jetbrains.kotlin.idea.search.projectScope
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.resolve.lazy.LazyEntity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * QuoteSystemCache is a project component which manages the transformations of KtFiles by the quote system.
 *
 * When initialized, it transforms all .kt files of the project in a background thread.
 */
class QuoteSystemCache(private val project: Project) : ProjectComponent, Disposable {
  companion object {
    fun getInstance(project: Project): QuoteSystemCache = project.getComponent(QuoteSystemCache::class.java)
  }

  private val metaCache: QuoteCache = DefaultQuoteCache()

  // pool where the quote system transformations are executed.
  // this is a single thread pool to avoid concurrent updates to the cache.
  // we keep a pool per project, so that we're able to shut it down when the project is closed
  private val pool: ExecutorService = Executors.newFixedThreadPool(1) { runnable -> Thread(runnable, "arrow worker, ${project.name}") }

  override fun initComponent() {
    // register an async file listener.
    // We need to update the transformations of .kt files as soon as they were modified.
    VirtualFileManager.getInstance().addAsyncFileListener(object : AsyncFileListener {
      // We only care about changes to Kotlin files.
      override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier? {
        // fixme properly handle remove events
        // fixme properly handle copy event: file is the source file, transform new file, too

        val relevantFiles: List<VirtualFile> = events.mapNotNull {
          val file = it.file
          if (it !is VFileContentChangeEvent || it !is VFileMoveEvent || it !is VFileCopyEvent) {
            return null
          }

          if (file != null && file.isRelevantFile()) {
            file
          } else {
            null
          }
        }

        if (relevantFiles.isEmpty()) {
          return null
        }

        return object : AsyncFileListener.ChangeApplier {
          override fun afterVfsChange() {
            LOG.info("afterVfsChange")
            // fixme data may have changed between prepareChange and afterVfsChange, take care of this

            // the docs of afterVfsChange states: "The implementations should be as fast as possible"
            // therefore we're moving this operation into the background
            refreshCache(relevantFiles.toKtFiles(project), resetCache = false, backgroundTask = false)
          }
        }
      }
    }, this)

    EditorFactory.getInstance().eventMulticaster.addDocumentListener(object : DocumentListener {
      override fun documentChanged(event: DocumentEvent) {
        val doc = event.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(doc)

        if (psiFile is KtFile && psiFile.isPhysical && !psiFile.isCompiled) {
          val vFile = psiFile.virtualFile
          if (vFile.isRelevantFile() && FileIndexFacade.getInstance(project).isInSourceContent(vFile)) {
            LOG.info("transforming ${psiFile.name} after change in editor")
            // fixme avoid this, this slows down the editor.
            //  it would be better to take the text and send the text content to the quote system
            // fixme this breaks in a live ide with "com.intellij.util.IncorrectOperationException: Must not modify PSI inside save listener"
            //   but doesn't fail in tests
            if (ApplicationManager.getApplication().isWriteAccessAllowed) {
              PsiDocumentManager.getInstance(project).commitDocument(doc)
            }

            // move this into the background to avoid blocking the editor
            // document listeners should be as fast as possible
            refreshCache(listOf(psiFile), resetCache = false, backgroundTask = true)
          }
        }
      }
    }, project)
  }

  override fun projectOpened() {
    // add a startup activity to populate the cache with a transformation of all project files
    // fixme sometimes initially opened files still show errors.
    //  This seems to be a timing issue between cache update and initial update.
    StartupManager.getInstance(project).runWhenProjectIsInitialized {
      ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Initializing arrow-meta...", false, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
        override fun run(indicator: ProgressIndicator) {
          LOG.info("Initializing quote system cache...")
          val files = readAction {
            project.collectAllKtFiles()
          }
          refreshCache(files, resetCache = true, backgroundTask = false)
        }
      })
    }
  }

  fun packages(): List<FqName> = metaCache.packages()

  fun resolved(name: FqName): List<DeclarationDescriptor> = metaCache.descriptors(name)

  /**
   * refreshCache updates the given source files with new transformations.
   * The transformations are executed in the background to avoid blocking the IDE.
   *
   * @param resetCache Defines if all previous transformations should be removed or not. Pass false for incremental updates.
   */
  private fun refreshCache(updatedFiles: List<KtFile>, resetCache: Boolean = true, backgroundTask: Boolean = true) {
    if (updatedFiles.isEmpty()) {
      return
    }

    val task = {
      // fixme execute under progressManager?
      val transformed = ReadAction.compute<List<Pair<KtFile, KtFile>>, Exception> {
        val start = System.currentTimeMillis()
        try {
          transformFiles(updatedFiles)
        } catch (e: Exception) {
          LOG.warn("error transforming files $updatedFiles. Falling back to empty transformation.", e)
          emptyList()
        } finally {
          val duration = System.currentTimeMillis() - start
          LOG.warn("kt file transformation: %d files, duration %d ms".format(updatedFiles.size, duration))
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
        if (resetCache) {
          metaCache.clear()
        }

        LOG.info("resolving descriptors of transformed files: ${metaCache.size} files")
        if (updatedFiles.isNotEmpty()) {
          // clear descriptors of all updatedFiles, which don't have a transformation result
          // e.g. because no meta-code is used anymore
          // fixme atm a transformation of a .kt file with syntax errors also returns an empty list of transformations
          //    we probably need to handle this, otherwise files with errors will always have unresolved references
          //    best would be partial transformation results for the valid parts of a file (Quote system changes needed)

          // fixme this lookup is slow (exponential?), optimize when necesary
          for (source in updatedFiles) {
            if (!transformed.any { it.first == source }) {
              metaCache.removeQuotedFile(source)
            }
          }

          // the kotlin facade needs files with source and target elements
          val kotlinCache = KotlinCacheService.getInstance(project)
          val facade = kotlinCache.getResolutionFacade(updatedFiles + transformed.map { it.second })

          // fixme: remove descriptors which belong to the newly transformed files
          transformed.forEach { (sourceFile, transformedFile) ->
            // fixme this triggers a resolve which already queries our synthetic resolve extensions
            val synthDescriptors = transformedFile.declarations.mapNotNull {
              val desc = facade.resolveToDescriptor(it, BodyResolveMode.FULL)
              if (desc.isMetaSynthetic()) desc else null
            }

            if (synthDescriptors.isNotEmpty()) {
              synthDescriptors.forEach { synthDescriptor ->
                try {
                  // fixme this triggers a call to the .resolved()
                  //    via MetaSyntheticPackageFragmentProvider.BuildCachePackageFragmentDescriptor.Scope.getContributedClassifier
                  if (synthDescriptor is LazyEntity) synthDescriptor.forceResolveAllContents()
                } catch (e: IndexNotReadyException) {
                  LOG.warn("Index wasn't ready to resolve: ${synthDescriptor.name}")
                }
              }
            }

            metaCache.update(sourceFile, transformedFile.packageFqName to synthDescriptors)
          }

          // refresh the highlighting of editors of modified files, using the new cache
          for ((originalPsiFile, _) in transformed) {
            DaemonCodeAnalyzer.getInstance(project).restart(originalPsiFile)
          }
        }
      }
    }

    LOG.info("refreshCache(): updating/adding ${updatedFiles.size} files, currently cached ${metaCache.size} files")
    if (backgroundTask) {
      pool.submit(task)
    } else {
      task()
    }
  }

  /**
   * Applies the Quote system's transformations on the input files and returns a mapping of
   * originalFile->transformedFile if the transformation changed the original file.
   */
  private fun transformFiles(sourceFiles: List<KtFile>): List<Pair<KtFile, KtFile>> {
    ApplicationManager.getApplication().assertReadAccessAllowed()

    // fixme is scope correct here? Unsure what CompilerContext is expecting here
    // fixme do we need to set more properties of the compiler context?
    val context = CompilerContext(project, messages, ElementScope.default(project))
    context.files = sourceFiles

    val resultFiles = arrayListOf<KtFile>()
    resultFiles.addAll(sourceFiles)

    // fixme: remove debugging code before it's used in production
    val allDuration = AtomicLong(0)
    analysisIdeExtensions.forEach { ext ->
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
        context.updateFiles(resultFiles, mutations, ext.match)
      } finally {
        val updateDuration = System.currentTimeMillis() - start
        LOG.warn("update of ${resultFiles.size} files with ${mutations.size} mutations: duration $updateDuration ms")
        allDuration.addAndGet(updateDuration)
      }
    }

    LOG.warn("transformation and update of all quotes and all files: duration $allDuration")

    // now, restore the association of sourceFile to transformed file
    // don't keep files which were not transformed
    return sourceFiles.zip(resultFiles).filter { it.first != it.second }
  }

  override fun dispose() {
    try {
      pool.shutdownNow()
    } catch (e: Exception) {
      LOG.warn("error shutting down pool", e)
    }

    metaCache.clear()
  }

  @TestOnly
  fun reset() {
    metaCache.clear()
  }

  @TestOnly
  fun forceRebuild() {
    reset()
    refreshCache(project.collectAllKtFiles(), backgroundTask = false)
    flush()
  }

  @TestOnly
  fun flush() {
    pool.submit { }.get(5000, TimeUnit.MILLISECONDS)
  }
}

private fun VirtualFile.isRelevantFile(): Boolean {
  return isValid &&
    this.fileType is KotlinFileType &&
    (isInLocalFileSystem || ApplicationManager.getApplication().isUnitTestMode)
}

/**
 * Collects all Kotlin files of the current project which are source files for Meta transformations.
 */
private fun Project.collectAllKtFiles(): List<KtFile> {
  val files = FileTypeIndex.getFiles(KotlinFileType.INSTANCE, projectScope()).filter {
    it.isRelevantFile()
  }
  LOG.info("collectKtFiles(): ${files.size} kotlin files found for project $name")

  return files.toKtFiles(this)
}

/**
 * Maps the list of VirtualFiles to a list of KtFiles.
 */
private fun List<VirtualFile>.toKtFiles(project: Project): List<KtFile> {
  val psiMgr = PsiManager.getInstance(project)
  return mapNotNull {
    when {
      it.isValid && it.isInLocalFileSystem && it.fileType is KotlinFileType ->
        // fixme use ViewProvider's files instead?
        psiMgr.findFile(it) as? KtFile
      else -> null
    }
  }
}

/**
 * messages is used to report messages generated by quote system via IntelliJ's log.
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