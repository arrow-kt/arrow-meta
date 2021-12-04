package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName

interface DeclarationDescriptor : Named, Annotated {
  val containingDeclaration: DeclarationDescriptor?
  val containingPackage: FqName?
  fun element(): Element?
  val fqNameSafe: FqName
  fun impl(): Any
}
