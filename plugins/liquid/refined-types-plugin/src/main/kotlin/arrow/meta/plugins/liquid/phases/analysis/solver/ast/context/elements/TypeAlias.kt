package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface TypeAlias : NamedDeclaration {
  fun isTopLevel(): Boolean
  fun getTypeReference(): TypeReference?
}
