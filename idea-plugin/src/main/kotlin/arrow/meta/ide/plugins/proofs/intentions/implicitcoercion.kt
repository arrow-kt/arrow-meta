package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.markers.coercionMessage
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

/**
 * [implicitCoercionIntention]: for explicit coercion to make it implicit
 */
val IdeMetaPlugin.implicitCoercionIntention: ExtensionPhase
  get() = addApplicableInspection(
    defaultFixText = "Make_coercion_implicit",
    kClass = KtDotQualifiedExpression::class.java,
    enabledByDefault = true,
    isApplicable = { ktCall: KtDotQualifiedExpression ->
      ktCall.ctx()?.let { compilerContext ->
        ktCall.implicitParticipatingTypes().any { (subtype, supertype) ->
          compilerContext.areTypesCoerced(subtype, supertype)
        }
      } ?: false
    },
    applyTo = { ktCall: KtDotQualifiedExpression, _, _ ->
      ktCall.replace(ktCall.receiverExpression)
    },
    inspectionText = { ktDotQualifiedExpression: KtDotQualifiedExpression ->
      // TODO: research ways to display this nicely and align it with [arrow.meta.ide.plugins.proofs.markers.CoercionKt.coerceProofLineMarker]
      val coercionMessage = ktDotQualifiedExpression.ctx()?.let { context ->
        ktDotQualifiedExpression.coercionProofMessage(context)
      }
      "Expression: ${ktDotQualifiedExpression.text} can be replaced for only its receiver because there is a $coercionMessage"
    },
    inspectionHighlightType = { ProblemHighlightType.WARNING },
    groupPath = ProofPath + arrayOf("Coercion")
  )

private fun KtDotQualifiedExpression.coercionProofMessage(ctx: CompilerContext): String =
  implicitParticipatingTypes().mapNotNull { (subtype, supertype) ->
    ctx.coerceProof(subtype, supertype)?.coercionMessage()
  }.firstOrNull() ?: "Proof not found"
