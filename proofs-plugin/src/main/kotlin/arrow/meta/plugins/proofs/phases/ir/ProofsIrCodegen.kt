package arrow.meta.plugins.proofs.phases.ir

import arrow.meta.Meta
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.phases.codegen.ir.filterMap
import arrow.meta.phases.codegen.ir.substitutedValueParameters
import arrow.meta.phases.codegen.ir.valueArguments
import arrow.meta.phases.resolve.typeArgumentsMap
import arrow.meta.phases.resolve.unwrappedNotNullableType
import arrow.meta.plugins.proofs.phases.ArrowCompileTime
import arrow.meta.plugins.proofs.phases.GivenProof
import arrow.meta.plugins.proofs.phases.givenProof
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.copyTypeArgumentsFrom
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.deepCopyWithSymbols
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.patchDeclarationParents
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

@Suppress("RedundantUnitReturnType")
class ProofsIrCodegen(
  val irUtils: IrUtils
) {

  fun IrUtils.matchedCandidateProofCall(
    fn: CallableDescriptor,
    typeSubstitutor: NewTypeSubstitutorByConstructorMap
  ): IrExpression {
    val irTypes = fn.substitutedIrTypes(typeSubstitutor).filterNotNull()
    return fn.irCall().apply {
      if (this is IrMemberAccessExpression<*>) {
        fn.typeParameters.forEachIndexed { n, descriptor ->
          //TODO determine why sometimes type susbtitution returns unbound type args. Ex: fun <A> SecondN<FirstN<A>>.flatten(): Second<A>
          putTypeArgument(n, irTypes.getOrElse(n) { pluginContext.irBuiltIns.nothingType })
        }
      }
    }
  }

  fun CompilerContext.givenProofCall(
    superType: KotlinType
  ): IrExpression? =
    irUtils.run {
      val candidate = givenProof(superType)
      candidate?.let { proof ->
        substitutedProofCall(proof, superType)
      }
    }

  private fun IrUtils.substitutedProofCall(proof: GivenProof, superType: KotlinType): IrExpression? =
    matchedCandidateProofCall(
      fn = proof.callableDescriptor,
      typeSubstitutor = proof.substitutor(superType)
    )

  fun GivenProof.substitutor(
    superType: KotlinType
  ): NewTypeSubstitutorByConstructorMap =
    ProofCandidate(
      proofType = to,
      otherType = superType.unwrappedNotNullableType,
      through = through
    ).typeSubstitutor


  fun CompilerContext.proveNestedCalls(expression: IrCall): IrCall =
    proveCall(expression)

  private fun CompilerContext.proveCall(expression: IrCall): IrCall =
    Log.Verbose({ "insertProof:\n ${expression.dump()} \nresult\n ${this.dump()}" }) {
      if (expression.symbol.owner.annotations.hasAnnotation(ArrowCompileTime)) {
        insertGivenCall(expression)
      } else expression
    }

  private fun CompilerContext.insertGivenCall(
    expression: IrCall
  ): IrCall {
    val replacement: IrCall? = expression.replacementCall()
    return if (replacement != null) {
      expression.substitutedValueParameters.forEachIndexed { index, (_, superType) ->
        givenProofCall(superType?.originalKotlinType!!)?.apply {
          if (replacement.getValueArgument(index) != null)
            replacement.putValueArgument(index, this)
        }
      }
      replacement.patchDeclarationParents()
    } else expression
  }

  companion object {
    operator fun <A> invoke(irUtils: IrUtils, f: ProofsIrCodegen.() -> A): A =
      f(ProofsIrCodegen(irUtils))
  }
}

internal fun Meta.removeCompileTimeDeclarations() = irModuleFragment {
  it.files.forEach { file ->
    file.declarations.removeIf { it.annotations.hasAnnotation(ArrowCompileTime) }
  }
  null
}

val ProofCandidate.typeSubstitutor: NewTypeSubstitutorByConstructorMap
  get() {
    val allArgsMap =
      proofType.typeArgumentsMap(otherType)
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

internal fun IrCall.replacementCall(): IrCall? =
  symbol.owner.body?.statements?.firstOrNull()?.filterMap<IrCall, IrCall>({ true }) {
    it
  }?.firstOrNull()?.run {
    copyTypeArgumentsFrom(this)
    this@replacementCall.valueArguments.forEach { (n, arg) ->
      if (valueArgumentsCount > n && arg != null)
        putValueArgument(n, arg)
    }
    deepCopyWithSymbols(this@replacementCall.symbol.owner)
  }