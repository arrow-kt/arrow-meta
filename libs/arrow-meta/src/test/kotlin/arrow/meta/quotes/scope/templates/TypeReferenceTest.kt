package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.TypeReferencePlugin
import org.junit.jupiter.api.Test

class TypeReferenceTest {

  companion object {
    val typeReferenceExpression =
      """
                         | //metadebug
                         | 
                         | val aBoxedA: Int? = 13
                         | """.source
  }

  @Test
  fun `Validate type reference properties`() {
    assertThis(
      CompilerTest(
        config = { listOf(addMetaPlugins(TypeReferencePlugin())) },
        code = { typeReferenceExpression },
        assert = { quoteOutputMatches(typeReferenceExpression) }
      )
    )
  }
}
