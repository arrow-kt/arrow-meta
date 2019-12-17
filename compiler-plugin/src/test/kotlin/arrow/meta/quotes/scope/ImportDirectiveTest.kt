package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ImportDirectivePlugin
import org.junit.Test

class ImportDirectiveTest  {

  private val importDirective = """
                         | //metadebug
                         | import kotlin.assert as testMessage
                         | """.source

  @Test
  fun `Validate import directive scope properties`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(ImportDirectivePlugin()) },
      code = { importDirective },
      assert = { quoteOutputMatches(importDirective) }
    ))
  }
}
