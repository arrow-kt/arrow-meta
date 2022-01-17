package arrow.meta.plugins.analysis.phases.analysis.solver.check.model

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CompilerMessageSourceLocation
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenCondition
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type

/** Data type used to handle `if` and `when` without subject uniformly. */
sealed class Condition(
  val condition: Element?,
  val isElse: Boolean,
  val body: Expression,
  val whole: Element
)

class SimpleCondition(
  val predicate: Expression?,
  isElse: Boolean,
  body: Expression,
  whole: Element
) : Condition(predicate, isElse, body, whole)

class SubjectCondition(
  val check: WhenCondition?,
  isElse: Boolean,
  body: Expression,
  whole: Element
) : Condition(check, isElse, body, whole)

// wrapping for missing else
class MissingElseBlockExpression(val whole: Expression, val thenExpression: Expression) :
  BlockExpression {
  override val firstStatement: Expression?
    get() = null
  override val statements: List<Expression>
    get() = emptyList()
  override val implicitReturnFromLast: Boolean
    get() = false
  override val text: String
    get() = "<implicit empty else block>"

  override fun impl(): Any = Unit
  override val psiOrParent: Element = this
  override fun parents(): List<Element> = thenExpression.parents()

  override fun getResolvedCall(context: ResolutionContext): ResolvedCall? = null
  override fun getVariableDescriptor(context: ResolutionContext): VariableDescriptor? = null
  override fun location(): CompilerMessageSourceLocation? = whole.location()
  override fun type(context: ResolutionContext): Type? = thenExpression.type(context)
  override fun lastBlockStatementOrThis(): Expression = this
}
