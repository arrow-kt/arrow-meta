package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.resolve.baseLineTypeChecker
import arrow.meta.plugins.proofs.phases.extensionProof
import org.jetbrains.kotlin.resolve.OverridingUtil
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.refinement.TypeRefinement

private const val logTypeSize: Int = 50

class ProofTypeChecker(private val compilerContext: CompilerContext) : NewKotlinTypeChecker {

  override fun isSubtypeOf(subtype: KotlinType, supertype: KotlinType): Boolean {
    val result = baseLineTypeChecker.isSubtypeOf(subtype, supertype)
    return if (!result && !subtype.isError && !supertype.isError) {
      Log.Verbose({ "ProofTypeChecker.isSubtypeOf: ${subtype.toString().take(logTypeSize)} : ${supertype.toString().take(logTypeSize)} -> $this" }) {
        compilerContext.extensionProof(subtype, supertype) != null
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
