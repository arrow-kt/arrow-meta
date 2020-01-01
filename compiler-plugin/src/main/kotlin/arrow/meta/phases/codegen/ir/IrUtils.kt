package arrow.meta.phases.codegen.ir

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.resolve.unwrappedNotNullableType
import arrow.meta.proofs.GivenUpperBound
import arrow.meta.proofs.Proof
import arrow.meta.proofs.ProofCandidate
import arrow.meta.proofs.givenTypeParametersAndUpperbounds
import arrow.meta.proofs.matchingCandidates
import arrow.meta.proofs.typeSubstitutor
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.expressions.putValueArgument
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.types.toKotlinType
import org.jetbrains.kotlin.ir.util.ConstantValueGenerator
import org.jetbrains.kotlin.ir.util.ReferenceSymbolTable
import org.jetbrains.kotlin.ir.util.TypeTranslator
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
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
    ).apply translator@{
      constantValueGenerator =
        ConstantValueGenerator(
          moduleDescriptor = backendContext.ir.irModule.descriptor,
          symbolTable = backendContext.ir.symbols.externalSymbolTable
        ).apply {
          this.typeTranslator = this@translator
        }
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
    typeSubstitutor: NewTypeSubstitutorByConstructorMap
  ): IrCall {
    val irTypes = fn.substitutedIrTypes(typeSubstitutor)
    return fn.irCall().apply {
      irTypes.forEachIndexed(this::putTypeArgument)
    }
  }

  fun CompilerContext.proofCall(
    proofs: List<Proof>,
    subType: KotlinType,
    superType: KotlinType
  ): IrCall? {
    val matchingCandidates = proofs.matchingCandidates(this, subType, superType)
    val proofs = matchingCandidates.map { (from, to, conversion) ->
      proofCall(
        fn = conversion,
        typeSubstitutor = ProofCandidate(
          from = from,
          to = to,
          subType = subType.unwrappedNotNullableType,
          superType = superType.unwrappedNotNullableType,
          through = conversion
        ).typeSubstitutor
      )
    }
    return proofs.firstOrNull() //TODO handle ambiguity and orphan selection
  }

  fun CompilerContext.insertProof(proofs: List<Proof>, it: IrVariable): IrVariable? {
    val targetType = it.type.originalKotlinType
    val valueType = it.initializer?.type?.originalKotlinType
    return if (targetType != null && valueType != null) {
      it.apply {
        val proofCall = proofCall(proofs, valueType, targetType)
        proofCall?.extensionReceiver = initializer
        proofCall?.also {
          initializer = it
        }
      }
    } else it
  }

  fun CompilerContext.insertCallProofs(proofs: List<Proof>, expression: IrCall): IrCall? =
    expression.apply {
      dfsCalls().forEach {
        insertCallProof(it, proofs)
      }
    }

  private fun CompilerContext.insertCallProof(expression: IrCall, proofs: List<Proof>): IrCall =
    Log.Verbose({ "insertProof:\n ${expression.dump()} \nresult\n ${this.dump()}" }) {
      val givenTypeParamUpperBound: GivenUpperBound = expression.descriptor.givenTypeParametersAndUpperbounds()
      val upperBound = givenTypeParamUpperBound.givenUpperBound
      if (upperBound != null) {
        irExtensionCallIfGivenUpperbounds(givenTypeParamUpperBound, proofs, expression)
      } else {
        val valueType = expression.dispatchReceiver?.type?.toKotlinType()
          ?: expression.extensionReceiver?.type?.toKotlinType()
        val targetType =
          givenTypeParamUpperBound.givenUpperBound
            ?: expression.descriptor.dispatchReceiverParameter?.type
            ?: expression.descriptor.extensionReceiverParameter?.type
        if (targetType != null && valueType != null && targetType != valueType) {
          expression.apply {
            val proofCall = proofCall(proofs, valueType, targetType)
            when {
              proofCall != null -> {
                when {
                  this.dispatchReceiver != null -> {
                    proofCall.extensionReceiver = this.dispatchReceiver
                    proofCall.also {
                      dispatchReceiver = it
                      extensionReceiver = null
                    }
                  }
                  this.extensionReceiver != null -> {
                    proofCall.extensionReceiver = this.extensionReceiver
                    proofCall.also {
                      dispatchReceiver = null
                      extensionReceiver = it
                    }
                  }
                }
              }
            }
          }
        }
      }
      expression
    }

  private fun CompilerContext.irExtensionCallIfGivenUpperbounds(
    givenUpperBound: GivenUpperBound,
    proofs: List<Proof>,
    expression: IrCall
  ): Unit {
    val upperBound = givenUpperBound.givenUpperBound
    if (upperBound != null) {
      givenUpperBound.givenValueParameters.forEach { valueParameterDescriptor ->
        val superType = valueParameterDescriptor.type
        val candidateSubtype = superType.arguments.firstOrNull()?.type
        val maybeCompanion = (candidateSubtype?.constructor?.declarationDescriptor as? ClassDescriptor)?.companionObjectDescriptor
        if (maybeCompanion != null) {
          val extensionCall = proofCall(proofs, maybeCompanion.defaultType, superType)?.also {
            it.extensionReceiver = IrGetObjectValueImpl(
              UNDEFINED_OFFSET,
              UNDEFINED_OFFSET,
              typeTranslator.translateType(maybeCompanion.defaultType),
              backendContext.ir.symbols.externalSymbolTable.referenceClass(maybeCompanion)
            )
          }
          extensionCall?.apply {
            expression.putValueArgument(valueParameterDescriptor, this)
          }
        }
      }
    }
  }

  fun CompilerContext.insertProof(proofs: List<Proof>, it: IrProperty): IrProperty? {
    val targetType = it.descriptor.returnType
    val valueType = it.backingField?.initializer?.expression?.type?.originalKotlinType
    return if (targetType != null && valueType != null && targetType != valueType) {
      it.backingField?.let { field ->
        val replacement = field.initializer?.expression?.let {
          proofCall(proofs, valueType, targetType)?.apply {
            extensionReceiver = it
          }
        }
        replacement?.let { field.initializer?.expression = it }
        it
      }
    } else it
  }

  fun CompilerContext.insertProof(proofs: List<Proof>, it: IrReturn): IrReturn? {
    val targetType = it.returnTarget.returnType
    val valueType = it.value.type.originalKotlinType
    return if (targetType != null && valueType != null && targetType != valueType) {
      proofCall(proofs, valueType, targetType)?.let { call ->
        call.extensionReceiver = it.value
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

