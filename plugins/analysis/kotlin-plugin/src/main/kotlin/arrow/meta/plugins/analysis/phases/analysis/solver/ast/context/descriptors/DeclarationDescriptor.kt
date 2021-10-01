package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName

interface DeclarationDescriptor : Named, Annotated {
  val module: ModuleDescriptor
  val containingDeclaration: DeclarationDescriptor?
  fun element(): Element?
  val fqNameSafe: FqName
}
