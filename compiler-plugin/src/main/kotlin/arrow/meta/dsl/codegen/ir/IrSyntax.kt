package arrow.meta.dsl.codegen.ir

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.codegen.ir.IRGeneration
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
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
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

/**
 * The codegen phase is where the compiler emits bytecode and metadata for the different platforms the Kotlin language targets.
 * In this phase, by default, the compiler would go into ASM codegen for the JVM, or into IR codegen if IR is enabled.
 * [IR] is the Intermediate Representation format the new Kotlin compiler backend targets.
 */
interface IrSyntax {

  /**
   * IR, The intermediate representation format, is a structured text format with significant indentation that contains
   * all the information the compiler knows about a program.
   * At this point, the compiler knows the structure of a program based on its sources, what the typed expressions are, and how
   * each of the generic type arguments gets applied.
   * The compiler emits information in this phase that is processed by interpreters and compilers
   * targeting any platform.
   * [IR Example]
   */
  fun IrGeneration(generate: (compilerContext: CompilerContext, file: IrFile, pluginContext: IrPluginContext) -> Unit): IRGeneration =
    object : IRGeneration {
      override fun CompilerContext.generate(
        file: IrFile,
        pluginContext: IrPluginContext
      ) {
        generate(this, file, pluginContext)
      }
    }

