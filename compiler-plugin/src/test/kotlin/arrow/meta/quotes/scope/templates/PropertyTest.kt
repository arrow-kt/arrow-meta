package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.PropertyPlugin
import org.junit.Test

class PropertyTest {

  companion object {
    private val property = """
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

    private val propertyGetter = """
            | //metadebug
            |
            |class Address {
            |    val name: String = "Holmes, Sherlock"
            |       get() { return "The name is" + field }
            |}
            | """.source

    private val propertySetter = """
            | //metadebug
            |
            | class Address {
            |    var name: String = "Holmes, Sherlock"
            |       set(value) {
            |            field = "The name is" + value
            |        }
            |   }
            | """.source

    private val propertyDelegate = """
            | //metadebug
            |
            | class Address {
            |     val name: String by lazy { "Holmes, Sherlock" }
            | }
            | """.source

    private val propertyDescriptor = """
            | //metadebug
            |
            | class Address {
            |     val descriptorEvaluation: String = ""
            | }
            | """.source

    val propertyExpressions = arrayOf(
      property,
      propertyGetter,
      propertySetter,
      propertyDelegate
    )
  }

  @Test
  fun `validate property scope properties`() {
    validate(property)
  }

  @Test
  fun `validate property getter scope properties`() {
    validate(propertyGetter)
  }

  @Test
  fun `validate property setter scope properties`() {
    validate(propertySetter)
  }

  @Test
  fun `validate property delegate scope properties`() {
    validate(propertyDelegate)
  }

  @Test
  fun `validate property descriptor`() {
    validate(
      propertyDescriptor,
      """
            | //metadebug
            |
            | class Address {
            |     val descriptorEvaluation: Boolean = true
            | }
            | """.source
    )
  }

  fun validate(source: Code.Source, transformedSource: Code.Source = source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(PropertyPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(transformedSource) }
    ))
  }
}