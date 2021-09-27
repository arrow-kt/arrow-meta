package arrow.meta.phases.codegen.ir

import arrow.meta.internal.Noop
import arrow.meta.phases.CompilerContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFactory
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrReturnTarget
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrTypeParametersContainer
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.descriptors.IrBuiltIns
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrReturnableBlock
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import arrow.meta.phases.codegen.ir.interpreter.IrInterpreter
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeSystemContext
import org.jetbrains.kotlin.ir.types.IrTypeSystemContextImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.ReferenceSymbolTable
import org.jetbrains.kotlin.ir.util.TypeTranslator
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.isTypeParameter
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi2ir.generators.TypeTranslatorImpl
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.calls.util.FakeCallableDescriptorForObject
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class IrUtils(
  val pluginContext: IrPluginContext,
  val compilerContext: CompilerContext,
  val moduleFragment: IrModuleFragment
) : ReferenceSymbolTable by pluginContext.symbols.externalSymbolTable,
  IrTypeSystemContext by IrTypeSystemContextImpl(pluginContext.irBuiltIns),
  IrFactory by pluginContext.irFactory {

  internal val irInterpreter: IrInterpreter = IrInterpreter(moduleFragment)

  val typeTranslator: TypeTranslator =
    TypeTranslatorImpl(
      symbolTable = pluginContext.symbols.externalSymbolTable,
      languageVersionSettings = pluginContext.languageVersionSettings,
      moduleDescriptor = moduleFragment.descriptor
    )

  fun interpretFunction(originalCall: IrCall, typeName: Name, value: IrConst<*>): IrExpression {
    val fnName = "require${typeName.asString()}"
    val fn = moduleFragment.files.flatMap { it.declarations }
      .filterIsInstance<IrFunction>().firstOrNull {
        it.name.asString() == fnName
      }
    val call = if (fn != null) {
      IrCallImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = irBuiltIns.unitType,
        symbol = fn.symbol as IrSimpleFunctionSymbol,
        typeArgumentsCount = 0,
        valueArgumentsCount = 1,
        origin = null,
        superQualifierSymbol = null
      ).also {
        it.putValueArgument(0, value)
      }
    } else null
    return if (call != null)
      irInterpreter.interpret(call)
    else irInterpreter.interpret(originalCall)
  }

  fun KotlinType.toIrType(): IrType =
    typeTranslator.translateType(this)

  fun CallableDescriptor.irCall(): IrExpression =
    when (this) {
      is PropertyDescriptor -> {
        val irField = pluginContext.symbols.externalSymbolTable.referenceField(this)
        irField.owner.correspondingPropertySymbol?.owner?.getter?.symbol?.let { irSimpleFunctionSymbol ->
          IrCallImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = irSimpleFunctionSymbol.owner.returnType,
            symbol = irSimpleFunctionSymbol,
            typeArgumentsCount = irSimpleFunctionSymbol.owner.typeParameters.size,
            valueArgumentsCount = irSimpleFunctionSymbol.owner.valueParameters.size
          )
        } ?: TODO("Unsupported irCall for $this")
      }
      is ClassConstructorDescriptor -> {
        val irSymbol = pluginContext.symbols.externalSymbolTable.referenceConstructor(this)
        IrConstructorCallImpl(
          startOffset = UNDEFINED_OFFSET,
          endOffset = UNDEFINED_OFFSET,
          type = irSymbol.owner.returnType,
          symbol = irSymbol,
          typeArgumentsCount = irSymbol.owner.typeParameters.size,
          valueArgumentsCount = irSymbol.owner.valueParameters.size,
          constructorTypeArgumentsCount = irSymbol.owner.typeParameters.size
        )
      }
      is FunctionDescriptor -> {
        val irSymbol = pluginContext.symbols.externalSymbolTable.referenceFunction(this)
        IrCallImpl(
          startOffset = UNDEFINED_OFFSET,
          endOffset = UNDEFINED_OFFSET,
          type = irSymbol.owner.returnType,
          symbol = irSymbol as IrSimpleFunctionSymbol,
          typeArgumentsCount = irSymbol.owner.typeParameters.size,
          valueArgumentsCount = irSymbol.owner.valueParameters.size
        )
      }
      is FakeCallableDescriptorForObject -> {
        val irSymbol = pluginContext.symbols.externalSymbolTable.referenceClass(classDescriptor)
        IrGetObjectValueImpl(
          startOffset = UNDEFINED_OFFSET,
          endOffset = UNDEFINED_OFFSET,
          type = irSymbol.owner.defaultType,
          symbol = irSymbol
        )
      }
      else -> {
        TODO("Unsupported ir call for $this")
      }
    }

  fun PropertyDescriptor.irGetterCall(): IrCall? {
    val irField = pluginContext.symbols.externalSymbolTable.referenceField(this)
    return irField.owner.correspondingPropertySymbol?.owner?.getter?.symbol?.let { irSimpleFunctionSymbol ->
      IrCallImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = irSimpleFunctionSymbol.owner.returnType,
        symbol = irSimpleFunctionSymbol,
        typeArgumentsCount = irSimpleFunctionSymbol.owner.typeParameters.size,
        valueArgumentsCount = irSimpleFunctionSymbol.owner.valueParameters.size
      )
    }
  }

  fun ClassDescriptor.irConstructorCall(): IrConstructorCall? {
    val irClass = pluginContext.symbols.externalSymbolTable.referenceClass(this)
    return irClass.constructors.firstOrNull()?.let { irConstructorSymbol ->
      IrConstructorCallImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = irConstructorSymbol.owner.returnType,
        symbol = irConstructorSymbol,
        typeArgumentsCount = irConstructorSymbol.owner.typeParameters.size,
        valueArgumentsCount = irConstructorSymbol.owner.valueParameters.size,
        constructorTypeArgumentsCount = declaredTypeParameters.size
      )
    }
  }

  fun CallableDescriptor.substitutedIrTypes(typeSubstitutor: NewTypeSubstitutorByConstructorMap): List<IrType?> =
    typeParameters.mapIndexed { _, typeParamDescriptor ->
      val newType = typeSubstitutor.map.entries.find {
        it.key.toString() == typeParamDescriptor.defaultType.toString()
      }
      newType?.value?.toIrType()
    }

  fun <A> IrFunction.transform(data: A, f: IrFunction.(a: A) -> Unit = Noop.effect2): IrStatement =
    transform(object : IrElementTransformer<A> {
      override fun visitFunction(declaration: IrFunction, data: A): IrStatement {
        f(declaration, data)
        return super.visitFunction(declaration, data)
      }
    }, data) as IrStatement

  @ExperimentalStdlibApi
  fun IrModuleFragment.interpret(expression: IrExpression): IrExpression =
    IrInterpreter(this).interpret(expression)
}

