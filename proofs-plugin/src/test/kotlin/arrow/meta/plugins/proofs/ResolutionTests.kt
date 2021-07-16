package arrow.meta.plugins.proofs

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.newMetaDependencies
import org.junit.jupiter.api.Test

class ResolutionTests {
  @Test
  fun `prohibited public proof of non user types`() {
    resolutionTest(
      """
      @Given
      fun n(): Int = 42 
        
      val x = given<Int>()
      """
    ) {
      failsWith {
        it.contains("This GivenProof test.n on the type kotlin.Int violates ownership rules, because public Proofs over 3rd party Types break coherence over the kotlin ecosystem. One way to solve this is to declare the Proof as an internal orphan.")
      }
    }
  }

  @Test
  fun `prohibited public proof over polymorphic type parameter`() {
    resolutionTest(
      """
      @Given
      fun <A> eq(): Eq<A> =
          object : Eq<A> {
              override fun A.eqv(b: A): Boolean =
                  this == b
          }
      """
    ) {
      failsWith {
        it.contains("This GivenProof test.eq on the type arrow.typeclasses.Eq<A> violates ownership rules, because public Proofs over 3rd party Types break coherence over the kotlin ecosystem. One way to solve this is to declare the Proof as an internal orphan.")
      }
    }
  }

  @Test
  fun `Member injection (object)`() {
    resolutionTest(
      """
      @Given
      internal fun n(): Int = 42 
      
      object Foo {
        fun foo(@Given x: Int): Int = x
      }
        
      val x = Foo.foo()
      """
    ) {
      "x".source.evalsTo(42)
    }
  }

  @Test
  fun `Member injection (class)`() {
    resolutionTest(
      """
      @Given
      internal fun n(): Int = 42 
      
      class Foo {
        fun foo(@Given x: Int): Int = x
      }
        
      val x = Foo().foo()
      """
    ) {
      "x".source.evalsTo(42)
    }
  }

  @Test
  fun `Member injection (class with injected args and members)`() {
    resolutionTest(
      """
      @Given
      internal fun n(): Int = 42 
      
      @Given
      class Foo(@Given val y: Int) {
        fun foo(@Given x: Int): Int = x + y
      }
        
      val x = Foo().foo()
      """
    ) {
      "x".source.evalsTo(42 * 2)
    }
  }

  @Test
  fun `Member injection (class with injected args and members re-scoped)`() {
    resolutionTest(
      """
      @Given
      internal fun n(): Int = 42 
      
      @Given
      class Foo(@Given val y: Int) {
        fun foo(@Given x: Int): Int = x + y
      }
      
      fun t(foo: Foo = Foo()): Foo = foo
        
      val x = t().foo()
      """
    ) {
      "x".source.evalsTo(42 * 2)
    }
  }

  @Test
  fun `Type bounds based injection`() {
    resolutionTest(
      """
      @Given
      internal fun n(): Int = 42 
      
      fun t(@Given x: Number): Number = x
        
      val x = t()
      """
    ) {
      "x".source.evalsTo(42)
    }
  }

  @Test
  fun `Ambiguous type bounds based injection`() {
    resolutionTest(
      """
      @Given
      internal fun n(): Int = 42 
      
      @Given
      internal fun d(): Double = 33.0
      
      fun t(@Given x: Number): Number = x
        
      val x = t()
      """
    ) {
      allOf(
        failsWith {
          it.contains(
            "Found ambiguous proofs for type kotlin.Number. Proofs : GivenProof test.n on the type kotlin.Int,\n" +
              "GivenProof test.d on the type kotlin.Double"
          )
        }
      )
    }
  }

