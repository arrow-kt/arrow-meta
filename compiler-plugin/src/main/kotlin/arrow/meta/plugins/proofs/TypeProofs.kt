package arrow.meta.plugins.proofs

/*
Type Proofs are injective proofs from A -> B
Talk about Curry Howard Correspondence
 */

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.resolve.ProofVertex
import arrow.meta.phases.resolve.`isSubtypeOf(NewKotlinTypeChecker)`
import arrow.meta.phases.resolve.applySmartCast
import arrow.meta.phases.resolve.dump
import arrow.meta.phases.resolve.initializeProofCache
import arrow.meta.phases.resolve.intersection
import arrow.meta.phases.resolve.typeProofs
import arrow.meta.phases.resolve.typeProofsGraph
import arrow.meta.proofs.Proof
import arrow.meta.proofs.ProofTypeChecker
import arrow.meta.proofs.suppressConstantExpectedTypeMismatch
import arrow.meta.proofs.suppressProvenTypeMismatch
import arrow.meta.proofs.suppressUpperboundViolated
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.typeUtil.isNothing
import org.jgrapht.Graph

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs" {
      var proofGraph: Graph<ProofVertex, Proof>? = null
      meta(
        enableIr(),
        analysis(
          doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
            module.initializeProofCache()
            null
          },
          analysisCompleted = { project, module, bindingTrace, files ->
//            val calls = bindingTrace.bindingContext.getSliceContents(BindingContext.CALL)
//            module.typeProofs.forEach {
//              calls.forEach { ktElement, call ->
//                val resolvedCall = call.getResolvedCall(bindingTrace.bindingContext)
//                val callReturnType = resolvedCall?.getReturnType()
//                if (callReturnType != null && !callReturnType.isNothing() && !callReturnType.isError && callReturnType.`isSubtypeOf(NewKotlinTypeChecker)`(it.from)) {
//                  if (ktElement is KtExpression) {
//                    val intersection = it.to.intersection(callReturnType)
//                    println("Smart cast $call for ${ktElement.text} with $callReturnType type: $intersection")
//                    bindingTrace.applySmartCast(call, ktElement, intersection) //TODO apply this in the synth resolution instead as the type classes plugin does and same for the IDE
//                  }
//                }
//              }
//            }
            null
          }
        ),
        suppressDiagnostic { this.suppressProvenTypeMismatch(it, module.typeProofs) },
        suppressDiagnostic { this.suppressConstantExpectedTypeMismatch(it, module.typeProofs) },
        suppressDiagnostic { it.suppressUpperboundViolated(module.typeProofs) },
        typeChecker { ProofTypeChecker(this) },
        irVariable { insertProof(module.typeProofs, it) },
        irProperty { insertProof(module.typeProofs, it) },
        irReturn { insertProof(module.typeProofs, it) },
        irDump()
      )
    }

fun KtParameter.isNullable(): Boolean =
  typeReference?.typeElement is KtNullableType