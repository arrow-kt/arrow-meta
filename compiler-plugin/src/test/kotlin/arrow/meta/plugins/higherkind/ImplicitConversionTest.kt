package arrow.meta.plugins.higherkind

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class ImplicitConversionTest {

  val prelude = """

    @Retention(AnnotationRetention.RUNTIME)
    @MustBeDocumented
    annotation class typeProof(val implicitConversion: Boolean = false)
  
    @typeProof(implicitConversion = true)
    fun String.safeToInt(): Int? =
      try { toInt() } catch (e: NumberFormatException) { null }

  """.trimIndent()

  @Test
  fun `initial test`() {
    val currentVersion = System.getProperty("CURRENT_VERSION")
    val arrowCoreData = Dependency("arrow-core-data:$currentVersion")
    assertThis(CompilerTest(
      config = { metaDependencies + addDependencies(arrowCoreData) },
      code = {
        """|$prelude
           |val a: Int? = "1"
           |val b: Int? = "boom!"
           |fun c(): Int? = a
           |fun d(): Int? = b
           |""".source
      },
      assert = {
        allOf(
          "a".source.evalsTo(1),
          "b".source.evalsTo(null),
          "c()".source.evalsTo(1),
          "d()".source.evalsTo(null)
        )
      }
    ))
  }
}
