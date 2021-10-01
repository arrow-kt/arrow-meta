
package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface NamedDeclaration : Declaration, Named {
  val nameAsSafeName: Name
  val fqName: FqName?
}