  @Test
  fun `Non termination on cyclic dependencies`() {
    resolutionTest(
      """
      @Given
      internal fun n(@Given s: String): Int = s.toInt() 
      
      @Given
      internal fun s(@Given n: Int): String = n.toString() 
      """
    ) {
      allOf(
        failsWith {
          it.contains(
            "This GivenProof on the type kotlin.Int has cyclic dependencies: GivenProof test.n on the type kotlin.Int,\n" +
              "GivenProof test.s on the type kotlin.String. Please verify that proofs dont depend on each other for resolution."
          )
        },
        failsWith {
          it.contains(
            "This GivenProof on the type kotlin.String has cyclic dependencies: GivenProof test.s on the type kotlin.String,\n" +
              "GivenProof test.n on the type kotlin.Int. Please verify that proofs dont depend on each other for resolution."
          )
        }
      )
    }
  }

  @Test
  fun `A provider may have injection arguments which are polymorphically resolved`() {
    resolutionTest(
      """
      @Given
      internal fun n(): Int = 42 
      
      class Foo(val n: Int)
      
      @Given
      internal fun fooProvider(@Given x: Int): Foo = Foo(x)
      
      fun <A> id(@Given ev: A): A = ev
        
      val x = id<Foo>().n
      """
    ) {
      "x".source.evalsTo(42)
    }
  }

  @Test
  fun `A polymorphic provider may have injection arguments which are polymorphically resolved`() {
    resolutionTest(
      """
      @Given
      internal fun n(): Int = 42 
      
      class Foo<A>(val n: A)
      
      @Given
      internal fun <A> fooProvider(@Given x: A): Foo<A> = Foo(x)
      
      fun <A> id(@Given ev: A): A = ev
        
      val x = id<Foo<Int>>().n
      """
    ) {
      "x".source.evalsTo(42)
    }
  }

  @Test
  fun `primitive internal orphan override`() {
    resolutionTest(
      """
      @Given
      internal fun n(): Int = 42 
        
      val x = given<Int>()
      """
    ) {
      "x".source.evalsTo(42)
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
      """
    ) {
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
      @Given
      internal fun n(): Int = 42 
      
      @Given
      internal fun n2(): Int = 0 
        
      val x = given<Int>()
      """
    ) {
      allOf(
        failsWith {
          it.contains(
            "This GivenProof test.n on the type kotlin.Int has following conflicting proof/s: GivenProof test.n2 on the type kotlin.Int.\n" +
              "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project."
          )
        },
        failsWith {
          it.contains(
            "This GivenProof test.n2 on the type kotlin.Int has following conflicting proof/s: GivenProof test.n on the type kotlin.Int.\n" +
              "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project."
          )
        }
      )
    }
  }

  @Test
  fun `ambiguous public given proofs`() {
    resolutionTest(
      """
      @Given
      fun person1(): Person =
        Person("X", 0)

      @Given
      fun person2(): Person =
        Person("X", 0)
      
      @Given
      internal fun person3(): Person =
        Person("X", 0)
      """
    ) {
      allOf(
        failsWith {
          it.contains(
            "This GivenProof test.person1 on the type test.Person has following conflicting proof/s: GivenProof test.person2 on the type test.Person.\n" +
              "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project."
          )
        },
        failsWith {
          it.contains(
            "This GivenProof test.person2 on the type test.Person has following conflicting proof/s: GivenProof test.person1 on the type test.Person.\n" +
              "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project."
          )
        }
      )
    }
  }


  @Test
  fun `ambiguous internal and public given proofs`() {
    resolutionTest(
      """
      @Given
      fun person1(): Person =
        Person("X", 0)

      @Given
      fun person2(): Person =
        Person("X", 0)
      
      @Given
      internal fun person3(): Person =
        Person("X", 0)
        
      @Given
      internal fun person4(): Person =
        Person("X", 0)
      """
    ) {
      allOf(
        failsWith {
          it.contains(
            "This GivenProof test.person1 on the type test.Person has following conflicting proof/s: GivenProof test.person2 on the type test.Person.\n" +
              "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project."
          )
        },
        failsWith {
          it.contains(
            "This GivenProof test.person3 on the type test.Person has following conflicting proof/s: GivenProof test.person4 on the type test.Person.\n" +
              "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project."
          )
        },
        failsWith {
          it.contains(
            "This GivenProof test.person4 on the type test.Person has following conflicting proof/s: GivenProof test.person3 on the type test.Person.\n" +
              "Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project."
          )
        }
      )
    }
  }

