package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.Meta
import arrow.meta.dsl.platform.cli
import arrow.meta.dsl.platform.ide
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
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

  override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean =
    Log.Verbose({ "ProofTypeChecker.isSubtypeOf: ${p0.toString().take(logTypeSize)} : ${p1.toString().take(logTypeSize)} -> $this" }) {
      if (!p0.isError && !p1.isError) {
        val result = baseLineTypeChecker.isSubtypeOf(p0, p1)
        if (!result && !p0.isError && !p1.isError) {
          compilerContext.extensionProof(p0, p1) != null
        } else result
      } else false
    }

  override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean =
    baseLineTypeChecker.equalTypes(p0, p1)

  @TypeRefinement
  override val kotlinTypeRefiner: KotlinTypeRefiner = ProofsKotlinTypeRefiner(baseLineTypeChecker.kotlinTypeRefiner, compilerContext)

  @TypeRefinement
  override val overridingUtil: OverridingUtil = OverridingUtil.createWithTypeRefiner(kotlinTypeRefiner)

  override fun transformToNewType(type: UnwrappedType): UnwrappedType =
    Log.Verbose({ "transformToNewType = $this" }) {
      baseLineTypeChecker.transformToNewType(type)
    }

}

/**
 * Follow the path of
 * isRefinementNeededForModule ->
 * isRefinementNeededForTypeConstructor ->
 * getOrPutScopeForClass ->
 */
@TypeRefinement
class ProofsKotlinTypeRefiner(val delegate: KotlinTypeRefiner, val compilerContext: CompilerContext) : KotlinTypeRefiner() {

  init {
    val refinerCapability = compilerContext.module?.getCapability(REFINER_CAPABILITY)?.value
    if (refinerCapability == null) {
      compilerContext.module?.getCapability(REFINER_CAPABILITY)?.value = this
    }
  }

  @TypeRefinement
  override fun findClassAcrossModuleDependencies(classId: ClassId): ClassDescriptor? =
    Log.Silent({ "ProofsKotlinTypeRefiner.findClassAcrossModuleDependencies $classId: ClassId = $this" }) {
      delegate.findClassAcrossModuleDependencies(classId)
    }


  @TypeRefinement
  override fun <S : MemberScope> getOrPutScopeForClass(classDescriptor: ClassDescriptor, compute: () -> S): S =
    Log.Silent({ "ProofsKotlinTypeRefiner.getOrPutScopeForClass $classDescriptor: ClassDescriptor = $this" }) {
      delegate.getOrPutScopeForClass(classDescriptor, compute)
    }

  @TypeRefinement
  override fun isRefinementNeededForModule(moduleDescriptor: ModuleDescriptor): Boolean =
    Log.Silent({ "ProofsKotlinTypeRefiner.isRefinementNeededForModule $moduleDescriptor: ModuleDescriptor = $this" }) {
      true
    }


  @TypeRefinement
  override fun isRefinementNeededForTypeConstructor(typeConstructor: TypeConstructor): Boolean =
    Log.Silent({ "ProofsKotlinTypeRefiner.isRefinementNeededForTypeConstructor $typeConstructor: TypeConstructor = $this" }) {
      delegate.isRefinementNeededForTypeConstructor(typeConstructor) ||
        compilerContext.module?.proofs?.any {
          it.from.constructor == typeConstructor
        } == true
    }


  @TypeRefinement
  override fun refineDescriptor(descriptor: DeclarationDescriptor): ClassifierDescriptor? =
    Log.Verbose({ "ProofsKotlinTypeRefiner.refineDescriptor $descriptor: DeclarationDescriptor = $this" }) {
      delegate.refineDescriptor(descriptor)
    }


  @TypeRefinement
  override fun refineSupertypes(classDescriptor: ClassDescriptor): Collection<KotlinType> =
    Log.Verbose({ "ProofsKotlinTypeRefiner.refineSupertypes $classDescriptor: ClassDescriptor = $this" }) {
      delegate.refineSupertypes(classDescriptor)
    }


  @TypeRefinement
  override fun refineType(type: KotlinType): KotlinType =
    Log.Verbose({ "ProofsKotlinTypeRefiner.refineType $type: KotlinType = $this" }) {
      delegate.refineType(type)
    }

}

fun Meta.registerArgumentTypeResolver(): ExtensionPhase =
  cli {
    analysis(
      doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
        Log.Verbose({ "analysis.registerArgumentTypeResolver.initializeProofCache + replace type checker" }) {
          module.initializeProofCache()
          replaceArgumentTypeResolverTypeChecker(componentProvider)
          null
        }
      }
    )
  } ?: ide {
    packageFragmentProvider { project, module, storageManager, trace, moduleInfo, lookupTracker ->
      componentProvider?.let(::replaceArgumentTypeResolverTypeChecker)
      null
    }
  } ?: ExtensionPhase.Empty

fun CompilerContext.replaceArgumentTypeResolverTypeChecker(componentProvider: ComponentProvider) {
  val argumentTypeResolver: ArgumentTypeResolver = componentProvider.get()
  replaceTypeChecker(argumentTypeResolver)
}

fun CompilerContext.replaceTypeChecker(argumentTypeResolver: ArgumentTypeResolver) =
  Log.Verbose({ "replaceArgumentTypeResolverTypeChecker $argumentTypeResolver" }) {
    val typeCheckerField = ArgumentTypeResolver::class.java.getDeclaredField("kotlinTypeChecker").also { it.isAccessible = true }
    typeCheckerField.set(argumentTypeResolver, ProofTypeChecker(this))
  }