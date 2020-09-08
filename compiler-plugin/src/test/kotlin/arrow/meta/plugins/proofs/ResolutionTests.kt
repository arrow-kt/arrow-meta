package arrow.meta.plugins.proofs

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.typeclasses.GivenTest
import org.junit.Ignore
import org.junit.Test

// build ide peace with annotator
class ResolutionTests {
  // the first tests define their proofs in the same package
  // adds ownership rules on types
  // skipped internal instances for public and internal proofs
  @Test
  fun `prohibited public proof of non user types`() {
    resolutionTest(
      """
      @Coercion
      fun String.toInt16(): Int? =
        toIntOrNull(16)
        
      val x: Int? = "30"
      """) {
      fails
    }
  }

  @Test
  fun `internal orphan override`() {
    resolutionTest(
      """
      @Coercion
      internal fun String.toInt16(): Int? =
        toIntOrNull(16)
        
      val x: Int? = "30"
      """) {
      "x".source.evalsTo(48)
    }
  }

  @Test
  fun `ambiguous internal orphans`() {
    resolutionTest(
      """
      @Coercion
      internal fun String.toInt16(): Int? = // "30" -> 48
        toIntOrNull(16)
      
      @Coercion
      internal fun String.toInt8(): Int? = // "30" -> 24
        toIntOrNull(8)
      """) {
      fails
    }
  }

  /**
   * some context to the following proofs.
   */
  @Test
  fun `ambiguous public coercion proofs`() {
    resolutionTest(
      """
      @Coercion
      fun Pair<String, Int>.toPerson(): Person =
        Person(first, second)

      @Coercion
      fun Pair<String, Int>.toPersonWithLeapYear(): Person =
        Person(first, second % 383)
      
      @Coercion
      internal fun Pair<String, Int>.toPersonMod355(): Person =
        Person(first, second % 355)
      """) {
      fails
    }
  }


  @Test
  fun `ambiguous internal and public coercion proofs`() {
    resolutionTest(
      """
      @Coercion
      fun Pair<String, Int>.toPerson(): Person =
        Person(first, second)
      
      @Coercion
      fun Pair<String, Int>.toPerson365(): Person =
        Person(first, second % 365)
      
      @Coercion
      internal fun Pair<String, Int>.toPersonWithLeapYear(): Person =
        Person(first, second % 383)
      
      @Coercion
      internal fun Pair<String, Int>.toPersonMod355(): Person =
        Person(first, second % 355)
      """) {
      fails
    }
  }

  @Test
  fun `prohibited published internal orphan`() {
    resolutionTest("""
      @Coercion
      @kotlin.PublishedApi
      internal fun String.toInt16(): Int? =
        toIntOrNull(16)
      """) {
      failsWith {
        it.contains("Internal overrides of proofs are not permitted to be published, as they break coherent proof resolution over the kotlin ecosystem. Please remove the @PublishedApi annotation.")
      }
    }
  }

  @Test
  // Fails, because constructor is not being filled with a proof
  fun `unresolved class provider due to non Semi-inductive implementation`() {
    givenResolutionTest(
      source = """
        @Given class X(val value: @Given String)
        val result = given<X>().value
      """) {
      fails
    }
  }

  @Test
  // Fails, because constructor is not being filled with a proof
  fun `unresolved class provider due to missing Proof for construction`() {
    givenResolutionTest(
      source = """
        @Given class X(val value: @Given String = given())
        val result = given<X>().value
      """) {
      fails
    }
  }

  @Test
  fun `resolved class provider due to Semi-inductive implementation`() {
    givenResolutionTest(
      source = """
        @Given class X(val value: @Given String = given())
        @Given
        internal val x: String = "yes!"
        val result = given<X>().value
      """) {
      "result".source.evalsTo("yes!")
    }
  }

  @Ignore // Currently Given injections with type params need to be reviewed #741 among other things
  @Test
  fun `resolved function due to Semi-inductive implementation`() {
    givenResolutionTest(
      source = """
      fun <A : @Given Semigroup<A>> List<A>.collapse(
        initial: A,
        f: @Given() (A) -> A = given()
      ): A =
        fold(initial) { acc: A, a: A ->
          acc.combine(f(a))
        }
      
      @Given
      internal fun <A> id(a: A): A = a
      
      val result = listOf("Hello ", "is it me", "your looking for").collapse(String.empty())
      """) {
      "result".source.evalsTo("Hello is it me, your looking for")
    }
  }


  @Test
  fun `resolved callable Member`() {
    givenResolutionTest(
      source = """
        @Given
        internal val x: (Int) -> String = { i -> i.toString() }
        val result = given<(Int) -> String>().invoke(5)
      """) {
      "result".source.evalsTo("5")
    }
  }

  private fun givenResolutionTest(source: String, assert: CompilerTest.Companion.() -> Assert) {
    val arrowVersion = System.getProperty("ARROW_VERSION")
    val arrowCoreData = Dependency("arrow-core-data:$arrowVersion")
    assertThis(CompilerTest(
      config = {
        metaDependencies + addDependencies(arrowCoreData)
      },
      code = {
        """
          |${GivenTest().prelude}
          |$source
        """.trimMargin().source
      },
      assert = assert
    ))
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
          |package test
          |import arrow.*
          |import arrowx.*
          |
          |data class Person(val name: String, val age: Int)
          |
          |$source
        """.trimMargin().source
      },
      assert = assert
    ))
  }
}