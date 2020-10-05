package arrow.meta.ir.syntax

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
import org.jetbrains.kotlin.ir.expressions.IrSetVariable
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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test


class IrSyntaxTest {

  @Test
  fun `Visits irModuleFragment`() {
    testIrVisit(IrModuleFragment::class.java)
  }

  @Test
  fun `Visits irFile`() {
    testIrVisit(IrFile::class.java)
  }

  @Test
  fun `Visits irDeclaration`() {
    testIrVisit(IrDeclaration::class.java)
  }

  @Disabled
  @Test
  fun `Visits irClass`() {
    testIrVisit(IrClass::class.java)
  }

  @Test
  fun `Visits irFunction`() {
    testIrVisit(IrFunction::class.java)
  }

  @Test
  fun `Visits irSimpleFunction`() {
    testIrVisit(IrSimpleFunction::class.java)
  }

  @Test
  fun `Visits irConstructor`() {
    testIrVisit(IrConstructor::class.java)
  }

  @Test
  fun `Visits irProperty`() {
    testIrVisit(IrProperty::class.java)
  }

  @Disabled
  @Test
  fun `Visits irField`() {
    testIrVisit(IrField::class.java)
  }

  @Test
  fun `Visits irLocalDelegatedProperty`() {
    testIrVisit(IrLocalDelegatedProperty::class.java)
  }

  @Test
  fun `Visits irEnumEntry`() {
    testIrVisit(IrEnumEntry::class.java)
  }

  @Test
  fun `Visits irAnonymousInitializer`() {
    testIrVisit(IrAnonymousInitializer::class.java)
  }

  @Test
  fun `Visits irVariable`() {
    testIrVisit(IrVariable::class.java)
  }

  @Disabled
  @Test
  fun `Visits irTypeParameter`() {
    testIrVisit(IrTypeParameter::class.java)
  }

  @Test
  fun `Visits irValueParameter`() {
    testIrVisit(IrValueParameter::class.java, """
      fun <A: @Given Semigroup<A>> A.mappend(b: A): A =
          this@mappend.combine(b)

        val result1 = String.empty()
        val result2 = "1".combine("1")
    """.trimIndent())
  }

  @Test
  fun `Visits irTypeAlias`() {
    testIrVisit(IrTypeAlias::class.java)
  }

  @Test
  fun `Visits irBody`() {
    testIrVisit(IrBody::class.java)
  }

  @Test
  fun `Visits irExpressionBody`() {
    testIrVisit(IrExpressionBody::class.java)
  }

  @Test
  fun `Visits irBlockBody`() {
    testIrVisit(IrBlockBody::class.java)
  }

  @Test
  fun `Visits irSyntheticBody`() {
    testIrVisit(IrSyntheticBody::class.java)
  }

  @Disabled
  @Test
  fun `Visits irSuspendableExpression`() {
    testIrVisit(IrSuspendableExpression::class.java)
  }

  @Disabled
  @Test
  fun `Visits irSuspensionPoint`() {
    testIrVisit(IrSuspensionPoint::class.java)
  }

  @Test
  fun `Visits irExpression`() {
    testIrVisit(IrExpression::class.java)
  }

  @Disabled
  @Test
  fun `Visits irConst`() {
    testIrVisit(IrConst::class.java)
  }

  @Disabled
  @Test
  fun `Visits irVararg`() {
    testIrVisit(IrVararg::class.java)
  }

  @Disabled
  @Test
  fun `Visits irSpreadElement`() {
    testIrVisit(IrSpreadElement::class.java)
  }

  @Test
  fun `Visits irContainerExpression`() {
    testIrVisit(IrContainerExpression::class.java)
  }

  @Disabled
  @Test
  fun `Visits irBlock`() {
    testIrVisit(IrBlock::class.java)
  }

  @Disabled
  @Test
  fun `Visits irComposite`() {
    testIrVisit(IrComposite::class.java)
  }

  @Test
  fun `Visits irStringConcatenation`() {
    testIrVisit(IrStringConcatenation::class.java)
  }

  @Test
  fun `Visits irDeclarationReference`() {
    testIrVisit(IrDeclarationReference::class.java)
  }

  @Test
  fun `Visits irSingletonReference`() {
    testIrVisit(IrGetSingletonValue::class.java)
  }

  @Test
  fun `Visits irGetObjectValue`() {
    testIrVisit(IrGetObjectValue::class.java)
  }

  @Test
  fun `Visits irGetEnumValue`() {
    testIrVisit(IrGetEnumValue::class.java)
  }

  @Test
  fun `Visits irValueAccess`() {
    testIrVisit(IrValueAccessExpression::class.java)
  }

