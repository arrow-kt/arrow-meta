package arrow.meta.ide.gradle

import com.intellij.openapi.application.AppUIExecutor
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.testFramework.RunAll
import com.intellij.util.ThrowableRunnable
import org.jetbrains.plugins.gradle.compiler.GradleDelegatedBuildTestCase
import org.junit.After


abstract class GradleTestSetUp : GradleDelegatedBuildTestCase() {
  /**
   * Without this override the test complains about a leaked Kotlin SDK
   */
  @After
  override fun tearDown(): Unit =
    RunAll(ThrowableRunnable {
      AppUIExecutor.onUiThread().inSmartMode(myProject).execute {
        WriteAction.runAndWait<Exception> {
          ProjectJdkTable.getInstance().allJdks.filter { it.name.startsWith("Kotlin") }
            .forEach { ProjectJdkTable.getInstance().removeJdk(it) }
        }
      }
    }, ThrowableRunnable { super.tearDown() }).run()
}