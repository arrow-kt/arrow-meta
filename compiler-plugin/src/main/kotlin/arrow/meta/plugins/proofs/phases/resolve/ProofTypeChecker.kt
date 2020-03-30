package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.Meta
import arrow.meta.dsl.platform.cli
import arrow.meta.dsl.platform.ide
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.resolve.baseLineTypeChecker
import arrow.meta.plugins.proofs.phases.extensionProof
import arrow.meta.plugins.proofs.phases.proofs
import arrow.meta.plugins.proofs.phases.resolve.cache.initializeProofCache
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.resolve.OverridingUtil
import org.jetbrains.kotlin.resolve.calls.ArgumentTypeResolver
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyPackageDescriptor
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker
import org.jetbrains.kotlin.types.checker.REFINER_CAPABILITY
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.refinement.TypeRefinement
import kotlin.contracts.ConditionalEffect
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.Returns
import kotlin.contracts.SimpleEffect
import kotlin.contracts.contract

private const val logTypeSize: Int = 50

class ProofTypeChecker(private val compilerContext: CompilerContext) : NewKotlinTypeChecker {

  override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean {
    val result = baseLineTypeChecker.isSubtypeOf(p0, p1)
    return if (!result && !p0.isError && !p1.isError) {
      Log.Silent({ "ProofTypeChecker.isSubtypeOf: ${p0.toString().take(logTypeSize)} : ${p1.toString().take(logTypeSize)} -> $this" }) {
        compilerContext.extensionProof(p0, p1) != null
      }
    } else result
  }


  override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean =
    baseLineTypeChecker.equalTypes(p0, p1)

  @TypeRefinement
  override val kotlinTypeRefiner: KotlinTypeRefiner = baseLineTypeChecker.kotlinTypeRefiner

  @TypeRefinement
  override val overridingUtil: OverridingUtil = OverridingUtil.createWithTypeRefiner(kotlinTypeRefiner)

  override fun transformToNewType(type: UnwrappedType): UnwrappedType =
    Log.Verbose({ "transformToNewType = $this" }) {
      baseLineTypeChecker.transformToNewType(type)
    }

}
