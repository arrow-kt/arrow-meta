package arrow.meta.ide.plugins.proofs.inspections

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import arrow.meta.ide.testing.env.types.LightTestSyntax.toKtFile
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

class CoercionInspectionTest : IdeTestSetUp() {

  override fun setUp() {
    super.setUp()
    myFixture.addFileToProject("arrow/prelude.kt", CoercionInspectionTestCode.prelude)
    myFixture.addFileToProject("consumer/consumer.kt", CoercionInspectionTestCode.twitterHandleDeclaration)
  }

  @org.junit.Test
  fun `coercion inspection test`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, Pair<List<HighlightInfo>, String>>>(
        IdeTest(
          code = CoercionInspectionTestCode.code1,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            val file = code.toKtFile(myFixture)
            val highlightInfos = collectInspections(code, myFixture, listOf(explicitCoercionKtProperty))
              .filter { it.inspectionToolId == EXPLICIT_COERCION_PROPERTIES_ID }
            val codeFixed = highlightInfos[0].fixFirstInspection(myFixture, file)
            Pair(highlightInfos, codeFixed)
          },
          result = resolvesWhen("CoercionInspectionTest1 for 1 implicit coercion") { pairResult ->
            pairResult.first.size == 1
              && pairResult.second == CoercionInspectionTestCode.code1_after_fix
          }
        ),
        IdeTest(
          code = CoercionInspectionTestCode.code2,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            val file = code.toKtFile(myFixture)
            val highlightInfos = collectInspections(code, myFixture, listOf(implicitCoercion))
              .filter { it.inspectionToolId == IMPLICIT_COERCION_INSPECTION_ID }
            val codeFixed = highlightInfos[0].fixFirstInspection(myFixture, file)
            Pair(highlightInfos, codeFixed)
          },
          result = resolvesWhen("CoercionInspectionTest2 for 1 explicit coercion") { pairResult ->
            pairResult.first.size == 1
              && pairResult.second == CoercionInspectionTestCode.code2_after_fix
          }
        ),
        IdeTest(
          code = CoercionInspectionTestCode.code3,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            val file = code.toKtFile(myFixture)
            val highlightInfos = collectInspections(code, myFixture, listOf(explicitCoercionKtValueArgument))
              .filter { it.inspectionToolId == EXPLICIT_COERCION_ARGUMENTS_ID }
            val codeFixed = highlightInfos[0].fixFirstInspection(myFixture, file)
            Pair(highlightInfos, codeFixed)
          },
          result = resolvesWhen("CoercionInspectionTest3 for 1 explicit coercion") { pairResult ->
            pairResult.first.size == 1
              && pairResult.second == CoercionInspectionTestCode.code3_after_fix
          }
        ))
    }
}
