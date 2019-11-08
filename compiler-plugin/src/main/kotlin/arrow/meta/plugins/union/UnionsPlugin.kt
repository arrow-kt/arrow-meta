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
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi2ir.findSingleFunction
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.typeUtil.makeNullable

val Meta.unionTypes2: Plugin
  get() =
    "Union Types" {
      implicitConversion(
        subtyping = KotlinType::union,
        equality = KotlinTypeChecker.DEFAULT::equalTypes,
        wrap = IrUtils::toUnion
      ) +
        implicitConversion(
          subtyping = KotlinType::nullableUnionTargets,
          equality = KotlinTypeChecker.DEFAULT::equalTypes,
          wrap = IrUtils::unionToNullable,
          additionalPhase = irTypeOperator { lift(it, { superType -> makeNullable().nullableUnionTargets(superType) }, IrUtils::unionToNullable) }
        )
    }

private fun IrUtils.toUnion(expression: IrExpression): IrExpression? =
  unionClassDescriptor()?.irConstructorCall()?.let {
    it.putValueArgument(0, expression)
    it
  }


private fun IrUtils.unionToNullable(expression: IrExpression): IrExpression? {
  val value = expression.type.makeNullable()
  println("Wrapping value : ${value.originalKotlinType}")
  return unliftCall(value, expression)
}


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
        suppressDiagnostic(Diagnostic::suppressTypeMismatch),
        /**
         * ```kotlin
         * fun proof(): Union2<String, Int> = 1
         * val willBeNullButOk: String? = proof() //ok
         * val willBeInt: Int? = proof() //ok
         * ```
         */
        irDump(),
        irTypeOperator(IrUtils::unlift),
        /**
         * ```diff
         * - val proof: Union2<String, Int> = 1
         * + val proof: Union2<String, Int> = Union(1) //inlined class
         * ```
         */
        irVariable(IrUtils::lift),
        /**
         * ```diff
         * - val proof: Union2<String, Int> = 1
         * + val proof: Union2<String, Int> = Union(1) //inlined class
         * ```
         */
        irProperty(IrUtils::lift),
        /**
         * ```kotlin:diff
         * - fun proof(): Union2<String, Int> = 1
         * + fun proof(): Union2<String, Int> = Union(1) //inlined class
         * ```
         */
        irReturn(IrUtils::lift),
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
private fun IrUtils.lift(it: IrReturn): IrExpression? {
  val targetType = it.returnTarget.returnType
  val valueType = it.value.type.originalKotlinType
  return if (targetType != null && valueType != null && targetType.union(valueType)) { //insert conversion
    liftConversion(it, targetType)
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
private fun IrUtils.lift(it: IrVariable): IrVariable? {
  val targetType = it.type.originalKotlinType
  val valueType = it.initializer?.type?.originalKotlinType
  return if (targetType != null && valueType != null && targetType.union(valueType)) { //insert conversion
    liftConversion(it)
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
private fun IrUtils.lift(it: IrProperty): IrProperty? {
  val targetType = it.descriptor.returnType
  val valueType = it.descriptor.getter?.returnType
  return if (targetType != null && valueType != null && targetType.union(valueType)) { //insert conversion
    liftConversion(it)
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
private fun IrUtils.unlift(it: IrTypeOperatorCall): IrExpression? {
  val targetType = it.argument.type.originalKotlinType
  val valueType = it.type.originalKotlinType
  return if (targetType != null && valueType != null && targetType.union(valueType)) { //insert conversion
    unlift(it.type, it)
  } else it
}

/**
 * @see [unlift]
 */
fun IrUtils.unlift(valueType: IrType, intercepted: IrTypeOperatorCall): IrCall? =
  unliftCall(valueType, intercepted.argument)

private fun IrUtils.unliftCall(typeArgument: IrType?, argument: IrExpression): IrCall? =
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
fun Diagnostic.suppressTypeMismatch(): Boolean =
  if (factory == Errors.TYPE_MISMATCH)
    Errors.TYPE_MISMATCH.cast(this).run {
      a.nullableUnionTargets(superType = b)
    }
  else false

/**
 * @see [lift]
 */
fun IrUtils.liftConversion(intercepted: IrProperty): IrProperty? =
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
 * @see [lift]
 */
fun IrUtils.liftConversion(intercepted: IrVariable): IrVariable? =
  intercepted.apply {
    initializer = unionClassDescriptor()?.irConstructorCall()?.apply {
      putValueArgument(0, initializer)
    }
  }


/**
 * @see [lift]
 */
fun IrUtils.liftConversion(intercepted: IrReturn, targetType: KotlinType?): IrReturn? =
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

/**
 * The arrow.Union inline class value holder
 */
internal fun IrUtils.unionClassDescriptor(): ClassDescriptor? =
  backendContext.ir.irModule.descriptor.findClassAcrossModuleDependencies(ClassId.fromString("Union"))



