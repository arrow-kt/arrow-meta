package arrow.meta.ide.plugins.proofs.coercions.implicit

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.external.ui.tooltip.util.applyMetaStyles
import arrow.meta.ide.plugins.proofs.coercions.coercionProofMessage
import arrow.meta.ide.plugins.proofs.coercions.implicitParticipatingTypes
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression

/**
 * [localImplicitCoercion]: for explicit coercion to make it implicit
 */
val IdeMetaPlugin.localImplicitCoercion: ExtensionPhase
  get() = addLocalInspection(
    inspection = implicitCoercion,
    level = HighlightDisplayLevel.WARNING,
    groupPath = ProofPath + arrayOf("Coercion")
  )

const val IMPLICIT_COERCION_INSPECTION_ID = "Make_coercion_implicit"

val IdeMetaPlugin.implicitCoercion: AbstractApplicabilityBasedInspection<KtDotQualifiedExpression>
  get() = applicableInspection(
    defaultFixText = IMPLICIT_COERCION_INSPECTION_ID,
    inspectionHighlightType = { ProblemHighlightType.WARNING },
    kClass = KtDotQualifiedExpression::class.java,
    inspectionText = { ktCall: KtDotQualifiedExpression ->
      // TODO: research ways to display this nicely and align it with [arrow.meta.ide.plugins.proofs.markers.CoercionKt.coerceProofLineMarker]
      val coercionMessage = ktCall.ctx()?.coercionProofMessage(ktCall)
      "Expression: ${ktCall.text} can be replaced for only its receiver because there is a $coercionMessage".applyMetaStyles()
    },
    isApplicable = { ktCall: KtDotQualifiedExpression ->
      (ktCall.parent !is KtSafeQualifiedExpression) &&
        ktCall.implicitParticipatingTypes()?.let { (subtype, supertype) ->
          ktCall.ctx().areTypesCoerced(subtype, supertype)
        } ?: false
    },
    applyTo = { ktCall: KtDotQualifiedExpression, _, _ ->
      ktCall.replace(ktCall.receiverExpression)
    }
  )
