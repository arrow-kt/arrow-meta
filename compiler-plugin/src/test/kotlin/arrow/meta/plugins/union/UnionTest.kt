package arrow.meta.plugins.union

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.Test


class UnionTest {

  @Test
  fun `Union uber constructor remains visible`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union2<String, Int> = Union(0)
        | 
        """.source
      },
      assert = {
        "f().value".source.evalsTo(0)
      }
    ))
  }

  @Test
  fun `Union implicitly converts from a typed value in the Union`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union2<String, Int> = 0
        | 
        """.source
      },
      assert = {
        "f().value".source.evalsTo(0)
      }
    ))
  }

  @Test
  fun `Union implicitly converts from a typed value in the Union 2`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union2<String, Int> = "a"
        | 
        """.source
      },
      assert = {
        "f().value".source.evalsTo("a")
      }
    ))
  }

  @Test
  fun `Union implicitly converts to a nullable type in the absent case`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union2<String, Int> = "a"
        |val x1: String? = Union.toNullable(f())
        |val x: String? = f()
        |fun z(): String? = x
        """.source
      },
      assert = {
        "z()".source.evalsTo("a")
      }
    ))
  }

  @Test
  fun `Union values don't require lifting`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun x1(): Union2<String, Int> = Union("a")
        |fun f1(): Union2<String, Int> = "a"
        |fun f12(): Union2<String, Int> { 
        | val s = "x"
        | val z: Union2<String, Int> = s
        | return z
        |}
        |fun f2(): Union2<String, Int> = 1
        |fun f3(): String? = f1()
        |fun f4(): Int? = f2()
        |fun f5(): Union2<String, Int> = "a" as Union2<String, Int>
        | 
        """.source
      },
      assert = {
        "f12().value".source.evalsTo("x")
      }
    ))
  }

  private val UnionTestPrelude: String =
    """|
      |interface UnionSyntax {
      |  val value: Any?
      |}
       |interface Union2<out A, out B> : UnionSyntax
        |interface Union3<out A, out B, out C> : Union2<A, B>
        |interface Union4<out A, out B, out C, out D> : Union3<A, B, C>
        |
        |
        |inline class Union(override val value: Any?) :
        |  Union2<Nothing, Nothing>,
        |  Union3<Nothing, Nothing, Nothing>,
        |  Union4<Nothing, Nothing, Nothing, Nothing> {
        |  
        |    companion object {
        |      @Suppress("UNCHECKED_CAST")
        |      fun <A> toNullable(union: UnionSyntax): A? =
        |        (union as Union).value as? A
        |    }
        |}
        |""".trimMargin()
}
