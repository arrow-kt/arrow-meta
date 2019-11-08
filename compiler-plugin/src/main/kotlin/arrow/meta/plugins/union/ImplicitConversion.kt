package arrow.meta.plugins.union

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker

fun Meta.implicitConversion(
  subtyping: KotlinType.(superType: KotlinType) -> Boolean,
  equality: KotlinType.(superType: KotlinType) -> Boolean = KotlinTypeChecker.DEFAULT::equalTypes,
  wrap: IrUtils.(IrExpression) -> IrExpression?,
  additionalPhase: ExtensionPhase = ExtensionPhase.Empty
): List<ExtensionPhase> {
  return meta(
    typeChecker { DelegateTypeChecker(it, subtyping, equality) },
    typeMismatchSuppression(subtyping),
    //irExpression { lift(it, subtyping, wrap)  }
    irVariable { lift(it, subtyping, wrap) },
    irProperty { lift(it, subtyping, wrap) },
    irReturn { lift(it, subtyping, wrap) }
  ) + additionalPhase
}


fun IrUtils.lift(
  it: IrTypeOperatorCall,
  subtyping: KotlinType.(superType: KotlinType) -> Boolean,
  wrap: IrUtils.(IrExpression) -> IrExpression?
): IrExpression? {
  val targetType = it.argument.type.originalKotlinType
  val valueType = it.type.originalKotlinType
  return if (targetType != null && valueType != null && valueType.subtyping(targetType)) { //insert conversion
    wrap(it.argument)
  } else it
}

private fun IrUtils.lift(
  it: IrVariable,
  subtyping: KotlinType.(superType: KotlinType) -> Boolean,
  wrap: IrUtils.(IrExpression) -> IrExpression?
): IrVariable? {
  val targetType = it.type.originalKotlinType
  val initializer = it.initializer
  val valueType = it.initializer?.type?.originalKotlinType
  return if (initializer != null && targetType != null && valueType != null && targetType.subtyping(valueType)) { //insert conversion
    it.apply {
      this.initializer = wrap(initializer)
    }
  } else it
}

private fun IrUtils.lift(
  it: IrProperty,
  subtyping: KotlinType.(superType: KotlinType) -> Boolean,
  wrap: IrUtils.(IrExpression) -> IrExpression?
): IrProperty? {
  val targetType = it.descriptor.returnType
  val valueType = it.descriptor.getter?.returnType
  return if (targetType != null && valueType != null && targetType.subtyping(valueType)) { //insert conversion
    it.backingField?.let { field ->
      val replacement = field.initializer?.expression?.let {
        wrap(it)
      }
      replacement?.let { field.initializer?.expression = it }
      it
    }
  } else it
}

private fun IrUtils.lift(it: IrReturn,
                         subtyping: KotlinType.(superType: KotlinType) -> Boolean,
                         wrap: IrUtils.(IrExpression) -> IrExpression?): IrExpression? {
  val targetType = it.returnTarget.returnType
  val valueType = it.value.type.originalKotlinType
  val result = wrap(it.value)
  return if (result != null && targetType != null && valueType != null && valueType.subtyping(targetType)) { //insert conversion
    IrReturnImpl(
      UNDEFINED_OFFSET,
      UNDEFINED_OFFSET,
      typeTranslator.translateType(targetType),
      it.returnTargetSymbol,
      result
    )
  } else it
}

private fun IrUtils.unlift(
  it: IrTypeOperatorCall,
  subtyping: KotlinType.(superType: KotlinType) -> Boolean,
  unwrap: IrUtils.(IrTypeOperatorCall) -> IrExpression?
): IrExpression? {
  val targetType = it.argument.type.originalKotlinType
  val valueType = it.type.originalKotlinType
  return if (targetType != null && valueType != null && targetType.subtyping(valueType)) { //insert conversion
    unwrap(it)
  } else it
}

private fun Meta.typeMismatchSuppression(suppressTypeMismatch: KotlinType.(KotlinType) -> Boolean): ExtensionPhase =
  suppressDiagnostic {
    if (it.factory == Errors.TYPE_MISMATCH)
      Errors.TYPE_MISMATCH.cast(it).run {
        a.suppressTypeMismatch(b)
      }
    else false
  }


private class DelegateTypeChecker(
  val typeChecker: KotlinTypeChecker,
  val subtyping: KotlinType.(superType: KotlinType) -> Boolean = typeChecker::isSubtypeOf,
  val equality: KotlinType.(superType: KotlinType) -> Boolean = typeChecker::equalTypes
) : KotlinTypeChecker by typeChecker {

  override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean {
    val underlyingResult = typeChecker.isSubtypeOf(p0, p1)
    return if (!underlyingResult) {
      val subType = p0.unwrap()
      val superType = p1.unwrap()
      val result: Boolean = subType.subtyping(superType)
      //println("DelegateTypeChecker.isSubtypeOf: $subType :$superType = $result")
      result
    } else underlyingResult
  }

  override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean =
    typeChecker.equalTypes(p0, p1).run {
      if (!this) equality(p0, p1) else this
    }

}


