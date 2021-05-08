package arrow.meta.plugins.liquid.phases.ir

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.codegen.ir.IRGeneration
import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.phases.codegen.ir.interpreter.builtins.compileTimeFunctions
import arrow.meta.phases.codegen.ir.interpreter.builtins.ops
import arrow.refinement.Constrains
import arrow.refinement.Refined
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.ir.backend.js.utils.realOverrideTarget
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrErrorExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.fqNameForIrSerialization
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal fun Meta.registerIrInterpreterCompileTimeFunctions(): IRGeneration =
  irModuleFragment {
    compileTimeFunctions +=
      ops<Refined<Any?, Any?>>() +
        ops<Constrains>()
    null
  }

internal fun Meta.validateIrCallsToRefinedFunctions(): IRGeneration =
  irCall {
    if (it.isCallToRefinedFunction()) {
      val subject = it.getValueArgument(0)
      evalRefinement(it)
    }
    null
  }

private fun IrUtils.evalRefinement(
  it: IrCall
): Unit {
  println("Evaluating refined call: ${it.dumpKotlinLike()}")
  val valueArg = it.getValueArgument(0)
  val value = valueArg as? IrConst<*>
  when {
    valueArg != null && value !is IrConst<*> -> throw IllegalArgumentException(
      """
      ${valueArg.dumpKotlinLike()} can't be verify at compile time. 
      Use `Predicate.orNull(${valueArg.dumpKotlinLike()})` for safe access or 
      `Predicate.require(${valueArg.dumpKotlinLike()})` for explicit unsafe instantiation
      """
    )
    value is IrConst<*> -> {
      val result = interpretFunction(it, Name.identifier(it.type.dumpKotlinLike()), value)
      compilerContext.handleInterpreterResult(result, it)
    }
  }
}

private fun IrCall.isCallToRefinedFunction(): Boolean =
  symbol.owner.realOverrideTarget.kotlinFqName == FqName("arrow.refinement.Refined.invoke")

private fun CompilerContext.handleInterpreterResult(
  result: IrExpression,
  it: IrCall
) {
  when (result) {
    is IrErrorExpression -> {
      val errorMessage = "${result.description} : ${it.symbol.owner.fqNameForIrSerialization}".trim()
      messageCollector?.report(
        CompilerMessageSeverity.ERROR,
        errorMessage
      )
    }
  }
}
