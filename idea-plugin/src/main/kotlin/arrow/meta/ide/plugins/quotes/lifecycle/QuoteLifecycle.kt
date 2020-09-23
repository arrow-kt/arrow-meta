package arrow.meta.ide.plugins.quotes.lifecycle

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.ctx
import arrow.meta.ide.dsl.utils.files
import arrow.meta.ide.dsl.utils.quoteRelevantFile
import arrow.meta.ide.dsl.utils.quoteRelevantFiles
import arrow.meta.ide.phases.resolve.LOG
import arrow.meta.ide.plugins.quotes.cache.QuoteCache
import arrow.meta.ide.plugins.quotes.highlighting.QuoteHighlightingCache
import arrow.meta.ide.plugins.quotes.system.QuoteSystemService
import arrow.meta.ide.plugins.quotes.system.cacheStrategy
import arrow.meta.ide.plugins.quotes.system.refreshCache
import arrow.meta.ide.testing.UnavailableServices
import arrow.meta.ide.testing.unavailableServices
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.quotes.QuoteDefinition
import arrow.meta.quotes.Scope
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.BulkAwareDocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.FileIndexFacade
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.concurrency.BoundedTaskExecutor
import com.intellij.util.ui.update.Update
import org.jetbrains.kotlin.idea.debugger.readAction
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.util.concurrent.TimeUnit

/**
 * [quoteLifecycle] addresses ide lifecycle specific manipulates utilizing the [QuoteSystemService], [QuoteCache] and [QuoteHighlightingCache].
 */
val IdeMetaPlugin.quoteLifecycle: ExtensionPhase
  get() = Composite(
    addProjectLifecycle(
      // the usual registration should be in `beforeProjectOpened`, but this is only possible when #446 is unlocked
      initialize = { project: Project ->
        project.quoteConfigs()?.let { (system, cache, ctx) ->
          initializeQuotes(project, system, cache, ctx)
        }
      }
      /* TODO: project is already disposed at this point are the following functions needed to preserve the lifecycle
      afterProjectClosed = { project: Project ->
        project.quoteConfigs()?.let { (quoteSystem, cache) ->
          try {
            quoteSystem.context.cacheExec.safeAs<BoundedTaskExecutor>()?.shutdownNow()
          } catch (e: Exception) {
            LOG.warn("error shutting down pool", e)
          } finally {
            cache.clear()
          }
        }
      }*/
    ),
    addPostStartupActivity(
      StartupActivity.DumbAware {
        quoteProjectOpened(it)
      }
    )
  )

data class QuoteConfigs(
  val quoteSystem: QuoteSystemService,
  val cache: QuoteCache,
  val ctx: CompilerContext
)

fun Project.quoteConfigs(): QuoteConfigs? =
  getService(QuoteSystemService::class.java)?.let { quoteSystem ->
    getService(QuoteCache::class.java)?.let { cache ->
      getService(CompilerContext::class.java)?.let {
        QuoteConfigs(quoteSystem, cache, it)
      }
    }
  }
    ?: throw UnavailableServices(listOf(QuoteSystemService::class.java, QuoteCache::class.java, CompilerContext::class.java))

@Suppress("UNCHECKED_CAST")
internal fun quoteProjectOpened(project: Project): Unit =
  project.quoteConfigs()?.let { (quoteSystem: QuoteSystemService, cache: QuoteCache, ctx: CompilerContext) ->
    // add a startup activity to populate the cache with a transformation of all project files
    //StartupManager.getInstance(project).runWhenProjectIsInitialized {
    runBackgroundableTask("Arrow Quote Initialization", project, cancellable = false) {
      try {
        LOG.info("Initializing quote system cache...")
        val files: List<KtFile> = runReadAction {
          project.quoteRelevantFiles().also {
            LOG.info("collected ${it.size} quote relevant files for Project:${project.name}")
          }
        }
        quoteSystem.refreshCache(cache, project, files, ctx.quotes as List<QuoteDefinition<KtElement, KtElement, Scope<KtElement>>>, cacheStrategy())
      } finally {
        try {
          quoteSystem.context.run {
            editorQueue.flush()
            docExec.safeAs<BoundedTaskExecutor>()?.waitAllTasksExecuted(5, TimeUnit.SECONDS)
            cacheExec.safeAs<BoundedTaskExecutor>()?.waitAllTasksExecuted(5, TimeUnit.SECONDS)
          }
        } catch (e: Exception) {
        }
        project.getService(QuoteHighlightingCache::class.java)?.run {
          map {
            it.copy(initialized = true)
          }
          value.extract().latch.countDown()
        } ?: unavailableServices(QuoteHighlightingCache::class.java)
      }
    }
  } ?: Unit

