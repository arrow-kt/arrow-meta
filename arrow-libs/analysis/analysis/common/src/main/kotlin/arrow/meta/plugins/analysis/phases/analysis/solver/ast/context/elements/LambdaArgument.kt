package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface LambdaArgument : ValueArgument {
  fun getLambdaExpression(): LambdaExpression?
}
