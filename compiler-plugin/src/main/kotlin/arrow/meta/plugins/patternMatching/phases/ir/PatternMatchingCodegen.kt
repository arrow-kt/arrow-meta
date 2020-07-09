package arrow.meta.plugins.patternMatching.phases.ir

import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.plugins.patternMatching.phases.analysis.PatternResolutionContext
import arrow.meta.plugins.patternMatching.phases.analysis.PlaceholderPropertyDescriptor
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.buildStatement
import org.jetbrains.kotlin.ir.builders.irEquals
import org.jetbrains.kotlin.ir.builders.irFalse
import org.jetbrains.kotlin.ir.builders.irIfThenElse
import org.jetbrains.kotlin.ir.builders.typeOperator
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetField
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.ir.expressions.IrWhen
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.util.ConstantValueGenerator
import org.jetbrains.kotlin.ir.util.TypeTranslator
import org.jetbrains.kotlin.ir.util.getArgumentsWithIr
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.psi2ir.findFirstFunction

fun IrUtils.patchIrWhen(irWhen: IrWhen, irBuilder: DeclarationIrBuilder): IrExpression {
  val patternContext = PatternResolutionContext(compilerContext)

  irWhen.branches.forEach { branch ->
    val patternCall = findPatternCall(patternContext, branch.condition) ?: return@forEach
    val (subject, constructorCall) = patternCall

    // todo replace with plugin context type translator in 1.4-M3
    val typeTranslator = TypeTranslator(
      backendContext.ir.symbols.externalSymbolTable,
      backendContext.irBuiltIns.languageVersionSettings,
      backendContext.builtIns
    ).apply {
      constantValueGenerator = ConstantValueGenerator(
        backendContext.ir.irModule.descriptor,
        backendContext.ir.symbols.externalSymbolTable
      )
    }

    val targetClass = constructorCall.descriptor.constructedClass
    val targetType = constructorCall.type

    fun componentCall(index: Int, startOffset: Int, endOffset: Int): IrCall {
      val descriptor = targetClass.findFirstFunction("component${index + 1}") { it.valueParameters.isEmpty() }
      val irType = typeTranslator.translateType(descriptor.returnType!!)
      return IrCallImpl(
        startOffset,
        endOffset,
        irType,
        backendContext.ir.symbols.externalSymbolTable.referenceFunction(descriptor),
        descriptor
      ).also { call ->
        call.dispatchReceiver = subject
      }
    }

    branch.condition = irBuilder.buildStatement(branch.condition.startOffset, branch.condition.endOffset) {
      val booleanType = context.irBuiltIns.booleanType
      val typeCheck: IrExpression =
        typeOperator(
          booleanType,
          subject,
          IrTypeOperator.INSTANCEOF,
          targetType
        )

      constructorCall.getArgumentsWithIr().foldIndexed(typeCheck) argLoop@ { index, acc, (param, expression) ->
        if (expression is IrGetField && expression.descriptor is PlaceholderPropertyDescriptor) return@argLoop acc

        irIfThenElse(
          booleanType,
          acc,
          irEquals(componentCall(index, startOffset, endOffset), expression),
          irFalse()
        )
      }
    }

    val resultTransformer = object : IrElementTransformerVoid() {
      override fun visitGetField(expression: IrGetField): IrExpression {
        val descriptor = expression.descriptor
        if (descriptor !is PlaceholderPropertyDescriptor) {
          return super.visitGetField(expression)
        }

        return componentCall(descriptor.parameterIndex, expression.startOffset, expression.endOffset)
      }
    }

    branch.result = branch.result.transform(resultTransformer, null)
  }

  return irWhen
}

// todo: replace with IrVisitor
fun IrUtils.findPatternCall(patternContext: PatternResolutionContext, expr: IrExpression): Pair<IrExpression, IrConstructorCall>? {
  // fixme: based on the template from tests, must be more complex
  if (expr !is IrCall) return null
  if (expr.symbol != backendContext.irBuiltIns.eqeqSymbol) return null

  val subject = expr.getValueArgument(0) ?: return null
  val caseCall = expr.getValueArgument(1)
  if (caseCall !is IrCall) return null
  if (caseCall.descriptor != patternContext.caseDescriptor) return null

  val constructorCall = caseCall.getValueArgument(0)
  if (constructorCall !is IrConstructorCall) return null

  return Pair(subject, constructorCall)
}
