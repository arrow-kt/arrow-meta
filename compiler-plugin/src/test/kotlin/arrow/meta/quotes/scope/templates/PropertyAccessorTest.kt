package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.PropertyAccessorPlugin
import org.junit.Test

class PropertyAccessorTest {
  companion object {
    val propertyAccessorGet = """
            | //metadebug
            |
            | class Wrapper {
            |   var isGePropertyAccessor: Boolean = true
            |    get() { return true }
            |  }
            | """.source

    val propertyAccessorSet = """
            | //metadebug
            |
            | class Wrapper {
            |   var isGePropertyAccessor: Boolean = true
            |    set(value) { field = !value }
            |  } 
            | """.source
  }

  @Test
  fun `validate property accessor get scope properties`() {
    validate(propertyAccessorGet)
  }

  @Test
  fun `validate property accessor set scope properties`() {
    validate(propertyAccessorSet)
  }

  fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(PropertyAccessorPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }
}