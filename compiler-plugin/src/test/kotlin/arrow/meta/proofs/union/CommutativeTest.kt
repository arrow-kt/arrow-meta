package arrow.meta.proofs.union

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class CommutativeTest {
  
  @Test
  fun `test commutative with 2 arity`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.*
           |
           |val f: Union2<String, Int> = 0
           |val x: Union2<Int, String> = f
           |val y: Int? = x
           |""".source
      },
      assert = {
        allOf("y".source.evalsTo(0))
      }
    ))
  }
  
  @Test
  fun `test commutative with 3 arity`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.*
           |
           |val f: Union3<String, Int, Double> = 0
           |val x: Union3<Double, Int, String> = f
           |val y: Int? = x
           |""".source
      },
      assert = {
        allOf("y".source.evalsTo(0))
      }
    ))
  }
}