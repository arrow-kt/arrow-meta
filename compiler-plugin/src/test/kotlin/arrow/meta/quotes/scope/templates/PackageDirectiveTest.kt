package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.PackageDirectivePlugin
import org.junit.Test

class PackageDirectiveTest  {
  
  @Test
  fun `validate package is transformed correctly`() {
    validate("test")
  }
  
  @Test
  fun `validate package names are reduced correctly`() {
    validate("package_names")
  }
  
  @Test
  fun `validate last package name is transformed correctly`() {
    validate("package_last_name")
  }
  
  private fun validate(lastPackage: String) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(PackageDirectivePlugin())) },
      code = { packageDeclaration(lastPackage) },
      assert = { quoteOutputMatches(packageDeclaration(lastPackage)) }
    ))
  }
  
  private fun packageDeclaration(lastPackage: String): Code.Source {
    return """
           | //metadebug
           | package arrow.meta.quotes.scope.$lastPackage
           """.source
  }
}