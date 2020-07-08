package arrow.meta.plugins.patternMatching.phases.ir

import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.plugins.patternMatching.phases.analysis.PatternResolutionContext
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.ir.expressions.IrWhen
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrTypeOperatorCallImpl
import org.jetbrains.kotlin.ir.util.ConstantValueGenerator
import org.jetbrains.kotlin.ir.util.TypeTranslator
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.psi2ir.findFirstFunction

fun IrUtils.patchIrWhen(irWhen: IrWhen): IrExpression {
  val patternContext = PatternResolutionContext(compilerContext)

  irWhen.branches.forEach { branch ->
    val patternCall = findPatternCall(patternContext, branch.condition) ?: return@forEach
    val (subject, constructor) = patternCall

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

    val targetClass = constructor.constructedClass
    val targetType = typeTranslator.translateType(constructor.returnType)

    branch.condition = IrTypeOperatorCallImpl(
      branch.condition.startOffset,
      branch.condition.endOffset,
      targetType,
      IrTypeOperator.INSTANCEOF,
      targetType,
      subject
    )

    val transformer = object : IrElementTransformerVoid() {
      override fun visitCall(expression: IrCall): IrExpression {
        if (expression.descriptor != patternContext.paramPlaceholder.getter) {
          return super.visitCall(expression)
        }

        // todo: support more than one component
        val componentCall = targetClass.findFirstFunction("component1") { it.valueParameters.isEmpty() }
        val irType = typeTranslator.translateType(componentCall.returnType!!)

        return IrCallImpl(
          expression.startOffset,
          expression.endOffset,
          irType,
          backendContext.ir.symbols.externalSymbolTable.referenceFunction(componentCall),
          componentCall
        ).also { call ->
          call.dispatchReceiver = subject
        }
      }
    }

    branch.result = branch.result.transform(transformer, null)
  }

  return irWhen
}

fun IrUtils.findPatternCall(patternContext: PatternResolutionContext, expr: IrExpression): Pair<IrExpression, ConstructorDescriptor>? {
  // fixme: based on the template from tests, must be more complex
  if (expr !is IrCall) return null
  if (expr.symbol != backendContext.irBuiltIns.eqeqSymbol) return null

  val subject = expr.getValueArgument(0) ?: return null
  val caseCall = expr.getValueArgument(1)
  if (caseCall !is IrCall) return null
  if (caseCall.descriptor != patternContext.caseDescriptor) return null

  val constructorCall = caseCall.getValueArgument(0)
  if (constructorCall !is IrConstructorCall) return null
  val targetConstructor = constructorCall.descriptor

  return Pair(subject, targetConstructor)
}
