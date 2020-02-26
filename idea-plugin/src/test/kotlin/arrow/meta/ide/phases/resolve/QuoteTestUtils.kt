package arrow.meta.ide.phases.resolve

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.KtFile
import java.util.concurrent.TimeUnit

interface TestQuoteSystemService {
  val service: QuoteSystemService

  fun flush(): Any? = service.exec.submit { }.get(5000, TimeUnit.MILLISECONDS)
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


fun updateAndAssertCache(cache: QuoteSystemCache, project: Project, myFixture: CodeInsightTestFixture, toUpdate: PsiFile, content: String, sizeBefore: Int, sizeAfter: Int, assertRetained: (List<DeclarationDescriptor>) -> Unit = {}) {
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