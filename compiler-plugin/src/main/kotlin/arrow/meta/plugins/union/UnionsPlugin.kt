package arrow.meta.plugins.union

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable

val Meta.unionTypes: Plugin
  get() =
    "Union Types" {
      meta(
        typeChecker(::UnionTypeChecker)
      )
    }


class UnionTypeChecker(val typeChecker: KotlinTypeChecker) : KotlinTypeChecker by typeChecker {

  private fun KotlinType.isUnion(): Boolean {
    println("type: " + constructor.declarationDescriptor?.fqNameSafe?.asString())
    return constructor.declarationDescriptor?.fqNameSafe?.asString()?.startsWith("Union") == true
  }

  private fun KotlinType.union(other: KotlinType): Boolean =
    isUnion() && arguments.contains(other.asTypeProjection())

  private fun KotlinType.nullableUnionTargets(other: KotlinType): Boolean =
   other.isMarkedNullable && isUnion() && arguments.contains(other.makeNotNullable().asTypeProjection())

  override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean {
    val underlyingResult = typeChecker.isSubtypeOf(p0, p1)
    return if (!underlyingResult) {
      val subType = p0.unwrap()
      val superType = p1.unwrap()
      val inUnion: Boolean = superType.union(subType) || subType.nullableUnionTargets(superType)
      val result = inUnion
      println("UnionTypeChecker.isSubtypeOf: $p0 <-> $p1 = $result, inUnion: $inUnion")
      result
    } else underlyingResult
  }

  override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean {
    //println("KindAwareTypeChecker.equalTypes: $p0 <-> $p1")
    val result = typeChecker.equalTypes(p0, p1)
    println("UnionTypeChecker.equalTypes: $p0 <-> $p1 = $result")
    return result
  }

}


