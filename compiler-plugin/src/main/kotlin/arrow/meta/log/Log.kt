package arrow.meta.log

import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult

sealed class Log {
  object Silent : Log()
  object Verbose : Log()
}

operator fun <A> Log.invoke(
  tag: A.() -> String,
  f: () -> A
): A =
  if (this is Log.Verbose) {
    val (time, result) = measureTimeMillisWithResult(f)
    println("${tag(result)} : [${time}ms]")
    result
  } else f()