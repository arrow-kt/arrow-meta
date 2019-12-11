package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import io.kotlintest.specs.AnnotationSpec

// TODO implement convertFinally in Converter to support FINALLY in AST

class FinallySectionTest : AnnotationSpec() {

  private val finallySection = """
                         | package 47deg.arrow-meta
                         | 
                         | //metadebug
                         | 
                         | fun measureTimeMillis(block: () -> Unit): Long {
                         |    val start = System.currentTimeMillis()
                         |    try {
                         |      block()
                         |    } finally {
                         |      return System.currentTimeMillis() - start
                         |    }
                         |  }
                         | """.source

  @Ignore
  @Test
  fun `Validate finally section scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins()) },  // TODO create a scope plugin for finally section
      code = { finallySection },
      assert = { quoteOutputMatches(finallySection) }
    ))
  }
}
