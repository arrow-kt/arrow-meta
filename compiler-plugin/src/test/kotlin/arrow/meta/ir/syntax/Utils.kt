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

fun <A : IrElement> testIrVisit(of: Class<A>, src: String = "val zero = 0"): Unit =
  assertThis(
    CompilerTest(
      config = { metaDependencies + addMetaPlugins(IrSyntaxPlugin()) },
      code = {
        """
        package test
        
        $src
      """.trimIndent().source
      },
      assert = {
        failsWith {
          it.contains(visits(of))
        }
      }
    )
  )