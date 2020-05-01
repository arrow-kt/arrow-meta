package arrow.meta.ide.plugins.proofs.inspections

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.coercions.explicit.COERCION_EXPLICIT_ARGS
import arrow.meta.ide.plugins.proofs.coercions.explicit.COERCION_EXPLICIT_PROP
import arrow.meta.ide.plugins.proofs.coercions.explicit.explicitCoercionKtProperty
import arrow.meta.ide.plugins.proofs.coercions.explicit.explicitCoercionKtValArg
import arrow.meta.ide.plugins.proofs.coercions.implicit.IMPLICIT_COERCION_INSPECTION_ID
import arrow.meta.ide.plugins.proofs.coercions.implicit.implicitCoercion
import arrow.meta.ide.plugins.proofs.markers.CoercionTestCode
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
    myFixture.addFileToProject("arrow/prelude.kt", CoercionTestCode.prelude)
    myFixture.addFileToProject("consumer/consumer.kt", CoercionTestCode.twitterHandleDeclaration)
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
          test = { code: Source, myFixture: CodeInsightTestFixture, ctx: IdeMetaPlugin ->
            val highlightInfos = collectInspections(code, myFixture, listOf(ctx.explicitCoercionKtProperty))
              .filter { it.inspectionToolId == COERCION_EXPLICIT_PROP }
            val codeFixed = highlightInfos[0].fixFirstInspection(myFixture, code.toKtFile(myFixture))
            Pair(highlightInfos, codeFixed)
          },
          result = resolvesWhen("CoercionInspectionTest1 for 1 implicit coercion") { pairResult ->
            pairResult.first.size == 1
              && pairResult.second == CoercionInspectionTestCode.code1_after_fix
          }
        ),
        IdeTest(
          code = CoercionInspectionTestCode.code2,
          test = { code: Source, myFixture: CodeInsightTestFixture, ctx: IdeMetaPlugin ->
            val highlightInfos = collectInspections(code, myFixture, listOf(ctx.implicitCoercion))
              .filter { it.inspectionToolId == IMPLICIT_COERCION_INSPECTION_ID }
            val codeFixed = highlightInfos[0].fixFirstInspection(myFixture, code.toKtFile(myFixture))
            Pair(highlightInfos, codeFixed)
          },
          result = resolvesWhen("CoercionInspectionTest2 for 1 explicit coercion") { pairResult ->
            pairResult.first.size == 1
              && pairResult.second == CoercionInspectionTestCode.code2_after_fix
          }
        ),
        IdeTest(
          code = CoercionInspectionTestCode.code3,
          test = { code: Source, myFixture: CodeInsightTestFixture, ctx: IdeMetaPlugin ->
            val highlightInfos = collectInspections(code, myFixture, listOf(ctx.explicitCoercionKtValArg))
              .filter { it.inspectionToolId == COERCION_EXPLICIT_ARGS }
            val codeFixed = highlightInfos[0].fixFirstInspection(myFixture, code.toKtFile(myFixture))
            Pair(highlightInfos, codeFixed)
          },
          result = resolvesWhen("CoercionInspectionTest3 for 1 explicit coercion") { pairResult ->
            pairResult.first.size == 1
              && pairResult.second == CoercionInspectionTestCode.code3_after_fix
          }
        ))
    }
}
