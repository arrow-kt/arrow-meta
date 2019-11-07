package arrow.meta.plugins.dummy

import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

// TODO no actual tests here, but unit testing will need to be written for the proof extensions and so on
// TODO focus for now is just creating an IR tree dump and seeing what gets spit out
class EqOperatorTest {

  companion object {
    const val EQ_FUNCTION = "@extension fun isEqual(): Boolean = 1 == 2"
    const val EQ_EXTENSION = """
      | object Id
      | 
      | @extension
      | fun IdEq() : Eq<Id> = Id.eq().run {
      |   Id.eqv(Id)
      | }
      | val x = Id == Id
      |
      """
    const val EQ_OPERATOR_CLASS = """
      |  sealed class Either<out A, out B> {
      |    class Left<out A> : Either<A, Nothing>()
      |    class Right<out B>(val value: B) : Either<Nothing, B>()
      |
      |    fun <A, B> isEitherEqual(value: B): Boolean = Left<A>() == Right(value)
      |  }
      """
  }

  /**
   * The testing framework [CompilerTest] will require dependencies for its configurations
   * and imports in the string source code itself. For example, in order for this test to be
   * able to successfully compile [Eq] in Arrow, we look for the location of the file in the project:
   *
   *  `./modules/core/arrow-core-data/src/main/kotlin/arrow/typeclasses/Eq.kt`
   *
   *  The dependency needed to be added to the [CompilerTest] then is `arrow-core-data`, and
   *  the import is `arrow.typeclasses.Eq`.
   *
   *  The latest versions of these dependencies can be located in gradle.properties of Arrow.
   */

  @Test
  fun `simple_case_function`() {
    val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin")))
    val arrowCoreData = Dependency("arrow-core-data:${System.getProperty("CURRENT_VERSION")}")

    assertThis(CompilerTest(
      config = {
        listOf(addCompilerPlugins(compilerPlugin)) + addDependencies(arrowCoreData)
      },
      code = {
        """
        | import arrow.typeclasses.Eq
        | 
        | object Id
        |
        | @extension
        | fun IdEq() : Eq<Id> = Id.eq().run {
        |   Id.eqv(Id)
        | }
        """.source
      },
      assert = {
        assertCompiles(        """
        | import arrow.typeclasses.Eq
        | 
        | object Id
        |
        | @extension
        | fun IdEq() : Eq<Id> = Id.eq().run {
        |   Id.eqv(Id)
        | }
        """.source)
      }
    ))
  }
}