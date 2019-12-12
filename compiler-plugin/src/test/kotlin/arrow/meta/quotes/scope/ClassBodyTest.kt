package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import io.kotlintest.specs.AnnotationSpec
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ClassBodyPlugin
import arrow.meta.quotes.scope.plugins.EnumBodyPlugin

class ClassBodyTest : AnnotationSpec() {

  @Test
  fun `validate class body scope properties are correctly generated`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(ClassBodyPlugin())) },
      code = { classBody() },
      assert = { quoteOutputMatches(
        """
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
        |   fun test() = 0
        | }
        """.source
      )}
    ))
  }
  
  @Test
  fun `validate enum body scope properties are correctly generated`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(EnumBodyPlugin())) },
      code = { enumBody() },
      assert = { quoteOutputMatches(
        """
        | enum class EnumBodyScopeTest {
        |   FOO, BAR;
        |
        |   fun foo() = 0
        |   fun test() = 0
        | }
        """.source
      )}
    ))
  }
  
  @Test
  fun `validate object body scope properties are correctly generated`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(ClassBodyPlugin())) },
      code = { objectBody() },
      assert = { quoteOutputMatches(
        """
        | object ObjectBodyScopeTest {
        |
        |   private val x = "x"
        |   private val y = "y"
        |   private val z = "z"
        |
        |   fun x() = x
        |   fun y() = y
        |   fun z() = z
        |   fun test() = 0
        | }
        """.source
      )}
    ))
  }
  
  private fun classBody(): Code.Source {
    return """
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
  }
  
  private fun enumBody(): Code.Source {
    return """
           | //metadebug
           |
           | enum class EnumBodyScopeTest {
           |   FOO, BAR;
           |
           |   fun foo() = 0
           | }
           """.source
  }
  
  private fun objectBody(): Code.Source {
    return """
           | //metadebug
           |
           | object ObjectBodyScopeTest {
           |
           |   private val x = "x"
           |   private val y = "y"
           |   private val z = "z"
           |
           |   fun x() = x
           |   fun y() = y
           |   fun z() = z
           | }
           """.source
  }
}