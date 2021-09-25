package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ValueArgument : ExpressionValueArgument {
  fun getArgumentName(): ValueArgumentName?
  fun isNamed(): Boolean
  fun asElement(): Element
  fun isExternal(): Boolean
}

