package arrow.meta.plugins.show

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class ShowTest {
  @Test
  fun `show simple case`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
        //metadebug
        val result = 0.show()
        """.source },
      assert = { allOf("result".source.evalsTo("0")) }
    ))
  }
}