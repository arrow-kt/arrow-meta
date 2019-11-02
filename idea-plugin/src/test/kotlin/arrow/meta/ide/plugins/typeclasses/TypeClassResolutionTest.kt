package arrow.meta.ide.plugins.typeclasses

import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.env.IdeHeavyTestSetUp
import arrow.meta.ide.testing.env.testResult
import arrow.meta.ide.testing.env.types.defaultConfig
import arrow.meta.ide.testing.resolves
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.types.ErrorType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import org.junit.Test

class TypeClassResolutionTest : IdeHeavyTestSetUp() {

  @Test
  fun test() {
    val d = IdeTest(
        code = TypeclassesTestCode.c1,
        myFixture = myFixture,
        test = { code, myFixture ->
          traverseResolution(
            code = code,
            myFixture = myFixture,
            module = myModule,
            project = project,
            compilerConfig = defaultConfig(System.getProperty("CURRENT_VERSION"))
          ) {
            it.takeIf { p -> p.safeAs<KtDeclaration>()?.type() is ErrorType }
          }
        },
        result = resolves("No Unresolved KtElements") {
          it.takeIf { l -> l.isEmpty() }
        }
    ).testResult()

    assert(true)
  }


  /*fun testSetup() {
    val code = TypeclassesTestCode.c1
    val module = myModule

    // setup content-root
    val roots = ModuleRootManager.getInstance(module).contentRoots
    assertSize(1, roots)
    val contentRoot = roots[0]

    // then add and exclude content-root/build
    val buildFolder = WriteAction.computeAndWait<VirtualFile, Throwable> {
      contentRoot.createChildDirectory(this, "build")
    }
    PsiTestUtil.addExcludedRoot(module, buildFolder)

    // then add content-root/src as source folder
    val srcFolder = WriteAction.computeAndWait<VirtualFile, Throwable> {
      contentRoot.createChildDirectory(this, "src")
    }
    PsiTestUtil.addExcludedRoot(module, srcFolder)

    val buildFolders = project.buildFolders()
    assertSize(1, buildFolders)

    // copy code snippet into a new virtual file
    // TODO: alternatively we could store a bunch of .kt files on disk and run the test for each
    val sourceFile = WriteAction.computeAndWait<VirtualFile, Throwable> {
      val sourceFile = srcFolder.createChildData(this, "source.kt")
      sourceFile.setBinaryContent(code.toByteArray())
      sourceFile
    }

    // TODO: make sure that the source file is copied into a source directory and the right dir structure
    myFixture.configureFromExistingVirtualFile(sourceFile)
    val psiFile = myFixture.file
    assertEquals(psiFile.fileType, KotlinFileType.INSTANCE)

    // TODO: fix compileCodeSnippet and copy data properly into build folder
    val classOutputDir = compileCodeSnippet(code)!!
    val target = myFixture.copyDirectoryToProject(classOutputDir.path, buildFolder.path)
    assertNotNull(target)

    // now make sure everything in the editor is resolved
    // TODO(jansorg): use arrow test code to collect PsiElement of the file
    // TODO(jansorg): finish test
    SyntaxTraverser.psiTraverser().children(psiFile).forEach { psi ->
      if (psi is KtDeclaration) {
        val psiType = psi.type()
        assertTrue(psiType !is ErrorType)
      }
    }
  }

  // compiles a code snippet and returns the directory which contains the generated .class files
  fun compileCodeSnippet(kotlinSource: String): VirtualFile? {
    val currentVersion = System.getProperty("CURRENT_VERSION")
    val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin")))
    val arrowAnnotations = Dependency("arrow-annotations:$currentVersion")

    //TODO(jansorg): don't assert, but access the output directory and copy it into a VirtualFile dir
    assertThis(CompilerTest(
      config = {
        addCompilerPlugins(compilerPlugin) + addDependencies(arrowAnnotations)
      },
      code = {
        Source(kotlinSource)
      },
      assert = {
        Assert.compiles
      }
    ))

    return null
  }*/
}
