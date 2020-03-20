package arrow.meta.ide.phases.resolve

import arrow.meta.ide.dsl.utils.quoteRelevantFiles
import arrow.meta.ide.plugins.quotes.QuoteCache
import arrow.meta.ide.plugins.quotes.QuoteSystemService
import arrow.meta.ide.plugins.quotes.cacheStrategy
import arrow.meta.internal.Noop
import arrow.meta.quotes.analysisIdeExtensions
import com.intellij.openapi.project.Project
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import com.intellij.util.concurrency.BoundedTaskExecutor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.util.concurrent.TimeUnit

interface TestQuoteSystemService {
  val service: QuoteSystemService

  fun flush(): Unit =
    service.context.run {
      editorQueue.flush()
      docExec.safeAs<BoundedTaskExecutor>()?.waitAllTasksExecuted(5, TimeUnit.SECONDS)
      cacheExec.safeAs<BoundedTaskExecutor>()?.waitAllTasksExecuted(5, TimeUnit.SECONDS)
    }


  fun forceRebuild(project: Project): Unit {
    val quoteFiles = project.quoteRelevantFiles()
    val cache = project.getService(QuoteCache::class.java)
    LOG.info("collected ${quoteFiles.size} quote relevant files for Project:${project.name}")
    service.refreshCache(cache, project, quoteFiles, analysisIdeExtensions, cacheStrategy())
    flush()
  }
}

fun toTestEnv(service: QuoteSystemService): TestQuoteSystemService =
  object : TestQuoteSystemService {
    override val service: QuoteSystemService = service
  }

fun Project.testQuoteSystem(): TestQuoteSystemService? =
  getService(QuoteSystemService::class.java)?.let(::toTestEnv)

fun updateAndAssertCache(
  cache: QuoteCache,
  service: TestQuoteSystemService,
  myFixture: CodeInsightTestFixture,
  file: KtFile,
  content: String,
  sizeBefore: Int,
  sizeAfter: Int,
  assertRetained: (List<DeclarationDescriptor>) -> Unit = Noop.effect1
) {
  val cachedElements = cache.descriptors(file.packageFqName)
  LightPlatformCodeInsightFixture4TestCase.assertEquals("Unexpected number of cached items", sizeBefore, cachedElements.size)

  runWriteAction {
    myFixture.openFileInEditor(file.virtualFile)
    myFixture.editor.document.setText(content)
  }
  service.flush()

  val newCachedElements = cache.descriptors(file.packageFqName)
  LightPlatformCodeInsightFixture4TestCase.assertEquals("Unexpected number of cached items", sizeAfter, newCachedElements.size)

  val retained = newCachedElements.filter { cachedElements.contains(it) }
  assertRetained(retained)
}