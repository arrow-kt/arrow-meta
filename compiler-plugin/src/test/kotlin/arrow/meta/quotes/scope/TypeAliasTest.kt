package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import org.junit.Ignore
import org.junit.Test

// TODO Ast to Expr Conversion needed

class TypeAliasTest {

  private val typeAlias = """
                         | //metadebug
                         | 
                         | typealias Predicate<T> = (T) -> Boolean
                         | """.trimMargin().source

  @Ignore
  @Test
  fun `Validate type alias properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins()) }, // TODO create a scope plugin for finally section
      code = { typeAlias },
      assert = { quoteOutputMatches(typeAlias) }
    ))
  }
}