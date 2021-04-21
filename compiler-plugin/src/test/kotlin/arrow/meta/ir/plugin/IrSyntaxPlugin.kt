package arrow.meta.ir.plugin

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.ir.syntax.visits
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrAnonymousInitializer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrErrorDeclaration
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrLocalDelegatedProperty
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeAlias
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrBreak
import org.jetbrains.kotlin.ir.expressions.IrBreakContinue
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrCallableReference
import org.jetbrains.kotlin.ir.expressions.IrCatch
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.expressions.IrComposite
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrContainerExpression
import org.jetbrains.kotlin.ir.expressions.IrContinue
import org.jetbrains.kotlin.ir.expressions.IrDeclarationReference
import org.jetbrains.kotlin.ir.expressions.IrDelegatingConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrDoWhileLoop
import org.jetbrains.kotlin.ir.expressions.IrDynamicExpression
import org.jetbrains.kotlin.ir.expressions.IrDynamicMemberExpression
import org.jetbrains.kotlin.ir.expressions.IrDynamicOperatorExpression
import org.jetbrains.kotlin.ir.expressions.IrElseBranch
import org.jetbrains.kotlin.ir.expressions.IrEnumConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrErrorCallExpression
import org.jetbrains.kotlin.ir.expressions.IrErrorExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrFieldAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.expressions.IrGetClass
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.expressions.IrGetField
import org.jetbrains.kotlin.ir.expressions.IrGetObjectValue
import org.jetbrains.kotlin.ir.expressions.IrGetSingletonValue
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrInstanceInitializerCall
import org.jetbrains.kotlin.ir.expressions.IrLocalDelegatedPropertyReference
import org.jetbrains.kotlin.ir.expressions.IrLoop
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrSetField
import org.jetbrains.kotlin.ir.expressions.IrSetValue
import org.jetbrains.kotlin.ir.expressions.IrSpreadElement
import org.jetbrains.kotlin.ir.expressions.IrStringConcatenation
import org.jetbrains.kotlin.ir.expressions.IrSuspendableExpression
import org.jetbrains.kotlin.ir.expressions.IrSuspensionPoint
import org.jetbrains.kotlin.ir.expressions.IrSyntheticBody
import org.jetbrains.kotlin.ir.expressions.IrThrow
import org.jetbrains.kotlin.ir.expressions.IrTry
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.IrValueAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.expressions.IrWhen
import org.jetbrains.kotlin.ir.expressions.IrWhileLoop

