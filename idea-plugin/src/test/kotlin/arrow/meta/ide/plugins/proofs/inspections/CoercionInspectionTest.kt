package arrow.meta.ide.plugins.proofs.inspections

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand

class CoercionInspectionTest : IdeTestSetUp() {

  override fun setUp() {
    super.setUp()
    myFixture.addFileToProject("arrow/prelude.kt", CoercionInspectionTestCode.prelude)
    myFixture.addFileToProject("consumer/consumer.kt", CoercionInspectionTestCode.twitterHandleDeclaration)
  }

  @org.junit.Test
  fun `coercion highlight inspection test`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, List<HighlightInfo>>>(
        IdeTest(
          code = CoercionInspectionTestCode.code1,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectInspections(code, myFixture, listOf(explicitCoercionInspectionSyntax))
              .filter { it.inspectionToolId == EXPLICIT_COERCION_INSPECTION_ID }
          },
          result = resolvesWhen("CoercionInspectionTest1 for 1 implicit coercion") { descriptor ->
            println("highlightInfos[${descriptor.size}] = $descriptor")
            descriptor.size == 1
          }
        ),
        IdeTest(
          code = CoercionInspectionTestCode.code2,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectInspections(code, myFixture, listOf(implicitCoercionInspectionSyntax))
              .filter { it.inspectionToolId == IMPLICIT_COERCION_INSPECTION_ID }
          },
          result = resolvesWhen("CoercionInspectionTest2 for 1 explicit coercion") { descriptor ->
            println("highlightInfos[${descriptor.size}] = $descriptor")
            descriptor.size == 1
          }
        ))
    }

  @org.junit.Test
  fun `coercion fix inspection test`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, String>>(
        IdeTest(
          code = CoercionInspectionTestCode.code1,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            val file = myFixture.configureByText(KotlinFileType.INSTANCE, code)
            val localFixAction = collectInspections(code, myFixture, listOf(explicitCoercionInspectionSyntax))
              .filter { it.inspectionToolId == EXPLICIT_COERCION_INSPECTION_ID }
              .flatMap { it.quickFixActionMarkers ?: emptyList() }
              .map { it.first.action }
              .firstOrNull()
            project.executeWriteCommand(localFixAction!!.text, null) {
              localFixAction.invoke(project, myFixture.editor, file)
            }
            file.text
          },
          result = resolvesWhen("CoercionInspectionFixTest1 for 1 explicit coercion") { resultCode ->
            println("Expected = ${CoercionInspectionTestCode.code1_after_fix}")
            println("Result   = $resultCode")
            resultCode == CoercionInspectionTestCode.code1_after_fix
          }
        ),
        IdeTest(
          code = CoercionInspectionTestCode.code2,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            val file = myFixture.configureByText(KotlinFileType.INSTANCE, code)
            val localFixAction = collectInspections(code, myFixture, listOf(implicitCoercionInspectionSyntax))
              .filter { it.inspectionToolId == IMPLICIT_COERCION_INSPECTION_ID }
              .flatMap { it.quickFixActionMarkers ?: emptyList() }
              .map { it.first.action }
              .firstOrNull()
            project.executeWriteCommand(localFixAction!!.text, null) {
              localFixAction.invoke(project, myFixture.editor, file)
            }
            file.text
          },
          result = resolvesWhen("CoercionInspectionFixTest2 for 1 implicit coercion") { resultCode ->
            println("Expected = ${CoercionInspectionTestCode.code2_after_fix}")
            println("Result   = $resultCode")
            resultCode == CoercionInspectionTestCode.code2_after_fix
          }
        ))
    }
}
