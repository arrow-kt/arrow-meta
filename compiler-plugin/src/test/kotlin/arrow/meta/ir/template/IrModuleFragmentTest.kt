package arrow.meta.ir.template

import arrow.meta.ir.IrPlugin
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.jupiter.api.Test

class IrModuleFragmentTest {

  @Test
  fun `Visits irModuleFragment`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(IrPlugin())) },
      code = {
        """
        package test
        
        val zero = 0
      """.trimIndent().source
      },
      assert = {
        failsWith {
          it.contains("IrModuleFragment is visited")
        }
      }
    ))
  }
}