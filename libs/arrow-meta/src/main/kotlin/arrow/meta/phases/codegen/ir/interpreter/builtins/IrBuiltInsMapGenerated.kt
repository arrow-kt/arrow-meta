package arrow.meta.phases.codegen.ir.interpreter.builtins

import arrow.meta.phases.codegen.ir.interpreter.state.ExceptionState
import org.jetbrains.kotlin.ir.interpreter.builtins.CompileTimeFunction
import org.jetbrains.kotlin.ir.interpreter.builtins.binaryFunctions
import org.jetbrains.kotlin.ir.interpreter.builtins.ternaryFunctions
import org.jetbrains.kotlin.ir.interpreter.builtins.unaryFunctions
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.full.staticProperties

fun KCallable<*>.op(): Pair<CompileTimeFunction, (Array<out Any?>) -> Any?> =
  CompileTimeFunction(name, parameters.mapNotNull {
    when (val c = it.type.classifier) {
      is KClass<*> -> c.simpleName
      is KTypeParameter -> c.name
      else -> null
    }
  }) to ::invokeCallable

fun Method.op(): Pair<CompileTimeFunction, (Array<out Any?>) -> Any?> =
  CompileTimeFunction(name, parameters.mapNotNull {
    it.type.simpleName
  }) to ::invokeMethod

private fun KCallable<*>.invokeCallable(args: Array<out Any?>): Any? =
  call(*args)

private fun Method.invokeMethod(args: Array<out Any?>): Any? =
  if (Modifier.isStatic(getModifiers())) invoke(null, *args)
  else invoke(args)

inline fun registerAssociatedMembers(klass: KClass<*>): Map<CompileTimeFunction, (Array<out Any?>) -> Any?> =
  klass.members.associate {
    it.op()
  } + klass.staticFunctions.associate {
    it.op()
  } + klass.staticProperties.associate {
    it.op()
  }

inline fun registerAssociatedMembers(javaClass: Class<*>): Map<CompileTimeFunction, (Array<out Any?>) -> Any?> =
  javaClass.methods.associate {
    it.op()
  }

inline fun <reified A> ops(): Map<CompileTimeFunction, (Array<out Any?>) -> Any?> =
  registerAssociatedMembers(A::class)

fun registerAssociatedMembers(className: String, kotlinReflection: Boolean = true): Map<CompileTimeFunction, (Array<out Any?>) -> Any?> =
  try {
    if (kotlinReflection) registerAssociatedMembers(Class.forName(className).kotlin)
    else registerAssociatedMembers(Class.forName(className))
  } catch (e: ClassNotFoundException) {
    emptyMap()
  } catch (e: ExceptionInInitializerError) {
    // attempt to load with java reflection unsupported kotlin reflection classes
    registerAssociatedMembers(className, false)
  } catch (e: UnsupportedOperationException) {
    registerAssociatedMembers(className, false)
  }

fun misc(): Map<CompileTimeFunction, (Array<out Any?>) -> Any?> =
  registerAssociatedMembers("kotlin.text.StringsKt")

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
      ops<ExceptionState>() +
      compilerIrInterpreterFunctions +
      misc()
    ).toMutableMap()
