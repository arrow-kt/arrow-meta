package arrow.meta.phases.resolve.synthetics

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType

/**
 * @see [ExtensionPhase]
 * @see [arrow.meta.dsl.resolve.ResolveSyntax.syntheticScopes]
 */
interface SyntheticScopeProvider : ExtensionPhase {
  fun CompilerContext.syntheticConstructor(constructor: ConstructorDescriptor): ConstructorDescriptor?
  fun CompilerContext.syntheticConstructors(classifierDescriptors: Collection<DeclarationDescriptor>): Collection<FunctionDescriptor>
  fun CompilerContext.syntheticConstructors(contributedClassifier: ClassifierDescriptor, location: LookupLocation): Collection<FunctionDescriptor>
  fun CompilerContext.syntheticExtensionProperties(receiverTypes: Collection<KotlinType>, location: LookupLocation): Collection<PropertyDescriptor>
  fun CompilerContext.syntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<PropertyDescriptor>
  fun CompilerContext.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor>
  fun CompilerContext.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor>
  fun CompilerContext.syntheticStaticFunctions(functionDescriptors: Collection<DeclarationDescriptor>): Collection<FunctionDescriptor>
  fun CompilerContext.syntheticStaticFunctions(contributedFunctions: Collection<FunctionDescriptor>, location: LookupLocation): Collection<FunctionDescriptor>
}