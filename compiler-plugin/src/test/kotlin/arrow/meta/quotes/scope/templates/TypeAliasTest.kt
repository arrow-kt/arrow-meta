package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.TypeAliasPlugin
import org.junit.Test

class TypeAliasTest  {

  companion object {
    private val typeAlias = """
                         | //metadebug
                         | 
                         | typealias IntegerPredicate = (Int) -> Boolean
                         | """.source

    private val typeAliasWithConstraints = """
                         | //metadebug
                         | 
                         | typealias Predicate<Int> = (Int) -> Boolean
                         | """.source

    private val typeAliasWithGenerics = """
                         | //metadebug
                         | 
                         | typealias Predicate<T> = (T) -> Boolean
                         | """.source

    private val typeAliasDescriptor = """
                         | //metadebug
                         | 
                         | typealias DescriptorEvaluation = String
                         | """.source

    val typeAliasExpressions = arrayOf(
      typeAlias,
      typeAliasWithConstraints,
      typeAliasWithGenerics
    )
  }

  @Test
  fun `Validate type alias properties`() {
    validate(typeAlias)
  }

  @Test
  fun `Validate type alias with constraints properties`() {
    validate(typeAliasWithConstraints)
  }
  
  @Test
  fun `Validate type alias properties with generics`() {
    validate(typeAliasWithGenerics)
  }

  @Test
  fun `Validate type alias descriptor`() {
    validate(
      typeAliasDescriptor,
      """
      | //metadebug
      | 
      | typealias DescriptorEvaluation = Boolean
      | """.source
    )
  }

  private fun validate(source: Code.Source, transformedSource: Code.Source = source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(TypeAliasPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(transformedSource) }
    ))
  }
}
