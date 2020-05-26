package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.lineMarker.LineMarkerDescription
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

class UnionLineMarkerTest : IdeTestSetUp() {

  override fun setUp() {
    super.setUp()
    myFixture.addFileToProject("arrow/unionPrelude.kt", UnionLineMarkerTestCode.unionPrelude)
  }

  @org.junit.Test
  fun `test coercion line marker`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, LineMarkerDescription>>(
        IdeTest(
          code = UnionLineMarkerTestCode.unionCode,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectLM(code, myFixture, ArrowIcons.COPRODUCT)
          },
          result = resolvesWhen("UnionLineMarkerTest1 for 3 LM ") { descriptor: LineMarkerDescription ->
            descriptor.lineMarker.size == 3 && descriptor.slowLM.isEmpty()
          }
        )
      )
    }
}
