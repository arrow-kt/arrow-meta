package arrow.meta.ide.plugins.quotes

import arrow.meta.ide.plugins.quotes.cache.QuoteCache
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.unavailable
import arrow.meta.quotes.ktFile
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import org.junit.Ignore
import org.junit.Test

class QuoteCacheTest : IdeTestSetUp() {
  /** This test updates a PsiFile in place and validated the cache afterwards. */
  @Ignore
  /*
  com.intellij.openapi.progress.ProcessCanceledException: com.intellij.serviceContainer.AlreadyDisposedException: Cannot create ServiceAdapter(descriptor=ServiceDescriptor(interface=org.jetbrains.kotlin.idea.caches.resolve.IdePackageOracleFactory, implementation=org.jetbrains.kotlin.idea.caches.resolve.IdePackageOracleFactory), pluginDescriptor=PluginDescriptor(name=Kotlin, id=org.jetbrains.kotlin, path=/home/rachel/workspace/arrow-meta/idea-plugin/build/idea-sandbox/plugins-test/Kotlin)) because container is already disposed (container=Project (name=light_temp, containerState=ACTIVE, componentStore=/tmp/unitTest_incrementalCacheUpdate5/light_temp.ipr)  (disposed temporarily))
  java.lang.RuntimeException: com.intellij.openapi.progress.ProcessCanceledException: com.intellij.serviceContainer.AlreadyDisposedException: Cannot create ServiceAdapter(descriptor=ServiceDescriptor(interface=org.jetbrains.kotlin.idea.caches.resolve.IdePackageOracleFactory, implementation=org.jetbrains.kotlin.idea.caches.resolve.IdePackageOracleFactory), pluginDescriptor=PluginDescriptor(name=Kotlin, id=org.jetbrains.kotlin, path=/home/rachel/workspace/arrow-meta/idea-plugin/build/idea-sandbox/plugins-test/Kotlin)) because container is already disposed (container=Project (name=light_temp, containerState=ACTIVE, componentStore=/tmp/unitTest_incrementalCacheUpdate5/light_temp.ipr)  (disposed temporarily))
  */
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
          } ?: throw unavailable(QuoteCache::class.java)
        } ?: throw unavailable(TestQuoteSystemService::class.java)
      }
  }

  /** This test creates two PsiFiles, updates one after the other in place and validates the cache state. */
  @Ignore
  /*
  ERROR: Invalid parent: temp:///src/testArrow of file temp:///src/testArrow/second.kt, file.valid=false
  java.lang.Throwable: Invalid parent: temp:///src/testArrow of file temp:///src/testArrow/second.kt, file.valid=false
   */
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
            } ?: throw unavailable(QuoteCache::class.java)
          } ?: throw unavailable(TestQuoteSystemService::class.java)
        }
      }
    }
  }
}
