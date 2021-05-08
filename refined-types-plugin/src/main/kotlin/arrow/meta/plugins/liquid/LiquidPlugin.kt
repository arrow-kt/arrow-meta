package arrow.meta.plugins.liquid

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.plugins.liquid.phases.ir.registerIrInterpreterCompileTimeFunctions
import arrow.meta.plugins.liquid.phases.ir.validateIrCallsToRefinedFunctions
import arrow.meta.plugins.liquid.phases.quotes.generateRefinedApi

val Meta.liquidExpressions: CliPlugin
  get() =
    "Compile time validation of refined types for Kotlin" {
      meta(
        generateRefinedApi(this@liquidExpressions),
        registerIrInterpreterCompileTimeFunctions(),
        validateIrCallsToRefinedFunctions()
      )
    }

