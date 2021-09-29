package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ObjectDeclaration : ClassOrObject {
  fun isCompanion(): Boolean
  fun isObjectLiteral(): Boolean
}
