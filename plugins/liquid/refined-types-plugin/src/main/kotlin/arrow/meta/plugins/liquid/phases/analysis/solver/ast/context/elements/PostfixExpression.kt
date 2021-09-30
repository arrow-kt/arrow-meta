
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface PostfixExpression : UnaryExpression {
  override val baseExpression: Expression?
}
