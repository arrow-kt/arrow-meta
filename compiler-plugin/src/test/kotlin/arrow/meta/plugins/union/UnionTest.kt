package arrow.meta.plugins.union

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UnionTest {
//
//  @Test
//  fun `Union uber constructor remains visible`() {
//    assertThis(CompilerTest(
//      config = { metaDependencies },
//      code = {
//        """
//        |${UnionTestPrelude}
//        |
//        |fun f(): Union2<String, Int> = Union(0)
//        |
//        """.source
//      },
//      assert = {
//        allOf("f().value".source.evalsTo(0))
//      }
//    ))
//  }

  @Test
  fun `Union accepts typed values in the union 2`() {
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
        allOf("f().value".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union accepts typed values in the union 3`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union3<String, Int, Double> = 0
        | 
        """.source
      },
      assert = {
        allOf("f().value".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union accepts typed values in the union 4`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union4<String, Int, Double, List<Int>> = 0
        | 
        """.source
      },
      assert = {
        allOf("f().value".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union accepts typed values in the union`() {
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
        allOf("f().value".source.evalsTo("a"))
      }
    ))
  }

  @Test
  fun `Union rejects typed values not in the union`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union2<String, Int> = 0.0
        | 
        """.source
      },
      assert = {
        allOf(failsWith { it.contains("The floating-point literal does not conform to the expected type Union2<String, Int>") })
      }
    ))
  }

  @Test
  fun `Union can convert to nullable types also present in the union 2`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union2<String, Int> = "a"
        |fun z(): String? = f()
        """.source
      },
      assert = {
        allOf("z()".source.evalsTo("a"))
      }
    ))
  }

  @Test
  fun `Union can convert to nullable types also present in the union 3`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union3<String, Int, Double> = 0
        |fun z(): Int? = f()
        """.source
      },
      assert = {
        allOf("z()".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union can convert to nullable types also present in the union 4`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union4<String, Int, Double, List<Int>> = 0
        |fun z(): Int? = f()
        """.source
      },
      assert = {
        allOf("z()".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union converts to nullable type with null when the union value is absent 2`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union2<String, Int> = 0
        |fun z(): String? = f()
        """.source
      },
      assert = {
        allOf("z()".source.evalsTo(null))
      }
    ))
  }

  @Test
  fun `Union converts to nullable type with null when the union value is absent 3`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union3<String, Int, Double> = 0
        |fun z(): String? = f()
        """.source
      },
      assert = {
        allOf("z()".source.evalsTo(null))
      }
    ))
  }

  @Test
  fun `Union converts to nullable type with null when the union value is absent 4`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union4<String, Int, Double, List<Int>> = 0
        |fun z(): String? = f()
        """.source
      },
      assert = {
        allOf("z()".source.evalsTo(null))
      }
    ))
  }

  @Test
  fun `Union can convert to nullable types letting inference pass through`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union2<String, Int> = 0
        |fun x() = f()
        |fun z(): Int? = x()
        """.source
      },
      assert = {
        allOf("z()".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union fails to convert nullable types not present in the union`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        |${UnionTestPrelude}
        |
        |fun f(): Union2<String, Int> = 0
        |fun z(): Double? = f()
        """.source
      },
      assert = {
        allOf(failsWith { it.contains("Type mismatch: inferred type is Union2<String, Int> but Double? was expected") })
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
        allOf("f12().value".source.evalsTo("x"))
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
        |      inline fun <reified A> toNullable(union: UnionSyntax): A? {
        |        val value = (union as Union).value
        |        return if (value != null && value is A) value
        |        else null
        |      }
        |    }
        |}
        |""".trimMargin()
}
