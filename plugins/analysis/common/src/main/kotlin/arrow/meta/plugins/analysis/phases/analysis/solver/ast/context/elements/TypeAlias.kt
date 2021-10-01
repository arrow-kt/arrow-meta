package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface TypeAlias : NamedDeclaration {
  fun isTopLevel(): Boolean
  fun getTypeReference(): TypeReference?
}
