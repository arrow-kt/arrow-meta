package arrow.meta.phases.codegen.ir

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.resolve.unwrappedNotNullableType
import arrow.meta.proofs.Proof
import arrow.meta.proofs.ProofCandidate
import arrow.meta.proofs.matchingCandidates
import arrow.meta.proofs.typeSubstitutor
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.ConstantValueGenerator
import org.jetbrains.kotlin.ir.util.ReferenceSymbolTable
import org.jetbrains.kotlin.ir.util.TypeTranslator
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.types.KotlinType

class IrUtils(
  val backendContext: BackendContext,
  val compilerContext: CompilerContext
) : ReferenceSymbolTable by backendContext.ir.symbols.externalSymbolTable {

  val typeTranslator: TypeTranslator =
    TypeTranslator(
      symbolTable = backendContext.ir.symbols.externalSymbolTable,
      languageVersionSettings = backendContext.irBuiltIns.languageVersionSettings,
      builtIns = backendContext.builtIns
    ).apply {
      constantValueGenerator =
        ConstantValueGenerator(
          moduleDescriptor = backendContext.ir.irModule.descriptor,
          symbolTable = backendContext.ir.symbols.externalSymbolTable
        )
    }

  fun IrFunctionAccessExpression.defaultValues(): List<String> =
    symbol.descriptor.valueParameters
      .mapNotNull { it.findPsi() as? KtParameter }
      .mapNotNull { it.defaultValue?.text }

  fun FunctionDescriptor.irCall(): IrCall {
    val irFunctionSymbol = backendContext.ir.symbols.externalSymbolTable.referenceFunction(this)
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
    val irField = backendContext.ir.symbols.externalSymbolTable.referenceField(this)
    return irField.owner.correspondingPropertySymbol?.owner?.getter?.symbol?.let { irSimpleFunctionSymbol ->
      IrCallImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = irSimpleFunctionSymbol.owner.returnType,
        symbol = irSimpleFunctionSymbol,
        descriptor = irSimpleFunctionSymbol.owner.descriptor,
        typeArgumentsCount = irSimpleFunctionSymbol.owner.descriptor.typeParameters.size,
        valueArgumentsCount = irSimpleFunctionSymbol.owner.descriptor.valueParameters.size
      )
    }
  }

  fun ClassDescriptor.irConstructorCall(): IrConstructorCall? {
    val irClass = backendContext.ir.symbols.externalSymbolTable.referenceClass(this)
    return irClass.constructors.firstOrNull()?.let { irConstructorSymbol ->
      IrConstructorCallImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = irConstructorSymbol.owner.returnType,
        symbol = irConstructorSymbol,
        descriptor = irConstructorSymbol.owner.descriptor,
        typeArgumentsCount = irConstructorSymbol.owner.descriptor.typeParameters.size,
        valueArgumentsCount = irConstructorSymbol.owner.descriptor.valueParameters.size,
        constructorTypeArgumentsCount = declaredTypeParameters.size
      )
    }
  }

  fun FunctionDescriptor.substitutedIrTypes(
    typeSubstitutor: NewTypeSubstitutorByConstructorMap
  ): List<IrType?> =
    typeParameters.mapIndexed { n, typeParamDescriptor ->
      val newType = typeSubstitutor.map.entries.find {
        it.key.toString() == typeParamDescriptor.defaultType.toString()
      }
      newType?.value?.let(typeTranslator::translateType)
    }

  fun proofCall(
    fn: FunctionDescriptor,
    typeSubstitutor: NewTypeSubstitutorByConstructorMap,
    initializer: IrExpression?
  ): IrCall {
    val irTypes = fn.substitutedIrTypes(typeSubstitutor)
    return fn.irCall().apply {
      extensionReceiver = initializer
      irTypes.forEachIndexed(this::putTypeArgument)
    }
  }

  fun proofCall(
    proofs: List<Proof>,
    subType: KotlinType,
    superType: KotlinType,
    initializer: IrExpression?
  ): IrCall? {
    val matchingCandidates = proofs.matchingCandidates(subType, superType)
    val proofs = matchingCandidates.map { (from, to, conversion) ->
      proofCall(
        fn = conversion,
        typeSubstitutor = ProofCandidate(
          from = from,
          to = to,
          subType = subType.unwrappedNotNullableType,
          superType = superType.unwrappedNotNullableType,
          through = conversion
        ).typeSubstitutor,
        initializer = initializer
      )
    }
    return proofs.firstOrNull() //TODO handle ambiguity and orphan selection
  }

  fun insertProof(proofs: List<Proof>, it: IrVariable): IrVariable? {
    val targetType = it.type.originalKotlinType
    val valueType = it.initializer?.type?.originalKotlinType
    return if (targetType != null && valueType != null) {
      it.apply {
        initializer = proofCall(proofs, valueType, targetType, initializer) ?: initializer
      }
    } else it
  }

  fun insertProof(proofs: List<Proof>, it: IrProperty): IrProperty? {
    val targetType = it.descriptor.returnType
    val valueType = it.backingField?.initializer?.expression?.type?.originalKotlinType
    return if (targetType != null && valueType != null) {
      it.backingField?.let { field ->
        val replacement = field.initializer?.expression?.let {
          proofCall(proofs, valueType, targetType, it)
        }
        replacement?.let { field.initializer?.expression = it }
        it
      }
    } else it
  }

  fun insertProof(proofs: List<Proof>, it: IrReturn): IrReturn? {
    val targetType = it.returnTarget.returnType
    val valueType = it.value.type.originalKotlinType
    return if (targetType != null && valueType != null) {
      proofCall(proofs, valueType, targetType, it.value)?.let { call ->
        IrReturnImpl(
          UNDEFINED_OFFSET,
          UNDEFINED_OFFSET,
          typeTranslator.translateType(targetType),
          it.returnTargetSymbol,
          call
        )
      } ?: it
    } else it
  }

}