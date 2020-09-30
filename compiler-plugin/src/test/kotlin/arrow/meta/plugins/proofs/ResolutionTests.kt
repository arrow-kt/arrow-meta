package arrow.meta.plugins.proofs

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.refinement.RefinementTests
import arrow.meta.plugins.typeclasses.GivenTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

// TODO: build ide peace with annotator
class ResolutionTests {
  @Test
  fun `prohibited public proof of non user types`() {
    resolutionTest(
      """
      @Coercion
      fun String.toInt16(): Int? =
        toIntOrNull(16)
        
      val x: Int? = "30"
      """) {
      failsWith {
        it.contains("This ExtensionProof test.toInt16: kotlin.String -> kotlin.Int? violates ownership rules, because public Proofs over 3rd party Types break coherence over the kotlin ecosystem. One way to solve this is to declare the Proof as an internal orphan.")
      }
    }
  }

  @Test
  fun `prohibited public proof over polymorphic type parameter`() {
    resolutionTest(
      """
      @Coercion
      fun <A> A.eq(): Eq<A> =
          object : Eq<A> {
              override fun A.eqv(b: A): Boolean =
                  this == b
          }
      """) {
      failsWith {
        it.contains("This ExtensionProof test.eq: A -> arrow.typeclasses.Eq<A> violates ownership rules, because public Proofs over 3rd party Types break coherence over the kotlin ecosystem. One way to solve this is to declare the Proof as an internal orphan.")
      }
    }
  }

