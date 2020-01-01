package arrow.meta.phases.codegen.ir

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrAnonymousInitializer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrErrorDeclaration
import org.jetbrains.kotlin.ir.declarations.IrExternalPackageFragment
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
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
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
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

fun <A> identity(): (A) -> A = { it }

class IrTransform(
  val root: IrElement,
  val element: (element: IrElement) -> IrElement = identity(),
  val moduleFragment: (moduleFragment: IrModuleFragment) -> IrModuleFragment = identity(),
  val file: (file: IrFile) -> IrFile = identity(),
  val externalPackageFragment: (packageFragment: IrExternalPackageFragment) -> IrExternalPackageFragment = identity(),
  val declaration: (declaration: IrDeclaration) -> IrStatement = identity(),
  val body: (body: IrBody) -> IrBody = identity(),
  val expression: (expression: IrExpression) -> IrExpression = identity(),
  val spread: (spreadElement: IrSpreadElement) -> IrSpreadElement = identity(),
  val catch: (catch: IrCatch) -> IrCatch = identity()
) : IrElementTransformer<Unit> {

  fun transform(): Unit {
    root.transformChildren(this, Unit)
  }

  override fun visitElement(element: IrElement, data: Unit): IrElement {
    element.transformChildren(this, data)
    return element(element)
  }

  override fun visitModuleFragment(declaration: IrModuleFragment, data: Unit): IrModuleFragment {
    declaration.transformChildren(this, data)
    return moduleFragment(declaration)
  }

  override fun visitFile(declaration: IrFile, data: Unit): IrFile {
    declaration.transformChildren(this, data)
    return file(declaration)
  }

  override fun visitExternalPackageFragment(declaration: IrExternalPackageFragment, data: Unit): IrExternalPackageFragment {
    declaration.transformChildren(this, data)
    return externalPackageFragment(declaration)
  }

  override fun visitDeclaration(declaration: IrDeclaration, data: Unit): IrStatement {
    declaration.transformChildren(this, data)
    return declaration(declaration)
  }

  override fun visitClass(declaration: IrClass, data: Unit) = visitDeclaration(declaration, data)
  override fun visitFunction(declaration: IrFunction, data: Unit) = visitDeclaration(declaration, data)
  override fun visitSimpleFunction(declaration: IrSimpleFunction, data: Unit) = visitFunction(declaration, data)
  override fun visitConstructor(declaration: IrConstructor, data: Unit) = visitFunction(declaration, data)
  override fun visitProperty(declaration: IrProperty, data: Unit) = visitDeclaration(declaration, data)
  override fun visitField(declaration: IrField, data: Unit) = visitDeclaration(declaration, data)
  override fun visitLocalDelegatedProperty(declaration: IrLocalDelegatedProperty, data: Unit) = visitDeclaration(declaration, data)
  override fun visitEnumEntry(declaration: IrEnumEntry, data: Unit) = visitDeclaration(declaration, data)
  override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer, data: Unit) = visitDeclaration(declaration, data)
  override fun visitVariable(declaration: IrVariable, data: Unit) = visitDeclaration(declaration, data)
  override fun visitTypeParameter(declaration: IrTypeParameter, data: Unit) = visitDeclaration(declaration, data)
  override fun visitValueParameter(declaration: IrValueParameter, data: Unit) = visitDeclaration(declaration, data)
  override fun visitTypeAlias(declaration: IrTypeAlias, data: Unit) = visitDeclaration(declaration, data)

  override fun visitBody(body: IrBody, data: Unit): IrBody {
    body.transformChildren(this, data)
    return body(body)
  }

  override fun visitExpressionBody(body: IrExpressionBody, data: Unit) = visitBody(body, data)
  override fun visitBlockBody(body: IrBlockBody, data: Unit) = visitBody(body, data)
  override fun visitSyntheticBody(body: IrSyntheticBody, data: Unit) = visitBody(body, data)

  override fun visitSuspendableExpression(expression: IrSuspendableExpression, data: Unit) = visitExpression(expression, data)
  override fun visitSuspensionPoint(expression: IrSuspensionPoint, data: Unit) = visitExpression(expression, data)

  override fun visitExpression(expression: IrExpression, data: Unit): IrExpression {
    expression.transformChildren(this, data)
    return expression(expression)
  }

  override fun <T> visitConst(expression: IrConst<T>, data: Unit) = visitExpression(expression, data)
  override fun visitVararg(expression: IrVararg, data: Unit) = visitExpression(expression, data)

  override fun visitSpreadElement(spread: IrSpreadElement, data: Unit): IrSpreadElement {
    return spread.also {
      it.transformChildren(this, data)
    }.run(this.spread)
  }

  override fun visitContainerExpression(expression: IrContainerExpression, data: Unit) = visitExpression(expression, data)
  override fun visitBlock(expression: IrBlock, data: Unit) = visitContainerExpression(expression, data)
  override fun visitComposite(expression: IrComposite, data: Unit) = visitContainerExpression(expression, data)
  override fun visitStringConcatenation(expression: IrStringConcatenation, data: Unit) = visitExpression(expression, data)

  override fun visitDeclarationReference(expression: IrDeclarationReference, data: Unit) = visitExpression(expression, data)
  override fun visitSingletonReference(expression: IrGetSingletonValue, data: Unit) = visitDeclarationReference(expression, data)
  override fun visitGetObjectValue(expression: IrGetObjectValue, data: Unit) = visitSingletonReference(expression, data)
  override fun visitGetEnumValue(expression: IrGetEnumValue, data: Unit) = visitSingletonReference(expression, data)
  override fun visitValueAccess(expression: IrValueAccessExpression, data: Unit) = visitDeclarationReference(expression, data)
  override fun visitGetValue(expression: IrGetValue, data: Unit) = visitValueAccess(expression, data)
  override fun visitSetVariable(expression: IrSetVariable, data: Unit) = visitValueAccess(expression, data)
  override fun visitFieldAccess(expression: IrFieldAccessExpression, data: Unit) = visitDeclarationReference(expression, data)
  override fun visitGetField(expression: IrGetField, data: Unit) = visitFieldAccess(expression, data)
  override fun visitSetField(expression: IrSetField, data: Unit) = visitFieldAccess(expression, data)
  override fun visitMemberAccess(expression: IrMemberAccessExpression, data: Unit): IrElement = visitExpression(expression, data)
  override fun visitFunctionAccess(expression: IrFunctionAccessExpression, data: Unit): IrElement = visitMemberAccess(expression, data)
  override fun visitCall(expression: IrCall, data: Unit) = visitFunctionAccess(expression, data)
  override fun visitConstructorCall(expression: IrConstructorCall, data: Unit): IrElement = visitFunctionAccess(expression, data)
  override fun visitDelegatingConstructorCall(expression: IrDelegatingConstructorCall, data: Unit) = visitFunctionAccess(expression, data)
  override fun visitEnumConstructorCall(expression: IrEnumConstructorCall, data: Unit) = visitFunctionAccess(expression, data)
  override fun visitGetClass(expression: IrGetClass, data: Unit) = visitExpression(expression, data)

  override fun visitCallableReference(expression: IrCallableReference, data: Unit) = visitMemberAccess(expression, data)
  override fun visitFunctionReference(expression: IrFunctionReference, data: Unit) = visitCallableReference(expression, data)
  override fun visitPropertyReference(expression: IrPropertyReference, data: Unit) = visitCallableReference(expression, data)
  override fun visitLocalDelegatedPropertyReference(expression: IrLocalDelegatedPropertyReference, data: Unit) =
    visitCallableReference(expression, data)

  override fun visitFunctionExpression(expression: IrFunctionExpression, data: Unit): IrElement = visitExpression(expression, data)

  override fun visitClassReference(expression: IrClassReference, data: Unit) = visitDeclarationReference(expression, data)

  override fun visitInstanceInitializerCall(expression: IrInstanceInitializerCall, data: Unit) = visitExpression(expression, data)

  override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit) = visitExpression(expression, data)

  override fun visitWhen(expression: IrWhen, data: Unit) = visitExpression(expression, data)

  override fun visitBranch(branch: IrBranch, data: Unit): IrBranch =
    branch.also {
      it.condition = it.condition.transform(this, data)
      it.result = it.result.transform(this, data)
    }

  override fun visitElseBranch(branch: IrElseBranch, data: Unit): IrElseBranch =
    branch.also {
      it.condition = it.condition.transform(this, data)
      it.result = it.result.transform(this, data)
    }

  override fun visitLoop(loop: IrLoop, data: Unit) = visitExpression(loop, data)
  override fun visitWhileLoop(loop: IrWhileLoop, data: Unit) = visitLoop(loop, data)
  override fun visitDoWhileLoop(loop: IrDoWhileLoop, data: Unit) = visitLoop(loop, data)
  override fun visitTry(aTry: IrTry, data: Unit) = visitExpression(aTry, data)

  override fun visitCatch(aCatch: IrCatch, data: Unit): IrCatch {
    aCatch.transformChildren(this, data)
    aCatch.transform(this, data)
    return catch(aCatch)
  }

  override fun visitBreakContinue(jump: IrBreakContinue, data: Unit) = visitExpression(jump, data)
  override fun visitBreak(jump: IrBreak, data: Unit) = visitBreakContinue(jump, data)
  override fun visitContinue(jump: IrContinue, data: Unit) = visitBreakContinue(jump, data)

  override fun visitReturn(expression: IrReturn, data: Unit) = visitExpression(expression, data)
  override fun visitThrow(expression: IrThrow, data: Unit) = visitExpression(expression, data)

  override fun visitDynamicExpression(expression: IrDynamicExpression, data: Unit) = visitExpression(expression, data)
  override fun visitDynamicOperatorExpression(expression: IrDynamicOperatorExpression, data: Unit) = visitDynamicExpression(expression, data)
  override fun visitDynamicMemberExpression(expression: IrDynamicMemberExpression, data: Unit) = visitDynamicExpression(expression, data)

  override fun visitErrorDeclaration(declaration: IrErrorDeclaration, data: Unit) = visitDeclaration(declaration, data)
  override fun visitErrorExpression(expression: IrErrorExpression, data: Unit) = visitExpression(expression, data)
  override fun visitErrorCallExpression(expression: IrErrorCallExpression, data: Unit) = visitErrorExpression(expression, data)
}