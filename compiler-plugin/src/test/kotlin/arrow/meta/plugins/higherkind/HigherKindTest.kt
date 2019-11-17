package arrow.meta.plugins.higherkind

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class HigherKindTest {

  val prelude: String = """
    import arrowx.*
    
  """.trimIndent()

  @Test
  fun `Kind does not require an inheritance relationship`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|$prelude
           |val concrete: Id<Int> = Id(0)
           |val kinded: Kind<`Id(_)`, Int> = concrete
           |val back: Id<Int> = kinded
           |val value: Int = back.value
           |""".source
      },
      assert = {
        allOf("value".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Kind can target std lib kinds like List(_)`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|$prelude
           |val concrete: List<Int> = listOf(0)
           |val kinded: Kind<`List(_)`, Int> = concrete
           |val back: List<Int> = kinded
           |""".source
      },
      assert = {
        allOf("back".source.evalsTo(listOf(0)))
      }
    ))
  }

  @Test
  fun `Tuples`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrowx.*
           |val tupled: Tuple2<String, Int> = Person("a", 1)
           |val person: Person = tupled
           |val result = person.name to person.age
           |""".source
      },
      assert = {
        allOf(
          "result".source.evalsTo(Pair("a", 1))
        )
      }
    ))
  }

  @Test
  fun `PositiveInt can be refined with type proofs`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.*
           |val one: PositiveInt? = 1
           |val pos: Int? = one
           |val minusOne: PositiveInt? = -1
           |val neg: Int? = minusOne
           |""".source
      },
      assert = {
        allOf(
          "pos".source.evalsTo(1),
          "neg".source.evalsTo(null)
        )
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

  @Test
  fun `Union converts to nullable type with null when the union value is absent 2`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = 0
           |fun z(): String? = f()
           |""".source
      },
      assert = {
        allOf("z()".source.evalsTo(null))
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
        allOf(failsWith { it.contains("Type mismatch: inferred type is Union2<String, Int> /* = Union4<String, Int, Impossible, Impossible> */ but Double? was expected") })
      }
    ))
  }

}