  fun irElement(f: IrUtils.(IrElement) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitElement(element: IrElement, data: Unit): IrElement =
          f(IrUtils(pluginContext, compilerContext), element)?.let { super.visitElement(it, data) } ?: super.visitElement(element, data)
      }, Unit)
    }

  fun irModuleFragment(f: IrUtils.(IrModuleFragment) -> IrModuleFragment?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitModuleFragment(declaration: IrModuleFragment, data: Unit): IrModuleFragment =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitModuleFragment(declaration, data)
      }, Unit)
    }

  fun irFile(f: IrUtils.(IrFile) -> IrFile?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitFile(declaration: IrFile, data: Unit): IrFile =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitFile(declaration, data)
      }, Unit)
    }

  fun irExternalPackageFragment(f: IrUtils.(IrExternalPackageFragment) -> IrExternalPackageFragment?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitExternalPackageFragment(declaration: IrExternalPackageFragment, data: Unit): IrExternalPackageFragment =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitExternalPackageFragment(declaration, data)
      }, Unit)
    }

  fun irDeclaration(f: IrUtils.(IrDeclaration) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDeclaration(declaration: IrDeclaration, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitDeclaration(declaration, data)
      }, Unit)
    }

  fun irClass(f: IrUtils.(IrClass) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitClass(declaration: IrClass, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitClass(declaration, data)
      }, Unit)
    }

  fun irFunction(f: IrUtils.(IrFunction) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitFunction(declaration: IrFunction, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitFunction(declaration, data)
      }, Unit)
    }

  fun irSimpleFunction(f: IrUtils.(IrSimpleFunction) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSimpleFunction(declaration: IrSimpleFunction, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitSimpleFunction(declaration, data)
      }, Unit)
    }

  fun irConstructor(f: IrUtils.(IrConstructor) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitConstructor(declaration: IrConstructor, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitConstructor(declaration, data)
      }, Unit)
    }

  fun irProperty(f: IrUtils.(IrProperty) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitProperty(declaration: IrProperty, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitProperty(declaration, data)
      }, Unit)
    }

  fun irField(f: IrUtils.(IrField) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitField(declaration: IrField, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitField(declaration, data)
      }, Unit)
    }

  fun irLocalDelegatedProperty(f: IrUtils.(IrLocalDelegatedProperty) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitLocalDelegatedProperty(declaration: IrLocalDelegatedProperty, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitLocalDelegatedProperty(declaration, data)
      }, Unit)
    }

  fun irEnumEntry(f: IrUtils.(IrEnumEntry) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitEnumEntry(declaration: IrEnumEntry, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitEnumEntry(declaration, data)
      }, Unit)
    }

  fun irAnonymousInitializer(f: IrUtils.(IrAnonymousInitializer) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitAnonymousInitializer(declaration, data)
      }, Unit)
    }

  fun irVariable(f: IrUtils.(IrVariable) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitVariable(declaration: IrVariable, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitVariable(declaration, data)
      }, Unit)
    }

  fun irTypeParameter(f: IrUtils.(IrTypeParameter) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitTypeParameter(declaration: IrTypeParameter, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitTypeParameter(declaration, data)
      }, Unit)
    }

  fun irValueParameter(f: IrUtils.(IrValueParameter) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitValueParameter(declaration: IrValueParameter, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitValueParameter(declaration, data)
      }, Unit)
    }

  fun irBody(f: IrUtils.(IrBody) -> IrBody?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitBody(body: IrBody, data: Unit): IrBody =
          f(IrUtils(pluginContext, compilerContext), body) ?: super.visitBody(body, data)
      }, Unit)
    }

  fun irExpressionBody(f: IrUtils.(IrExpressionBody) -> IrBody?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitExpressionBody(body: IrExpressionBody, data: Unit): IrBody =
          f(IrUtils(pluginContext, compilerContext), body) ?: super.visitExpressionBody(body, data)
      }, Unit)
    }

  fun irBlockBody(f: IrUtils.(IrBlockBody) -> IrBody?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitBlockBody(body: IrBlockBody, data: Unit): IrBody =
          f(IrUtils(pluginContext, compilerContext), body) ?: super.visitBlockBody(body, data)
      }, Unit)
    }

  fun irSyntheticBody(f: IrUtils.(IrSyntheticBody) -> IrBody?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSyntheticBody(body: IrSyntheticBody, data: Unit): IrBody =
          f(IrUtils(pluginContext, compilerContext), body) ?: super.visitSyntheticBody(body, data)
      }, Unit)
    }

  fun irSuspendableExpression(f: IrUtils.(IrSuspendableExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSuspendableExpression(expression: IrSuspendableExpression, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitSuspendableExpression(expression, data)
      }, Unit)
    }

  fun irSuspensionPoint(f: IrUtils.(IrSuspensionPoint) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSuspensionPoint(expression: IrSuspensionPoint, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitSuspensionPoint(expression, data)
      }, Unit)
    }

  fun irExpression(f: IrUtils.(IrExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitExpression(expression: IrExpression, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitExpression(expression, data)
      }, Unit)
    }

  fun <A> Meta.irConst(f: IrUtils.(IrConst<A>) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun <T> visitConst(expression: IrConst<T>, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression as IrConst<A>) ?: super.visitConst(expression, data)
      }, Unit)
    }

  fun irVararg(f: IrUtils.(IrVararg) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitVararg(expression: IrVararg, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitVararg(expression, data)
      }, Unit)
    }

  fun irSpreadElement(f: IrUtils.(IrSpreadElement) -> IrSpreadElement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSpreadElement(spread: IrSpreadElement, data: Unit): IrSpreadElement =
          f(IrUtils(pluginContext, compilerContext), spread) ?: super.visitSpreadElement(spread, data)
      }, Unit)
    }

  fun irContainerExpression(f: IrUtils.(IrContainerExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitContainerExpression(expression: IrContainerExpression, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitContainerExpression(expression, data)
      }, Unit)
    }

  fun irBlock(f: IrUtils.(IrBlock) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitBlock(expression: IrBlock, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitBlock(expression, data)
      }, Unit)
    }

  fun irComposite(f: IrUtils.(IrComposite) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitComposite(expression: IrComposite, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitComposite(expression, data)
      }, Unit)
    }

  fun irStringConcatenation(f: IrUtils.(IrStringConcatenation) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitStringConcatenation(expression: IrStringConcatenation, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitStringConcatenation(expression, data)
      }, Unit)
    }

  fun irDeclarationReference(f: IrUtils.(IrDeclarationReference) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDeclarationReference(expression: IrDeclarationReference, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitDeclarationReference(expression, data)
      }, Unit)
    }

  fun irSingletonReference(f: IrUtils.(IrGetSingletonValue) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSingletonReference(expression: IrGetSingletonValue, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitSingletonReference(expression, data)
      }, Unit)
    }

  fun irGetObjectValue(f: IrUtils.(IrGetObjectValue) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitGetObjectValue(expression: IrGetObjectValue, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitGetObjectValue(expression, data)
      }, Unit)
    }

  fun irGetEnumValue(f: IrUtils.(IrGetEnumValue) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitGetEnumValue(expression: IrGetEnumValue, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitGetEnumValue(expression, data)
      }, Unit)
    }

  fun irValueAccess(f: IrUtils.(IrValueAccessExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitValueAccess(expression: IrValueAccessExpression, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitValueAccess(expression, data)
      }, Unit)
    }

  fun irGetValue(f: IrUtils.(IrGetValue) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitGetValue(expression: IrGetValue, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitGetValue(expression, data)
      }, Unit)
    }

  fun irSetVariable(f: IrUtils.(IrSetVariable) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSetVariable(expression: IrSetVariable, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitSetVariable(expression, data)
      }, Unit)
    }

  fun irFieldAccess(f: IrUtils.(IrFieldAccessExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitFieldAccess(expression: IrFieldAccessExpression, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitFieldAccess(expression, data)
      }, Unit)
    }

  fun irGetField(f: IrUtils.(IrGetField) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitGetField(expression: IrGetField, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitGetField(expression, data)
      }, Unit)
    }

  fun irSetField(f: IrUtils.(IrSetField) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSetField(expression: IrSetField, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitSetField(expression, data)
      }, Unit)
    }

  fun irMemberAccess(f: IrUtils.(IrMemberAccessExpression) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitMemberAccess(expression: IrMemberAccessExpression, data: Unit): IrElement =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitMemberAccess(expression, data)
      }, Unit)
    }

  fun irFunctionAccess(f: IrUtils.(IrFunctionAccessExpression) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitFunctionAccess(expression: IrFunctionAccessExpression, data: Unit): IrElement =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitFunctionAccess(expression, data)
      }, Unit)
    }

  fun irCall(f: IrUtils.(IrCall) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitCall(expression: IrCall, data: Unit): IrElement =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitCall(expression, data)
      }, Unit)
    }

  fun irConstructorCall(f: IrUtils.(IrConstructorCall) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitConstructorCall(expression: IrConstructorCall, data: Unit): IrElement =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitConstructorCall(expression, data)
      }, Unit)
    }

  fun irDelegatingConstructorCall(f: IrUtils.(IrDelegatingConstructorCall) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDelegatingConstructorCall(expression: IrDelegatingConstructorCall, data: Unit): IrElement =
          f(IrUtils(pluginContext, compilerContext), expression)
            ?: super.visitDelegatingConstructorCall(expression, data)
      }, Unit)
    }

  fun irEnumConstructorCall(f: IrUtils.(IrEnumConstructorCall) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitEnumConstructorCall(expression: IrEnumConstructorCall, data: Unit): IrElement =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitEnumConstructorCall(expression, data)
      }, Unit)
    }

  fun irGetClass(f: IrUtils.(IrGetClass) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitGetClass(expression: IrGetClass, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitGetClass(expression, data)
      }, Unit)
    }

  fun irCallableReference(f: IrUtils.(IrCallableReference) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitCallableReference(expression: IrCallableReference, data: Unit): IrElement =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitCallableReference(expression, data)
      }, Unit)
    }

  fun irFunctionReference(f: IrUtils.(IrFunctionReference) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitFunctionReference(expression: IrFunctionReference, data: Unit): IrElement =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitFunctionReference(expression, data)
      }, Unit)
    }

  fun irPropertyReference(f: IrUtils.(IrPropertyReference) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): IrElement =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitPropertyReference(expression, data)
      }, Unit)
    }

  fun irLocalDelegatedPropertyReference(f: IrUtils.(IrLocalDelegatedPropertyReference) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitLocalDelegatedPropertyReference(expression: IrLocalDelegatedPropertyReference, data: Unit): IrElement =
          f(IrUtils(pluginContext, compilerContext), expression)
            ?: super.visitLocalDelegatedPropertyReference(expression, data)
      }, Unit)
    }

  fun irClassReference(f: IrUtils.(IrClassReference) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitClassReference(expression: IrClassReference, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitClassReference(expression, data)
      }, Unit)
    }

  fun irInstanceInitializerCall(f: IrUtils.(IrInstanceInitializerCall) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitInstanceInitializerCall(expression: IrInstanceInitializerCall, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitInstanceInitializerCall(expression, data)
      }, Unit)
    }

  fun irTypeOperator(f: IrUtils.(IrTypeOperatorCall) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitTypeOperator(expression, data)
      }, Unit)
    }

  fun irWhen(f: IrUtils.(IrWhen) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitWhen(expression: IrWhen, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitWhen(expression, data)
      }, Unit)
    }

  fun irBranch(f: IrUtils.(IrBranch) -> IrBranch?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitBranch(branch: IrBranch, data: Unit): IrBranch =
          f(IrUtils(pluginContext, compilerContext), branch) ?: super.visitBranch(branch, data)
      }, Unit)
    }

  fun irElseBranch(f: IrUtils.(IrElseBranch) -> IrElseBranch?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitElseBranch(branch: IrElseBranch, data: Unit): IrElseBranch =
          f(IrUtils(pluginContext, compilerContext), branch) ?: super.visitElseBranch(branch, data)
      }, Unit)
    }

  fun irLoop(f: IrUtils.(IrLoop) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitLoop(loop: IrLoop, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), loop) ?: super.visitLoop(loop, data)
      }, Unit)
    }

  fun irWhileLoop(f: IrUtils.(IrWhileLoop) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitWhileLoop(loop: IrWhileLoop, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), loop) ?: super.visitWhileLoop(loop, data)
      }, Unit)
    }

  fun irDoWhileLoop(f: IrUtils.(IrDoWhileLoop) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDoWhileLoop(loop: IrDoWhileLoop, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), loop) ?: super.visitDoWhileLoop(loop, data)
      }, Unit)
    }

  fun irTry(f: IrUtils.(IrTry) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitTry(aTry: IrTry, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), aTry) ?: super.visitTry(aTry, data)
      }, Unit)
    }

  fun irCatch(f: IrUtils.(IrCatch) -> IrCatch?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitCatch(aCatch: IrCatch, data: Unit): IrCatch =
          f(IrUtils(pluginContext, compilerContext), aCatch) ?: super.visitCatch(aCatch, data)
      }, Unit)
    }

  fun irBreakContinue(f: IrUtils.(IrBreakContinue) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitBreakContinue(jump: IrBreakContinue, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), jump) ?: super.visitBreakContinue(jump, data)
      }, Unit)
    }

  fun irBreak(f: IrUtils.(IrBreak) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitBreak(jump: IrBreak, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), jump) ?: super.visitBreak(jump, data)
      }, Unit)
    }

  fun irContinue(f: IrUtils.(IrContinue) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitContinue(jump: IrContinue, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), jump) ?: super.visitContinue(jump, data)
      }, Unit)
    }

  fun irReturn(f: IrUtils.(IrReturn) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitReturn(expression: IrReturn, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitReturn(expression, data)
      }, Unit)
    }

  fun irThrow(f: IrUtils.(IrThrow) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitThrow(expression: IrThrow, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitThrow(expression, data)
      }, Unit)
    }

  fun irDynamicExpression(f: IrUtils.(IrDynamicExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDynamicExpression(expression: IrDynamicExpression, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitDynamicExpression(expression, data)
      }, Unit)
    }

  fun irDynamicOperatorExpression(f: IrUtils.(IrDynamicOperatorExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDynamicOperatorExpression(expression: IrDynamicOperatorExpression, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression)
            ?: super.visitDynamicOperatorExpression(expression, data)
      }, Unit)
    }

  fun irDynamicMemberExpression(f: IrUtils.(IrDynamicMemberExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDynamicMemberExpression(expression: IrDynamicMemberExpression, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitDynamicMemberExpression(expression, data)
      }, Unit)
    }

  fun irErrorDeclaration(f: IrUtils.(IrErrorDeclaration) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitErrorDeclaration(declaration: IrErrorDeclaration, data: Unit): IrStatement =
          f(IrUtils(pluginContext, compilerContext), declaration) ?: super.visitErrorDeclaration(declaration, data)
      }, Unit)
    }

  fun irErrorExpression(f: IrUtils.(IrErrorExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitErrorExpression(expression: IrErrorExpression, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitErrorExpression(expression, data)
      }, Unit)
    }

  fun irErrorCallExpression(f: IrUtils.(IrErrorCallExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, pluginContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitErrorCallExpression(expression: IrErrorCallExpression, data: Unit): IrExpression =
          f(IrUtils(pluginContext, compilerContext), expression) ?: super.visitErrorCallExpression(expression, data)
      }, Unit)
    }

  fun irDump(): IRGeneration = IrGeneration { compilerContext, file, pluginContext ->
    println(file.dump())
  }
}
