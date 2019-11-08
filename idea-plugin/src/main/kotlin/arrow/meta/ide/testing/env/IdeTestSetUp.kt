package arrow.meta.ide.testing.env

import com.intellij.testFramework.builders.EmptyModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase

/**
 * This is the entry point for Test classes JUnit initializes the Test Environment and registers your costume ide-plugin.
 * This empty abstract class is needed, as the underlying TestFramework may change for future versions.
 */
abstract class IdeLightTestSetUp : LightPlatformCodeInsightFixture4TestCase()

/**
 * This is the entry point for Test classes which make use of Ide "heavy tests".
 * JUnit initializes the Test Environment and registers your costume ide-plugin.
 * This empty abstract class is needed, as the underlying TestFramework may change for future versions.
 */
abstract class IdeHeavyTestSetUp : CodeInsightFixtureTestCase<EmptyModuleFixtureBuilder<*>>() 
