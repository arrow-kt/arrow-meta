package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ClassBodyPlugin
import org.junit.jupiter.api.Test

class ClassBodyTest  {

  companion object {
    private val classBodyScopeTest = """
        | //metadebug
        | 
        | class ClassBodyScopeTest {
        |
        |   private val x = "x"
        |   private val y = "y"
        |   private val z = "z"
        |
        |   companion object {
        |     fun init() = ClassBodyScopeTest()
        |   }
        |
        |   fun x() = x
        |   fun y() = y
        |   fun z() = z
        | }
        """.source

    private val enumBodyScopeTest = """
      | //metadebug
      | 
      | enum class EnumBodyScopeTest {
      |   FOO, BAR;
      |
      |   fun foo() = 0
      |   fun test() = 0
      | }
      | """.source

    private val objectBodyScopeTest = """
      | //metadebug
      |
      | object ObjectBodyScopeTest {
      |   private val x = "x"
      |   private val y = "y"
      |   private val z = "z"
      |
      |   fun x() = x
      |   fun y() = y
      |   fun z() = z
      |   fun test() = 0
      | }
      | """.source

    val classBodyExpressions = arrayOf(
      classBodyScopeTest,
      enumBodyScopeTest,
      objectBodyScopeTest
    )
  }

  @Test
  fun `Validate class body scope properties`() {
      validate(classBodyScopeTest)
  }
  
  @Test
  fun `Validate enum body scope properties`() {
    validate(enumBodyScopeTest)
  }
  
  @Test
  fun `Validate object body scope properties`() {
    validate(objectBodyScopeTest)
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(ClassBodyPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }
}