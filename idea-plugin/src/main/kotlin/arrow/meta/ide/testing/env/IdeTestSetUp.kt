package arrow.meta.ide.testing.env

import arrow.meta.ide.testing.Source
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase

/**
 * This is the entry point for Test classes JUnit initializes the Test Environment and registers your custom ide-plugin.
 * This empty abstract class is needed, as the underlying TestFramework may change for future versions.
 * Please note, that this set up is solely for light UI tests, that don't require a build environment or generated sources.
 * In those cases, one may supply one of the `HeavyIdeaTests` or `Testcase` the one in our test directory for gradle.
 */
abstract class IdeTestSetUp(
  vararg val dependencies: TestFile = emptyArray()
) : LightPlatformCodeInsightFixture4TestCase() {
  override fun setUp() {
    super.setUp()
    dependencies.forEach { (path, code) ->
      myFixture.addFileToProject(path, code)
    }
  }
}

data class TestFile(val relativePath: String, val code: Source)

fun Source.file(relativePath: String): TestFile =
  TestFile(relativePath, this)