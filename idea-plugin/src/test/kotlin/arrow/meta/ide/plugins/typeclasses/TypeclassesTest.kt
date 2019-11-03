package arrow.meta.ide.plugins.typeclasses

import arrow.meta.plugin.testing.CompilationStatus
import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.acquire
import com.intellij.testFramework.builders.EmptyModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import org.junit.Assert
import org.junit.Test

class TypeclassesTest : CodeInsightFixtureTestCase<EmptyModuleFixtureBuilder<*>>() {
  @Test
  fun test() {
    Assert.assertNotNull(myFixture) // Just to test that the HeavyTestEnvironment is setUp
    val result = acquire(
      TypeclassesTestCode.c1
    ) {
      val currentVersion = System.getProperty("CURRENT_VERSION")
      val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin")))
      val arrowAnnotations = Dependency("arrow-annotations:$currentVersion")
      val arrowCoreData = Dependency("arrow-core-data:$currentVersion")
      addCompilerPlugins(compilerPlugin) + addDependencies(arrowAnnotations, arrowCoreData)
    }
    return Assert.assertTrue(result.actualStatus == CompilationStatus.OK)
  }

}