package arrow.meta.plugins.proofs

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import io.kotlintest.sourceRef
import org.junit.Test

// build ide peace with annotator
class ResolutionTests {
  @Test
  fun `internal orphan override`() {
    resolutionTest(
      source = """
      @Coercion
      fun String.toInt10(): Int? = // 30 -> 30
        toIntOrNull(10)
      
      @Coercion
      internal fun String.toInt16(): Int? = // 30 -> 48
        toIntOrNull(16)
      """) {
      compiles
    }
  }

  @Test
  fun `ambiguous internal orphans`() {
    resolutionTest(
      """
      @Coercion
      fun String.toInt10(): Int? = // 30 -> 30
        toIntOrNull(10)
      
      @Coercion
      internal fun String.toInt16(): Int? = // 30 -> 48
        toIntOrNull(16)
      
      @Coercion
      internal fun String.toInt8(): Int? = // 30 -> 24
        toIntOrNull(8)
      """) {
      compiles
    }
  }


  @Test
  fun `ambiguous public coercions`() {
    resolutionTest(
      source = """
      @Coercion
      fun String.toInt10(): Int? = // 30 -> 30
        toIntOrNull(10)
      
      @Coercion
      fun String.toInt16(): Int? = // 30 -> 48
        toIntOrNull(16)
      
      @Coercion
      internal fun String.toInt8(): Int? = // 30 -> 24
        toIntOrNull(8)
      """) {
      compiles
    }
  }


  @Test
  fun `prohibited published internal orphan`() {
    resolutionTest("""
      @Coercion
      fun String.toInt10(): Int? = // 30 -> 30
        toIntOrNull(10)
      
      @Coercion
      @PublishedApi
      internal fun String.toInt16(): Int? = // 30 -> 48
        toIntOrNull(16)
      """) {
      compiles
    }
  }

  private fun resolutionTest(source: String, assert: CompilerTest.Companion.() -> Assert) {
    val arrowVersion = System.getProperty("ARROW_VERSION")
    val arrowCoreData = Dependency("arrow-core-data:$arrowVersion")
    assertThis(CompilerTest(
      config = {
        metaDependencies + addDependencies(arrowCoreData)
      },
      code = {
        """
          |import arrow.*
          |import arrowx.*
          |
          |$source
          |
        """.trimMargin().source
      },
      assert = assert
    ))
  }

}