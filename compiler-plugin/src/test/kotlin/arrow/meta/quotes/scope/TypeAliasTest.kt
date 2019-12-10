package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import io.kotlintest.specs.AnnotationSpec

// TODO Ast to Expr Conversion needed

class TypeAliasTest : AnnotationSpec() {

  private val typeAlias = """
                         | //metadebug
                         | 
                         | typealias Predicate<T> = (T) -> Boolean
                         | """.source

  @Ignore
  @Test
  fun `Validate type alias properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins()) }, // TODO create a scope plugin for finally section
      code = { typeAlias },
      assert = { quoteOutputMatches(typeAlias) }
    ))
  }
}
