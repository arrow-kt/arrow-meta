package arrow.meta.ir.syntax

import arrow.meta.ir.plugin.IrSyntaxPlugin
import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.ir.IrElement

internal fun <A : IrElement> visits(element: Class<A>): String =
  "${element.name} is visited"

internal fun <A : IrElement> irVisit(element: Class<A>): IrUtils.(A) -> A? = { _ ->
  this.compilerContext.messageCollector?.report(CompilerMessageSeverity.ERROR, visits(element))
  null
}

fun <A : IrElement> testIrVisit(of: Class<A>, src: String = ""): Unit =
  assertThis(
    CompilerTest(
      config = { metaDependencies + addMetaPlugins(IrSyntaxPlugin()) },
      code = {
        """
        package test
        import arrow.*
        import arrowx.*
        
        $src
        
        val zero = 0
        
        sealed class ABC<A> {
          data class A(val a: Int) : ABC<Int>()
          data class B(val b: String = "" + "Hello") : ABC<String>()
          data class C(val c: Long = 44L.and(3L)) : ABC<Long>()
          object D : ABC<Nothing>()
        }

        enum class Position {
          FIRST, SECOND, THIRD
        }

        @Suppress("UNCHECKED_CAST")
        class Box<A>(val a: Int, val b : A, val box: ABC<A> = ABC.D as ABC<A>)

        typealias A<B> = Box<B>

        var h: Int
          get() = 0
          set(value) {
            value * 3
          }

        suspend fun hello(): Unit =
          println("Hello")
          
        fun <A> provider(evidence: @Given A = arrow.given): A =
            evidence

        suspend fun foo(vararg a: Int) {
          hello()
          val f = { a: Int -> 9 - a }
          val boo = try {
            var j = 0
            while (j < 1000) {
              fun s(): Int = 8
              if (j < 9) {
                ::s
                break
              }
              continue
            }
            val d = if (3 > f(2)) 0 else 33
          } catch (e: Exception) {
            println()
          } finally {
            9 - 6
          }
        }
      """.trimIndent().source
      },
      assert = {
        failsWith {
          it.contains(visits(of))
        }
      }
    )
  )