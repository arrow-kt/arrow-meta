package arrow.meta.phases.codegen.ir

import arrow.meta.phases.CompilerContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.ConstantValueGenerator
import org.jetbrains.kotlin.ir.util.ReferenceSymbolTable
import org.jetbrains.kotlin.ir.util.TypeTranslator
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.calls.util.FakeCallableDescriptorForObject
import org.jetbrains.kotlin.resolve.descriptorUtil.module

class IrUtils(
  val pluginContext: IrPluginContext,
  val compilerContext: CompilerContext
) : ReferenceSymbolTable by pluginContext.symbols.externalSymbolTable {

  val typeTranslator: TypeTranslator =
    TypeTranslator(
      symbolTable = pluginContext.symbols.externalSymbolTable,
      languageVersionSettings = pluginContext.languageVersionSettings,
      builtIns = pluginContext.builtIns
    ).apply translator@{
      constantValueGenerator =
        ConstantValueGenerator(
          moduleDescriptor = pluginContext.builtIns.builtInsModule.module,
          symbolTable = pluginContext.symbols.externalSymbolTable
        ).apply {
          this.typeTranslator = this@translator
        }
    }

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
            typeArgumentsCount = irSimpleFunctionSymbol.owner.descriptor.typeParameters.size,
            valueArgumentsCount = irSimpleFunctionSymbol.owner.descriptor.valueParameters.size
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
          typeArgumentsCount = irSymbol.owner.descriptor.typeParameters.size,
          valueArgumentsCount = irSymbol.owner.descriptor.valueParameters.size,
          constructorTypeArgumentsCount = irSymbol.owner.descriptor.typeParameters.size
        )
      }
      is FunctionDescriptor -> {
        val irSymbol = pluginContext.symbols.externalSymbolTable.referenceFunction(this)
        IrCallImpl(
          startOffset = UNDEFINED_OFFSET,
          endOffset = UNDEFINED_OFFSET,
          type = irSymbol.owner.returnType,
          symbol = irSymbol,
          typeArgumentsCount = irSymbol.owner.descriptor.typeParameters.size,
          valueArgumentsCount = irSymbol.owner.descriptor.valueParameters.size
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
        typeArgumentsCount = irSimpleFunctionSymbol.owner.descriptor.typeParameters.size,
        valueArgumentsCount = irSimpleFunctionSymbol.owner.descriptor.valueParameters.size
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
        typeArgumentsCount = irConstructorSymbol.owner.descriptor.typeParameters.size,
        valueArgumentsCount = irConstructorSymbol.owner.descriptor.valueParameters.size,
        constructorTypeArgumentsCount = declaredTypeParameters.size
      )
    }
  }

  fun CallableDescriptor.substitutedIrTypes(typeSubstitutor: NewTypeSubstitutorByConstructorMap): List<IrType?> =
    typeParameters.mapIndexed { _, typeParamDescriptor ->
      val newType = typeSubstitutor.map.entries.find {
        it.key.toString() == typeParamDescriptor.defaultType.toString()
      }
      newType?.value?.let(typeTranslator::translateType)
    }

}

fun IrCall.dfsCalls(): List<IrCall> {
  val calls = arrayListOf<IrCall>()
  val recursiveVisitor = object : IrElementVisitor<Unit, Unit> {
    override fun visitElement(element: IrElement, data: Unit) {
      if (element is IrCall) {
        calls.addAll(element.dfsCalls())
      }
    }
  }
  acceptChildren(recursiveVisitor, Unit)
  calls.add(this)
  return calls
}

