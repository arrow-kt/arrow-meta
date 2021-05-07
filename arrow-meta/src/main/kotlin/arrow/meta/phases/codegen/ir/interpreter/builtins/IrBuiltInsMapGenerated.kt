package arrow.meta.phases.codegen.ir.interpreter.builtins

import arrow.meta.phases.codegen.ir.interpreter.state.ExceptionState
import org.jetbrains.kotlin.ir.interpreter.builtins.CompileTimeFunction
import org.jetbrains.kotlin.ir.interpreter.builtins.binaryFunctions
import org.jetbrains.kotlin.ir.interpreter.builtins.binaryOperation
import org.jetbrains.kotlin.ir.interpreter.builtins.ternaryFunctions
import org.jetbrains.kotlin.ir.interpreter.builtins.unaryFunctions
import org.jetbrains.kotlin.ir.interpreter.builtins.unaryOperation
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3
import kotlin.reflect.KTypeParameter
import kotlin.reflect.javaType

fun KCallable<*>.op(): Pair<CompileTimeFunction, Function1<Array<out Any?>, *>> =
  CompileTimeFunction(name, parameters.mapNotNull {
    when (val c = it.type.classifier) {
      is KClass<*> -> c.simpleName
      is KTypeParameter -> c.name
      else -> null
    }
  }) to ::invokeCallable

private fun KCallable<*>.invokeCallable(args: Array<out Any?>): Any? =
  call(*args)

inline fun <reified A> ops(): Map<CompileTimeFunction, Function1<Array<out Any?>, *>> =
  A::class.members.associate { it.op() }

val compilerIrInterpreterFunctions: Map<CompileTimeFunction, (Array<out Any?>) -> Any?> =
  unaryFunctions.mapNotNull { (c, f) ->
    CompileTimeFunction(c.methodName, c.args) to { args: Array<out Any?> ->
      f(args[0])
    }
  }.toMap() +
    binaryFunctions.mapNotNull { (c, f) ->
      CompileTimeFunction(c.methodName, c.args) to { args: Array<out Any?> ->
        f(args[0], args[1])
      }
    }.toMap() +
    ternaryFunctions.mapNotNull { (c, f) ->
      CompileTimeFunction(c.methodName, c.args) to { args: Array<out Any?> ->
        f(args[0], args[1], args[3])
      }
    }.toMap()

val compileTimeFunctions: MutableMap<CompileTimeFunction, (Array<out Any?>) -> Any?> =
  (
    ops<Boolean>() +
    ops<Char>() +
    ops<Byte>() +
    ops<Float>() +
    ops<Double>() +
    ops<Short>() +
    ops<UInt>() +
    ops<Long>() +
    ops<ULong>() +
    ops<String>() +
    ops<CharSequence>() +
    ops<BooleanArray>() +
    ops<CharArray>() +
    ops<ByteArray>() +
    ops<ShortArray>() +
    ops<IntArray>() +
    ops<FloatArray>() +
    ops<DoubleArray>() +
    ops<LongArray>() +
    ops<Array<Any?>>() +
    ops<Any>() +
    ops<ExceptionState>()
      + compilerIrInterpreterFunctions
    ).toMutableMap()
