package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type

interface TypeAliasDescriptor : ClassifierDescriptorWithTypeParameters {

  val underlyingType: Type

  val expandedType: Type

  val classDescriptor: ClassDescriptor?

  val constructors: Collection<TypeAliasConstructorDescriptor>
}
