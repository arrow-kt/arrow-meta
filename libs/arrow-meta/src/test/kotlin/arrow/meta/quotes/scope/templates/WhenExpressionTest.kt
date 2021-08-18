package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.WhenExpressionPlugin
import org.junit.jupiter.api.Test

class WhenExpressionTest {

  companion object {
    val doMaths = """
            | //metadebug
            |
            |   fun doMaths(x: Int) {
            |     when {
            |       x + 2 == 4 -> { println("I can do maths") }
            |       else -> { println("I cannot do maths") }
            |     }
            |   }
            | """.source

    val singleExpressionFunction = """
            | //metadebug
            |
            |   fun doMaths(x: Int) = when(x) {
            |     in 1..10 -> { println("it's in the range") }
            |     else -> { println("not on it!") }
            |   }
            | """.source

    val sealedClass = """
            | //metadebug
            |   
            | sealed class SupportedMaths {
            |    object Add: SupportedMaths()
            |    object Multiply: SupportedMaths()
            | }
            | 
            | fun doMaths(numbers: SupportedMaths, x: Int, y: Int) = when (numbers) {
            |     is SupportedMaths.Add -> println(x + y)
            |     is SupportedMaths.Multiply -> println(x * y)
            | }
            | """.source

    val whenExpressions = arrayOf(
      doMaths,
      singleExpressionFunction // ,
      // sealedClass Todo: Make PBT fails
    )
  }

  @Test
  fun `Validate when expression scope properties`() {
    validate(doMaths)
  }

  @Test
  fun `Validate when expression with single expression function`() {
    validate(singleExpressionFunction)
  }

  @Test
  fun `Validate when expression with sealed classes`() {
    validate(sealedClass)
  }

  fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(WhenExpressionPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }
}
