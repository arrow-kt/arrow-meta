package arrow.meta.ide.plugins.proofs.repo

import arrow.meta.ide.gradle.GradleTestSetUp
import arrow.meta.ide.testing.IdeEnvironment
import com.intellij.openapi.module.ModuleManager
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.Ignore
import org.junit.Test
@Ignore
class GradleTypeproofsTest : GradleTestSetUp() {

  @Test
  fun testGradleProject() {
    IdeEnvironment.run {
      val gitResult = gitClone(myProject, "https://github.com/arrow-kt/arrow-typeproofs.git", myProjectRoot)
      assertTrue(gitResult.success())

      val arrowProjectDir = myProjectRoot.findChild("arrow-typeproofs")
      myProjectRoot = arrowProjectDir
      importProject()
      assertNotNull(ModuleManager.getInstance(myProject).findModuleByName("arrow-typeproofs"))
      assertNotNull(ModuleManager.getInstance(myProject).findModuleByName("arrow-typeproofs.main"))
      assertNotNull(ModuleManager.getInstance(myProject).findModuleByName("arrow-typeproofs.test"))

      compileModules("arrow-typeproofs")
      assertThatCode {
        gradle(
          myProject,
          projectPath,
          tasks = listOf(
            "clean", "build"
          )
        ).forEach(::println)
      }.doesNotThrowAnyException()
    }
  }
}
