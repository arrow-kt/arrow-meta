package arrow.meta.plugins.union

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable

val Meta.unionTypes: Plugin
  get() =
    "Union Types" {
      meta(
        typeChecker(::UnionTypeChecker),
        suppressDiagnostic(Diagnostic::typeMismatchOnNullableReceivers),
        irVariable(IrUtils::applyUnion),
        irReturn(IrUtils::applyUnion)
      )
    }

private fun IrUtils.applyUnion(it: IrReturn): IrExpression? {
  val targetType = it.returnTarget.returnType
  val valueType = it.value.type.originalKotlinType
  return if (targetType != null && valueType != null && targetType.union(valueType)) { //insert conversion
    applyUnionLift(it, targetType)
  } else it
}

private fun IrUtils.applyUnion(it: IrVariable): IrStatement? {
  val targetType = it.type.originalKotlinType
  val valueType = it.initializer?.type?.originalKotlinType
  return if (targetType != null && valueType != null && targetType.union(valueType)) { //insert conversion
    applyUnionLift(it)
  } else it
}

private fun Diagnostic.typeMismatchOnNullableReceivers(): Boolean =
  if (factory == Errors.TYPE_MISMATCH)
    Errors.TYPE_MISMATCH.cast(this).run {
      b.nullableUnionTargets(subType = a)
    }
  else false

fun IrUtils.applyUnionLift(intercepted: IrVariable): IrVariable? =
  intercepted.apply {
    initializer = unionClassDescriptor()?.irConstructorCall()?.apply {
      putValueArgument(0, initializer)
    }
  }

private fun IrUtils.unionClassDescriptor() =
  backendContext.ir.irModule.descriptor.findClassAcrossModuleDependencies(ClassId.fromString("Union"))

fun IrUtils.applyUnionLift(intercepted: IrReturn, targetType: KotlinType?): IrReturn? =
  targetType?.let { unionType ->
    val unionImpl = backendContext.ir.irModule.descriptor.findClassAcrossModuleDependencies(ClassId.fromString("Union"))
    return unionImpl?.irConstructorCall()?.let { constructorCall ->
      constructorCall.putValueArgument(0, intercepted.value)
      IrReturnImpl(
        UNDEFINED_OFFSET,
        UNDEFINED_OFFSET,
        typeTranslator.translateType(unionType),
        intercepted.returnTargetSymbol,
        constructorCall
      )
    }
  }

private fun KotlinType.nullableUnionTargets(subType: KotlinType): Boolean =
  subType.isMarkedNullable && isUnion() && arguments.contains(subType.makeNotNullable().asTypeProjection())

private fun KotlinType.isUnion(): Boolean {
  println("type: " + constructor.declarationDescriptor?.name?.asString())
  return constructor.declarationDescriptor?.name?.asString()?.startsWith("Union") == true
}

private fun KotlinType.union(subType: KotlinType) =
  isUnion() && arguments.contains(subType.asTypeProjection()) || nullableUnionTargets(subType)

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

  override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean {
    //println("KindAwareTypeChecker.equalTypes: $p0 <-> $p1")
    val result = typeChecker.equalTypes(p0, p1)
    println("UnionTypeChecker.equalTypes: $p0 <-> $p1 = $result")
    return result
  }

}



