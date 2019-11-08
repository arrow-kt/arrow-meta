package arrow.meta.ide.testing.env.types

import arrow.meta.ide.phases.config.buildFolders
import arrow.meta.ide.testing.Source
import com.tschuchort.compiletesting.KotlinCompilation.Result
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.io.File
import io.github.classgraph.ClassGraph

data class HeavyTestSetUp(
  val buildDir: VirtualFile,
  val srcDir: VirtualFile,
  val ktFile: KtFile,
  val traversable: List<PsiElement>,
  val outDirTarget: VirtualFile
)

object HeavyTestSyntax : CommonTestSyntax {

  fun compile(source: String): Result {
    val currentVersion = System.getProperty("CURRENT_VERSION")

    return KotlinCompilation().apply {
      sources = listOf(SourceFile.kotlin("Example.kt", source))
      classpaths = listOf(classpathOf("arrow-annotations:$currentVersion"), classpathOf("arrow-core-data:$currentVersion"))
      pluginClasspaths = listOf(classpathOf("compiler-plugin"))
    }.compile()
  }

  val Result.outDirFile: VirtualFile?
    get() = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(outputDirectory)

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

  private fun classpathOf(dependency: String): File {
    val regex = Regex(".*${dependency.replace(':', '-')}.*")
    val file = ClassGraph().classpathFiles.firstOrNull { classpath -> classpath.name.matches(regex) }
    return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
  }
}


