package arrow.meta.ide.testing.env

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase

/**
 * This is the entry point for Test classes JUnit initializes the Test Environment and registers your custom ide-plugin.
 * This empty abstract class is needed, as the underlying TestFramework may change for future versions.
 */
abstract class IdeTestSetUp : LightPlatformCodeInsightFixture4TestCase()

abstract class IdeHeavyTestSetUp : HeavyPlatformTestCase() {
  val projectDir: VirtualFile? = project.baseDir
}