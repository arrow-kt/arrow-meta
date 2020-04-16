package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.folding.FoldingRegions
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.junit.Test

class UnionTypesTest : IdeTestSetUp() {
  @Test
  fun `folding builder test for Union types`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, FoldingRegions>>(
        IdeTest(
          code = UnionTypesTestCode.code1,
          test = { code: Source, myFixture: CodeInsightTestFixture, ctx: IdeMetaPlugin ->
            collectFR(code, myFixture, ArrowIcons.OPTICS)
          },
          result = resolvesWhen("UnionTypesTest for 0 Union folding regions ") {
            println("foldingRegions= ${it.foldingRegions}")
            it.foldingRegions.isEmpty()
          }
        )
      )
    }
}