package arrow.meta.plugins.proofs

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.newMetaDependencies
import org.junit.jupiter.api.Test

class ContextTest {

  @Test
  fun `context injection`() {
    givenTest(
      source =
        """
        @contextual fun n(): Int = 0  
          
        fun Int.program(): String = "oops"  
        val result = resolve { program() }
        
      """,
      expected = "result" to 0
    )
  }

  val prelude =
    """
    package test
    
    annotation class contextual

    fun <A> resolve(f: Nothing.() -> A): A =
      TODO()
    //metadebug
  """.trimIndent()

  private fun givenTest(source: String, expected: Pair<String, Any?>) {
    val codeSnippet = """
       $prelude
       $source
      """
    assertThis(
      CompilerTest(
        config = { listOf(addArguments("-Xuse-fir")) + newMetaDependencies() },
        code = { codeSnippet.source },
        assert = { allOf(expected.first.source.evalsTo(expected.second)) }
      )
    )
  }
}

fun <A> resolve(f: Nothing.() -> A): A = TODO()
fun <A> resolve(): A = TODO()
val resolve: Nothing get() = TODO()

fun Int.program(n: Int = resolve): String = "oops"

class Activity {
  val n: Int = resolve()

  fun onCreate() {
  }
}


fun main() {
  resolve { program() }
}
