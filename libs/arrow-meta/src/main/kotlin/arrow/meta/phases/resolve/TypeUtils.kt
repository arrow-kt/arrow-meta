package arrow.meta.phases.resolve

import org.jetbrains.kotlin.types.IntersectionTypeConstructor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeAttributes
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner
import org.jetbrains.kotlin.types.checker.NewKotlinTypeCheckerImpl
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable

val baseLineTypeChecker: NewKotlinTypeCheckerImpl =
  NewKotlinTypeCheckerImpl(KotlinTypeRefiner.Default)

fun KotlinType.typeArgumentsMap(other: KotlinType): Map<TypeProjection, TypeProjection> =
  if (isTypeParameter()) mapOf(this.asTypeProjection() to other.asTypeProjection())
  else
    arguments
      .mapIndexed { n, typeProjection ->
        other.arguments.getOrNull(n)?.let { typeProjection to it }
      }
      .filterNotNull()
      .toMap()

val KotlinType.unwrappedNotNullableType: UnwrappedType
  get() = makeNotNullable().unwrap()

/** Returns an intersection of this [KotlinType] with [other] */
fun KotlinType.intersection(vararg other: KotlinType): KotlinType {
  val constructor = IntersectionTypeConstructor(listOf(this) + other.toList())
  return KotlinTypeFactory.simpleTypeWithNonTrivialMemberScope(
    TypeAttributes.Empty,
    constructor,
    emptyList(),
    false,
    constructor.createScopeForKotlinType()
  )
}
