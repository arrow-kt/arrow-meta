package arrow.meta.proofs

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.resolve.`isSubtypeOf(NewKotlinTypeChecker)`
import arrow.meta.phases.resolve.baseLineTypeChecker
import arrow.meta.phases.resolve.typeProofs
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.isError

private const val logTypeSize: Int = 50

class ProofTypeChecker(private val compilerContext: CompilerContext) : KotlinTypeChecker {

  override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean {
    return if (!p0.isError && !p1.isError) {
      val result = p0.`isSubtypeOf(NewKotlinTypeChecker)`(p1)
      val subTypes = if (!result && !p0.isError && !p1.isError) {
        if (p1.nestedCallOnExtension(p0)) true
        else compilerContext.module?.typeProofs?.subtypingProof(compilerContext, p0, p1) != null
      } else result
      println("typeConversion: ${p0.toString().take(logTypeSize)} : ${p1.toString().take(logTypeSize)} -> $subTypes")
      subTypes
    } else false
  }

  private fun KotlinType.nestedCallOnExtension(p0: KotlinType) =
    arguments.isNotEmpty()
      && annotations.hasAnnotation(FqName("arrowx.given"))
      && arguments.first().type.`isSubtypeOf(NewKotlinTypeChecker)`(p0)

  override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean {
    val result = baseLineTypeChecker.equalTypes(p0, p1)
    println("typeConversion:equalTypes: $p0 : $p1 -> $result")
    return result
  }
}