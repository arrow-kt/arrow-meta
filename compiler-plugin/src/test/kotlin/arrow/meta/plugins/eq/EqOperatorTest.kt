package arrow.meta.plugins.eq

import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

// TODO no actual tests here, but unit testing will need to be written for the proof extensions and so on
// focus for now is just creating an IR tree dump and seeing what gets spit out
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

  @Test
  fun `simple_case_function`() {
    val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin")))
    val arrowAnnotations = Dependency("arrow-annotations:0.10.3-SNAPSHOT")

    assertThis(CompilerTest(
      config = {
        addCompilerPlugins(compilerPlugin) + addDependencies(arrowAnnotations)
      },
      code = {
        """
        | object Id
        |
        | fun IdEq() : Eq<Id> = Id.eq().run {
        |   Id.eqv(Id)
        | }
        | val x = Id == Id
        | 
        """.source
      },
      assert = {
        compiles
      }
    ))
    assert(true)
  }
}
