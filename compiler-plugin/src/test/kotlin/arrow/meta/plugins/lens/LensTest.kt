package arrow.meta.plugins.lens

import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class LensTest {

  @Test
  fun `Initial lens test`() {

    val currentVersion = System.getProperty("CURRENT_VERSION")
    val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin")))
    val arrowAnnotations = Dependency("arrow-annotations:$currentVersion")
    val arrowOptics = Dependency("arrow-optics:$currentVersion")
    val codeSnippet = """
      | 
      | //metadebug
      | 
      | data class TestLenses(
      |   val a: String,
      |   val b: String
      | )
      | 
      """

    assertThis(CompilerTest(
        config = {
          addCompilerPlugins(compilerPlugin) + addDependencies(arrowAnnotations, arrowOptics)
        },
        code = {
          codeSnippet.source
        },
        assert = {
          quoteOutputMatches(
            """
            | data class TestLenses public constructor (val a: String, val b: String) {
            |
            |   companion object {
            |     @arrow.synthetic val a: arrow.optics.Lens<TestLenses, String> = arrow.optics.Lens(
            |       get = { testlenses -> testlenses.a },
            |       set = { testlenses, a -> testlenses.copy(a = a) }
            |     )
            |     @arrow.synthetic val b: arrow.optics.Lens<TestLenses, String> = arrow.optics.Lens(
            |       get = { testlenses -> testlenses.b },
            |       set = { testlenses, b -> testlenses.copy(b = b) }
            |     )
            |     @arrow.synthetic val iso: arrow.optics.Iso<TestLenses, Pair<String, String>> = arrow.optics.Iso(
            |       get = { (a, b) -> Pair(a, b) },
            |       reverseGet = { (a, b) -> TestLenses(a, b) }
            |     )
            |   }
            | }
        """.source)
        }
    ))
  }

}

