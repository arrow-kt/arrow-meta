/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package arrow.meta.phases.codegen.ir.interpreter

import arrow.meta.phases.codegen.ir.interpreter.builtins.evaluateIntrinsicAnnotation
import arrow.meta.phases.codegen.ir.interpreter.stack.Variable
import arrow.meta.phases.codegen.ir.interpreter.state.Common
import arrow.meta.phases.codegen.ir.interpreter.state.Complex
import arrow.meta.phases.codegen.ir.interpreter.state.ExceptionState
import arrow.meta.phases.codegen.ir.interpreter.state.Primitive
import arrow.meta.phases.codegen.ir.interpreter.state.State
import arrow.meta.phases.codegen.ir.interpreter.state.Wrapper
import javassist.ClassPool
import javassist.NotFoundException
import javassist.bytecode.Descriptor
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.interpreter.toIrConst
import org.jetbrains.kotlin.ir.symbols.IrFieldSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueParameterSymbol
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.lang.reflect.Method
import org.jetbrains.kotlin.ir.declarations.IrAnnotationContainer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrTypeParametersContainer
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.interpreter.getAnnotation
import org.jetbrains.kotlin.ir.interpreter.hasAnnotation
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.types.isBooleanArray
import org.jetbrains.kotlin.ir.types.isByteArray
import org.jetbrains.kotlin.ir.types.isCharArray
import org.jetbrains.kotlin.ir.types.isDoubleArray
import org.jetbrains.kotlin.ir.types.isFloatArray
import org.jetbrains.kotlin.ir.types.isIntArray
import org.jetbrains.kotlin.ir.types.isLongArray
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.types.isShortArray
import org.jetbrains.kotlin.ir.types.isString
import org.jetbrains.kotlin.ir.types.isUnsignedType
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.parentClassOrNull

internal fun IrFunction.getDispatchReceiver(): IrValueParameterSymbol? = this.dispatchReceiverParameter?.symbol

internal fun IrFunction.getExtensionReceiver(): IrValueParameterSymbol? = this.extensionReceiverParameter?.symbol

internal fun IrFunction.getReceiver(): IrSymbol? = this.getDispatchReceiver() ?: this.getExtensionReceiver()

internal fun IrFunctionAccessExpression.getBody(): IrBody? =
  this.symbol.owner.body

internal fun State.toIrExpression(expression: IrExpression): IrExpression {
  val start = expression.startOffset
  val end = expression.endOffset
  val type = expression.type.makeNotNull()
  return when (this) {
    is Primitive<*> ->
      when {
        this.value == null -> this.value.toIrConst(type, start, end)
        type.isPrimitiveType() || type.isString() -> this.value.toIrConst(type, start, end)
        else -> expression // TODO support for arrays
      }
    is Complex -> {
      val stateType = this.irClass.defaultType
      when {
        stateType.isUnsignedType() -> (this.fields.single().state as Primitive<*>).value.toIrConst(type, start, end)
        else -> expression
      }
    }
    else -> expression // TODO support
  }
}

internal fun Any?.toState(irType: IrType): State {
  return when (this) {
    is State -> this
    is Boolean, is Char, is Byte, is Short, is Int, is Long, is String, is Float, is Double, is Array<*>, is ByteArray,
    is CharArray, is ShortArray, is IntArray, is LongArray, is FloatArray, is DoubleArray, is BooleanArray -> Primitive(
      this,
      irType
    )
    null -> Primitive(this, irType)
    else -> Wrapper(this, irType.classOrNull!!.owner)
  }
}

internal fun <T> IrConst<T>.toPrimitive(): Primitive<T> {
  return Primitive(this.value, this.type)
}

internal fun IrAnnotationContainer.getEvaluateIntrinsicValue(): String? {
  if (this is IrClass && this.fqNameWhenAvailable?.startsWith(Name.identifier("java")) == true) return this.fqNameWhenAvailable?.asString()
  if (!this.hasAnnotation(evaluateIntrinsicAnnotation)) return null
  return (this.getAnnotation(evaluateIntrinsicAnnotation)?.getValueArgument(0) as IrConst<*>).value.toString()
}

internal fun getPrimitiveClass(irType: IrType, asObject: Boolean = false): Class<*>? =
  when (irType.getPrimitiveType()) {
    PrimitiveType.BOOLEAN -> if (asObject) Boolean::class.javaObjectType else Boolean::class.java
    PrimitiveType.CHAR -> if (asObject) Char::class.javaObjectType else Char::class.java
    PrimitiveType.BYTE -> if (asObject) Byte::class.javaObjectType else Byte::class.java
    PrimitiveType.SHORT -> if (asObject) Short::class.javaObjectType else Short::class.java
    PrimitiveType.INT -> if (asObject) Int::class.javaObjectType else Int::class.java
    PrimitiveType.FLOAT -> if (asObject) Float::class.javaObjectType else Float::class.java
    PrimitiveType.LONG -> if (asObject) Long::class.javaObjectType else Long::class.java
    PrimitiveType.DOUBLE -> if (asObject) Double::class.javaObjectType else Double::class.java
    else -> when {
      irType.isString() -> String::class.java
      else -> null
    }
  }

