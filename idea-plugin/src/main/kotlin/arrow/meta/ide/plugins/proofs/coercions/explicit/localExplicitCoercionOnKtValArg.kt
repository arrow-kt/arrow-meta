package arrow.meta.ide.plugins.proofs.coercions.explicit

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.coercions.explicit
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.psi.KtValueArgument

/**
 * [localExplicitCoercionOnKtValArg]: adds an explict call for implicit coercions on arguments
 */
val IdeMetaPlugin.localExplicitCoercionOnKtValArg: ExtensionPhase
  get() = addLocalInspection(
    inspection = explicitCoercionKtValArg,
    level = HighlightDisplayLevel.WEAK_WARNING,
    groupPath = ProofPath + arrayOf("Coercion")
  )
const val COERCION_EXPLICIT_ARGS = "Coercion_explicit_args"
val IdeMetaPlugin.explicitCoercionKtValArg: AbstractApplicabilityBasedInspection<KtValueArgument>
  get() = applicableInspection(
    defaultFixText = COERCION_EXPLICIT_ARGS,
    inspectionHighlightType = { ProblemHighlightType.INFORMATION },
    kClass = KtValueArgument::class.java,
    inspectionText = { "Not used at the moment because the highlight type used is ProblemHighlightType.INFORMATION" },
    isApplicable = { ktCall: KtValueArgument ->
      ktCall.participatingTypes()?.let { (subtype, supertype) ->
        ktCall.ctx()?.areTypesCoerced(subtype, supertype)
      } ?: false
    },
    applyTo = { ktCall: KtValueArgument, _, _ ->
      ktCall.ctx()?.explicit(ktCall)
    }
  )