package arrow.meta.ide.testing.env.types

import arrow.meta.ide.compile.CompilationResult
import arrow.meta.ide.compile.compile
import arrow.meta.ide.phases.config.buildFolders
import arrow.meta.ide.testing.Source
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

data class HeavyTestSetUp(
  val buildDir: VirtualFile,
  val srcDir: VirtualFile,
  val ktFile: KtFile,
  val traversable: List<PsiElement>,
  val outDirTarget: VirtualFile
)

object HeavyTestSyntax : CommonTestSyntax {

  val CompilationResult.outDirFile: VirtualFile?
    get() = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(classesDirectory)

  fun Source.addMetaDataToBuild(buildDir: VirtualFile, myFixture: CodeInsightTestFixture): VirtualFile? =
    compile(this).outDirFile?.let { myFixture.copyDirectoryToProject(it.path, buildDir.path) }

  fun Source.toFile(name: String = "Source.kt", dir: VirtualFile): VirtualFile? =
    WriteAction.computeAndWait<VirtualFile, Throwable> {
      val sourceFile = dir.createChildData(this, name)
      sourceFile.setBinaryContent(toByteArray())
      sourceFile
    }

  fun Source.copyToDir(name: String = "Source.kt", dir: VirtualFile, myFixture: CodeInsightTestFixture): KtFile? =
    toFile(name, dir)?.let { file: VirtualFile ->
      myFixture.configureFromExistingVirtualFile(file)
      myFixture.file.safeAs()
    }

  /**
   * Creates a new directory "name" as sub-directory of the receiver and registers it as excluded directory.
   * @param receiver is the root file
   */
  fun VirtualFile.addExcludedDir(name: String, module: Module): VirtualFile? =
    WriteAction.computeAndWait<VirtualFile, Throwable> { this.createChildDirectory(this@HeavyTestSyntax, name) }
      .also { PsiTestUtil.addExcludedRoot(module, it) }

  /**
   * Creates a new directory "name" as sub-directory of the receiver and registers it as a source root.
   * @param receiver is the root file
   */
  fun VirtualFile.addSourceDir(name: String, module: Module): VirtualFile? =
    WriteAction.computeAndWait<VirtualFile, Throwable> { this.createChildDirectory(this@HeavyTestSyntax, name) }
      .also { PsiTestUtil.addSourceRoot(module, it) }

  val Module.root: VirtualFile?
    get() = ModuleRootManager.getInstance(this).contentRoots.takeIf { it.isNotEmpty() }?.firstOrNull()

  fun Source.ideHeavySetup(
    module: Module,
    myFixture: CodeInsightTestFixture,
    srcDirName: String = "src",
    buildDirName: String = "build",
    srcFileName: String = "Source.kt"
  ): HeavyTestSetUp? =
    module.root?.let { root: VirtualFile ->
      root.addExcludedDir(buildDirName, module)?.let { buildDir: VirtualFile ->
        root.addSourceDir(srcDirName, module)?.let { srcDir: VirtualFile ->
          module.buildFolders().takeIf { it.isNotEmpty() }?.run {
            copyToDir(srcFileName, srcDir, myFixture)?.let { ktFile: KtFile ->
              addMetaDataToBuild(buildDir, myFixture)?.let { target: VirtualFile ->
                HeavyTestSetUp(buildDir, srcDir, ktFile, ktFileToList(myFixture), target)
              }
            }
          }
        }
      }
    }
}
