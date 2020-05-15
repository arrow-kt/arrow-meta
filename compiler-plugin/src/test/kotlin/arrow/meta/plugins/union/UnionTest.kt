package arrow.meta.plugins.union

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class UnionTest {

  @Test
  fun `Union accepts typed values in nested unions`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.*
           |
           |fun f(): Union2<String, Union2<Int, Double>> = 2
           |fun y(): Union3<String, Int, Double> = f()
           |fun x(): Int? = y()
           |""".source
      },
      assert = {
        allOf("x()".source.evalsTo(2))
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
  fun `Union accepts typed values in the union 4`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union4
           |fun f(): Union4<String, Int, Double, List<Int>> = 0
           |val x: Int? = f()
           """.source
      },
      assert = {
        allOf("x".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union accepts typed values in the union`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = "a"
           |val x: String? = f()
           |""".source
      },
      assert = {
        allOf("x".source.evalsTo("a"))
      }
    ))
  }

  @Test
  fun `Union rejects typed values not in the union`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = 0.0
           |
           |""".source
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
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = "a"
           |fun z(): String? = f()
           |""".source
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
        """|import arrow.Union3
           |fun f(): Union3<String, Int, Double> = 0
           |fun z(): Int? = f()
           |""".source
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
        """|import arrow.Union4
           |fun f(): Union4<String, Int, Double, List<Int>> = 0
           |fun z(): Int? = f()
           |""".source
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
  fun `Union converts to nullable type with null when the union value is absent 3`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union3
           |fun f(): Union3<String, Int, Double> = 0
           |fun z(): String? = f()
           |""".source
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
        """|import arrow.Union4
           |fun f(): Union4<String, Int, Double, List<Int>> = 0
           |fun z(): String? = f()
           |""".source
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
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = 0
           |val x = f()
           |fun z(): Int? = x
           |""".source
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
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = 0
           |fun z(): Double? = f()
           |""".source
      },
      assert = {
        allOf(failsWith { it.contains("but Double? was expected") })
      }
    ))
  }

  @Test
  fun `Union coercion with a single value argument evals correctly to the expected type`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
          import arrow.Union2
          import arrow.first
          
          data class UserName(val name: String)
          data class Password(val hash: String)
        
          fun help(id: Union2<UserName, Password>): String? {
            val userName: UserName? = id
            val password: Password? = id
            return userName?.name ?: password?.hash
          }
          
          val userName = UserName("userName")
          val result = help(userName)
        """.trimIndent().source
      },
      assert = {
        allOf("result".source.evalsTo("userName"))
      }
    ))
  }

  @Test
  fun `Union coercion within the value arguments evals correctly to the expected type`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
          import arrow.Union2
          import arrow.first
          
          data class UserName(val name: String)
          data class Password(val hash: String)
        
          fun help(id: Union2<UserName, Password>, id2: Union2<UserName, Password>): String? {
              val userName: UserName? = id
              val password: Password? = id2
              return userName?.name ?: password?.hash
          }
    
          val userName = UserName("userName")
          val result = help(userName, userName)
        """.trimIndent().source
      },
      assert = {
        allOf("result".source.evalsTo("userName"))
      }
    ))
  }
}