inline fun <reified E, B> IrElement.filterMap(
  crossinline filter: (E) -> Boolean,
  crossinline map: (E) -> B
): List<B> {
  val els = arrayListOf<B>()
  val visitor = object : IrElementVisitor<Unit, Unit> {
    override fun visitElement(element: IrElement, data: Unit) {
      if (element is E && filter(element)) {
        els.add(map(element))
      }
      element.acceptChildren(this, Unit)
    }
  }
  acceptChildren(visitor, Unit)
  return els
}

/**
 * returns the index and the value argument
 */
val IrCall.valueArguments: List<Pair<Int, IrExpression?>>
  get() {
    val args = arrayListOf<Pair<Int, IrExpression?>>()
    for (i in 0 until valueArgumentsCount) {
      args.add(i to getValueArgument(i))
    }
    return args.toList()
  }

/**
 * returns the index and the type argument
 */
val IrCall.typeArguments: List<Pair<Int, IrType?>>
  get() {
    val args = arrayListOf<Pair<Int, IrType?>>()
    for (i in 0 until typeArgumentsCount) {
      args.add(i to getTypeArgument(i))
    }
    return args.toList()
  }

val IrCall.substitutedValueParameters: List<Pair<IrValueParameter, IrType?>>
  get() = symbol.owner.substitutedValueParameters(this)

val IrTypeParametersContainer.allTypeParameters: List<IrTypeParameter>
  get() = if (this is IrConstructor)
    parentAsClass.typeParameters + typeParameters
  else
    typeParameters

fun IrMemberAccessExpression<*>.getTypeSubstitutionMap(container: IrTypeParametersContainer): Map<IrTypeParameter, IrType> =
  container.allTypeParameters.withIndex().associate {
    it.value to getTypeArgument(it.index)!!
  }

val IrMemberAccessExpression<*>.typeSubstitutions: Map<IrTypeParameter, IrType>
  get() = symbol.owner.safeAs<IrTypeParametersContainer>()?.let(::getTypeSubstitutionMap) ?: emptyMap()

/**
 * returns a Pair of the descriptor and it's substituted KotlinType at the call-site
 */
private fun IrSimpleFunction.substitutedValueParameters(call: IrCall): List<Pair<IrValueParameter, IrType?>> =
  valueParameters
    .map {
      val type = it.type
      it to (type.takeIf { t -> !t.isTypeParameter() }
        ?: typeParameters
          .firstOrNull { typeParam -> typeParam.defaultType == type }
          ?.let { typeParam ->
            call.getTypeArgument(typeParam.index)
          } ?: type // Could not resolve the substituted KotlinType
        )
    }

fun IrReturnTarget.returnType(irBuiltIns: IrBuiltIns) =
  when (this) {
    is IrConstructor -> irBuiltIns.unitType
    is IrFunction -> returnType
    is IrReturnableBlock -> type
    else -> error("Unknown ReturnTarget: $this")
  }
