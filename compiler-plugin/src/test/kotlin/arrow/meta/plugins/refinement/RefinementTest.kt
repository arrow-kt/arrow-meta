package arrow.meta.plugins.refinement

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.Ignore
import org.junit.Test

class RefinementTest {

  /** Positive Int */

  @Test
  fun `positive int from int`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        import arrowx.*
        
        //metadebug
        
        val result: PositiveInt = 1.positive()
        """.source },
      assert = { allOf("result".source.evalsTo(1)) }
    ))
  }

  @Test
  fun `positive int from wrong int case`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        import arrowx.*
        
        //metadebug
        
        val result: PositiveInt = (-1).positive()
        """.source },
      assert = { allOf("result".source.evalsTo(null)) }
    ))
  }

  // TODO: it is right?
  @Test
  fun `positive int declaration`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        import arrowx.*
        
        //metadebug
        
        val result: PositiveInt = 1
        """.source },
      assert = {
        failsWith { it.contains("The integer literal does not conform to the expected type PositiveInt") }
      }
    ))
  }

  // TODO: it is right?
  @Test
  fun `wrong positive int declaration`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        import arrowx.*
        
        //metadebug
        
        val result: PositiveInt = (-1)
        """.source },
      assert = {
        failsWith { it.contains("Type mismatch: inferred type is Int but PositiveInt was expected") }
      }
    ))
  }

  /** Int String */

  @Test
  fun `IntString from int`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        import arrowx.*
        
        //metadebug
        
        val result: IntString = "1".int()
        """.source },
      assert = { allOf("result".source.evalsTo("1")) }
    ))
  }

  @Test
  fun `IntString from wrong int`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        import arrowx.*
        
        //metadebug
        
        val result: IntString = "Not Int".int()
        """.source },
      assert = { allOf("result".source.evalsTo(null)) }
    ))
  }

  // TODO: it is right?
  @Test
  fun `IntString declaration`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        import arrowx.*
        
        //metadebug
        
        val result: IntString = "1"
        """.source },
      assert = {
        failsWith { it.contains("Type mismatch: inferred type is String but IntString was expected") }
      }
    ))
  }

  // TODO: it is right?
  @Test
  fun `wrong IntString declaration`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        import arrowx.*
        
        //metadebug
        
        val result: IntString = "Not Int"
        """.source },
      assert = {
        failsWith { it.contains("Type mismatch: inferred type is String but IntString was expected") }
      }
    ))
  }
}