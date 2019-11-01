package arrow.meta.ide.testing.env.types

import arrow.meta.ide.phases.config.buildFolders
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.env.types.HeavyTestSyntax.plus
import arrow.meta.plugin.testing.CompilationData
import arrow.meta.plugin.testing.CompilationResult
import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.Config
import arrow.meta.plugin.testing.ConfigSyntax
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.compilationData
import arrow.meta.plugin.testing.compile
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
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

val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin")))
val arrowAnnotations = { str: String -> Dependency("arrow-annotations:$str") }
val arrowCoreData = { str: String -> Dependency("arrow-core-data:$str") }

val defaultConfig: (String) -> List<Config>
  get() = { str -> HeavyTestSyntax.addCompilerPlugins(compilerPlugin) + HeavyTestSyntax.addDependencies(arrowAnnotations(str), arrowCoreData(str)) }

object HeavyTestSyntax : CommonTestSyntax, ConfigSyntax {
  override val emptyConfig: Config
    get() = Config.Empty

  fun Source.compile(config: List<Config>): CompilationResult =
    compile(config.compilationData(CompilationData(source = listOf(this.trimMargin()))))

  val CompilationResult.outDirFile: VirtualFile?
    get() = TODO("::outputDirectory")

  fun Source.addMetaDataToBuild(config: List<Config>, buildDir: VirtualFile, myFixture: CodeInsightTestFixture): VirtualFile? =
    compile(config).outDirFile?.let { myFixture.copyDirectoryToProject(it.path, buildDir.path) }

  fun Source.toFile(name: String = "Source.kt", dir: VirtualFile): VirtualFile? =
    WriteAction.computeAndWait<VirtualFile, Throwable> {
      val sourceFile = dir.createChildData(this, name)
      sourceFile.setBinaryContent(toByteArray())
      sourceFile
    }

  fun Source.copyToDir(name: String = "Source.kt", dir: VirtualFile, myFixture: CodeInsightTestFixture): KtFile? =
    toFile(name, dir)?.let { file: VirtualFile ->
      myFixture.configureFromExistingVirtualFile(file)
      myFixture.file.fileType.safeAs()
    }

  /**
   * @param receiver is the root file
   */
  fun VirtualFile.addDir(name: String, module: Module): VirtualFile? =
    WriteAction.computeAndWait<VirtualFile, Throwable> { this.createChildDirectory(this@HeavyTestSyntax, "build") }
      .also { PsiTestUtil.addExcludedRoot(module, it) }

  val Module.root: VirtualFile?
    get() = ModuleRootManager.getInstance(this).contentRoots.takeIf { it.isNotEmpty() }?.firstOrNull()

  fun Source.ideHeavySetup(
    module: Module,
    project: Project,
    myFixture: CodeInsightTestFixture,
    srcDirName: String = "src",
    buildDirName: String = "build",
    srcFileName: String = "Source.kt",
    config: List<Config>
  ): HeavyTestSetUp? =
    module.root?.let { root: VirtualFile ->
      root.addDir(buildDirName, module)?.let { buildDir: VirtualFile ->
        root.addDir(srcDirName, module)?.let { srcDir: VirtualFile ->
          project.buildFolders().takeIf { it.isNotEmpty() }?.run {
            copyToDir(srcFileName, srcDir, myFixture)?.let { ktFile: KtFile ->
              addMetaDataToBuild(config, buildDir, myFixture)?.let { target: VirtualFile ->
                HeavyTestSetUp(buildDir, srcDir, ktFile, ktFileToList(myFixture), target)
              }
            }
          }
        }
      }
    }
}


