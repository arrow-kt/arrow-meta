package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.StringTemplateEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.StringTemplateEntryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.StringTemplateEntryString
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.StringTemplateExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtEscapeStringTemplateEntry
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateEntryWithExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

class KotlinStringTemplateExpression(val impl: KtStringTemplateExpression) :
  StringTemplateExpression, KotlinExpression {
  override fun impl(): KtStringTemplateExpression = impl

  override val entries: List<StringTemplateEntry> =
    impl.entries.mapNotNull { entry ->
      when (entry) {
        is KtEscapeStringTemplateEntry -> StringTemplateEntryString(entry.unescapedValue)
        is KtLiteralStringTemplateEntry -> StringTemplateEntryString(entry.text)
        is KtStringTemplateEntryWithExpression ->
          entry.expression?.let { e -> StringTemplateEntryExpression(e.model()) }
        else -> null
      }
    }
}
