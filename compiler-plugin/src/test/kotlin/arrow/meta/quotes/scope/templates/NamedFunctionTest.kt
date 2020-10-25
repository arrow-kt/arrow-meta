package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.NamedFunctionPlugin
import org.junit.jupiter.api.Test
import arrow.meta.plugin.testing.CompilerTest.Companion.source

class NamedFunctionTest {

    companion object {
        private val helloWorldFunction = """ fun helloWorld(): String = "Hello ΛRROW Meta!" """.namedFunction()

        private val helloWorldPrivateFunction = """ private fun helloWorld(): String = "Hello ΛRROW Meta!" """.namedFunction()

        private val helloWorldInlineFunction = """ private inline fun helloWorld(): String = "Hello ΛRROW Meta!" """.namedFunction()

        private val extensionFunction = """ fun Int.identity() = this """.namedFunction()

        private fun String.namedFunction(): Code.Source {
            return """
      | //metadebug
      | 
      | class Wrapper {
      |    $this
      |  }
      | """.trimMargin().trim().source
        }
    }

    @Test
    fun `base case`() {
        validate(helloWorldFunction)
    }

    @Test
    fun `function with visibility modifier`() {
        validate(helloWorldPrivateFunction)
    }

    @Test
    fun `function with modality modifier`() {
        validate(helloWorldInlineFunction)
    }

    @Test
    fun `extension function`() {
        validate(extensionFunction)
    }

    private fun validate(source: Code.Source) {
        assertThis(CompilerTest(
          config = { listOf(addMetaPlugins(NamedFunctionPlugin())) },
          code = { source },
          assert = { quoteOutputMatches(source) }
        ))
    }
}