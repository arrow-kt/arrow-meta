package arrow.meta.ide.testing.dsl.synthetic

import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.env.IdeTestTypeSyntax
import arrow.meta.plugin.testing.Config
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

interface SyntheticResolutionTestSyntax {
  fun IdeTestTypeSyntax.traverseResolution(
    code: Source,
    srcFileName: String = "Source.kt",
    compilerConfig: List<Config>,
    module: Module,
    myFixture: CodeInsightTestFixture,
    srcDirName: String = "src",
    buildDirName: String = "build",
    f: (PsiElement) -> PsiElement?
  ): List<PsiElement> =
    heavyTest {
      code.ideHeavySetup(module, myFixture, srcDirName, buildDirName, srcFileName, compilerConfig)?.traversable
        ?.mapNotNull(f)
    } ?: emptyList()
}