@Suppress("UNCHECKED_CAST")
internal fun initializeQuotes(project: Project, quoteSystem: QuoteSystemService, cache: QuoteCache, ctx: CompilerContext): Unit {
  // register an async file listener.
  // We need to update the transformations of .kt files as soon as they were modified.
  VirtualFileManager.getInstance().addAsyncFileListener(object : AsyncFileListener {
    // We only care about changes to Kotlin files.
    override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier? {
      // fixme properly handle remove events
      // fixme properly handle copy event: file is the source file, transform new file, too

      val relevantFiles: List<VirtualFile> =
        events.filter { vfile: VFileEvent ->
          vfile is VFileContentChangeEvent && vfile is VFileMoveEvent && vfile is VFileCopyEvent
          vfile.file?.quoteRelevantFile() ?: false
        }
          .mapNotNull { vFile: VFileEvent -> vFile.file }

      if (relevantFiles.isEmpty()) {
        return null
      }

      return object : AsyncFileListener.ChangeApplier {
        override fun afterVfsChange() {
          LOG.info("afterVfsChange")
          // fixme data may have changed between prepareChange and afterVfsChange, take care of this
          quoteSystem.refreshCache(
            cache,
            project,
            relevantFiles
              .filter { it.quoteRelevantFile() && it.isInLocalFileSystem }
              .files(project),
            ctx.quotes as List<QuoteDefinition<KtElement, KtElement, Scope<KtElement>>>,
            cacheStrategy(false))
        }
      }
    }
  }, project)

  EditorFactory.getInstance().eventMulticaster.addDocumentListener(object : BulkAwareDocumentListener.Simple {
    override fun afterDocumentChange(document: Document) {
      quoteSystem.context.editorQueue.queue(Update.create(document) {
        //  cancel ongoing updates of the same document
        KEY_DOC_UPDATE.get(document)?.let {
          KEY_DOC_UPDATE.set(document, null)
          it.cancel()
        }

        val indicator: ProgressIndicator = EmptyProgressIndicator(ModalityState.NON_MODAL)
        KEY_DOC_UPDATE.set(document, indicator)
        val app: Application = ApplicationManager.getApplication()
        if (app.isUnitTestMode) {
          readAction {
            ProgressManager.getInstance().runProcess({
              app.updateDoc(document, indicator, project, quoteSystem, cache, ctx)
            }, indicator)
          }
        } else {
          ReadAction.nonBlocking {
            ProgressManager.getInstance().runProcess({
              app.updateDoc(document, indicator, project, quoteSystem, cache, ctx)
            }, indicator)
          }.wrapProgress(indicator)
            .expireWith(project)
            .submit(quoteSystem.context.docExec)
        }
      })
    }
  }, project)
}

private val KEY_DOC_UPDATE = Key.create<ProgressIndicator>("arrow.quoteDocUpdate")


/**
 * lifecycle dependent
 */
@Suppress("UNCHECKED_CAST")
private fun Application.updateDoc(doc: Document, progressIndicator: ProgressIndicator, project: Project, quoteSystem: QuoteSystemService, cache: QuoteCache, ctx: CompilerContext): Unit {
  assertReadAccessAllowed()
  FileDocumentManager.getInstance()
    .getFile(doc)
    // proceed unless
    ?.takeUnless { it is LightVirtualFile || !it.quoteRelevantFile() || !FileIndexFacade.getInstance(project).isInSourceContent(it) }
    ?.let { _ ->
      PsiDocumentManager.getInstance(project)
        .getPsiFile(doc)
        ?.safeAs<KtFile>()
        ?.takeIf { it.isPhysical && !it.isCompiled }
        ?.let { ktFile ->
          LOG.info("transforming ${ktFile.name} after change in editor")
          // fixme avoid this, this slows down the editor.
          //  it would be better to take the text and send the text content to the quote system
          // fixme this breaks in a live ide with "com.intellij.util.IncorrectOperationException: Must not modify PSI inside save listener"
          //   but doesn't fail in tests
          if (isWriteAccessAllowed) {
            PsiDocumentManager.getInstance(project).commitDocument(doc)
          }
          quoteSystem.refreshCache(cache, project, listOf(ktFile), ctx.quotes as List<QuoteDefinition<KtElement, KtElement, Scope<KtElement>>>, cacheStrategy(false, progressIndicator))
        }
    }
}
