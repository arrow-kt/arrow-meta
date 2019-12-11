package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import io.kotlintest.specs.AnnotationSpec
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ClassBodyPlugin

class ClassBodyTest : AnnotationSpec() {
  
  private val classBody = """
                          | //metadebug
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
    
  @Test
  fun `validate class body scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(ClassBodyPlugin())) },
      code = { classBody },
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
}