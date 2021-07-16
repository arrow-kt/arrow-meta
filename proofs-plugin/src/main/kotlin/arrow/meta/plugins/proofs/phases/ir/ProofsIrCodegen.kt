package arrow.meta.plugins.proofs.phases.ir

import arrow.meta.Meta
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.phases.codegen.ir.filterMap
import arrow.meta.phases.codegen.ir.substitutedValueParameters
import arrow.meta.phases.codegen.ir.typeArguments
import arrow.meta.phases.codegen.ir.valueArguments
import arrow.meta.phases.resolve.typeArgumentsMap
import arrow.meta.phases.resolve.unwrappedNotNullableType
import arrow.meta.plugins.proofs.phases.ArrowCompileTime
import arrow.meta.plugins.proofs.phases.GivenProof
import arrow.meta.plugins.proofs.phases.contextualAnnotations
import arrow.meta.plugins.proofs.phases.givenProof
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.descriptors.toIrBasedDescriptor
import org.jetbrains.kotlin.ir.descriptors.toIrBasedKotlinType
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.copyTypeArgumentsFrom
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.deepCopyWithSymbols
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.patchDeclarationParents
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.FqName
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
          putTypeArgument(n, irTypes.getOrElse(n) { pluginContext.irBuiltIns.nothingType })
        }
        fn.valueParameters.forEachIndexed { n, descriptor ->
          val contextFqName = descriptor.contextualAnnotations().firstOrNull()
          if (contextFqName != null) {
            val argProof = this@matchedCandidateProofCall.compilerContext.givenProofCall(
              contextFqName,
              irTypes.getOrElse(n) { pluginContext.irBuiltIns.nothingType }.toIrBasedKotlinType()
            )
            if (argProof != null)
              putValueArgument(n, argProof)
          }
        }
      }
    }
  }

  fun CompilerContext.givenProofCall(
    context: FqName,
    superType: KotlinType
  ): IrExpression? =
    irUtils.run {
      val candidate = givenProof(context, superType)
      candidate?.givenProof?.let { proof ->
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


  fun CompilerContext.proveNestedCalls(expression: IrCall): IrMemberAccessExpression<*> =
    proveCall(expression)

  private fun CompilerContext.proveCall(expression: IrCall): IrMemberAccessExpression<*> =
    Log.Verbose({ "insertProof:\n ${expression.dump()} \nresult\n ${this.dump()}" }) {
      if (expression.symbol.owner.annotations.hasAnnotation(ArrowCompileTime)) {
        insertGivenCall(expression)
      } else expression
    }

  private fun CompilerContext.insertGivenCall(
    expression: IrCall
  ): IrMemberAccessExpression<*> {
    val replacement: IrMemberAccessExpression<*>? = expression.replacementCall()
    return if (replacement != null) {
      expression.substitutedValueParameters.forEachIndexed { index, (param, superType) ->
        processValueParameter(param, superType, replacement, index)
      }
      replacement
    } else expression
  }

  private fun CompilerContext.processValueParameter(
    param: IrValueParameter,
    superType: IrType?,
    replacement: IrMemberAccessExpression<*>?,
    index: Int
  ) {
    val contextFqName = param.toIrBasedDescriptor().contextualAnnotations().firstOrNull()
    val type = superType?.originalKotlinType
    if (contextFqName != null && type != null) {
      givenProofCall(contextFqName, type)?.apply {
        if (this is IrCall) {
          symbol.owner.valueParameters.forEachIndexed { n, param ->
            processValueParameter(param, param.type, this, n)
          }
        }
        //todo we need to recursively place over this expression inductive steps
        if (replacement != null && replacement.valueArgumentsCount > index)
          replacement.putValueArgument(index, this)
      }
    }
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

internal fun IrCall.replacementCall(): IrMemberAccessExpression<*>? =
  symbol.owner.body?.statements?.firstOrNull()
    ?.filterMap<IrMemberAccessExpression<*>, IrMemberAccessExpression<*>>({ true }) {
      it
    }?.firstOrNull()?.run {
      val rep = deepCopyWithSymbols(this@replacementCall.symbol.owner)
      this@replacementCall.typeArguments.forEach { (n, arg) ->
        if (rep.typeArgumentsCount > n && arg != null)
          rep.putTypeArgument(n, arg)
      }
      this@replacementCall.valueArguments.forEach { (n, arg) ->
        if (rep.valueArgumentsCount > n && arg != null)
          rep.putValueArgument(n, arg)
      }

      //rep.extensionReceiver = this@replacementCall.extensionReceiver
      rep.dispatchReceiver = this@replacementCall.extensionReceiver
      rep
    }