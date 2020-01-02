package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.plugins.proofs.phases.proofs
import arrow.meta.plugins.proofs.phases.callables
import arrow.meta.plugins.proofs.phases.extending
import arrow.meta.plugins.proofs.phases.fromSubtyping
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.types.KotlinType
import java.util.*

class ProofsSyntheticResolver : SyntheticResolveExtension {

  override fun generateSyntheticClasses(thisDescriptor: ClassDescriptor, name: Name, ctx: LazyClassContext, declarationProvider: ClassMemberDeclarationProvider, result: MutableSet<ClassDescriptor>) {
    Log.Verbose({ "MetaSyntheticResolver.generateSyntheticClasses: $thisDescriptor, name: $name" }) {
    }
  }

  override fun generateSyntheticClasses(thisDescriptor: PackageFragmentDescriptor, name: Name, ctx: LazyClassContext, declarationProvider: PackageMemberDeclarationProvider, result: MutableSet<ClassDescriptor>) {
    Log.Verbose({ "MetaSyntheticResolver.generateSyntheticClasses `package`: $thisDescriptor, name: $name" }) {
    }
  }

  override fun generateSyntheticSecondaryConstructors(thisDescriptor: ClassDescriptor, bindingContext: BindingContext, result: MutableCollection<ClassConstructorDescriptor>) {
    Log.Verbose({ "MetaSyntheticResolver.generateSyntheticSecondaryConstructors: $thisDescriptor, $bindingContext" }) {
    }
  }

  override fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? =
    Log.Verbose({ "MetaSyntheticResolver.getSyntheticCompanionObjectNameIfNeeded: $thisDescriptor $this" }) {
      null
    }

  override fun getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> =
    Log.Verbose({ "MetaSyntheticResolver.getSyntheticNestedClassNames: $thisDescriptor $this" }) {
      emptyList()
    }

  override fun addSyntheticSupertypes(
    thisDescriptor: ClassDescriptor,
    supertypes: MutableList<KotlinType>
  ) {
    Log.Verbose({ "MetaSyntheticResolver.addSyntheticSupertypes: $thisDescriptor, supertypes: $supertypes: $this" }) {
    }
  }

  override fun generateSyntheticMethods(
    thisDescriptor: ClassDescriptor,
    name: Name,
    bindingContext: BindingContext,
    fromSupertypes: List<SimpleFunctionDescriptor>,
    result: MutableCollection<SimpleFunctionDescriptor>
  ) {
    val proofs = thisDescriptor.module.proofs
    Log.Verbose({ "MetaSyntheticResolver.generateSyntheticMethods: $thisDescriptor, name: $name, proofs: $proofs result: $this" }) {
      proofs
        .extending(thisDescriptor.defaultType)
        .flatMapTo(result) {
          it.callables { true }
            .filterIsInstance<SimpleFunctionDescriptor>()
            .map {
              it.copy(thisDescriptor, Modality.FINAL, Visibilities.PUBLIC, CallableMemberDescriptor.Kind.SYNTHESIZED, true)
            }
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
    }
  }

  override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> =
    Log.Verbose({ "MetaSyntheticResolver.getSyntheticFunctionNames: $thisDescriptor, result: $this" }) {
      thisDescriptor.module.proofs
        .extending(thisDescriptor.defaultType)
        .flatMap { proof ->
          proof.callables { true }.map { it.name }
        }
    }


}

