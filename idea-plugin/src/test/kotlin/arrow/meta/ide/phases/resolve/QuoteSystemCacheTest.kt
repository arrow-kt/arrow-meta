package arrow.meta.ide.phases.resolve

import arrow.meta.quotes.ktFile
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.KtFile
import org.junit.Test

class QuoteSystemCacheTest : LightPlatformCodeInsightFixture4TestCase() {
  /** This test updates a PsiFile in place and validated the cache afterwards. */
  @Test
  fun testIncrementalCacheUpdate() {
    val file = myFixture.addFileToProject("testArrow/source.kt", "package testArrow")

    // this is the initial rebuild and the initial cache population
    val cache = QuoteSystemCache.getInstance(project)
    cache.forceRebuild()

    val code = """
      package testArrow
      import arrow.higherkind

      @higherkind
      class IdOriginal<out A>(val value: A)
    """.trimIndent()

    updateAndAssertCache(file, code, 0, 5)
  }

  /** This test creates two PsiFiles, updates one after the other in place and validates the cache state. */
  @Test
  fun testIncrementalCacheUpdateMultipleFiles() {
    val codeFirst = """
      package testArrow
      import arrow.higherkind
      @higherkind
      class IdOriginalFirst<out A>(val value: A)
    """.trimIndent()

    val codeSecond = """
      package testArrow
      import arrow.higherkind
      @higherkind
      class IdOriginalSecond<out A>(val value: A)
    """.trimIndent()

    val codeThird = """
      package testArrowOther
      import arrow.higherkind
      @higherkind
      class IdOriginalThird<out A>(val value: A)
    """.trimIndent()

    val fileFirst = myFixture.addFileToProject("testArrow/first.kt", codeFirst)
    val fileSecond = myFixture.addFileToProject("testArrow/second.kt", codeSecond)
    // fileThird is in a different package
    val fileThird = myFixture.addFileToProject("testArrowOther/third.kt", codeThird)

    // this is the initial rebuild and the initial cache population
    val cache = QuoteSystemCache.getInstance(project)
    cache.forceRebuild()

    updateAndAssertCache(fileFirst, codeFirst.replace("IdOriginalFirst", "IdRenamedFirst"), 10, 10) { retained ->
      assertTrue("nothing from the original file must be retained", retained.none { it.ktFile()?.name?.contains("first") == true })
    }
    updateAndAssertCache(fileSecond, codeSecond.replace("IdOriginalSecond", "IdRenamedSecond"), 10, 10) { retained ->
      assertTrue("nothing from the original file must be retained", retained.none { it.ktFile()?.name?.contains("second") == true })
    }
    updateAndAssertCache(fileThird, codeThird.replace("IdOriginalThird", "IdRenamedThird"), 5, 5) { retained ->
      assertTrue("previously cached elements of the updated PsiFile must have been dropped from the cache: $retained",
        retained.isEmpty())
    }

    // remove all meta-related source from the first file
    // and make sure that all the descriptors are removed from the cache
    updateAndAssertCache(fileFirst, "package testArrow", 10, 5) { retained ->
      assertTrue("nothing from the original file must be retained", retained.none { it.ktFile()?.name?.contains("first") == true })
    }
  }

  private fun updateAndAssertCache(toUpdate: PsiFile, content: String, sizeBefore: Int, sizeAfter: Int, assertRetained: (List<DeclarationDescriptor>) -> Unit = {}) {
    val cache = QuoteSystemCache.getInstance(project)

    val packageFqName = (toUpdate as KtFile).packageFqName
    val cachedElements = cache.resolved(packageFqName)
    assertEquals("Unexpected number of cached items", sizeBefore, cachedElements.size)

    runWriteAction {
      myFixture.openFileInEditor(toUpdate.virtualFile)
      myFixture.editor.document.setText(content)
    }
    cache.flush()

    val newCachedElements = cache.resolved(packageFqName).orEmpty()
    assertEquals("Unexpected number of cached items", sizeAfter, newCachedElements.size)

    val retained = newCachedElements.filter { cachedElements.contains(it) }
    assertRetained(retained)
  }
}

