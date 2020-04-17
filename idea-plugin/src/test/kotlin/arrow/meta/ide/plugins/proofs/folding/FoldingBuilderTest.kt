package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.junit.Test

class FoldingBuilderTest : IdeTestSetUp() {
  @Test
  fun `folding builder test for Union and Tuple types`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, List<FoldingDescriptor>>>(
        IdeTest(
          code = FoldingBuilderTestCode.unionCode,
          test = { code: Source, myFixture: CodeInsightTestFixture, ctx: IdeMetaPlugin ->
            collectFR(code, myFixture) { unionTypeMatches(it) }
          },
          result = resolvesWhen("Union foldingBuilder should return 4 folding regions") {
            println("foldingRegions[${it.size}] = $it")
            it.size == 4
          }
        ),
        IdeTest(
          code = FoldingBuilderTestCode.tupleCode,
          test = { code: Source, myFixture: CodeInsightTestFixture, ctx: IdeMetaPlugin ->
            collectFR(code, myFixture) { tupleTypeMatches(it) }
          },
          result = resolvesWhen("Tuple foldingBuilder should return 19 folding regions") {
            println("foldingRegions[${it.size}] = $it")
            it.size == 19
          }
        )
      )
    }
}