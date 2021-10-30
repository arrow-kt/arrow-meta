package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.PropertyAccessorPlugin
import org.junit.jupiter.api.Test

class PropertyAccessorTest {
  companion object {
    private val propertyAccessorGet =
      """
            | //metadebug
            |
            | class Wrapper {
            |   var isPropertyAccessor: Boolean = true
            |    get() { return true }
            |  }
            | """.source

    private val propertyAccessorSet =
      """
            | //metadebug
            |
            | class Wrapper {
            |   var isPropertyAccessor: Boolean = true
            |    set(value) { field = !value }
            |  } 
            | """.source

    private val propertyAccessorVisibility =
      """
            | //metadebug
            |
            | class Wrapper {
            |   var isPropertyAccessor: Boolean = true
            |    private set(value) { field = !value }
            |  } 
            | """.source

    val propertyAccessorExpressions =
      arrayOf(propertyAccessorGet, propertyAccessorSet, propertyAccessorVisibility)
  }

  @Test
  fun `validate property accessor get scope properties`() {
    validate(propertyAccessorGet)
  }

  @Test
  fun `validate property accessor set scope properties`() {
    validate(propertyAccessorSet)
  }

  @Test
  fun `validate property accessor visibility scope properties`() {
    validate(propertyAccessorVisibility)
  }

  fun validate(source: Code.Source) {
    assertThis(
      CompilerTest(
        config = { listOf(addMetaPlugins(PropertyAccessorPlugin())) },
        code = { source },
        assert = { quoteOutputMatches(source) }
      )
    )
  }
}
