package arrow.meta.ide.gradle

import arrow.meta.ide.jetbrainsGradle.GradleDelegatedBuildTestCase
import arrow.meta.ide.testing.IdeEnvironment
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.testFramework.EdtTestUtil
import com.intellij.testFramework.RunAll
import com.intellij.util.ThrowableRunnable
import org.junit.After
import org.junit.Test

class GradleTypeproofsTest : GradleDelegatedBuildTestCase() {
  /**
   * Without this the test complains about a leaked Kotlin SDK
   */
  @After
  override fun tearDown() {
    RunAll(ThrowableRunnable {
      EdtTestUtil.runInEdtAndWait(ThrowableRunnable {
        WriteAction.runAndWait<Exception> {
          ProjectJdkTable.getInstance().allJdks.filter { it.name.startsWith("Kotlin") }
            .forEach { ProjectJdkTable.getInstance().removeJdk(it) }
        }
      })
    }, ThrowableRunnable { super.tearDown() }).run()
  }

  @Test
  fun testGradleProject() {
    val gitResult = IdeEnvironment.gitClone(myProject, "https://github.com/arrow-kt/arrow-typeproofs.git", myProjectRoot)
    assertTrue(gitResult.success())

    val arrowProjectDir = myProjectRoot.findChild("arrow-typeproofs")
    myProjectRoot = arrowProjectDir
    importProject()

    assertNotNull(ModuleManager.getInstance(myProject).findModuleByName("arrow-typeproofs"))
    assertNotNull(ModuleManager.getInstance(myProject).findModuleByName("arrow-typeproofs.main"))
    assertNotNull(ModuleManager.getInstance(myProject).findModuleByName("arrow-typeproofs.test"))

    compileModules("arrow-typeproofs")
  }
}