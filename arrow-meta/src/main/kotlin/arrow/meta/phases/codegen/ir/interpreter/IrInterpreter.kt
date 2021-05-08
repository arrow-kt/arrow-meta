/**
 * Modified version of the Kotlin Compiler IR Interpreter that can also load
 * and evaluate methods from external modules through reflection.
 *
 *
 *
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package arrow.meta.phases.codegen.ir.interpreter

import arrow.meta.phases.codegen.ir.interpreter.builtins.compileTimeFunctions
import arrow.meta.phases.codegen.ir.interpreter.intrinsics.IntrinsicEvaluator
import arrow.meta.phases.codegen.ir.interpreter.stack.StackImpl
import arrow.meta.phases.codegen.ir.interpreter.stack.Variable
import arrow.meta.phases.codegen.ir.interpreter.state.Common
import arrow.meta.phases.codegen.ir.interpreter.state.Complex
import arrow.meta.phases.codegen.ir.interpreter.state.ExceptionState
import arrow.meta.phases.codegen.ir.interpreter.state.Lambda
import arrow.meta.phases.codegen.ir.interpreter.state.Primitive
import arrow.meta.phases.codegen.ir.interpreter.state.State
import arrow.meta.phases.codegen.ir.interpreter.state.Wrapper
import arrow.meta.phases.codegen.ir.interpreter.state.asBooleanOrNull
import arrow.meta.phases.codegen.ir.interpreter.state.asString
import arrow.meta.phases.codegen.ir.interpreter.state.checkNullability
import arrow.meta.phases.codegen.ir.interpreter.state.isSubtypeOf
import org.jetbrains.kotlin.backend.common.ir.allParameters
import org.jetbrains.kotlin.backend.jvm.codegen.psiElement
import org.jetbrains.kotlin.builtins.UnsignedType
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrAnonymousInitializer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.descriptors.IrBuiltIns
import org.jetbrains.kotlin.ir.descriptors.toIrBasedDescriptor
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrBreak
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrCatch
import org.jetbrains.kotlin.ir.expressions.IrComposite
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrContinue
import org.jetbrains.kotlin.ir.expressions.IrDeclarationReference
import org.jetbrains.kotlin.ir.expressions.IrDelegatingConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrDoWhileLoop
import org.jetbrains.kotlin.ir.expressions.IrEnumConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrErrorExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.expressions.IrGetField
import org.jetbrains.kotlin.ir.expressions.IrGetObjectValue
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrInstanceInitializerCall
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrSetField
import org.jetbrains.kotlin.ir.expressions.IrSetValue
import org.jetbrains.kotlin.ir.expressions.IrSpreadElement
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrStringConcatenation
import org.jetbrains.kotlin.ir.expressions.IrSyntheticBody
import org.jetbrains.kotlin.ir.expressions.IrThrow
import org.jetbrains.kotlin.ir.expressions.IrTry
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.expressions.IrWhen
import org.jetbrains.kotlin.ir.expressions.IrWhileLoop
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrErrorExpressionImpl
import org.jetbrains.kotlin.ir.interpreter.BreakLoop
import org.jetbrains.kotlin.ir.interpreter.BreakWhen
import org.jetbrains.kotlin.ir.interpreter.Continue
import org.jetbrains.kotlin.ir.interpreter.Exception
import org.jetbrains.kotlin.ir.interpreter.ExecutionResult
import org.jetbrains.kotlin.ir.interpreter.Next
import org.jetbrains.kotlin.ir.interpreter.Return
import org.jetbrains.kotlin.ir.interpreter.ReturnLabel
import org.jetbrains.kotlin.ir.interpreter.builtins.CompileTimeFunction
import org.jetbrains.kotlin.ir.interpreter.check
import org.jetbrains.kotlin.ir.interpreter.exceptions.InterpreterException
import org.jetbrains.kotlin.ir.interpreter.exceptions.InterpreterMethodNotFoundException
import org.jetbrains.kotlin.ir.interpreter.exceptions.InterpreterTimeOutException
import org.jetbrains.kotlin.ir.interpreter.getVarargType
import org.jetbrains.kotlin.ir.interpreter.toIrConst
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.getUnsignedType
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.types.isArray
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.IdSignature
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.fileOrNull
import org.jetbrains.kotlin.ir.util.fqNameForIrSerialization
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isGetter
import org.jetbrains.kotlin.ir.util.isLocal
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.isSubclassOf
import org.jetbrains.kotlin.ir.util.nameForIrSerialization
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.load.kotlin.computeJvmDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import java.lang.invoke.MethodHandle
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.dropLast
import kotlin.collections.emptyMap
import kotlin.collections.filter
import kotlin.collections.filterIsInstance
import kotlin.collections.filterNotNull
import kotlin.collections.first
import kotlin.collections.firstOrNull
import kotlin.collections.flatMap
import kotlin.collections.forEach
import kotlin.collections.forEachIndexed
import kotlin.collections.indices
import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString
import kotlin.collections.last
import kotlin.collections.listOf
import kotlin.collections.listOfNotNull
import kotlin.collections.map
import kotlin.collections.mapIndexed
import kotlin.collections.mapNotNull
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.orEmpty
import kotlin.collections.plus
import kotlin.collections.plusAssign
import kotlin.collections.set
import kotlin.collections.single
import kotlin.collections.toList
import kotlin.collections.toMap
import kotlin.collections.toTypedArray
import kotlin.collections.zip
import kotlin.concurrent.thread
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.kotlinFunction

private const val MAX_COMMANDS = 500_000

internal class IrInterpreter(private val irBuiltIns: IrBuiltIns, private val bodyMap: Map<IdSignature, IrBody> = emptyMap()) {
  private val irExceptions = mutableListOf<IrClass>()

  private val stack = StackImpl()
  private var commandCount = 0

  private val mapOfEnums = mutableMapOf<IrSymbol, Complex>()
  private val mapOfObjects = mutableMapOf<IrSymbol, Complex>()

  constructor(irModule: IrModuleFragment) : this(irModule.irBuiltins) {
    irExceptions.addAll(
      irModule.files
        .flatMap { it.declarations }
        .filterIsInstance<IrClass>()
        .filter { it.isSubclassOf(irBuiltIns.throwableClass.owner) }
    )
  }

  private fun Any?.getType(defaultType: IrType): IrType {
    return when (this) {
      is Boolean -> irBuiltIns.booleanType
      is Char -> irBuiltIns.charType
      is Byte -> irBuiltIns.byteType
      is Short -> irBuiltIns.shortType
      is Int -> irBuiltIns.intType
      is Long -> irBuiltIns.longType
      is String -> irBuiltIns.stringType
      is Float -> irBuiltIns.floatType
      is Double -> irBuiltIns.doubleType
      null -> irBuiltIns.nothingNType
      else -> when (defaultType.classifierOrNull?.owner) {
        is IrTypeParameter -> stack.getVariable(defaultType.classifierOrFail).state.irClass.defaultType
        else -> defaultType
      }
    }
  }

  private fun incrementAndCheckCommands() {
    commandCount++
    if (commandCount >= MAX_COMMANDS) throw InterpreterTimeOutException()
  }

  fun interpret(expression: IrExpression): IrExpression {
    stack.clean()
    lateinit var result: IrExpression
    thread(start = true) {
      result = try {
        when (val returnLabel = expression.interpret().returnLabel) {
          ReturnLabel.REGULAR -> stack.popReturnValue().toIrExpression(expression)
          ReturnLabel.EXCEPTION -> {
            val message = (stack.popReturnValue() as ExceptionState).getFullDescription()
            IrErrorExpressionImpl(expression.startOffset, expression.endOffset, expression.type, "\n" + message)
          }
          else -> TODO("$returnLabel not supported as result of interpretation")
        }
      } catch (e: InterpreterException) {
        // TODO don't handle, throw to lowering
        IrErrorExpressionImpl(expression.startOffset, expression.endOffset, expression.type, "\n" + e.message)
      }
    }.join()
    return result
  }

  private fun IrElement.interpret(): ExecutionResult {
    try {
      incrementAndCheckCommands()
      val executionResult = when (this) {
        is IrSimpleFunction -> interpretFunction(this)
        is IrCall -> interpretCall(this)
        is IrConstructorCall -> interpretConstructorCall(this)
        is IrEnumConstructorCall -> interpretEnumConstructorCall(this)
        is IrDelegatingConstructorCall -> interpretDelegatedConstructorCall(this)
        is IrInstanceInitializerCall -> interpretInstanceInitializerCall(this)
        is IrBody -> interpretBody(this)
        is IrBlock -> interpretBlock(this)
        is IrReturn -> interpretReturn(this)
        is IrSetField -> interpretSetField(this)
        is IrGetField -> interpretGetField(this)
        is IrGetValue -> interpretGetValue(this)
        is IrGetObjectValue -> interpretGetObjectValue(this)
        is IrGetEnumValue -> interpretGetEnumValue(this)
        is IrEnumEntry -> interpretEnumEntry(this)
        is IrConst<*> -> interpretConst(this)
        is IrVariable -> interpretVariable(this)
        is IrSetValue -> interpretSetVariable(this)
        is IrTypeOperatorCall -> interpretTypeOperatorCall(this)
        is IrBranch -> interpretBranch(this)
        is IrWhileLoop -> interpretWhile(this)
        is IrDoWhileLoop -> interpretDoWhile(this)
        is IrWhen -> interpretWhen(this)
        is IrBreak -> interpretBreak(this)
        is IrContinue -> interpretContinue(this)
        is IrVararg -> interpretVararg(this)
        is IrSpreadElement -> interpretSpreadElement(this)
        is IrTry -> interpretTry(this)
        is IrCatch -> interpretCatch(this)
        is IrThrow -> interpretThrow(this)
        is IrStringConcatenation -> interpretStringConcatenation(this)
        is IrFunctionExpression -> interpretFunctionExpression(this)
        is IrFunctionReference -> interpretFunctionReference(this)
        is IrComposite -> interpretComposite(this)

        else -> TODO("${this.javaClass} not supported")
      }

      return executionResult.getNextLabel(this) { this@getNextLabel.interpret() }
    } catch (e: InterpreterException) {
      throw e
    } catch (e: Throwable) {
      // catch exception from JVM such as: ArithmeticException, StackOverflowError and others
      val exceptionName = e::class.java.simpleName
      val irExceptionClass =
        irExceptions.firstOrNull { it.name.asString() == exceptionName } ?: irBuiltIns.throwableClass.owner
      stack.pushReturnValue(ExceptionState(e, irExceptionClass, stack.getStackTrace()))
      return org.jetbrains.kotlin.ir.interpreter.Exception
    }
  }

  // this method is used to get stack trace after exception
  private fun interpretFunction(irFunction: IrSimpleFunction): ExecutionResult {
    if (irFunction.fileOrNull != null) stack.setCurrentFrameName(irFunction)

    if (irFunction.body is IrSyntheticBody) return handleIntrinsicMethods(irFunction)
    return irFunction.body?.interpret() ?: throw InterpreterException("Ir function must be with body")
  }

  private fun MethodHandle?.invokeMethod(irFunction: IrFunction): ExecutionResult {
    this ?: return handleIntrinsicMethods(irFunction)
    val result = this.invokeWithArguments(irFunction.getArgsForMethodInvocation(stack.getAll()))
    stack.pushReturnValue(result.toState(result.getType(irFunction.returnType)))

    return Next
  }

  private fun handleIntrinsicMethods(irFunction: IrFunction): ExecutionResult {
    return IntrinsicEvaluator().evaluate(irFunction, stack) { this.interpret() }
  }

  private fun calculateBuiltIns(irFunction: IrFunction): ExecutionResult {
    val methodName = when (val property = (irFunction as? IrSimpleFunction)?.correspondingPropertySymbol) {
      null -> irFunction.name.asString()
      else -> property.owner.name.asString()
    }
    val args = stack.getAll().map { it.state }

    val receiverType = irFunction.dispatchReceiverParameter?.type
    val argsType = listOfNotNull(receiverType) + irFunction.valueParameters.map { it.type }
    val argsValues = args.map {
      when (it) {
        is Complex -> when (irFunction.fqNameWhenAvailable?.asString()) {
          // must explicitly convert Common to String in String plus method or else will be taken default toString from Common
          "kotlin.String.plus" -> stack.apply { interpretToString(it) }.popReturnValue().asString()
          else -> it.getOriginal()
        }
        is Primitive<*> -> it.value
        is Lambda -> it // lambda can be used in built in calculation, for example, in null check or toString
        else -> TODO("unsupported type of argument for builtins calculations: ${it::class.java}")
      }
    }

    val signature = CompileTimeFunction(
      methodName,
      argsType.mapNotNull {
        val name = it.originalKotlinType?.constructor?.declarationDescriptor?.name?.asString()
        if (it.isNullable()) "$name?" else name
      })
    val function = compileTimeFunctions[signature]

    val resolvedArgValues = argsValues.toTypedArray().resolve()

    if (function != null) {
      val result = function(*resolvedArgValues.toTypedArray())
      stack.pushReturnValue(result.toState(result.getType(irFunction.returnType)))
    } else {
      val callable = irFunction.classLoadedFunction()
      val function =
        callable ?: throw InterpreterMethodNotFoundException("Can't resolve function ${irFunction.fqNameForIrSerialization} in compiler evaluable builtin functions.")
      val result = function(*resolvedArgValues.toTypedArray())

      stack.pushReturnValue(result.toState(result.getType(irFunction.returnType)))
    }
    return Next
  }

  private fun calculateRangeTo(type: IrType): ExecutionResult {
    val constructor = type.classOrNull!!.owner.constructors.first()
    val constructorCall = IrConstructorCallImpl.fromSymbolOwner(constructor.returnType, constructor.symbol)

    val primitiveValueParameters = stack.getAll().map { it.state as Primitive<*> }
    primitiveValueParameters.forEachIndexed { index, primitive ->
      constructorCall.putValueArgument(index, primitive.value.toIrConst(primitive.type))
    }

    val constructorValueParameters = constructor.valueParameters.map { it.symbol }.zip(primitiveValueParameters)
    return stack.newFrame(initPool = constructorValueParameters.map { Variable(it.first, it.second) }) {
      constructorCall.interpret()
    }
  }

  private fun interpretValueParameters(
    expression: IrFunctionAccessExpression, irFunction: IrFunction, pool: MutableList<Variable>
  ): ExecutionResult {
    // if irFunction is lambda and it has receiver, then first descriptor must be taken from extension receiver
    val receiverAsFirstArgument = when (expression.dispatchReceiver?.type?.isFunction()) {
      true -> listOfNotNull(irFunction.getExtensionReceiver())
      else -> listOf()
    }
    val valueParametersSymbols = receiverAsFirstArgument + irFunction.valueParameters.map { it.symbol }

    val valueArguments = (0 until expression.valueArgumentsCount).map { expression.getValueArgument(it) }
    val defaultValues = expression.symbol.owner.valueParameters.map {
      it.defaultValue?.expression
      when (it.defaultValue?.expression) {
        is IrErrorExpression -> IrConstImpl.defaultValueForType(
          UNDEFINED_OFFSET,
          UNDEFINED_OFFSET,
          it.type
        )
        else -> it.defaultValue?.expression
      }
    }

    return stack.newFrame(asSubFrame = true, initPool = pool) {
      for (i in valueArguments.indices) {
        (valueArguments[i] ?: defaultValues[i])?.interpret()?.check { return@newFrame it }
          ?: stack.pushReturnValue(listOf<Any?>().toPrimitiveStateArray(expression.getVarargType(i)!!)) // if vararg is empty

        stack.peekReturnValue().checkNullability(valueParametersSymbols[i].owner.type) {
          val method = irFunction.getCapitalizedFileName() + "." + irFunction.fqNameWhenAvailable
          val parameter = valueParametersSymbols[i].owner.name
          throw IllegalArgumentException("Parameter specified as non-null is null: method $method, parameter $parameter")
        }

        with(Variable(valueParametersSymbols[i], stack.popReturnValue())) {
          stack.addVar(this)  //must add value argument in current stack because it can be used later as default argument
          pool.add(this)
        }
      }
      Next
    }
  }

  private fun interpretCall(expression: IrCall): ExecutionResult {
    val valueArguments = mutableListOf<Variable>()
    // dispatch receiver processing
    val rawDispatchReceiver = expression.dispatchReceiver
    rawDispatchReceiver?.interpret()?.check { return it }
    val dispatchReceiver =
      rawDispatchReceiver?.let { stack.popReturnValue() }?.checkNullability(expression.dispatchReceiver?.type)

    // extension receiver processing
    val rawExtensionReceiver = expression.extensionReceiver
    rawExtensionReceiver?.interpret()?.check { return it }
    val extensionReceiver =
      rawExtensionReceiver?.let { stack.popReturnValue() }?.checkNullability(expression.extensionReceiver?.type)

    // get correct ir function
    val irFunction = dispatchReceiver?.getIrFunctionByIrCall(expression) ?: expression.symbol.owner
    val functionReceiver = dispatchReceiver.getCorrectReceiverByFunction(irFunction)

    // it is important firstly to add receiver, then arguments; this order is used in builtin method call
    irFunction.getDispatchReceiver()
      ?.let { functionReceiver?.let { receiver -> valueArguments.add(Variable(it, receiver)) } }
    irFunction.getExtensionReceiver()
      ?.let { extensionReceiver?.let { receiver -> valueArguments.add(Variable(it, receiver)) } }

    interpretValueParameters(expression, irFunction, valueArguments).check { return it }

    valueArguments.addAll(getTypeArguments(irFunction, expression) { stack.getVariable(it).state })
    if (dispatchReceiver is Complex) valueArguments.addAll(dispatchReceiver.typeArguments)
    if (extensionReceiver is Complex) valueArguments.addAll(extensionReceiver.typeArguments)

    val isLocal = (dispatchReceiver as? Complex)?.getOriginal()?.irClass?.isLocal ?: irFunction.isLocal
    if (isLocal) valueArguments.addAll(dispatchReceiver.extractNonLocalDeclarations())

    if (functionReceiver is Complex && irFunction.parentClassOrNull?.isInner == true) {
      generateSequence(functionReceiver.outerClass) { (it.state as? Complex)?.outerClass }.forEach {
        valueArguments.add(
          it
        )
      }
    }

    return stack.newFrame(asSubFrame = irFunction.isInline || irFunction.isLocal, initPool = valueArguments) {
      // inline only methods are not presented in lookup table, so must be interpreted instead of execution
      val isInlineOnly = irFunction.hasAnnotation(FqName("kotlin.internal.InlineOnly"))
      return@newFrame when {
        dispatchReceiver is Wrapper && !isInlineOnly -> dispatchReceiver.getMethod(irFunction).invokeMethod(irFunction)
        irFunction.hasAnnotation(arrow.meta.phases.codegen.ir.interpreter.builtins.evaluateIntrinsicAnnotation) -> Wrapper.getStaticMethod(
          irFunction
        ).invokeMethod(irFunction)
        dispatchReceiver is Primitive<*> -> calculateBuiltIns(irFunction) // 'is Primitive' check for js char and js long
        irFunction.body == null ->
          irFunction.trySubstituteFunctionBody() ?: irFunction.tryCalculateLazyConst() ?: calculateBuiltIns(irFunction)
        else -> irFunction.interpret()
      }
    }.check { return it }.implicitCastIfNeeded(expression.type, irFunction.returnType, stack)
  }

  private fun IrFunction.trySubstituteFunctionBody(): ExecutionResult? {
    val signature = this.symbol.signature ?: return null
    val body = bodyMap[signature]

    return body?.let {
      try {
        this.body = it
        this.interpret()
      } finally {
        this.body = null
      }
    }
  }

  // TODO fix in FIR2IR; const val getter must have body with IrGetField node
  private fun IrFunction.tryCalculateLazyConst(): ExecutionResult? {
    if (this !is IrSimpleFunction) return null
    return this.correspondingPropertySymbol?.owner?.backingField?.initializer?.interpret()
  }

  private fun interpretInstanceInitializerCall(call: IrInstanceInitializerCall): ExecutionResult {
    val irClass = call.classSymbol.owner

    // properties processing
    val classProperties = irClass.declarations.filterIsInstance<IrProperty>()
    classProperties.forEach { property ->
      property.backingField?.initializer?.expression?.interpret()?.check { return it }
      val receiver = irClass.thisReceiver!!.symbol
      if (property.backingField?.initializer != null) {
        val receiverState = stack.getVariable(receiver).state
        val propertyVar = Variable(property.symbol, stack.popReturnValue())
        receiverState.setField(propertyVar)
      }
    }

    // init blocks processing
    val anonymousInitializer = irClass.declarations.filterIsInstance<IrAnonymousInitializer>().filter { !it.isStatic }
    anonymousInitializer.forEach { init -> init.body.interpret().check { return it } }

    return Next
  }

  private fun interpretConstructor(constructorCall: IrFunctionAccessExpression): ExecutionResult {
    val owner = constructorCall.symbol.owner
    val valueArguments = mutableListOf<Variable>()

    interpretValueParameters(constructorCall, owner, valueArguments).check { return it }

    val irClass = owner.parent as IrClass
    val typeArguments = getTypeArguments(irClass, constructorCall) { stack.getVariable(it).state }
    if (irClass.hasAnnotation(arrow.meta.phases.codegen.ir.interpreter.builtins.evaluateIntrinsicAnnotation) || irClass.fqNameWhenAvailable!!.startsWith(
        Name.identifier("java")
      )
    ) {
      return stack.newFrame(initPool = valueArguments) { Wrapper.getConstructorMethod(owner).invokeMethod(owner) }
        .apply { stack.peekReturnValue().addTypeArguments(typeArguments) }
    }

    if (irClass.defaultType.isArray() || irClass.defaultType.isPrimitiveArray()) {
      // array constructor doesn't have body so must be treated separately
      return stack.newFrame(initPool = valueArguments) { handleIntrinsicMethods(owner) }
        .apply { stack.peekReturnValue().addTypeArguments(typeArguments) }
    }

    val state = Common(irClass).apply { this.addTypeArguments(typeArguments) }
    if (irClass.isLocal) state.fields.addAll(stack.getAll()) // TODO save only necessary declarations
    if (irClass.isInner) {
      constructorCall.dispatchReceiver!!.interpret().check { return it }
      state.outerClass = Variable(irClass.parentAsClass.thisReceiver!!.symbol, stack.popReturnValue())
    }
    valueArguments.add(Variable(irClass.thisReceiver!!.symbol, state)) //used to set up fields in body
    return stack.newFrame(initPool = valueArguments + state.typeArguments) {
      val statements = constructorCall.getBody()!!.statements
      // enum entry use IrTypeOperatorCall with IMPLICIT_COERCION_TO_UNIT as delegation call, but we need the value
      ((statements[0] as? IrTypeOperatorCall)?.argument ?: statements[0]).interpret().check { return@newFrame it }
      val returnedState = stack.popReturnValue() as Complex

      for (i in 1 until statements.size) statements[i].interpret().check { return@newFrame it }

      stack.pushReturnValue(state.apply { this.setSuperClassInstance(returnedState) })
      Next
    }
  }

  private fun interpretConstructorCall(constructorCall: IrConstructorCall): ExecutionResult {
    return interpretConstructor(constructorCall)
  }

  private fun interpretEnumConstructorCall(enumConstructorCall: IrEnumConstructorCall): ExecutionResult {
    return interpretConstructor(enumConstructorCall)
  }

  private fun interpretDelegatedConstructorCall(delegatingConstructorCall: IrDelegatingConstructorCall): ExecutionResult {
    if (delegatingConstructorCall.symbol.owner.parent == irBuiltIns.anyClass.owner) {
      val anyAsStateObject = Common(irBuiltIns.anyClass.owner)
      stack.pushReturnValue(anyAsStateObject)
      return Next
    }

    return interpretConstructor(delegatingConstructorCall)
  }

  private fun interpretConst(expression: IrConst<*>): ExecutionResult {
    fun getSignedType(unsignedType: IrType): IrType? = when (unsignedType.getUnsignedType()) {
      UnsignedType.UBYTE -> irBuiltIns.byteType
      UnsignedType.USHORT -> irBuiltIns.shortType
      UnsignedType.UINT -> irBuiltIns.intType
      UnsignedType.ULONG -> irBuiltIns.longType
      else -> null
    }

    val signedType = getSignedType(expression.type)
    return if (signedType != null) {
      val unsignedClass = expression.type.classOrNull!!
      val constructor = unsignedClass.constructors.single().owner
      val constructorCall = IrConstructorCallImpl.fromSymbolOwner(constructor.returnType, constructor.symbol)
      constructorCall.putValueArgument(0, expression.value.toIrConst(signedType))

      constructorCall.interpret()
    } else {
      stack.pushReturnValue(expression.toPrimitive())
      Next
    }
  }

  private fun interpretStatements(statements: List<IrStatement>): ExecutionResult {
    var executionResult: ExecutionResult = Next
    for (statement in statements) {
      when (statement) {
        is IrClass -> if (statement.isLocal) Next else TODO("Only local classes are supported")
        is IrFunction -> if (statement.isLocal) Next else TODO("Only local functions are supported")
        else -> executionResult = statement.interpret().check { return it }
      }
    }
    return executionResult
  }

  private fun interpretBlock(block: IrBlock): ExecutionResult {
    return stack.newFrame(asSubFrame = true) { interpretStatements(block.statements) }
  }

  private fun interpretBody(body: IrBody): ExecutionResult {
    return stack.newFrame(asSubFrame = true) { interpretStatements(body.statements) }
  }

  private fun interpretReturn(expression: IrReturn): ExecutionResult {
    expression.value.interpret().check { return it }
    return Return.addOwnerInfo(expression.returnTargetSymbol.owner)
  }

  private fun interpretWhile(expression: IrWhileLoop): ExecutionResult {
    while (true) {
      expression.condition.interpret().check { return it }
      if (stack.popReturnValue().asBooleanOrNull() != true) break
      expression.body?.interpret()?.check { return it }
    }
    return Next
  }

  private fun interpretDoWhile(expression: IrDoWhileLoop): ExecutionResult {
    do {
      // pool from body must be seen to condition expression, so must create temp frame here
      stack.newFrame(asSubFrame = true) {
        expression.body?.interpret()?.check { return@newFrame it }
        expression.condition.interpret().check { return@newFrame it }
        Next
      }.check { return it }

      if (stack.popReturnValue().asBooleanOrNull() != true) break
    } while (true)
    return Next
  }

  private fun interpretWhen(expression: IrWhen): ExecutionResult {
    var executionResult: ExecutionResult = Next
    for (branch in expression.branches) {
      executionResult = branch.interpret().check { return it }
    }
    return executionResult
  }

  private fun interpretBranch(expression: IrBranch): ExecutionResult {
    val executionResult = expression.condition.interpret().check { return it }
    if (stack.popReturnValue().asBooleanOrNull() == true) {
      expression.result.interpret().check { return it }
      return BreakWhen
    }
    return executionResult
  }

  private fun interpretBreak(breakStatement: IrBreak): ExecutionResult {
    return BreakLoop.addOwnerInfo(breakStatement.loop)
  }

  private fun interpretContinue(continueStatement: IrContinue): ExecutionResult {
    return Continue.addOwnerInfo(continueStatement.loop)
  }

  private fun interpretSetField(expression: IrSetField): ExecutionResult {
    expression.value.interpret().check { return it }

    // receiver is null only for top level var, but it cannot be used in constexpr; corresponding check is on frontend
    val receiver = (expression.receiver as IrDeclarationReference).symbol
    val propertySymbol = expression.symbol.owner.correspondingPropertySymbol!!
    stack.getVariable(receiver).apply { this.state.setField(Variable(propertySymbol, stack.popReturnValue())) }
    return Next
  }

  private fun interpretGetField(expression: IrGetField): ExecutionResult {
    val receiver = (expression.receiver as? IrDeclarationReference)?.symbol
    val field = expression.symbol.owner
    // for java static variables
    if (field.origin == IrDeclarationOrigin.IR_EXTERNAL_JAVA_DECLARATION_STUB && field.isStatic) {
      val initializerExpression = field.initializer?.expression
      if (initializerExpression is IrConst<*>) {
        return interpretConst(initializerExpression)
      }
      stack.pushReturnValue(Wrapper.getStaticGetter(field)!!.invokeWithArguments().toState(field.type))
      return Next
    }
    if (field.origin == IrDeclarationOrigin.PROPERTY_BACKING_FIELD && field.correspondingPropertySymbol?.owner?.isConst == true) {
      return field.initializer?.expression?.interpret() ?: Next
    }
    // receiver is null, for example, for top level fields
    val result = receiver?.let { stack.getVariable(receiver).state.getState(field.correspondingPropertySymbol!!) }
      ?: return (expression.symbol.owner.initializer?.expression?.interpret() ?: Next)
    stack.pushReturnValue(result)
    return Next
  }

  private fun interpretGetValue(expression: IrGetValue): ExecutionResult {
    val owner = expression.type.classOrNull?.owner
    // used to evaluate constants inside object
    if (owner != null && owner.isObject) return getOrCreateObjectValue(owner) // TODO is this correct behaviour?
    stack.pushReturnValue(stack.getVariable(expression.symbol).state)
    return Next
  }

  private fun interpretVariable(expression: IrVariable): ExecutionResult {
    expression.initializer?.interpret()?.check { return it } ?: return Next
    stack.addVar(Variable(expression.symbol, stack.popReturnValue()))
    return Next
  }

  private fun interpretSetVariable(expression: IrSetValue): ExecutionResult {
    expression.value.interpret().check { return it }

    if (stack.contains(expression.symbol)) {
      stack.getVariable(expression.symbol).apply { this.state = stack.popReturnValue() }
    } else {
      stack.addVar(Variable(expression.symbol, stack.popReturnValue()))
    }
    return Next
  }

  private fun interpretGetObjectValue(expression: IrGetObjectValue): ExecutionResult {
    return getOrCreateObjectValue(expression.symbol.owner)
  }

  private fun getOrCreateObjectValue(objectClass: IrClass): ExecutionResult {
    mapOfObjects[objectClass.symbol]?.let { return Next.apply { stack.pushReturnValue(it) } }

    val objectState = when {
      objectClass.hasAnnotation(arrow.meta.phases.codegen.ir.interpreter.builtins.evaluateIntrinsicAnnotation) -> Wrapper.getCompanionObject(
        objectClass
      )
      else -> Common(objectClass).apply { setSuperClassRecursive() } // TODO test type arguments
    }
    mapOfObjects[objectClass.symbol] = objectState
    stack.pushReturnValue(objectState)
    return Next
  }

  private fun interpretGetEnumValue(expression: IrGetEnumValue): ExecutionResult {
    mapOfEnums[expression.symbol]?.let { return Next.apply { stack.pushReturnValue(it) } }

    val enumEntry = expression.symbol.owner
    val enumClass = enumEntry.symbol.owner.parentAsClass
    val valueOfFun = enumClass.declarations.single { it.nameForIrSerialization.asString() == "valueOf" } as IrFunction
    enumClass.declarations.filterIsInstance<IrEnumEntry>().forEach {
      val executionResult = when {
        enumClass.hasAnnotation(arrow.meta.phases.codegen.ir.interpreter.builtins.evaluateIntrinsicAnnotation) -> {
          val enumEntryName = it.name.asString().toState(irBuiltIns.stringType)
          val enumNameAsVariable = Variable(valueOfFun.valueParameters.first().symbol, enumEntryName)
          stack.newFrame(initPool = listOf(enumNameAsVariable)) {
            Wrapper.getEnumEntry(enumClass)!!.invokeMethod(valueOfFun)
          }
        }
        else -> interpretEnumEntry(it)
      }
      executionResult.check { result -> return result }
      mapOfEnums[it.symbol] = stack.popReturnValue() as Complex
    }

    stack.pushReturnValue(mapOfEnums[expression.symbol]!!)
    return Next
  }

  private fun interpretEnumEntry(enumEntry: IrEnumEntry): ExecutionResult {
    val enumClass = enumEntry.symbol.owner.parentAsClass
    val enumEntries = enumClass.declarations.filterIsInstance<IrEnumEntry>()

    val enumSuperCall = (enumClass.primaryConstructor?.body?.statements?.firstOrNull() as? IrEnumConstructorCall)
    if (enumEntries.isNotEmpty() && enumSuperCall != null) {
      val valueArguments = listOf(
        enumEntry.name.asString().toIrConst(irBuiltIns.stringType),
        enumEntries.indexOf(enumEntry).toIrConst(irBuiltIns.intType)
      )
      valueArguments.forEachIndexed { index, irConst -> enumSuperCall.putValueArgument(index, irConst) }
    }

    val executionResult = enumEntry.initializerExpression?.interpret()?.check { return it }
    enumSuperCall?.apply {
      (0 until this.valueArgumentsCount).forEach {
        putValueArgument(
          it,
          null
        )
      }
    } // restore to null
    return executionResult
      ?: throw InterpreterException("Initializer at enum entry ${enumEntry.fqNameWhenAvailable} is null")
  }

  private fun interpretTypeOperatorCall(expression: IrTypeOperatorCall): ExecutionResult {
    val executionResult = expression.argument.interpret().check { return it }
    val typeClassifier = expression.typeOperand.classifierOrFail
    val isReified = (typeClassifier.owner as? IrTypeParameter)?.isReified == true
    val isErased = typeClassifier.owner is IrTypeParameter && !isReified
    val typeOperand =
      if (isReified) stack.getVariable(typeClassifier).state.irClass.defaultType else expression.typeOperand

    when (expression.operator) {
      // coercion to unit means that return value isn't used
      IrTypeOperator.IMPLICIT_COERCION_TO_UNIT -> stack.popReturnValue()
      IrTypeOperator.CAST, IrTypeOperator.IMPLICIT_CAST -> {
        if (!isErased && !stack.peekReturnValue().isSubtypeOf(typeOperand)) {
          val convertibleClassName = stack.popReturnValue().irClass.fqNameWhenAvailable
          throw ClassCastException("$convertibleClassName cannot be cast to ${typeOperand.render()}")
        }
      }
      IrTypeOperator.SAFE_CAST -> {
        if (!isErased && !stack.peekReturnValue().isSubtypeOf(typeOperand)) {
          stack.popReturnValue()
          stack.pushReturnValue(null.toState(irBuiltIns.nothingNType))
        }
      }
      IrTypeOperator.INSTANCEOF -> {
        val isInstance = isErased || stack.peekReturnValue().isSubtypeOf(typeOperand)
        stack.pushReturnValue(isInstance.toState(irBuiltIns.nothingType))
      }
      IrTypeOperator.NOT_INSTANCEOF -> {
        val isInstance = isErased || stack.peekReturnValue().isSubtypeOf(typeOperand)
        stack.pushReturnValue((!isInstance).toState(irBuiltIns.nothingType))
      }
      IrTypeOperator.IMPLICIT_NOTNULL -> {

      }
      IrTypeOperator.SAM_CONVERSION -> {
        //TODO do something with this ?
      }
      else -> TODO("${expression.operator} not implemented")
    }
    return executionResult
  }

  private fun interpretVararg(expression: IrVararg): ExecutionResult {
    val args = expression.elements.flatMap {
      it.interpret().check { executionResult -> return executionResult }
      return@flatMap when (val result = stack.popReturnValue()) {
        is Wrapper -> listOf(result.value)
        is Primitive<*> ->
          when (val value = result.value) {
            is ByteArray -> value.toList()
            is CharArray -> value.toList()
            is ShortArray -> value.toList()
            is IntArray -> value.toList()
            is LongArray -> value.toList()
            is FloatArray -> value.toList()
            is DoubleArray -> value.toList()
            is BooleanArray -> value.toList()
            is Array<*> -> value.toList()
            else -> listOf(value)
          }
        else -> listOf(result)
      }
    }

    val array = when ((expression.type.classifierOrFail.owner as? IrDeclaration)?.nameForIrSerialization?.asString()) {
      "UByteArray", "UShortArray", "UIntArray", "ULongArray" -> {
        val owner = expression.type.classOrNull!!.owner
        val storageProperty =
          owner.declarations.filterIsInstance<IrProperty>().first { it.name.asString() == "storage" }
        val primitiveArray = args.map { ((it as Common).fields.single().state as Primitive<*>).value }
        val unsignedArray = primitiveArray.toPrimitiveStateArray(storageProperty.backingField!!.type)
        Common(owner).apply {
          setSuperClassRecursive()
          fields.add(Variable(storageProperty.symbol, unsignedArray))
        }
      }
      else -> args.toPrimitiveStateArray(expression.type)
    }
    stack.pushReturnValue(array)
    return Next
  }

  private fun interpretSpreadElement(spreadElement: IrSpreadElement): ExecutionResult {
    return spreadElement.expression.interpret().check { return it }
  }

  private fun interpretTry(expression: IrTry): ExecutionResult {
    try {
      expression.tryResult.interpret().check(ReturnLabel.EXCEPTION) { return it } // if not exception -> return
      val exception = stack.peekReturnValue() as ExceptionState
      for (catchBlock in expression.catches) {
        if (exception.isSubtypeOf(catchBlock.catchParameter.type.classOrNull!!.owner)) {
          catchBlock.interpret().check { return it }
          break
        }
      }
    } finally {
      expression.finallyExpression?.interpret()?.check { return it }
    }

    return Next
  }

  private fun interpretCatch(expression: IrCatch): ExecutionResult {
    val catchParameter = Variable(expression.catchParameter.symbol, stack.popReturnValue())
    return stack.newFrame(asSubFrame = true, initPool = listOf(catchParameter)) {
      expression.result.interpret()
    }
  }

  private fun interpretThrow(expression: IrThrow): ExecutionResult {
    expression.value.interpret().check { return it }
    when (val exception = stack.popReturnValue()) {
      is Common -> stack.pushReturnValue(ExceptionState(exception, stack.getStackTrace()))
      is Wrapper -> stack.pushReturnValue(ExceptionState(exception, stack.getStackTrace()))
      is ExceptionState -> stack.pushReturnValue(exception)
      else -> throw InterpreterException("${exception::class} cannot be used as exception state")
    }
    return Exception
  }

  private fun interpretStringConcatenation(expression: IrStringConcatenation): ExecutionResult {
    val result = StringBuilder()
    expression.arguments.forEach {
      it.interpret().check { executionResult -> return executionResult }
      interpretToString(stack.popReturnValue()).check { executionResult -> return executionResult }
      result.append(stack.popReturnValue().asString())
    }

    stack.pushReturnValue(result.toString().toState(expression.type))
    return Next
  }

  private fun interpretToString(state: State): ExecutionResult {
    val result = when (state) {
      is Primitive<*> -> state.value.toString()
      is Wrapper -> state.value.toString()
      is Common -> {
        val toStringFun = state.getToStringFunction()
        return stack.newFrame(initPool = mutableListOf(Variable(toStringFun.getReceiver()!!, state))) {
          toStringFun.body?.let { toStringFun.interpret() } ?: calculateBuiltIns(toStringFun)
        }
      }
      is Lambda -> state.toString()
      else -> throw InterpreterException("${state::class.java} cannot be used in StringConcatenation expression")
    }
    stack.pushReturnValue(result.toState(irBuiltIns.stringType))
    return Next
  }

  private fun interpretFunctionExpression(expression: IrFunctionExpression): ExecutionResult {
    val lambda = Lambda(expression.function, expression.type.classOrNull!!.owner)
    if (expression.function.isLocal) lambda.fields.addAll(stack.getAll()) // TODO save only necessary declarations
    stack.pushReturnValue(lambda)
    return Next
  }

  private fun interpretFunctionReference(reference: IrFunctionReference): ExecutionResult {
    stack.pushReturnValue(Lambda(reference.symbol.owner, reference.type.classOrNull!!.owner))
    return Next
  }

  private fun interpretComposite(expression: IrComposite): ExecutionResult {
    return when (expression.origin) {
      IrStatementOrigin.DESTRUCTURING_DECLARATION -> interpretStatements(expression.statements)
      null -> interpretStatements(expression.statements) // is null for body of do while loop
      else -> TODO("${expression.origin} not implemented")
    }
  }
}

internal fun IrFunction.classLoadedFunction(): ((Array<out Any?>) -> Any?)? =
  fqNameForIrSerialization.pathSegments().let {
    val className = it.dropLast(1).joinToString(".") { it.asString() }
    val methodName =
      if (isGetter || isSetter) it.last().asString().replace("<", "").replace(">", "").replace("-", "")
      else it.last().asString()
    val method = method(className, methodName, allParameters.map { it.type.dumpKotlinLike() })
    if (method != null) { args ->
      val f = method.kotlinFunction
      f?.isAccessible = true
      val reflectionArgs = f?.parameters?.mapIndexed { n, it ->
        if (it.isOptional) null
        else if (it.isVararg) {
          val a = mutableListOf<Any>()
          val varargs = args[0] as Array<Object>
          for (arg in varargs) a += arg
          it to a.toTypedArray()
        }
        else it to args[n]
      }?.filterNotNull().orEmpty().toMap()
      try {
        f?.callBy(reflectionArgs)
      } catch (e: InvocationTargetException) {
        throw InterpreterException(e.targetException.message ?: e.targetException.toString())
      }
    }
    else null
  }

private fun Array<out Any?>.resolve(): List<Any?> =
  map {
    when (it) {
      is Wrapper -> it.value
      is Common ->
        if (it.irClass.isCompanion && it.irClass.psiElement == null) {
          val value =
            Class.forName(it.irClass.parentClassOrNull?.fqNameForIrSerialization?.asString()).kotlin.companionObjectInstance
          value
        } else it
      else -> it
    }
  }

internal fun IrFunction.method(className: String, methodName: String, parameterTypes: List<String>): Method? {
  //TODO use signature instead of name
  val signature = this.toIrBasedDescriptor().computeJvmDescriptor(true, true)
  val foundClass = try {
    Class.forName(className)
  } catch (e: ClassNotFoundException) {
    try {
      Class.forName(className.replace(".Companion", ""))
    } catch (e: ClassNotFoundException) {
      null
    }
  }
  val method = foundClass?.methods?.firstOrNull {
    val methodSignature = it.getSignature()
    signature == methodSignature
  }
  return method
}
