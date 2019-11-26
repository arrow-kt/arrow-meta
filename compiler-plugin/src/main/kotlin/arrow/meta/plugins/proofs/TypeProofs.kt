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
import arrow.meta.phases.resolve.intersection
import arrow.meta.phases.resolve.typeProofs
import arrow.meta.phases.resolve.typeProofsGraph
import arrow.meta.plugins.comprehensions.isBinding
import arrow.meta.proofs.Proof
import arrow.meta.proofs.ProofTypeChecker
import arrow.meta.proofs.dump
import arrow.meta.proofs.hasProof
import arrow.meta.proofs.matchingCandidates
import arrow.meta.proofs.shortestPath
import arrow.meta.proofs.suppressProvenTypeMismatch
import arrow.meta.proofs.suppressUpperboundViolated
import org.jetbrains.kotlin.diagnostics.Errors.UNRESOLVED_REFERENCE_WRONG_RECEIVER
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.typeUtil.isNothing
import org.jetbrains.kotlin.utils.addToStdlib.cast
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import org.jgrapht.Graph

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs" {
      var proofGraph: Graph<ProofVertex, Proof>? = null
      meta(
        enableIr(),
        analysis(
          doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
            proofGraph = module.typeProofsGraph
            proofGraph?.dump()
            null
          }
        ),
        analysis(
          doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
            null
          },
          analysisCompleted = { project, module, bindingTrace, files ->
            val calls = bindingTrace.bindingContext.getSliceContents(BindingContext.CALL)
            module.typeProofs.forEach {
              calls.forEach { ktElement, call ->
                val resolvedCall = call.getResolvedCall(bindingTrace.bindingContext)
                val callReturnType = resolvedCall?.getReturnType()
                if (callReturnType != null && !callReturnType.isNothing() && !callReturnType.isError && callReturnType.`isSubtypeOf(NewKotlinTypeChecker)`(it.from)) {
                  if (ktElement is KtExpression) {
                    val intersection = it.to.intersection(callReturnType)
                    println("Smart cast $call for ${ktElement.text} with $callReturnType type: $intersection")
                    bindingTrace.applySmartCast(call, ktElement, intersection) //TODO apply this in the synth resolution instead as the type classes plugin does and same for the IDE
                  }
                }
              }
            }
            null
          }
        ),
        suppressDiagnostic { it.suppressProvenTypeMismatch(module.typeProofs) },
        suppressDiagnostic { it.suppressUpperboundViolated(module.typeProofs) },
        suppressDiagnostic {
          val proofs = module.typeProofs
          val factory = it.factory
          if (factory == UNRESOLVED_REFERENCE_WRONG_RECEIVER) {
            UNRESOLVED_REFERENCE_WRONG_RECEIVER.cast(it).let {
              val calls: List<ResolvedCall<*>> = it.a.toList()
              calls.forEach {
                val from = it.getReturnType()
                val to = it.extensionReceiver?.type
                if (to != null) {
                  val hasProof = proofs.hasProof(from, to)
                  if (proofGraph != null) {
                    val path = proofGraph?.shortestPath(from, to)
                    if (path != null)
                      println("Found chained path: $path")
                  }
                  println("hasProof [$from -> $to] : $hasProof")
                  if (hasProof) {
                    val candidates = proofs.matchingCandidates(from, to)
                    candidates.dump()
                  }
                }
              }
            }
          }
          true
        },
        suppressDiagnosticWithTrace {
          val proofs = module.typeProofs
          val parent = it.psiElement.parent
          if (parent is KtCallExpression) {
            val call = bindingContext.get(BindingContext.CALL, it.psiElement.cast())
            val resolvedCall = call?.getResolvedCall(bindingContext)
            val from = resolvedCall?.getReturnType()
            val to = call?.explicitReceiver?.safeAs<ExpressionReceiver>()?.type
            if (from != null && to != null) {
              val hasProof = proofs.hasProof(from, to)
              if (proofGraph != null) {
                val path = proofGraph?.shortestPath(from, to)
                println("Found chained path: $path")
              }
              println("hasProof [$from -> $to] : $hasProof")
              if (hasProof) {
                val candidates = proofs.matchingCandidates(from, to)
                candidates.dump()
              }
            }
            println("$it: [${it.psiElement.text}], resolvedCall: $resolvedCall")
          }
          false
        },
        typeChecker { ProofTypeChecker(this) },
        irVariable { insertProof(module.typeProofs, it) },
        irProperty { insertProof(module.typeProofs, it) },
        irReturn { insertProof(module.typeProofs, it) },
        irDump()
      )
    }


