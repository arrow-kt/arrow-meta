package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.PropertyPlugin
import org.junit.Test

class PropertyTest {

  companion object {
    val property = """
            | //metadebug
            |
            | class Address {
            |     val name: String = "Holmes, Sherlock"
            |     private var street: String = "Baker"
            |     var city: String = "London"
            |     var state: String? = ""
            |     private var zip: String = "123456"
            | }
            | """.source
  }

  @Test
  fun `validate property scope properties`() {
    validate(property)
  }

  fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(PropertyPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }
}