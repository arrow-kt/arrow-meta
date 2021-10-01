package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface ObjectDeclaration : ClassOrObject {
  fun isCompanion(): Boolean
  fun isObjectLiteral(): Boolean
}
