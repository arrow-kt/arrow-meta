package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ExpressionWithLabel : Expression {
  fun getTargetLabel(): SimpleNameExpression?
  fun getLabelName(): String?
  fun getLabelNameAsName(): Name?
}
