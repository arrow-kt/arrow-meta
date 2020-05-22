package arrow.meta.ide.gradle

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.testFramework.EdtTestUtil
import com.intellij.testFramework.RunAll
import org.junit.After
import com.intellij.util.ThrowableRunnable
import org.jetbrains.plugins.gradle.compiler.GradleDelegatedBuildTestCase


abstract class GradleTestSetUp : GradleDelegatedBuildTestCase() {
  /**
   * Without this override the test complains about a leaked Kotlin SDK
   */
  @After
  override fun tearDown(): Unit =
    RunAll(ThrowableRunnable {
      EdtTestUtil.runInEdtAndWait(ThrowableRunnable {
        WriteAction.runAndWait<Exception> {
          ProjectJdkTable.getInstance().allJdks.filter { it.name.startsWith("Kotlin") }
            .forEach { ProjectJdkTable.getInstance().removeJdk(it) }
        }
      })
    }, ThrowableRunnable { super.tearDown() }).run()
}