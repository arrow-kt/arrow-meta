package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface AnonymousInitializer : Declaration {
  val containingDeclaration: Declaration
  val body: Expression?
}

