package arrow.meta.ide.phases.resolve

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.name.FqName
import org.junit.Test

class QuoteSystemCacheTest : LightPlatformCodeInsightFixture4TestCase() {
  @Test
  fun testIncrementalCacheUpdate() {
    val code = """
      package testArrow
      import arrow.higherkind

      @higherkind
      class IdOriginal<out A>(val value: A)
    """.trimIndent()

    val file = myFixture.configureByText(KotlinFileType.INSTANCE, code)
    myFixture.openFileInEditor(file.virtualFile)

    // this is the initial rebuild and the initial cache population
    val cache = QuoteSystemCache.getInstance(project)
    cache.forceRebuild()

    val cachedElements = cache.resolved(FqName("testArrow"))
    assertNotNull(cachedElements)
    assertEquals(5, cachedElements!!.size)

    val updatedCode = """
      package testArrow
      import arrow.higherkind

      @higherkind
      class IdRenamed<out A>(val value: A)
    """.trimIndent()

    runWriteAction {
      // this incrementally updates the cache
      myFixture.editor.document.setText(updatedCode)
    }
    cache.flush()
    val newCachedElements = cache.resolved(FqName("testArrow"))
    assertNotNull(newCachedElements)
    assertEquals("the cache must only contain the new psi elements and must have dropped the previously cached elements",
      5, newCachedElements!!.size)
    assertTrue("previously cached elements of the updated PsiFile must have been dropped from the cache",
      cachedElements.none { newCachedElements.contains(it) })
  }
}