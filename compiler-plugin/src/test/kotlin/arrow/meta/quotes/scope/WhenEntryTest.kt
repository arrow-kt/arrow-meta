package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.WhenEntryPlugin
import io.kotlintest.specs.AnnotationSpec

class WhenEntryTest : AnnotationSpec() {

  private val whenEntry = """
                         | //metadebug
                         | 
                         | class Wrapper {
                         |   fun doMaths(x: Int) {
                         |     when {
                         |       x + 2 == 4 -> println("I can do maths")
                         |       else -> println("I cannot do maths")
                         |     }
                         |   }
                         | }
                         | """.source

  @Test
  fun `Validate when entry scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(WhenEntryPlugin())) },
      code = { whenEntry },
      assert = { quoteOutputMatches(whenEntry) }
    ))
  }
}
