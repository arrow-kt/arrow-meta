package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ForExpressionPlugin
import org.junit.jupiter.api.Test

class ForExpressionTest {

    companion object {
        private val someFunction = """
                | fun someFunction() {
                |   for (i in 1..10) {
                |     println(i)
                |   }
                | }
                | """.forExpression()

      private val singleLineFunction = """
                  | fun singleLineFunction() {
                  |     for (i in 1..10) println(i)
                  | }
                  |   """.forExpression()

      private val destructuringDeclarationFunction = """
                  | fun destructuringDeclarationFunction() {
                  |         for ((index, value) in listOf("a", "b", "c").withIndex()) {
                  |              println("index: " + index)
                  |              println("value: " + value)
                  |         }
                  | }
                  |   """.forExpression()

        private fun String.forExpression(): Code.Source {
            return """
                      | //metadebug
                      | 
                      | class Wrapper {
                      |   $this
                      | }
                      | """.source
        }

        val forExpressions = arrayOf(
          someFunction,
          singleLineFunction,
          destructuringDeclarationFunction
        )
    }

    @Test
    fun `Validate for expression scope properties`() {
        validate(someFunction)
    }

    @Test
    fun `Validate for expression single line scope properties`() {
        validate(singleLineFunction)
    }

    @Test
    fun `Validate for expression destructuring declaration scope properties`() {
        validate(destructuringDeclarationFunction)
    }

    fun validate(source: Code.Source) {
        assertThis(CompilerTest(
            config = { listOf(addMetaPlugins(ForExpressionPlugin())) },
            code = { source },
            assert = { quoteOutputMatches(source) }
        ))
    }
}
