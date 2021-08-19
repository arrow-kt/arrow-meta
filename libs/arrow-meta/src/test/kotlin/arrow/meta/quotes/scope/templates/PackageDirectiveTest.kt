package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.PackageDirectivePlugin
import org.junit.jupiter.api.Test

class PackageDirectiveTest {

  companion object {
    private val test = "test".packageDeclaration()
    private val package_names = "package_names".packageDeclaration()
    private val package_last_name = "package_last_name".packageDeclaration()

    private fun String.packageDeclaration(): Code.Source {
      return """
           | //metadebug
           | package arrow.meta.quotes.scope.$this
           """.source
    }

    val packageExpressions = arrayOf(
      test,
      package_names,
      package_last_name
    )
  }

  @Test
  fun `validate package is transformed correctly`() {
    validate(test)
  }

  @Test
  fun `validate package names are reduced correctly`() {
    validate(package_names)
  }

  @Test
  fun `validate last package name is transformed correctly`() {
    validate(package_last_name) // TODO
  }

  private fun validate(lastPackage: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(PackageDirectivePlugin())) },
      code = { lastPackage },
      assert = { quoteOutputMatches(lastPackage) }
    ))
  }
}
