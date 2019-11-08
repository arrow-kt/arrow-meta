package arrow.meta.ide.testing.dsl.synthetic

import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.env.IdeTestTypeSyntax
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

interface SyntheticResolutionTestSyntax {
  fun IdeTestTypeSyntax.traverseResolution(
    code: Source,
    srcFileName: String = "Source.kt",
    module: Module,
    myFixture: CodeInsightTestFixture,
    srcDirName: String = "src",
    buildDirName: String = "build",
    f: (PsiElement) -> PsiElement?
  ): List<PsiElement> =
    heavyTest {
      code.ideHeavySetup(module, myFixture, srcDirName, buildDirName, srcFileName)?.traversable
        ?.mapNotNull(f)
    } ?: emptyList()
}
