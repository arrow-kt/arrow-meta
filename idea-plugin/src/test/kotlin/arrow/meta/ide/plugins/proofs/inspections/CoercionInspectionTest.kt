package arrow.meta.ide.plugins.proofs.inspections

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import com.intellij.codeHighlighting.Pass
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.core.moveCaret
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.psi.KtFile

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
      listOf<IdeTest<IdeMetaPlugin, String>>(
        IdeTest(
          code = CoercionInspectionTestCode.code1,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            val file = myFixture.configureByText(KotlinFileType.INSTANCE, code)
            myFixture.openFileInEditor(file.virtualFile)

            val inspection: AbstractKotlinInspection = explicitCoercionInspectionSyntax
            myFixture.enableInspections(inspection)
            myFixture.editor.moveCaret(myFixture.file.text.indexOf("@danieeehh"))
            //val psi = myFixture.elementAtCaret

            val highlightInfos = CodeInsightTestFixtureImpl.instantiateAndRun(
              file, myFixture.editor, intArrayOf(
              Pass.LINE_MARKERS,
              Pass.EXTERNAL_TOOLS,
              Pass.POPUP_HINTS,
              Pass.UPDATE_ALL,
              Pass.UPDATE_FOLDING,
              Pass.WOLF
            ), (file as? KtFile)?.isScript() == true
            )
            // .filter { it.description != null /*&& caretOffset in it.startOffset..it.endOffset*/ }

            // TODO() currently only built-in inspections are instantiated, none from Meta
            println("highlightInfos[${highlightInfos.size}] = $highlightInfos")
            //collectLM(code, myFixture, ArrowIcons.ICON4)
            ""
          },
          result = resolvesWhen("CoercionInspectionTest1 for 2 LM ") { descriptor ->
//            println("CoercionTest1 Result[${descriptor.lineMarker.size}]=${descriptor.lineMarker}")
//            descriptor.lineMarker.size == 2 && descriptor.slowLM.isEmpty()
            true
          }
        ),
        IdeTest(
          code = CoercionInspectionTestCode.code2,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->

            val file = myFixture.configureByText(KotlinFileType.INSTANCE, code)
            myFixture.openFileInEditor(file.virtualFile)

            val inspection: AbstractKotlinInspection = implicitCoercionInspectionSyntax
            myFixture.enableInspections(inspection)
            myFixture.editor.moveCaret(myFixture.file.text.indexOf("@danieeehh"))
            //val psi = myFixture.elementAtCaret

            val highlightInfos = CodeInsightTestFixtureImpl.instantiateAndRun(
              file, myFixture.editor, intArrayOf(
              Pass.LINE_MARKERS,
              Pass.EXTERNAL_TOOLS,
              Pass.POPUP_HINTS,
              Pass.UPDATE_ALL,
              Pass.UPDATE_FOLDING,
              Pass.WOLF
            ), (file as? KtFile)?.isScript() == true
            )
            // .filter { it.description != null /*&& caretOffset in it.startOffset..it.endOffset*/ }

            // TODO() currently only built-in inspections are instantiated, none from Meta
            println("highlightInfos[${highlightInfos.size}] = $highlightInfos")
            //collectLM(code, myFixture, ArrowIcons.ICON4)
            ""
          },
          result = resolvesWhen("CoercionInspectionTest2 for 2 LM ") { descriptor ->
//            println("CoercionTest1 Result[${descriptor.lineMarker.size}]=${descriptor.lineMarker}")
//            descriptor.lineMarker.size == 2 && descriptor.slowLM.isEmpty()
            true
          }
        ))
    }
}
