package arrow.meta.plugins.liquid.phases.ir

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.codegen.ir.IRGeneration
import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.phases.codegen.ir.interpreter.builtins.compileTimeFunctions
import arrow.meta.phases.codegen.ir.interpreter.builtins.ops
import arrow.refinement.Constraints
import arrow.refinement.Refined
import org.jetbrains.kotlin.backend.jvm.codegen.psiElement
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageUtil
import org.jetbrains.kotlin.ir.backend.js.utils.realOverrideTarget
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrErrorExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.fqNameForIrSerialization
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal fun Meta.registerIrInterpreterCompileTimeFunctions(): IRGeneration =
  irModuleFragment {
    compileTimeFunctions +=
      ops<Refined<Any?, Any?>>() +
        ops<Constraints>()
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
  val valueArg = it.getValueArgument(0)
  val value = valueArg as? IrConst<*>
  when {
    valueArg != null && value !is IrConst<*> && valueArg is IrMemberAccessExpression<*> -> {
      val psi = valueArg.psiElement
      if (psi != null) {
        compilerContext.messageCollector?.report(
          severity = CompilerMessageSeverity.ERROR,
          message = "${psi.text} can't be verified at compile time. Use `Predicate.orNull(${psi.text})` for safe access or `Predicate.require(${psi.text})` for explicit unsafe instantiation",
          location =
          MessageUtil.psiElementToMessageLocation(psi)
        )
      } else {
        compilerContext.messageCollector?.report(
          severity = CompilerMessageSeverity.ERROR,
          message = "${valueArg.dumpKotlinLike()} can't be verified at compile time. Use `Predicate.orNull(${valueArg.dumpKotlinLike()})` for safe access or `Predicate.require(${valueArg.dumpKotlinLike()})` for explicit unsafe instantiation",
        )
      }
    }
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
