package arrow.meta.plugins.liquid.phases.ir

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.codegen.ir.IRGeneration
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.backend.jvm.codegen.psiElement
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.com.intellij.openapi.util.text.LineColumn
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.ir.declarations.IrMutableAnnotationContainer
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrErrorExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.constructedClass
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.hasEqualFqName
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.psiUtil.startOffset

internal val refinedAnnotationFqName = FqName("arrow.Refinement")

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
  val result = irInterpreter.interpret(it)
  compilerContext.handleInterpreterResult(result, it)
}

private fun IrCall.isCallToRefinedFunction(): Boolean =
  symbol.owner.kotlinFqName == FqName("arrow.Predicate.invoke")

private fun CompilerContext.handleInterpreterResult(
  result: IrExpression,
  it: IrCall
) {
  when (result) {
    is IrErrorExpression -> {
      val errorMessage = "${result.description} evaluating: ${it.dumpKotlinLike()}"
      val psiElement = it.psiElement
      when {
        psiElement != null -> {
          val lineColumn = StringUtil.offsetToLineColumn(psiElement.text, psiElement.startOffset)
          messageCollector?.report(
            CompilerMessageSeverity.ERROR, errorMessage, //psiElement.compilerMessageLocation(lineColumn)
          )
        }
        else -> messageCollector?.report(
          CompilerMessageSeverity.ERROR,
          result.description + " evaluating: ${it.dumpKotlinLike()}"
        )
      }
    }
  }
}

private fun PsiElement.compilerMessageLocation(lineColumn: LineColumn): CompilerMessageLocation? =
  CompilerMessageLocation.create(containingFile.virtualFile.path, lineColumn.line, lineColumn.column, text)

private fun IrMutableAnnotationContainer.refinedAnnotation(): IrConstructorCall? =
  annotations.find { it.symbol.owner.constructedClass.hasEqualFqName(refinedAnnotationFqName) }
