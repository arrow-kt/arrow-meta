package arrow.meta.ide.phases.resolve

import arrow.meta.internal.Noop
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.KtFile

interface TestQuoteSystemService {
  val service: QuoteSystemService

  fun flush() {
    //editorUpdateQueue.flush()

    // use sleep, until we find a better way to wait for non blocking read actions
    Thread.sleep(5000)
  }

  fun forceRebuild(project: Project): Unit {
    //TODO: service.refreshCache(project, quoteSystem.cache, project.quoteRelevantFiles())
    flush()
  }
}

fun toTestEnv(service: QuoteSystemService): TestQuoteSystemService =
  object : TestQuoteSystemService {
    override val service: QuoteSystemService = service
  }

fun Project.testQuoteSystem(): TestQuoteSystemService? =
  getService(QuoteSystemService::class.java)?.let(::toTestEnv)

fun updateAndAssertCache(cache: QuoteSystemCache, project: Project, myFixture: CodeInsightTestFixture, toUpdate: PsiFile, content: String, sizeBefore: Int, sizeAfter: Int, assertRetained: (List<DeclarationDescriptor>) -> Unit = Noop.effect1) {
  val packageFqName = (toUpdate as KtFile).packageFqName
  val cachedElements = cache.descriptors(packageFqName)
  LightPlatformCodeInsightFixture4TestCase.assertEquals("Unexpected number of cached items", sizeBefore, cachedElements.size)

  runWriteAction {
    myFixture.openFileInEditor(toUpdate.virtualFile)
    myFixture.editor.document.setText(content)
  }
  cache.flushForTest()

  val newCachedElements = cache.descriptors(packageFqName)
  LightPlatformCodeInsightFixture4TestCase.assertEquals("Unexpected number of cached items", sizeAfter, newCachedElements.size)

  val retained = newCachedElements.filter { cachedElements.contains(it) }
  assertRetained(retained)
}