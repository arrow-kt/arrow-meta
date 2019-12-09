package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.phases.resolve.synthetic
import arrow.meta.phases.resolve.toSynthetic
import arrow.meta.phases.resolve.typeProofs
import arrow.meta.proofs.extensions
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.types.KotlinType
import java.util.*

class ProofsSyntheticResolver : SyntheticResolveExtension {

  override fun addSyntheticSupertypes(
    thisDescriptor: ClassDescriptor,
    supertypes: MutableList<KotlinType>
  ) {
//    Log.Verbose({ "MetaSyntheticResolver.addSyntheticSupertypes: $thisDescriptor, supertypes: $supertypes: $this" }) {
//      thisDescriptor.module.typeProofs
//        .subtyping(thisDescriptor.defaultType)
//        .mapTo(supertypes, Proof::to)
//    }
  }

  override fun generateSyntheticMethods(
    thisDescriptor: ClassDescriptor,
    name: Name,
    bindingContext: BindingContext,
    fromSupertypes: List<SimpleFunctionDescriptor>,
    result: MutableCollection<SimpleFunctionDescriptor>
  ) {
    val proofs = thisDescriptor.module.typeProofs
    Log.Verbose({ "MetaSyntheticResolver.generateSyntheticMethods: $thisDescriptor, name: $name, proofs: $proofs result: $this" }) {
      proofs
        .extensions(thisDescriptor.defaultType)
        .flatMapTo(result) {
          it.to.memberScope.getContributedDescriptors { true }
            .filterIsInstance<SimpleFunctionDescriptor>()
            .toList()
            .toSynthetic()
        }
    }
  }

  override fun generateSyntheticProperties(
    thisDescriptor: ClassDescriptor,
    name: Name,
    bindingContext: BindingContext,
    fromSupertypes: ArrayList<PropertyDescriptor>,
    result: MutableSet<PropertyDescriptor>
  ) {
    Log.Verbose({ "MetaSyntheticResolver.generateSyntheticProperties: $thisDescriptor, name: $name, result: $this" }) {
      thisDescriptor.module.typeProofs
        .extensions(thisDescriptor.defaultType)
        .flatMapTo(result) {
          it.to.memberScope.getContributedDescriptors { true }
            .filterIsInstance<PropertyDescriptor>()
            .map { it.synthetic() }
        }
    }
  }

  override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> =
    Log.Verbose({ "MetaSyntheticResolver.getSyntheticFunctionNames: $thisDescriptor, result: $this" }) {
      thisDescriptor.module.typeProofs
        .extensions(thisDescriptor.defaultType)
        .flatMap { proof ->
          proof.to.memberScope.getContributedDescriptors { true }
            .filterIsInstance<SimpleFunctionDescriptor>()
            .map { it.name }
        }
    }


}

