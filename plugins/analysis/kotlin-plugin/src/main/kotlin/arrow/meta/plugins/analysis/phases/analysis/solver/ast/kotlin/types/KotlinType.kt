package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types

import arrow.meta.phases.resolve.unwrappedNotNullableType
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.isAnyOrNullableAny
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.jetbrains.kotlin.types.typeUtil.isByte
import org.jetbrains.kotlin.types.typeUtil.isChar
import org.jetbrains.kotlin.types.typeUtil.isDouble
import org.jetbrains.kotlin.types.typeUtil.isFloat
import org.jetbrains.kotlin.types.typeUtil.isInt
import org.jetbrains.kotlin.types.typeUtil.isLong
import org.jetbrains.kotlin.types.typeUtil.isShort
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.types.typeUtil.isUnsignedNumberType

internal class KotlinType(val impl: org.jetbrains.kotlin.types.KotlinType) : Type {
  override val descriptor: ClassDescriptor?
    get() = TypeUtils.getClassDescriptor(impl)?.model()
  override val unwrappedNotNullableType: Type
    get() = KotlinType(impl.unwrappedNotNullableType)
  override val isMarkedNullable: Boolean
    get() = impl.isMarkedNullable

  override fun isBoolean(): Boolean =
    impl.isBoolean()

  override fun isNullable(): Boolean =
    impl.isNullable()

  override fun isSubtypeOf(other: Type): Boolean =
    other is KotlinType && impl.isSubtypeOf(other.impl)

  override fun isInt(): Boolean =
    impl.isInt()

  override fun isLong(): Boolean =
    impl.isLong()

  override fun isFloat(): Boolean =
    impl.isFloat()

  override fun isDouble(): Boolean =
    impl.isDouble()

  override fun isTypeParameter(): Boolean =
    impl.isTypeParameter()

  override fun isAnyOrNullableAny(): Boolean =
    impl.isAnyOrNullableAny()

  override fun isByte(): Boolean =
    impl.isByte()

  override fun isShort(): Boolean =
    impl.isShort()

  override fun isUnsignedNumberType(): Boolean =
    impl.isUnsignedNumberType()

  override fun isChar(): Boolean =
    impl.isChar()
}