open class IrSyntaxPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    "IrSyntaxPlugin" {
      meta(
        irModuleFragment(irVisit(IrModuleFragment::class.java)),
        irFile(irVisit(IrFile::class.java)),
        irDeclaration(irVisit(IrDeclaration::class.java)),
        irClass(irVisit(IrClass::class.java)),
        irFunction(irVisit(IrFunction::class.java)),
        irSimpleFunction(irVisit(IrSimpleFunction::class.java)),
        irConstructor(irVisit(IrConstructor::class.java)),
        irProperty(irVisit(IrProperty::class.java)),
        irField(irVisit(IrField::class.java)),
        irLocalDelegatedProperty(irVisit(IrLocalDelegatedProperty::class.java)),
        irEnumEntry(irVisit(IrEnumEntry::class.java)),
        irAnonymousInitializer(irVisit(IrAnonymousInitializer::class.java)),
        irVariable(irVisit(IrVariable::class.java)),
        irTypeParameter(irVisit(IrTypeParameter::class.java)),
        irValueParameter(irVisit(IrValueParameter::class.java)),
        irTypeAlias(irVisit(IrTypeAlias::class.java)),
        irBody(irVisit(IrBody::class.java)),
        irExpressionBody(irVisit(IrExpressionBody::class.java)),
        irBlockBody(irVisit(IrBlockBody::class.java)),
        irSyntheticBody(irVisit(IrSyntheticBody::class.java)),
        irSuspendableExpression(irVisit(IrSuspendableExpression::class.java)),
        irSuspensionPoint(irVisit(IrSuspensionPoint::class.java)),
        irExpression(irVisit(IrExpression::class.java)),
        irConst(irVisit(IrConst::class.java)),
        irVararg(irVisit(IrVararg::class.java)),
        irSpreadElement(irVisit(IrSpreadElement::class.java)),
        irContainerExpression(irVisit(IrContainerExpression::class.java)),
        irBlock(irVisit(IrBlock::class.java)),
        irComposite(irVisit(IrComposite::class.java)),
        irStringConcatenation(irVisit(IrStringConcatenation::class.java)),
        irDeclarationReference(irVisit(IrDeclarationReference::class.java)),
        irSingletonReference(irVisit(IrGetSingletonValue::class.java)),
        irGetObjectValue(irVisit(IrGetObjectValue::class.java)),
        irGetEnumValue(irVisit(IrGetEnumValue::class.java)),
        irValueAccess(irVisit(IrValueAccessExpression::class.java)),
        irGetValue(irVisit(IrGetValue::class.java)),
        irSetValue(irVisit(IrSetValue::class.java)),
        irFieldAccess(irVisit(IrFieldAccessExpression::class.java)),
        irGetField(irVisit(IrGetField::class.java)),
        irSetField(irVisit(IrSetField::class.java)),
        irMemberAccess(irVisit(IrMemberAccessExpression::class.java)),
        irFunctionAccess(irVisit(IrFunctionAccessExpression::class.java)),
        irCall(irVisit(IrCall::class.java)),
        irConstructorCall(irVisit(IrConstructorCall::class.java)),
        irDelegatingConstructorCall(irVisit(IrDelegatingConstructorCall::class.java)),
        irEnumConstructorCall(irVisit(IrEnumConstructorCall::class.java)),
        irGetClass(irVisit(IrGetClass::class.java)),
        irCallableReference(irVisit(IrCallableReference::class.java)),
        irFunctionReference(irVisit(IrFunctionReference::class.java)),
        irPropertyReference(irVisit(IrPropertyReference::class.java)),
        irLocalDelegatedPropertyReference(irVisit(IrLocalDelegatedPropertyReference::class.java)),
        irClassReference(irVisit(IrClassReference::class.java)),
        irInstanceInitializerCall(irVisit(IrInstanceInitializerCall::class.java)),
        irTypeOperator(irVisit(IrTypeOperatorCall::class.java)),
        irWhen(irVisit(IrWhen::class.java)),
        irBranch(irVisit(IrBranch::class.java)),
        irElseBranch(irVisit(IrElseBranch::class.java)),
        irLoop(irVisit(IrLoop::class.java)),
        irWhileLoop(irVisit(IrWhileLoop::class.java)),
        irDoWhileLoop(irVisit(IrDoWhileLoop::class.java)),
        irTry(irVisit(IrTry::class.java)),
        irCatch(irVisit(IrCatch::class.java)),
        irBreakContinue(irVisit(IrBreakContinue::class.java)),
        irBreak(irVisit(IrBreak::class.java)),
        irContinue(irVisit(IrContinue::class.java)),
        irReturn(irVisit(IrReturn::class.java)),
        irThrow(irVisit(IrThrow::class.java)),
        irDynamicExpression(irVisit(IrDynamicExpression::class.java)),
        irDynamicOperatorExpression(irVisit(IrDynamicOperatorExpression::class.java)),
        irDynamicMemberExpression(irVisit(IrDynamicMemberExpression::class.java)),
        irErrorDeclaration(irVisit(IrErrorDeclaration::class.java)),
        irErrorExpression(irVisit(IrErrorExpression::class.java)),
        irErrorCallExpression(irVisit(IrErrorCallExpression::class.java))
      )
    }
  )
}

internal fun <A : IrElement> irVisit(element: Class<A>): IrUtils.(A) -> A? = { _ ->
  this.compilerContext.messageCollector?.report(CompilerMessageSeverity.ERROR, visits(element))
  null
}