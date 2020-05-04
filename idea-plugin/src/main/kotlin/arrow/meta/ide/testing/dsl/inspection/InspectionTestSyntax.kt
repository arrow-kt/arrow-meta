package arrow.meta.ide.testing.dsl.inspection

import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.IdeTestSyntax
import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInspection.InspectionProfileEntry
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile

interface InspectionTestSyntax {
  /**
   * enables all [inspections] and returns the aggregated highlighting information of the file.
   */
  fun IdeTestSyntax.collectInspections(
    code: Source,
    myFixture: CodeInsightTestFixture,
    inspections: List<InspectionProfileEntry>,
    toIgnore: List<Int> = ignoredHighlighting,
    changes: (KtFile) -> Boolean = { it.isScript() }
  ): List<HighlightInfo> =
    lightTest {
      inspections.forEach { myFixture.enableInspections(it) }
      code.toKtFile(myFixture)?.run { highlighting(myFixture, toIgnore, changes) }
    }.orEmpty()

  /**
   * https://github.com/JetBrains/kotlin/blob/d95a94a6c1ba08e83c7974a108e0b186ca96c3f7/idea/tests/org/jetbrains/kotlin/idea/inspections/AbstractLocalInspectionTest.kt#L140
   */
  val InspectionTestSyntax.ignoredHighlighting: List<Int>
    get() = listOf(Pass.LINE_MARKERS, Pass.EXTERNAL_TOOLS, Pass.POPUP_HINTS, Pass.UPDATE_ALL, Pass.UPDATE_FOLDING, Pass.WOLF)
}