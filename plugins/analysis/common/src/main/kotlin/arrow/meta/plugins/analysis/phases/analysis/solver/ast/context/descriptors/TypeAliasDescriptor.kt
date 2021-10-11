package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type

interface TypeAliasDescriptor : ClassifierDescriptorWithTypeParameters {

  val underlyingType: Type

  val expandedType: Type

  val classDescriptor: ClassDescriptor?

  val constructors: Collection<TypeAliasConstructorDescriptor>
}
