package arrow.meta.plugins.higherkind

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class TypeProofTest {

  @Test
  fun `Union accepts typed values in the union 3`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union3
           |fun f(): Union3<String, Int, Double> = 0
           |val x: Int? = f()
           |""".source
      },
      assert = {
        allOf("x".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union fails to convert nullable types not present in the union`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = 0
           |fun z(): Double? = f()
           |""".source
      },
      assert = {
        allOf(failsWith { it.contains("Type mismatch: inferred type is Union2<String, Int> but Double? was expected") })
      }
    ))
  }

  @Test
  fun `Union accepts typed values in the union 2`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.*
           |
           |fun f(): Union2<String, Int> = 0
           |val x: Int? = f()
           |""".source
      },
      assert = {
        allOf("x".source.evalsTo(0))
      }
    ))
  }

}
