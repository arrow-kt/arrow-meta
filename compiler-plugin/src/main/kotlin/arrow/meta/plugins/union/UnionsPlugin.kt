package arrow.meta.plugins.union

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi2ir.findSingleFunction
import org.jetbrains.kotlin.types.KotlinType

/**
 *
 * The Union Types Plugin allows users to define typed Unions of arbitrary arity eliminating
 * nesting and wrappers.
 *
 * https://en.wikipedia.org/wiki/Union_type
 *
 * ```kotlin:diff
 * - sealed class Error {
 * -   object NotANumber : Error()
 * - }
 * -
 * - fun parse(s: String): Either<Error, Int> =
 * -   if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
 * -   else Either.Left(Error.NotANumber)
 * + object NotANumber
 * + typealias `NotANumber | Int` = Union2<NotANumber, Int>
 * +
 * + fun parse(s: String): `NotANumber | Int` =
 * +  if (s.matches(Regex("-?[0-9]+"))) s.toInt()
 * +  else NotANumber
 * ```
 */
val Meta.unionTypes: Plugin
  get() =
    "Union Types" {
      meta(
        /**
         * ```kotlin
         * A? : Union2<A, B>
         * B? : Union2<A, B>
         *
         * fun proof(): Union2<String, Int> = 1
         * ```
         */
        typeChecker(::UnionTypeChecker),
        /**
         * ```kotlin
         * fun proof(): Union2<String, Int> = 1
         * val willBeNullButOk: String? = proof() //ok
         * val willBeInt: Int? = proof() //ok
         * ```
         */
        suppressDiagnostic(Diagnostic::suppressTypeMismatchOnNullableReceivers),
        /**
         * ```kotlin
         * fun proof(): Union2<String, Int> = 1
         * val willBeNullButOk: String? = proof() //ok
         * val willBeInt: Int? = proof() //ok
         * ```
         */
        irDump(),
        irTypeOperator(IrUtils::insertNullableConversion),
        /**
         * ```diff
         * - val proof: Union2<String, Int> = 1
         * + val proof: Union2<String, Int> = Union(1) //inlined class
         * ```
         */
        irVariable(IrUtils::insertUnionConversion),
        /**
         * ```diff
         * - val proof: Union2<String, Int> = 1
         * + val proof: Union2<String, Int> = Union(1) //inlined class
         * ```
         */
        irProperty(IrUtils::insertUnionConversion),
        /**
         * ```kotlin:diff
         * - fun proof(): Union2<String, Int> = 1
         * + fun proof(): Union2<String, Int> = Union(1) //inlined class
         * ```
         */
        irReturn(IrUtils::insertUnionConversion),
        irDump()
      )
    }

/**
 * Lifts a [IrReturn] of `a: A` into a [`Union<A, B>(a: A)`] value
 *
 * ```kotlin:diff
 * - fun proof(): Union2<String, Int> = 1
 * + fun proof(): Union2<String, Int> = Union(1) //inlined class
 * ```
 */
private fun IrUtils.insertUnionConversion(it: IrReturn): IrExpression? {
  val targetType = it.returnTarget.returnType
  val valueType = it.value.type.originalKotlinType
  return if (targetType != null && valueType != null && targetType.union(valueType)) { //insert conversion
    unionConversion(it, targetType)
  } else it
}

/**
 * Lifts a [IrVariable] of `a: A` into a [`Union<A, B>(a: A)`] value
 *
 * ```kotlin:diff
 * - val proof: Union2<String, Int> = 1
 * + val proof: Union2<String, Int> = Union(1) //inlined class
 * ```
 */
private fun IrUtils.insertUnionConversion(it: IrVariable): IrVariable? {
  val targetType = it.type.originalKotlinType
  val valueType = it.initializer?.type?.originalKotlinType
  return if (targetType != null && valueType != null && targetType.union(valueType)) { //insert conversion
    unionConversion(it)
  } else it
}

