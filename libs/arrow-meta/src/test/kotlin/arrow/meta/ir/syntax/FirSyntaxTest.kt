package arrow.meta.ir.syntax

import arrow.meta.ir.plugin.FirSyntaxPlugin
import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.jupiter.api.Test

class FirSyntaxTest {

  @Test
  fun `Test FIR Syntax`() {
    testFirVisit("""
      private class Pepe
      val x = Pepe::class::visibility.toString()
      """.trimIndent()) { "x".source.evalsTo("PUBLIC") }
  }
}

private fun testFirVisit(src: String = "", assert: CompilerTest.Companion.() -> Assert): Unit =
  assertThis(
    CompilerTest(
      config = {
        listOf(addArguments("-Xuse-fir")) + metaDependencies + addMetaPlugins(FirSyntaxPlugin())
      },
      code = { src.source },
      assert = assert
    )
  )

fun ble() {
  class Pepe
  Pepe::class::visibility.toString()
}
