package arrow.meta.plugins.union

import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable

/**
 * A [KotlinTypeChecker] that understands that:
 * ```kotlin
 * A? : Union<A, B>
 * B? : Union<A, B>
 * ```
 */
class UnionTypeChecker(val typeChecker: KotlinTypeChecker) : KotlinTypeChecker by typeChecker {

  override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean {
    val underlyingResult = typeChecker.isSubtypeOf(p0, p1)
    return if (!underlyingResult) {
      val subType = p0.unwrap()
      val superType = p1.unwrap()
      val inUnion: Boolean = superType.union(subType)
      val result = inUnion
      println("UnionTypeChecker.isSubtypeOf: $subType <-> $superType = $result, inUnion: $inUnion")
      result
    } else underlyingResult
  }

  override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean =
    typeChecker.equalTypes(p0, p1)

}

fun KotlinType.nullableUnionTargets(subType: KotlinType): Boolean =
  subType.isMarkedNullable && isUnion() && arguments.contains(subType.makeNotNullable().asTypeProjection())

private fun KotlinType.isUnion(): Boolean {
  println("type: " + constructor.declarationDescriptor?.name?.asString())
  return constructor.declarationDescriptor?.name?.asString()?.startsWith("Union") == true
}

fun KotlinType.union(subType: KotlinType) =
  isUnion() && arguments.contains(subType.asTypeProjection()) || nullableUnionTargets(subType)