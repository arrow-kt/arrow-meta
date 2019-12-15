package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ClassBodyPlugin
import org.junit.Test

class ClassBodyTest  {

  @Test
  fun `Validate class body scope properties`() {
      validate("""
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
        """.source)
  }
  
  @Test
  fun `Validate enum body scope properties`() {
    validate("""
      | //metadebug
      | 
      | enum class EnumBodyScopeTest {
      |   FOO, BAR;
      |
      |   fun foo() = 0
      |   fun test() = 0
      | }
      | """.source)
  }
  
  @Test
  fun `Validate object body scope properties`() {
    validate("""
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
      | """.source)
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(ClassBodyPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }
}