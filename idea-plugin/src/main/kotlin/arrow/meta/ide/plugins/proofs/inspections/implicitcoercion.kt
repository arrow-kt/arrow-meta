package arrow.meta.ide.plugins.proofs.inspections

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.IdeSyntax
import arrow.meta.ide.plugins.proofs.inspections.PairTypes.Companion.pairOrNull
import arrow.meta.ide.plugins.proofs.markers.coercionMessage
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression

/**
 * [implicitCoercionInspection]: for explicit coercion to make it implicit
 */
val IdeMetaPlugin.implicitCoercionInspection: ExtensionPhase
  get() = addLocalInspection(
    inspection = implicitCoercion,
    level = HighlightDisplayLevel.WARNING,
    groupPath = ProofPath + arrayOf("Coercion")
  )

const val IMPLICIT_COERCION_INSPECTION_ID = "Make_coercion_implicit"

val IdeSyntax.implicitCoercion: AbstractApplicabilityBasedInspection<KtDotQualifiedExpression>
  get() = applicableInspection(
    defaultFixText = IMPLICIT_COERCION_INSPECTION_ID,
    inspectionHighlightType = { ProblemHighlightType.WARNING },
    kClass = KtDotQualifiedExpression::class.java,
    inspectionText = { ktDotQualifiedExpression: KtDotQualifiedExpression ->
      // TODO: research ways to display this nicely and align it with [arrow.meta.ide.plugins.proofs.markers.CoercionKt.coerceProofLineMarker]
      val coercionMessage = ktDotQualifiedExpression.ctx()?.let { context ->
        ktDotQualifiedExpression.coercionProofMessage(context)
      }
      "Expression: ${ktDotQualifiedExpression.text} can be replaced for only its receiver because there is a $coercionMessage"
    },
    isApplicable = { ktCall: KtDotQualifiedExpression ->
      (ktCall.parent !is KtSafeQualifiedExpression) && ktCall.ctx()?.let { compilerContext ->
        ktCall.implicitParticipatingTypes().any { (subtype, supertype) ->
          compilerContext.areTypesCoerced(subtype, supertype)
        }
      } ?: false
    },
    applyTo = { ktCall: KtDotQualifiedExpression, _, _ ->
      ktCall.replace(ktCall.receiverExpression)
    }
  )

private fun KtDotQualifiedExpression.implicitParticipatingTypes(): List<PairTypes> =
  listOfNotNull(receiverExpression.resolveKotlinType() pairOrNull selectorExpression?.resolveKotlinType())

private fun KtDotQualifiedExpression.coercionProofMessage(ctx: CompilerContext): String =
  implicitParticipatingTypes().mapNotNull { (subtype, supertype) ->
    ctx.coerceProof(subtype, supertype)?.coercionMessage()
  }.firstOrNull() ?: "Proof not found"
