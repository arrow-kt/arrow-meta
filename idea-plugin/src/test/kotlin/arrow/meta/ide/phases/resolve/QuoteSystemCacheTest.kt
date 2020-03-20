package arrow.meta.ide.phases.resolve

import arrow.meta.ide.plugins.quotes.QuoteCache
import arrow.meta.ide.testing.UnavailableService
import arrow.meta.quotes.ktFile
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import org.junit.Test

class QuoteSystemComponentTest : LightPlatformCodeInsightFixture4TestCase() {
  /** This test updates a PsiFile in place and validated the cache afterwards. */
  @Test
  fun testIncrementalCacheUpdate() {
    myFixture.addFileToProject("testArrow/source.kt", "package testArrow")
      .safeAs<KtFile>()
      ?.let { file ->
        // this is the initial rebuild and the initial cache population
        project.testQuoteSystem()?.let { service: TestQuoteSystemService ->
          service.forceRebuild(project) //no need
          project.getService(QuoteCache::class.java)?.let { cache: QuoteCache ->
            val code = """
                package testArrow
                import arrow.higherkind
          
                @higherkind
                class IdOriginal<out A>(val value: A)
              """.trimIndent()

            updateAndAssertCache(cache, service, myFixture, file, code, 0, 5)
          } ?: throw UnavailableService(QuoteCache::class.java)
        } ?: throw UnavailableService(TestQuoteSystemService::class.java)
      }
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

    myFixture.addFileToProject("testArrow/first.kt", codeFirst).safeAs<KtFile>()?.let { first ->
      myFixture.addFileToProject("testArrow/second.kt", codeSecond).safeAs<KtFile>()?.let { second ->
        // fileThird is in a different package
        myFixture.addFileToProject("testArrowOther/third.kt", codeThird).safeAs<KtFile>()?.let { third ->

          // this is the initial rebuild and the initial cache population
          project.testQuoteSystem()?.let { service: TestQuoteSystemService ->
            project.getService(QuoteCache::class.java)?.let { cache: QuoteCache ->
              service.forceRebuild(project)

              updateAndAssertCache(cache, service, myFixture, first, codeFirst.replace("IdOriginalFirst", "IdRenamedFirst"), 10, 10) { retained ->
                assertTrue("nothing from the original file must be retained", retained.none { it.ktFile()?.name?.contains("first") == true })
              }
              updateAndAssertCache(cache, service, myFixture, second, codeSecond.replace("IdOriginalSecond", "IdRenamedSecond"), 10, 10) { retained ->
                assertTrue("nothing from the original file must be retained", retained.none { it.ktFile()?.name?.contains("second") == true })
              }
              updateAndAssertCache(cache, service, myFixture, third, codeThird.replace("IdOriginalThird", "IdRenamedThird"), 5, 5) { retained ->
                assertTrue("previously cached elements of the updated PsiFile must have been dropped from the cache: $retained",
                  retained.isEmpty())
              }

              // remove all meta-related source from the first file
              // and make sure that all the descriptors are removed from the cache
              updateAndAssertCache(cache, service, myFixture, first, "package testArrow", 10, 5) { retained ->
                assertTrue("nothing from the original file must be retained", retained.none { it.ktFile()?.name?.contains("first") == true })
              }
            } ?: throw UnavailableService(QuoteCache::class.java)
          } ?: throw UnavailableService(TestQuoteSystemService::class.java)
        }
      }
    }
  }
}
