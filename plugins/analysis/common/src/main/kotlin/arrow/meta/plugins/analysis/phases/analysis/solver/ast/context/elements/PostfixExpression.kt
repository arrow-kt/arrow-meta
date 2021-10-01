
package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface PostfixExpression : UnaryExpression {
  override val baseExpression: Expression?
}