  @Disabled
  @Test
  fun `@Extension internal orphan override`() {
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
  fun `@Given internal orphan override`() {
    givenResolutionTest(
      source = """
        @Given
        val p1 = Person("Peter Parker", 22)
        
        @Given
        internal val p2 = Person("Harry Potter", 14)
        
        val result = given<Person>()
        val name = result.name
        val age = result.age
      """) {
      allOf(
        "name".source.evalsTo("Harry Potter"),
        "age".source.evalsTo(14)
      )
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
      allOf(
        failsWith {
          it.contains("This ExtensionProof test.toInt16: kotlin.String -> kotlin.Int? has following conflicting proof/s: ExtensionProof test.toInt8: kotlin.String -> kotlin.Int?.")
        },
        failsWith {
          it.contains("This ExtensionProof test.toInt8: kotlin.String -> kotlin.Int? has following conflicting proof/s: ExtensionProof test.toInt16: kotlin.String -> kotlin.Int?.\n" +
            "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project.")
        }
      )
    }
  }

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
      allOf(
        failsWith {
          it.contains("This ExtensionProof test.toPerson: kotlin.Pair<kotlin.String, kotlin.Int> -> test.Person has following conflicting proof/s: ExtensionProof test.toPersonWithLeapYear: kotlin.Pair<kotlin.String, kotlin.Int> -> test.Person.\n" +
            "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project.")
        },
        failsWith {
          it.contains("This ExtensionProof test.toPersonWithLeapYear: kotlin.Pair<kotlin.String, kotlin.Int> -> test.Person has following conflicting proof/s: ExtensionProof test.toPerson: kotlin.Pair<kotlin.String, kotlin.Int> -> test.Person.\n" +
            "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project.")
        }
      )
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
      allOf(
        failsWith {
          it.contains("This ExtensionProof test.toPerson: kotlin.Pair<kotlin.String, kotlin.Int> -> test.Person has following conflicting proof/s: ExtensionProof test.toPerson365: kotlin.Pair<kotlin.String, kotlin.Int> -> test.Person.\n" +
            "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project.")
        },
        failsWith {
          it.contains("This ExtensionProof test.toPerson365: kotlin.Pair<kotlin.String, kotlin.Int> -> test.Person has following conflicting proof/s: ExtensionProof test.toPerson: kotlin.Pair<kotlin.String, kotlin.Int> -> test.Person.\n" +
            "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project.")
        },
        failsWith {
          it.contains("This ExtensionProof test.toPersonWithLeapYear: kotlin.Pair<kotlin.String, kotlin.Int> -> test.Person has following conflicting proof/s: ExtensionProof test.toPersonMod355: kotlin.Pair<kotlin.String, kotlin.Int> -> test.Person.\n" +
            "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project.")
        },
        failsWith {
          it.contains("This ExtensionProof test.toPersonMod355: kotlin.Pair<kotlin.String, kotlin.Int> -> test.Person has following conflicting proof/s: ExtensionProof test.toPersonWithLeapYear: kotlin.Pair<kotlin.String, kotlin.Int> -> test.Person.\n" +
            "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project.")
        }
      )
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

  @Test // TODO: Add compiler warning that nothing will be injected without arrow.given as a default value
  fun `unresolved class provider due to non injected given as default value and missing GivenProof on String`() {
    givenResolutionTest(
      source = """
        @Given class X(val value: @Given String)
        val result = given<X>().value
      """) {
      allOf(
        failsWith { it.contains("This GivenProof on the type test.X cant be semi-inductively resolved. Please verify that all parameters have default value or that other injected given values have a corresponding proof.") },
        failsWith { it.contains("There is no Proof for this type test.X to resolve this call. Either define a corresponding GivenProof or provide an evidence explicitly at this call-site.") }
      )
    }
  }

  @Test
  fun `unresolved class provider due to missing GivenProof on String`() {
    givenResolutionTest(
      source = """
        @Given class X(val value: @Given String = given())
        val result = given<X>().value
      """) {
      allOf(
        failsWith { it.contains("This GivenProof on the type test.X cant be semi-inductively resolved. Please verify that all parameters have default value or that other injected given values have a corresponding proof.") },
        failsWith { it.contains("There is no Proof for this type kotlin.String to resolve this call. Either define a corresponding GivenProof or provide an evidence explicitly at this call-site.") },
        failsWith { it.contains("There is no Proof for this type test.X to resolve this call. Either define a corresponding GivenProof or provide an evidence explicitly at this call-site.") }
      )
    }
  }

  @Test
  fun `resolved class provider due to coherent Semi-inductive implementation`() {
    givenResolutionTest(
      source = """
        @Given class X(val value: @Given String = given(), val p: @Given Person = given())
        
        @Given
        internal val x: String = "yes!"
        
        @Given
        val publicPerson = Person("Peter Schmitz", 22)
        
        @Given
        internal val orphan = Person("Micheal Müller", 16)
        
        val result = given<X>()
        val value = result.value
        val name = result.p.name
        val age = result.p.age
      """) {
      allOf(
        "value".source.evalsTo("yes!"),
        "name".source.evalsTo("Micheal Müller"),
        "age".source.evalsTo(16)
      )

    }
  }

  @Disabled // Currently Given injections with type params need to be reviewed #741 among other things
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
  fun `unresolved callable Member`() {
    givenResolutionTest(
      source = """
        val result = given<(Int) -> String>().invoke(5)
      """) {
      failsWith {
        it.contains("There is no Proof for this type (kotlin.Int) -> kotlin.String to resolve this call. Either define a corresponding GivenProof or provide an evidence explicitly at this call-site.")
      }
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

  @Disabled // TODO: Add compiler Error after fixing resolution
  @Test
  fun `unresolved polymorphic constraint`() {
    givenResolutionTest(
      """
        fun <A> A.mappend(b: A, ctx: @Given Semigroup<A> = given()): A =
          ctx.run { this@mappend.combine(b) }
          
        val result = 1.mappend(1)
      """.trimIndent()
    ) {
      failsWith {
        it.contains("There is no Proof for this type Semigroup<Int> to resolve this call. Either define a corresponding GivenProof or provide an evidence explicitly at this call-site.")
      }
    }
  }

  @Test
  fun `incorrect refinement proof, because of too many refinements`() {
    refinementResolutionTest(
      source = {
        """
      inline class PositiveInt(val value: Int)  {
        companion object : Refined<Int, PositiveInt> {
          override val target : (Int) -> PositiveInt = ::PositiveInt
          override val validate: Int.() -> Map<String, Boolean> = {
            mapOf(
              "Should be >= 0" to (this >= 0)
            )
          }
        }
        
        object Override : Refined<Int, PositiveInt> {
          override val target : (Int) -> PositiveInt = ::PositiveInt
          override val validate: Int.() -> Map<String, Boolean> = {
            mapOf(
              "Should be > 0" to (this > 0)
            )
          }
        }
      }
    """
      },
      assert = {
        failsWith {
          it.contains("Refinements can only be defined over one companion object, which implements arrow.Refined<kotlin.Int,test.PositiveInt>. Please remove this object or remove the superType Refined.")
        }
      }
    )
  }

  @Test
  fun `incorrect refinement proof, because of wrong "to"`() {
    refinementResolutionTest(
      source = {
        """
      $positiveInt    
      inline class StrictPositiveInt(val value: Int)  {
        companion object : Refined<String, PositiveInt> {
          override val target : (String) -> PositiveInt = TODO()
          override val validate: String.() -> Map<String, Boolean> = 
            TODO()
        }
      }
    """
      },
      assert = {
        failsWith {
          it.contains("This Refinement can only be defined over one companion object, which implements arrow.Refined<kotlin.Int,test.StrictPositiveInt>.")
        }
      }
    )
  }


  private fun refinementResolutionTest(source: RefinementTests.() -> String, assert: CompilerTest.Companion.() -> Assert) {
    RefinementTests().run {
      refinementTest(source(this), assert = assert)
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
          |data class Person(val name: String, val age: Int)
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
          |import arrow.typeclasses.*
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