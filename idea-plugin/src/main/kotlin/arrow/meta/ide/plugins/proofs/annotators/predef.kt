package arrow.meta.ide.plugins.proofs.annotators

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.LocalQuickFixBase
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemDescriptorBase
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import java.awt.Color
import java.awt.Font

internal val implicitProofAnnotatorTextAttributes =
  TextAttributes(null, null, Color(192, 192, 192), EffectType.WAVE_UNDERSCORE, Font.PLAIN)

fun addLocalQuickFix(
  name: String,
  familyName: String,
  applyFix: (project: Project, descriptor: ProblemDescriptor) -> Unit
) = object : LocalQuickFixBase(name, familyName) {
  override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
    applyFix(project, descriptor)
  }
}

fun addProblemDescriptor(
  startElement: PsiElement,
  endElement: PsiElement,
  descriptionTemplate: String,
  localQuickFixes: Array<LocalQuickFix>,
  highlightType: ProblemHighlightType = ProblemHighlightType.INFORMATION,
  isAfterEndOfLine: Boolean = false,
  rangeInElement: TextRange? = null,
  showTooltip: Boolean = false,
  onTheFly: Boolean = true
) = ProblemDescriptorBase(
  startElement,
  endElement,
  descriptionTemplate,
  localQuickFixes,
  highlightType,
  isAfterEndOfLine,
  rangeInElement,
  showTooltip,
  onTheFly
)