package arrow.meta.proofs

import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker

class ProofTypeChecker(private val proofs: List<Proof>) : KotlinTypeChecker {
  override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean {
    val result = NewKotlinTypeChecker.isSubtypeOf(p0, p1)
    val subTypes = if (!result) {
      proofs.hasProof(p0, p1)
    } else result
    //println("typeConversion:isSubtypeOf: $p0 : $p1 -> $subTypes")
    return subTypes
  }

  override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean {
    val result = NewKotlinTypeChecker.equalTypes(p0, p1)
    //println("typeConversion:equalTypes: $p0 : $p1 -> $result")
    return result
  }
}