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
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
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
 * It currently transforms all .kt files of the current project.
 * This could be changed to incremental updates when necessary.
 */
class QuoteSystemCache(private val project: Project) : ProjectComponent, Disposable, AsyncFileListener {
  companion object {
    fun getInstance(project: Project): QuoteSystemCache = project.getComponent(QuoteSystemCache::class.java)

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
  }

  private val metaCache: MetaTransformationCache = DefaultMetaTransformationCache()

  // keep a pool per project, so that we're able to shut it down when the project is closed
  private val pool: ExecutorService = Executors.newFixedThreadPool(1) { runnable -> Thread(runnable, "arrow worker, ${project.name}") }

  override fun initComponent() {
    VirtualFileManager.getInstance().addAsyncFileListener(this, this)

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
            // fixme this break in a ide with "com.intellij.util.IncorrectOperationException: Must not modify PSI inside save listener"
            //   but doesn't fail in tests
//            if (ApplicationManager.getApplication().isWriteAccessAllowed) {
//              psiMgr.commitDocument(doc)
//            }

            // move this into the background to avoid blocking the editor
            pool.submit {
              refreshCache(listOf(psiFile), resetCache = false)
            }
          }
        }
      }
    }, project)
  }

  override fun projectOpened() {
    // register startup activity to populate the cache with a transformation of all project files,
    // moving this into initComponent isn't working as the open files remain with errors
    StartupManager.getInstance(project).runWhenProjectIsInitialized {
      ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Initializing arrow-meta...", false, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
        override fun run(indicator: ProgressIndicator) {
          LOG.info("Initializing quote system cache...")
          val files = readAction {
            project.collectAllKtFiles()
          }
          refreshCache(files, resetCache = true)
        }
      })
    }
  }

  fun packageList(): List<FqName> = metaCache.packageList()

  fun resolved(name: FqName): List<DeclarationDescriptor>? = metaCache.resolved(name)

  /**
   * Applies the Quote system's transformations on the input files and returns a mapping of
   * originalFile->transformedFile if the transformation changed the original file.
   */
  private fun transformFiles(sourceFiles: List<KtFile>): List<Pair<KtFile, KtFile>> {
    ApplicationManager.getApplication().assertReadAccessAllowed()

    // fixme scope?
    val context = CompilerContext(project, messages, ElementScope.default(project))
    // fixme do we need to set more properties of the compiler context?
    context.files = sourceFiles

    val resultFiles = arrayListOf<KtFile>()
    resultFiles.addAll(sourceFiles)

    // fixme jansorg: remove debugging code before merging
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

      LOG.warn("transformation created for all quotes: duration $allDuration ms")

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
    // remove files which were not transformed
    return sourceFiles.zip(resultFiles).filter { it.first != it.second }
  }

  private fun refreshCache(updatedFiles: List<KtFile>, resetCache: Boolean = true) {
    if (updatedFiles.isEmpty()) {
      return
    }

    LOG.info("refreshCache(): updating/adding ${updatedFiles.size} files, cached ${metaCache.size} files")
    pool.submit {
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

      // fixme: temporarily write metadata to disk
      if (transformed.isNotEmpty()) {
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
      }

      // update the resolved data when index access is possible
      // fixme protect against multiple transformations at once
      // fixme this might be delayed, make sure we're handling this correctly
      DumbService.getInstance(project).runReadActionInSmartMode {
        if (resetCache) {
          metaCache.clear()
        }

        LOG.info("resolving descriptors of transformed files: ${metaCache.size} files")
        if (updatedFiles.isNotEmpty()) {
          // clear descriptors of all updatedFiles, which don't have a transformation result
          // e.g. because no meta-code is used anymore
          // fixme atm a transformation of a kt file with syntax errors also returns an empty list of transformations
          //    we probably should handle this, otherwise files with errors will show more unresolved references
          for (source in updatedFiles) {
            // fixme this lookup is slow (exponential?), it could be optimized when necesary
            if (!transformed.any { it.first == source }) {
              metaCache.removeTransformations(source)
            }
          }

          // the facade needs files with source and target elements
          val kotlinCache = KotlinCacheService.getInstance(project)
          val facade = kotlinCache.getResolutionFacade(updatedFiles + transformed.map { it.second })

          // fixme: remove descriptors which belong to the newly transformed files
          transformed.forEach { (sourceFile, transformedFile) ->
            // fixme this triggers a resolve which queries our synthetic resolve extensions
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

            metaCache.updateTransformations(sourceFile, transformedFile, synthDescriptors)
          }

          // refresh the highlighting of editors of modified files, using the new cache
          for ((originalPsiFile, _) in transformed) {
            DaemonCodeAnalyzer.getInstance(project).restart(originalPsiFile)
          }
        }
      }
    }
  }

  /**
   * We only care about changes to Kotlin files.
   */
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
        ApplicationManager.getApplication().executeOnPooledThread {
          refreshCache(relevantFiles.toKtFiles(project), resetCache = false)
        }
      }
    }
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
    refreshCache(project.collectAllKtFiles())
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

private fun List<VirtualFile>.toKtFiles(project: Project): List<KtFile> {
  val psiMgr = PsiManager.getInstance(project)
  return mapNotNull {
    when {
      // fixme make sure that files belong to the current project?
      it.isValid && it.isInLocalFileSystem && it.fileType is KotlinFileType ->
        // fixme use ViewProvider's files instead?
        psiMgr.findFile(it) as? KtFile
      else -> null
    }
  }
}