  @Test
  fun `Visits irGetValue`() {
    testIrVisit(IrGetValue::class.java)
  }

  @Test
  fun `Visits irSetVariable`() {
    testIrVisit(IrSetVariable::class.java)
  }

  @Disabled
  @Test
  fun `Visits irFieldAccess`() {
    testIrVisit(IrFieldAccessExpression::class.java)
  }

  @Test
  fun `Visits irGetField`() {
    testIrVisit(IrGetField::class.java)
  }

  @Test
  fun `Visits irSetField`() {
    testIrVisit(IrSetField::class.java)
  }

  @Disabled
  @Test
  fun `Visits irMemberAccess`() {
    testIrVisit(IrMemberAccessExpression::class.java)
  }

  @Test
  fun `Visits irFunctionAccess`() {
    testIrVisit(IrFunctionAccessExpression::class.java)
  }

  @Test
  fun `Visits irCall`() {
    testIrVisit(IrCall::class.java)
  }

  @Test
  fun `Visits irConstructorCall`() {
    testIrVisit(IrConstructorCall::class.java)
  }

  @Test
  fun `Visits irDelegatingConstructorCall`() {
    testIrVisit(IrDelegatingConstructorCall::class.java)
  }

  @Test
  fun `Visits irEnumConstructorCall`() {
    testIrVisit(IrEnumConstructorCall::class.java)
  }

  @Test
  fun `Visits irGetClass`() {
    testIrVisit(IrGetClass::class.java)
  }

  @Test
  fun `Visits irCallableReference`() {
    testIrVisit(IrCallableReference::class.java)
  }

  @Test
  fun `Visits irFunctionReference`() {
    testIrVisit(IrFunctionReference::class.java)
  }

  @Test
  fun `Visits irPropertyReference`() {
    testIrVisit(IrPropertyReference::class.java)
  }

  @Test
  fun `Visits irLocalDelegatedPropertyReference`() {
    testIrVisit(IrLocalDelegatedPropertyReference::class.java)
  }

  @Test
  fun `Visits irClassReference`() {
    testIrVisit(IrClassReference::class.java)
  }

  @Test
  fun `Visits irInstanceInitializerCall`() {
    testIrVisit(IrInstanceInitializerCall::class.java)
  }

  @Test
  fun `Visits irTypeOperator`() {
    testIrVisit(IrTypeOperatorCall::class.java)
  }

  @Test
  fun `Visits irWhen`() {
    testIrVisit(IrWhen::class.java)
  }

  @Test
  fun `Visits irBranch`() {
    testIrVisit(IrBranch::class.java)
  }

  @Test
  fun `Visits irElseBranch`() {
    testIrVisit(IrElseBranch::class.java)
  }

  @Test
  fun `Visits irLoop`() {
    testIrVisit(IrLoop::class.java)
  }

  @Test
  fun `Visits irWhileLoop`() {
    testIrVisit(IrWhileLoop::class.java)
  }

  @Test
  fun `Visits irDoWhileLoop`() {
    testIrVisit(IrDoWhileLoop::class.java)
  }

  @Test
  fun `Visits irTry`() {
    testIrVisit(IrTry::class.java)
  }

  @Disabled
  @Test
  fun `Visits irCatch`() {
    testIrVisit(IrCatch::class.java)
  }

  @Test
  fun `Visits irBreakContinue`() {
    testIrVisit(IrBreakContinue::class.java)
  }

  @Disabled
  @Test
  fun `Visits irBreak`() {
    testIrVisit(IrBreak::class.java)
  }

  @Test
  fun `Visits irContinue`() {
    testIrVisit(IrContinue::class.java)
  }

  @Test
  fun `Visits irReturn`() {
    testIrVisit(IrReturn::class.java)
  }

  @Disabled
  @Test
  fun `Visits irThrow`() {
    testIrVisit(IrThrow::class.java)
  }

  @Disabled
  @Test
  fun `Visits irDynamicExpression`() {
    testIrVisit(IrDynamicExpression::class.java)
  }

  @Test
  fun `Visits irDynamicOperatorExpression`() {
    testIrVisit(IrDynamicOperatorExpression::class.java)
  }

  @Test
  fun `Visits irDynamicMemberExpression`() {
    testIrVisit(IrDynamicMemberExpression::class.java)
  }

  @Test
  fun `Visits irErrorDeclaration`() {
    testIrVisit(IrErrorDeclaration::class.java)
  }

  @Disabled
  @Test
  fun `Visits irErrorExpression`() {
    testIrVisit(IrErrorExpression::class.java)
  }

  @Disabled
  @Test
  fun `Visits irErrorCallExpression`() {
    testIrVisit(IrErrorCallExpression::class.java)
  }
}