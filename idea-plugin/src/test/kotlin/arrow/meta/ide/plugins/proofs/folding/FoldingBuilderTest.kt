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
            myFixture.addFileToProject("arrow/union/unions.kt", FoldingBuilderTestCode.unionPrelude)
            collectFolding(code, myFixture) { it.unionTypeMatches() }
          },
          result = resolvesWhen("Unions foldingBuilder should return 3 folding regions") {
            it.size == 3
          }
        ),
        IdeTest(
          code = FoldingBuilderTestCode.tupleCode,
          test = { code: Source, myFixture: CodeInsightTestFixture, ctx: IdeMetaPlugin ->
            myFixture.addFileToProject("arrow/tuples/tuples.kt", FoldingBuilderTestCode.tuplePrelude)
            collectFolding(code, myFixture) { it.tupleTypeMatches() }
          },
          result = resolvesWhen("Tuples foldingBuilder should return 3 folding regions") {
            it.size == 3
          }
        )
      )
    }
}