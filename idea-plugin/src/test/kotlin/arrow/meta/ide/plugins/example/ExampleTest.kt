package arrow.meta.ide.plugins.example

import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import arrow.meta.ide.testing.resolves
import org.junit.Test

class ExampleTest : IdeTestSetUp() {
  @Test
  fun `test if lineMarker is displayed`(): Unit =
    ideTest(
      IdeTest(
        myFixture = myFixture,
        code = """
        | fun hello(): String =
        |   "Hello world!"
        """.trimIndent(),
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.PURE)
        },
        result = resolves("LineMarker Test for helloWorld") {
          it.takeIf { collected ->
            collected.lineMarker.size == 1
          }
        }
      )
    )
}