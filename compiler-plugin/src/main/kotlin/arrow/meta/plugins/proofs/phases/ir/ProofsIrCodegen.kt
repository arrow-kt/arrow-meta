package arrow.meta.plugins.proofs.phases.ir

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.phases.codegen.ir.dfsCalls
import arrow.meta.phases.resolve.typeArgumentsMap
import arrow.meta.phases.resolve.unwrappedNotNullableType
import arrow.meta.plugins.proofs.phases.resolve.GivenUpperBound
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.resolve.ProofCandidate
import arrow.meta.plugins.proofs.phases.resolve.matchingCandidates
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.expressions.putValueArgument
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.types.toKotlinType
import org.jetbrains.kotlin.ir.util.TypeTranslator
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

class ProofsIrCodegen(
  val irUtils: IrUtils
) {

  fun IrUtils.matchedCandidateProofCall(
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
  ): IrCall? =
    irUtils.run {
      val matchingCandidates = proofs.matchingCandidates(this@proofCall, subType, superType)
      val proofs = matchingCandidates.map { (from, to, conversion) ->
        matchedCandidateProofCall(
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
      proofs.firstOrNull() //TODO handle ambiguity and orphan selection
    }


  fun CompilerContext.proveVariable(proofs: List<Proof>, it: IrVariable): IrVariable? {
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

  fun CompilerContext.proveNestedCalls(proofs: List<Proof>, expression: IrCall): IrCall? =
    expression.apply {
      dfsCalls().forEach {
        proveCall(it, proofs)
      }
    }

  private fun CompilerContext.proveCall(expression: IrCall, proofs: List<Proof>): IrCall =
    Log.Verbose({ "insertProof:\n ${expression.dump()} \nresult\n ${this.dump()}" }) {
      val givenTypeParamUpperBound = GivenUpperBound(expression.descriptor)
      val upperBound = givenTypeParamUpperBound.givenUpperBound
      if (upperBound != null) insertExtensionGivenCall(givenTypeParamUpperBound, proofs, expression)
      else insertExtensionSyntaxCall(expression, proofs)
      expression
    }

  private fun CompilerContext.insertExtensionSyntaxCall(expression: IrCall, proofs: List<Proof>) {
    val valueType = expression.dispatchReceiver?.type?.toKotlinType()
      ?: expression.extensionReceiver?.type?.toKotlinType()
    val targetType =
      expression.descriptor.dispatchReceiverParameter?.type
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

  private fun CompilerContext.insertExtensionGivenCall(
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
            val companionType = irUtils.typeTranslator.translateType(maybeCompanion.defaultType)
            val companionClass = irUtils.backendContext.ir.symbols.externalSymbolTable.referenceClass(maybeCompanion)
            it.extensionReceiver = companionCall(companionType, companionClass)
          }
          extensionCall?.apply {
            expression.putValueArgument(valueParameterDescriptor, this)
          }
        }
      }
    }
  }

  fun companionCall(companionType: IrType, companionClass: IrClassSymbol): IrGetObjectValueImpl =
    IrGetObjectValueImpl(
      UNDEFINED_OFFSET,
      UNDEFINED_OFFSET,
      companionType,
      companionClass
    )

  fun CompilerContext.proveProperty(proofs: List<Proof>, it: IrProperty): IrProperty? {
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

  fun CompilerContext.proveReturn(proofs: List<Proof>, it: IrReturn): IrReturn? {
    val targetType = it.returnTarget.returnType
    val valueType = it.value.type.originalKotlinType
    return if (targetType != null && valueType != null && targetType != valueType) {
      proofCall(proofs, valueType, targetType)?.let { call ->
        call.extensionReceiver = it.value
        IrReturnImpl(
          UNDEFINED_OFFSET,
          UNDEFINED_OFFSET,
          irUtils.typeTranslator.translateType(targetType),
          it.returnTargetSymbol,
          call
        )
      } ?: it
    } else it
  }

  companion object {
    operator fun <A> invoke(irUtils: IrUtils, f: ProofsIrCodegen.() -> A): A =
      f(ProofsIrCodegen(irUtils))
  }
}

val ProofCandidate.typeSubstitutor: NewTypeSubstitutorByConstructorMap
  get() {
    val allArgsMap =
      from.typeArgumentsMap(subType)
        .filter { it.key.type.isTypeParameter() } +
        mapOf(
          through.module.builtIns.nothingType.asTypeProjection() to TypeUtils.DONT_CARE.asTypeProjection()
        )
    return NewTypeSubstitutorByConstructorMap(
      allArgsMap.map {
        it.key.type.constructor to it.value.type.unwrap()
      }.toMap()
    )


  }