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
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.FileIndexFacade
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
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
import org.jetbrains.kotlin.idea.search.projectScope
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.resolve.lazy.LazyEntity
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * QuoteSystemCache is a project component which managees the transformations of KtFiles by the quote system.
 *
 * It currently transforms all .kt files of the current project.
 * This could be changed to incremental updates when necessary.
 */
class QuoteSystemCache(private val project: Project) : ProjectComponent, Disposable, AsyncFileListener, AsyncFileListener.ChangeApplier {
  companion object {
    fun getInstance(project: Project): QuoteSystemCache = project.getComponent(QuoteSystemCache::class.java)

    private val pool: ExecutorService = Executors.newFixedThreadPool(1) { runnable -> Thread(runnable, "arrow worker thread") }

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

  // fixme must not be used in production, because caching PsiElement this way is bad
  // fixme cache both per module? modules may define different ktfiles for the same package fqName
  private val transformedFiles = ConcurrentHashMap<KtFile, KtFile>()
  private val resolved = ConcurrentHashMap<FqName, List<DeclarationDescriptor>>()

  private val psiManager = PsiManager.getInstance(project)

  override fun initComponent() {
    VirtualFileManager.getInstance().addAsyncFileListener(this, this)

    EditorFactory.getInstance().eventMulticaster.addDocumentListener(object : DocumentListener {
      override fun documentChanged(event: DocumentEvent) {
        val doc = event.document
        val psiMgr = PsiDocumentManager.getInstance(project)
        val psiFile = psiMgr.getPsiFile(doc)

        // fixme skip if psiFile doesn't belong to the current project

        if (psiFile is KtFile && psiFile.virtualFile != null && psiFile.isPhysical && !psiFile.isCompiled) {
          if (FileIndexFacade.getInstance(project).isInSourceContent(psiFile.virtualFile)) {
            LOG.info("transforming ${psiFile.text} after change in editor")
            // fixme avoid this, this slows down the editor.
            //  it would be better to take the text and send the text content to the quote system
            // psiMgr.commitDocument(doc)

            // fixme refresh in background?
            // fixme make this interruptible?
            refreshCache(collectProjectKtFiles().toKtFiles())
          }
        }
      }
    }, project)
  }

  override fun projectOpened() {
    StartupManager.getInstance(project).runWhenProjectIsInitialized {
      val files = collectProjectKtFiles()
      refreshCache(files.toKtFiles())
    }
  }

  override fun projectClosed() {}

  /**
   * Collects all Kotlin files of the current project which are source files for Meta transformations.
   */
  private fun collectProjectKtFiles(): List<VirtualFile> {
    val files = FileTypeIndex.getFiles(KotlinFileType.INSTANCE, project.projectScope()).filter {
      it.isValid && it.isInLocalFileSystem
    }
    LOG.info("collectKtFiles(): ${files.size} kotlin files found for project ${project.name}")
    return files
  }

  fun packageList(): List<FqName> {
    val packages = LinkedHashSet<FqName>()
    transformedFiles.values.forEach { packages.add(it.packageFqName) }
    return packages.toList()
  }

  fun resolved(name: FqName): List<DeclarationDescriptor>? {
    return resolved[name]
  }

  private fun List<VirtualFile>.toKtFiles(): List<KtFile> {
    return mapNotNull {
      when {
        // fixme make sure that files belong to the current project?
        it.isValid && it.isInLocalFileSystem && it.fileType is KotlinFileType ->
          // fixme use ViewProvider's files instead?
          psiManager.findFile(it) as? KtFile
        else -> null
      }
    }
  }

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
        // this replaces the files with transformed files in resultFiles
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

    LOG.info("refreshCache(): updating/adding ${updatedFiles.size} files, cached ${transformedFiles.size} files")
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
      if (transformedFiles.isNotEmpty()) {
        if (!ApplicationManager.getApplication().isUnitTestMode) {
          ApplicationManager.getApplication().invokeLater {
            WriteAction.run<Exception> {
              transformedFiles.forEach { (original, transformed) ->
                val metaFile = original.virtualFile.parent.findOrCreateChildData(this, transformed.name + ".txt")
                metaFile.setBinaryContent(transformed.text.toByteArray())
              }
            }
          }
        }
      }

      if (resetCache) {
        transformedFiles.clear()
      }
      transformedFiles.putAll(transformed)

      // update the resolved data when index access is possible
      // fixme protect against multiple transformations at once
      DumbService.getInstance(project).runReadActionInSmartMode {
        LOG.info("resolving descriptors of transformed files: $transformedFiles files")
        val kotlinCache = KotlinCacheService.getInstance(project)

        // the resolve facade must get all files in the project for a successful resolve
        // files, which were transformed must not be passed, because the transformation result is already added
        val transformedSourceFiles = transformedFiles.map { it.key }.toSet()
        val untransformedSourceFiles = updatedFiles.filter { it !in transformedSourceFiles }
        val transformationResults = transformedFiles.values

        val filesForResolve = untransformedSourceFiles + transformationResults
        if (filesForResolve.isNotEmpty()) {
          if (resetCache) {
            resolved.clear()
          }

          val facade = kotlinCache.getResolutionFacade(filesForResolve)
          // fixme: remove descriptors which belong to the newly transformed files
          transformedFiles.forEach { (_, transformedFile) ->
            val packageName = transformedFile.packageFqName

            val newDeclarations = transformedFile.declarations.map { facade.resolveToDescriptor(it, BodyResolveMode.FULL) }
            val cachedDescriptors = resolved[packageName] ?: emptyList()
            val leftovers = cachedDescriptors.filterNot { it in newDeclarations }
            val newCachedPackageDescriptors = newDeclarations + leftovers

            val synthDescriptors = newCachedPackageDescriptors.filter { it.isMetaSynthetic() }
            if (synthDescriptors.isNotEmpty()) {
              synthDescriptors.forEach { synthDescriptor ->
                try {
                  if (synthDescriptor is LazyEntity) synthDescriptor.forceResolveAllContents()
                } catch (e: IndexNotReadyException) {
                  LOG.warn("Index wasn't ready to resolve: ${synthDescriptor.name}")
                }
              }
              resolved[packageName] = synthDescriptors
            }
          }

          // refresh the highlighting of the current editor, using the new cache
          DaemonCodeAnalyzer.getInstance(project).restart()
        }
      }
    }
  }

  /**
   *  we only care about changes to Kotlin files
   */
  override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier? {
    return when {
      events.any { e -> e.isValid && e.file?.let { it.isValid && it.isInLocalFileSystem && it.fileType is KotlinFileType } == true } -> this
      else -> null
    }
  }

  override fun afterVfsChange() {
    LOG.debug("MetaSyntheticPackageFragmentProvider.afterVfsChange")
    refreshCache(collectProjectKtFiles().toKtFiles())
  }

  override fun dispose() {
    transformedFiles.clear()
    resolved.clear()
  }

  @TestOnly
  fun reset() {
    transformedFiles.clear()
    resolved.clear()
  }

  @TestOnly
  fun forceRebuild() {
    reset()
    refreshCache(collectProjectKtFiles().toKtFiles())
    flush()
  }

  @TestOnly
  fun flush() {
    pool.submit { }.get(5000, TimeUnit.MILLISECONDS)
  }
}
