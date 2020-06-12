package arrow.meta.ide.plugins.proofs.coercions.implicit

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.coercions.implicitParticipatingTypes
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
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
      ktCall.implicitParticipatingTypes()?.let { (subtype, supertype) ->
        ktCall.ctx()?.coerceProof(subtype, supertype)?.let {
          "Apply implicit coercion available by ${it.through}"
        }
      } ?: "Proof not found"
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
