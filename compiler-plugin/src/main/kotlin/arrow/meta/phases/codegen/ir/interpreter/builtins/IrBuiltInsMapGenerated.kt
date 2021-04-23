package arrow.meta.phases.codegen.ir.interpreter.builtins

import arrow.meta.phases.codegen.ir.interpreter.state.ExceptionState
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3

inline fun <reified A> KFunction1<A, *>.unaryOp(): Pair<CompileTimeFunction, Function<*>> =
  CompileTimeFunction(name, listOfNotNull(A::class.simpleName)) to this

inline fun <reified A, reified B> KFunction2<A, B, *>.binaryOp(): Pair<CompileTimeFunction, Function<*>> =
  CompileTimeFunction(name, listOfNotNull(A::class.simpleName, B::class.simpleName)) to this

inline fun <reified A, reified B, reified C> KFunction3<A, B, C, *>.ternaryOp(): Pair<CompileTimeFunction, Function<*>> =
  CompileTimeFunction(name, listOfNotNull(A::class.simpleName, B::class.simpleName)) to this

inline fun KCallable<*>.op(): Pair<CompileTimeFunction, Function1<Array<out Any?>, *>> =
  CompileTimeFunction(name, parameters.map { it.type.toString() }) to { args: Array<out Any?> -> call(args) }

inline fun <reified A> ops(): Map<CompileTimeFunction, Function1<Array<out Any?>, *>> =
  A::class.members.associate { it.op() }

val compileTimeFunctions =
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
