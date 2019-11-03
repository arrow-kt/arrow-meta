package arrow.meta.plugins.union

import arrow.core.Some
import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class UnionTest {

  @Test
  fun `Union values don't require lifting`() {
    val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin")))
    val arrowAnnotations = Dependency("arrow-annotations:${System.getProperty("CURRENT_VERSION")}")

    assertThis(CompilerTest(
      config = {
        addCompilerPlugins(compilerPlugin) + addDependencies(arrowAnnotations)
      },
      code = {
        """
        | interface Union2<out A, out B>
        | 
        | typealias Result = Union2<String, Int>
        | 
        | fun f1(): Result = "a"
        | fun f2(): Result = 1
        | fun f3(): String? = f1()
        | fun f4(): Int? = f2()
        | 
        """.source
      },
      assert = {
        "f1()".source.evalsTo("a")
      }
    ))
  }
}
