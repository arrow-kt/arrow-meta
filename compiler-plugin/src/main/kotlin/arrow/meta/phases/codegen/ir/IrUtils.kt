package arrow.meta.phases.codegen.ir

import arrow.meta.phases.CompilerContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.ConstantValueGenerator
import org.jetbrains.kotlin.ir.util.ReferenceSymbolTable
import org.jetbrains.kotlin.ir.util.TypeTranslator
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap

class IrUtils(
    val pluginContext: IrPluginContext,
    val compilerContext: CompilerContext
) : ReferenceSymbolTable by pluginContext.symbolTable {

    val typeTranslator: TypeTranslator =
        TypeTranslator(
            symbolTable = pluginContext.symbolTable,
            languageVersionSettings = pluginContext.languageVersionSettings,
            builtIns = pluginContext.builtIns
        ).apply {
            constantValueGenerator =
                ConstantValueGenerator(
                    moduleDescriptor = pluginContext.moduleDescriptor,
                    symbolTable = pluginContext.symbolTable
                )
        }

    fun FunctionDescriptor.irCall(): IrCall {
        val irFunctionSymbol = pluginContext.symbolTable.referenceFunction(this)
        return IrCallImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = irFunctionSymbol.owner.returnType,
            symbol = irFunctionSymbol,
            descriptor = irFunctionSymbol.descriptor,
            typeArgumentsCount = irFunctionSymbol.owner.descriptor.typeParameters.size,
            valueArgumentsCount = irFunctionSymbol.owner.descriptor.valueParameters.size
        )
    }

    fun PropertyDescriptor.irGetterCall(): IrCall? {
        val irField = pluginContext.symbolTable.referenceField(this)
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
        val irClass = pluginContext.symbolTable.referenceClass(this)
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

    fun FunctionDescriptor.substitutedIrTypes(typeSubstitutor: NewTypeSubstitutorByConstructorMap): List<IrType?> =
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