/**
 * Lifts a [IrVariable] of `a: A` into a [`Union<A, B>(a: A)`] value
 *
 * ```kotlin:diff
 * - val proof: Union2<String, Int> = 1
 * + val proof: Union2<String, Int> = Union(1) //inlined class
 * ```
 */
private fun IrUtils.insertUnionConversion(it: IrProperty): IrProperty? {
  val targetType = it.descriptor.returnType
  val valueType = it.descriptor.getter?.returnType
  return if (targetType != null && valueType != null && targetType.union(valueType)) { //insert conversion
    unionConversion(it)
  } else it
}



/**
 * Lifts a [IrTypeOperatorCall] implicit cast from a `Union<A, B>(a: A)` into a `a: A?` value
 *
 * ```kotlin
 * fun proof(): Union2<String, Int> = 1
 * val willBeNullButOk: String? = proof() //ok
 * val willBeInt: Int? = proof() //ok
 * ```
 */
private fun IrUtils.insertNullableConversion(it: IrTypeOperatorCall): IrExpression? {
  val targetType = it.argument.type.originalKotlinType
  val valueType = it.type.originalKotlinType
  return if (targetType != null && valueType != null && targetType.union(valueType)) { //insert conversion
    nullableConversion(it.type, it)
  } else it
}

/**
 * @see [insertNullableConversion]
 */
fun IrUtils.nullableConversion(valueType: IrType, intercepted: IrTypeOperatorCall): IrCall? =
  nullableConversionCall(valueType, intercepted.argument)

private fun IrUtils.nullableConversionCall(typeArgument: IrType?, argument: IrExpression): IrCall? =
  unionClassDescriptor()
    ?.companionObjectDescriptor?.let { companion ->
    companion.unsubstitutedMemberScope.findSingleFunction(Name.identifier("toNullable")).irCall().apply {
      dispatchReceiver = IrGetObjectValueImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = typeTranslator.translateType(companion.defaultType),
        symbol = referenceClass(companion)
      )
      putValueArgument(0, argument)
      putTypeArgument(0, typeArgument)
    }
  }


/**
 * Suppresses diagnostics if they refer to a type mismatch on a valid union
 *
 * ```kotlin
 * fun proof(): Union2<String, Int> = 1
 * val willBeNullButOk: String? = proof() //ok
 * val willBeInt: Int? = proof() //ok
 * ```
 */
fun Diagnostic.suppressTypeMismatchOnNullableReceivers(): Boolean =
  if (factory == Errors.TYPE_MISMATCH)
    Errors.TYPE_MISMATCH.cast(this).run {
      b.nullableUnionTargets(subType = a)
    }
  else false

/**
 * @see [insertUnionConversion]
 */
fun IrUtils.unionConversion(intercepted: IrProperty): IrProperty? =
  intercepted.backingField?.let { field ->
    val replacement = field.initializer?.expression?.let {
      unionClassDescriptor()?.irConstructorCall()?.apply {
        putValueArgument(0, it)
      }
    }
    replacement?.let { field.initializer?.expression = it }
    intercepted
  }

/**
 * @see [insertUnionConversion]
 */
fun IrUtils.unionConversion(intercepted: IrVariable): IrVariable? =
  intercepted.apply {
    initializer = unionClassDescriptor()?.irConstructorCall()?.apply {
      putValueArgument(0, initializer)
    }
  }

/**
 * The arrow.Union inline class value holder
 */
private fun IrUtils.unionClassDescriptor(): ClassDescriptor? =
  backendContext.ir.irModule.descriptor.findClassAcrossModuleDependencies(ClassId.fromString("Union"))

/**
 * @see [insertUnionConversion]
 */
fun IrUtils.unionConversion(intercepted: IrReturn, targetType: KotlinType?): IrReturn? =
  targetType?.let { unionType ->
    return unionClassDescriptor()?.irConstructorCall()?.let { constructorCall ->
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





