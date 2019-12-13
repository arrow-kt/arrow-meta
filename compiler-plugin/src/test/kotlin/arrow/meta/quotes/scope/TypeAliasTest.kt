package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.TypeAliasPlugin
import io.kotlintest.specs.AnnotationSpec

class TypeAliasTest : AnnotationSpec() {

  @Test
  fun `Validate type alias properties`() {
    val typeAlias = """
                         | //metadebug
                         | 
                         | typealias IntegerPredicate = (Int) -> Boolean
                         | """.source

    validate(typeAlias)
  }

  @Test
  fun `Validate type alias with constraints properties`() {
     val typeAlias = """
                         | //metadebug
                         | 
                         | typealias Predicate<Int> = (Int) -> Boolean
                         | """.source

    validate(typeAlias)
  }
  
  @Test
  fun `Validate type alias properties with generics`() {
    val typeAlias = """
                         | //metadebug
                         | 
                         | typealias Predicate<T> = (T) -> Boolean
                         | """.source

    validate(typeAlias)
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(TypeAliasPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }
}
