package arrow.meta.proofs

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.resolve.typeProofs
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker
import org.jetbrains.kotlin.types.isError
import java.util.*
import kotlin.collections.ArrayList

class ProofTypeChecker(private val compilerContext: CompilerContext) : KotlinTypeChecker {

  override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean {
    return if (!p0.isError && !p1.isError) {
      val result = NewKotlinTypeChecker.isSubtypeOf(p0, p1)
      val subTypes = if (!result && !p0.isError && !p1.isError) {
        compilerContext.module.typeProofs.hasProof(p0, p1)
      } else result
      println("typeConversion: $p0 : $p1 -> $subTypes")
      subTypes
    } else false
  }

  override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean {
    val result = NewKotlinTypeChecker.equalTypes(p0, p1)
    //println("typeConversion:equalTypes: $p0 : $p1 -> $result")
    return result
  }
}