internal fun IrFunction.getArgsForMethodInvocation(args: List<Variable>): List<Any?> {
  val argsValues = args.map {
    when (val state = it.state) {
      is ExceptionState -> state.getThisAsCauseForException()
      is Wrapper -> state.value
      is Primitive<*> -> state.value
      else -> throw AssertionError("${state::class} is unsupported as argument for wrapper method invocation")
    }
  }.toMutableList()

  // TODO if vararg isn't last parameter
  // must convert vararg array into separated elements for correct invoke
  if (this.valueParameters.lastOrNull()?.varargElementType != null) {
    val varargValue = argsValues.last()
    argsValues.removeAt(argsValues.size - 1)
    argsValues.addAll(varargValue as Array<out Any?>)
  }

  return argsValues
}

internal fun List<Any?>.toPrimitiveStateArray(type: IrType): Primitive<*> {
  return when {
    type.isByteArray() -> Primitive(ByteArray(size) { i -> (this[i] as Number).toByte() }, type)
    type.isCharArray() -> Primitive(CharArray(size) { i -> this[i] as Char }, type)
    type.isShortArray() -> Primitive(ShortArray(size) { i -> (this[i] as Number).toShort() }, type)
    type.isIntArray() -> Primitive(IntArray(size) { i -> (this[i] as Number).toInt() }, type)
    type.isLongArray() -> Primitive(LongArray(size) { i -> (this[i] as Number).toLong() }, type)
    type.isFloatArray() -> Primitive(FloatArray(size) { i -> (this[i] as Number).toFloat() }, type)
    type.isDoubleArray() -> Primitive(DoubleArray(size) { i -> (this[i] as Number).toDouble() }, type)
    type.isBooleanArray() -> Primitive(BooleanArray(size) { i -> this[i].toString().toBoolean() }, type)
    else -> Primitive<Array<*>>(this.toTypedArray(), type)
  }
}

internal fun getTypeArguments(
  container: IrTypeParametersContainer,
  expression: IrFunctionAccessExpression,
  mapper: (IrTypeParameterSymbol) -> State
): List<Variable> {
  fun IrType.getState(): State {
    return this.classOrNull?.owner?.let { Common(it) } ?: mapper(this.classifierOrFail as IrTypeParameterSymbol)
  }

  val typeArguments = container.typeParameters.mapIndexed { index, typeParameter ->
    val typeArgument = expression.getTypeArgument(index)!!
    Variable(typeParameter.symbol, typeArgument.getState())
  }.toMutableList()

  if (container is IrSimpleFunction) {
    container.returnType.classifierOrFail.owner.safeAs<IrTypeParameter>()
      ?.let { typeArguments.add(Variable(it.symbol, expression.type.getState())) }
  }

  return typeArguments
}

internal fun State?.extractNonLocalDeclarations(): List<Variable> {
  this ?: return listOf()
  val state = this.takeIf { it !is Complex } ?: (this as Complex).getOriginal()
  return state.fields.filter { it.symbol !is IrFieldSymbol }
}

internal fun State?.getCorrectReceiverByFunction(irFunction: IrFunction): State? {
  if (this !is Complex) return this

  val original: Complex = getOriginal()
  val other = irFunction.parentClassOrNull?.thisReceiver ?: return this
  return generateSequence(original) { it.superClass }.firstOrNull { it.irClass.thisReceiver == other } ?: this
}

internal fun IrFunction.getCapitalizedFileName() = this.file.name.replace(".kt", "Kt").capitalizeAsciiOnly()

internal fun IrType.isPrimitiveArray(): Boolean {
  return this.getClass()?.fqNameWhenAvailable?.toUnsafe()?.let { StandardNames.isPrimitiveArray(it) } ?: false
}

internal fun IrType.isFunction() =
  this.getClass()?.fqNameWhenAvailable?.asString()?.startsWith("kotlin.Function") ?: false

internal fun IrType.isTypeParameter() = classifierOrNull is IrTypeParameterSymbol

internal fun IrType.isInterface() = classOrNull?.owner?.kind == ClassKind.INTERFACE

internal fun IrType.isThrowable() = this.getClass()?.fqNameWhenAvailable?.asString() == "kotlin.Throwable"

internal fun Method.getSignature(): String? =
  try {
    name + Descriptor.ofMethod(
      ClassPool.getDefault().get(returnType.canonicalName),
      parameterTypes.map { ClassPool.getDefault().get(it.canonicalName) }.toTypedArray()
    )
  } catch (e: NotFoundException) {
    null
  }
