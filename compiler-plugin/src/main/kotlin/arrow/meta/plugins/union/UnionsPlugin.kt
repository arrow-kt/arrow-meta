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
 * -   object NoZeroReciprocal : Error()
 * - }
 * -
 * - fun parse(s: String): Either<Error, Int> =
 * -   if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
 * -   else Either.Left(Error.NotANumber)
 * + object NotANumber
 * + object NoZeroReciprocal
 * + typealias Error = Union2<NotANumber, NoZeroReciprocal>
 * +
 * + fun parse(s: String): Union2<Error, Int> =
 * +  if (s.matches(Regex("-?[0-9]+"))) s.toInt()
 * +  else NotANumber
 * ```
 */
val Meta.unionTypes: Plugin
  get() =
    "Union Types" {
      meta(
        /**
         * A? : Union2<A, B>
         * B? : Union2<A, B>
         *
         * fun proof(): Union2<String, Int> = 1
         */
        typeChecker(::UnionTypeChecker),
        /**
         * fun proof(): Union2<String, Int> = 1
         * val willBeNullButOk: String? = proof() //ok
         * val willBeInt: Int? = proof() //ok
         */
        suppressDiagnostic(Diagnostic::typeMismatchOnNullableReceivers),
        /**
         * ```kotlin:diff
         * - val proof: Union2<String, Int> = 1
         * + val proof: Union2<String, Int> = Union(1) //inlined class zero cost
         * ```
         */
        irVariable(IrUtils::applyUnion),
        /**
         * ```kotlin:diff
         * - fun proof(): Union2<String, Int> = 1
         * + fun proof(): Union2<String, Int> = Union(1) //inlined class zero cost
         * ```
         */
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