  @Test
  fun `prohibited published internal orphan`() {
    resolutionTest(
      """
      @Given
      @kotlin.PublishedApi
      internal fun n(): Int = 0
      """
    ) {
      failsWith {
        it.contains("Internal overrides of proofs are not permitted to be published, as they break coherent proof resolution over the kotlin ecosystem. Please remove the @PublishedApi annotation.")
      }
    }
  }

  @Test // TODO: Add compiler warning that nothing will be injected without arrow.given as a default value
  fun `unresolved class provider due to non injected given as default value and missing GivenProof on String`() {
    givenResolutionTest(
      source = """
        @Given class X(@Given val value: String)
        val result = given<X>().value
      """
    ) {
      allOf(
        failsWith { it.contains("This GivenProof on the type test.X cant be semi-inductively resolved. Please verify that all parameters have default value or that other injected given values have a corresponding proof.") }
      )
    }
  }

  @Test
  fun `unresolved class provider due to missing GivenProof on String`() {
    givenResolutionTest(
      source = """
        @Given class X(@Given val value: String)
        val result = given<X>().value
      """
    ) {
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
        @Given class X(@Given val value: String,  @Given val p: Person)
        
        @Given
        internal val x: String = "yes!"
        
        @Given
        val publicPerson: Person = Person("Peter Schmitz", 22)
        
        @Given
        internal val orphan: Person = Person("Micheal Müller", 16)
        
        val result = X()
        val value = result.value
        val name = result.p.name
        val age = result.p.age
      """
    ) {
      allOf(
        "value".source.evalsTo("yes!"),
        "name".source.evalsTo("Micheal Müller"),
        "age".source.evalsTo(16)
      )

    }
  }

  @Test
  fun `unresolved callable Member`() {
    givenResolutionTest(
      source = """
        val result = given<(Int) -> String>().invoke(5)
      """
    ) {
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
      """
    ) {
      "result".source.evalsTo("5")
    }
  }

  private fun givenResolutionTest(source: String, assert: CompilerTest.Companion.() -> Assert) {
    val arrowVersion = System.getProperty("ARROW_VERSION")
    val arrowCoreData = Dependency("arrow-core:$arrowVersion")
    assertThis(
      CompilerTest(
        config = {
          newMetaDependencies() + addDependencies(arrowCoreData)
        },
        code = {
          """
          |${GivenTest().prelude}
          |data class Person(val name: String, val age: Int)
          |$source
        """.trimMargin().source
        },
        assert = assert
      )
    )
  }


  private fun resolutionTest(source: String, assert: CompilerTest.Companion.() -> Assert) {
    val arrowVersion = System.getProperty("ARROW_VERSION")
    val arrowCoreData = Dependency("arrow-core:$arrowVersion")
    assertThis(
      CompilerTest(
        config = {
          newMetaDependencies() + addDependencies(arrowCoreData)
        },
        code = {
          """
          |package test
          |import arrow.typeclasses.*
          |import arrow.*
          |
          |import arrow.Context
          |
          |@Context
          |@Retention(AnnotationRetention.RUNTIME)
          |@Target(
          |  AnnotationTarget.CLASS,
          |  AnnotationTarget.FUNCTION,
          |  AnnotationTarget.PROPERTY,
          |  AnnotationTarget.VALUE_PARAMETER
          |)
          |@MustBeDocumented
          |annotation class Given
          |
          |inline fun <A> given(@Given identity: A): A = identity
          |
          |data class Person(val name: String, val age: Int)
          |
          |$source
        """.trimMargin().source
        },
        assert = assert
      )
    )
  }
}
