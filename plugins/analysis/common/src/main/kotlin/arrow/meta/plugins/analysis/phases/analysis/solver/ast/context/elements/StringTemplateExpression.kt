package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

sealed interface StringTemplateEntry

data class StringTemplateEntryString(val string: String) : StringTemplateEntry

data class StringTemplateEntryExpression(val expression: Expression) : StringTemplateEntry

interface StringTemplateExpression : Expression {
  val entries: List<StringTemplateEntry>
}
