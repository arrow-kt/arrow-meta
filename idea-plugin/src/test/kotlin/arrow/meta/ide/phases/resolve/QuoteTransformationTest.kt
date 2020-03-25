package arrow.meta.ide.phases.resolve

import arrow.meta.ide.plugins.quotes.cache.QuoteCache
import arrow.meta.ide.plugins.quotes.resolve.isMetaSynthetic
import arrow.meta.ide.testing.unavailable
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.core.moveCaret
import org.jetbrains.kotlin.name.FqName
import org.junit.Ignore
import org.junit.Test

class QuoteTransformationTest : LightPlatformCodeInsightFixture4TestCase() {

  @Test
  fun higherKindTransformation() {
    val code = """
      package testArrow
      
      import arrow.higherkind

      @higherkind
      class Id1<out A>(val value: A)
      
      val id : Id1Of<Int> = Id1(42)
    """.trimIndent()

    val file = myFixture.configureByText(KotlinFileType.INSTANCE, code)
    myFixture.openFileInEditor(file.virtualFile)

    myFixture.editor.moveCaret(myFixture.file.text.indexOf("Id1Of"))
    project.testQuoteSystem()?.forceRebuild(project)
      ?: throw unavailable(TestQuoteSystemService::class.java)
    val psi = myFixture.elementAtCaret
    assertEquals("@arrow.synthetic typealias Id1Of<A> = arrow.Kind<ForId1, A>", psi.text)
  }

  @Test
  fun higherKindTransformationTyping() {
    val code = """
      package testArrow
      
      import arrow.higherkind

      @higherkind
      class Id2<out A>(val value: A)
    """.trimIndent()

    val file = myFixture.configureByText(KotlinFileType.INSTANCE, code)
    myFixture.openFileInEditor(file.virtualFile)

    myFixture.editor.moveCaret(file.textLength)
    myFixture.type("\nval x: Id2Of<Int> = Id2(42)")

    myFixture.editor.moveCaret(myFixture.file.text.indexOf("Id2Of"))

    project.testQuoteSystem()?.forceRebuild(project)
      ?: throw unavailable(TestQuoteSystemService::class.java)
    val psi = myFixture.elementAtCaret
    assertEquals("@arrow.synthetic typealias Id2Of<A> = arrow.Kind<ForId2, A>", psi.text)
  }

  // fixme: this test is still failing, see below for the reason
  @Test
  @Ignore
  fun higherKindAllCacheItemsResolved() {
    val code = """
      package testArrow
      import arrow.higherkind

      @higherkind
      class Old<caret>Id<out A>(val value: A)
    """.trimIndent()

    project.testQuoteSystem()?.let { quoteService: TestQuoteSystemService ->
      project.getService(QuoteCache::class.java)?.let { cache: QuoteCache ->
        myFixture.configureByText(KotlinFileType.INSTANCE, code)
        quoteService.forceRebuild(project)

        val descriptors = cache.descriptors(FqName("testArrow")).orEmpty()
        assertEquals(5, descriptors.size)
        descriptors.forEach {
          assertTrue(it.isMetaSynthetic())
          // fixme @arrow.synthetic and @arrow.Kind are unresolved,
          //  we need to create a module dependency to arrow-annotations in the test project (at runtime)
          assertFalse("IntelliJ's error debug markers must not exist, as they indicate unresolved references: $it", it.toString().contains("@[ERROR"))
        }
      } ?: throw unavailable(QuoteCache::class.java)
    } ?: throw unavailable(TestQuoteSystemService::class.java)
  }
}