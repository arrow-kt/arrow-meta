package arrow.meta.log

import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult

sealed class Log {
  object Silent : Log()
  object Verbose : Log()
}

operator fun <A> Log.invoke(
  tag: A.() -> String,
  f: () -> A
): A {
  val (time, result) = measureTimeMillisWithResult(f)
  if (this is Log.Verbose) {
    println("${tag(result)} : [${time}ms]: $result")
  }
  return result
}