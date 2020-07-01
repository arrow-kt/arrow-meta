package arrow.meta.ide.plugins.proofs.inspections.coercions.explicit

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.explicit
import arrow.meta.ide.plugins.proofs.participatingTypes
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.types.KotlinType

/**
 * [localExplicitCoercionOnKtValArg]: adds an explict call for implicit coercions on arguments
 */
val IdeMetaPlugin.localExplicitCoercionOnKtValArg: ExtensionPhase
  get() = addLocalInspection(
    inspection = explicitCoercionKtValArg,
    groupPath = ProofPath + arrayOf("Coercion"),
    groupDisplayName = "Coercion",
    level = HighlightDisplayLevel.WEAK_WARNING
  )
const val COERCION_EXPLICIT_ARGS = "CoercionExplicitArgs"
val IdeMetaPlugin.explicitCoercionKtValArg: AbstractApplicabilityBasedInspection<KtValueArgument>
  get() = applicableInspection(
    defaultFixText = COERCION_EXPLICIT_ARGS,
    staticDescription = "Make coercion explicit for arguments",
    inspectionHighlightType = { ProblemHighlightType.INFORMATION },
    kClass = KtValueArgument::class.java,
    inspectionText = { "Not used at the moment because the highlight type used is ProblemHighlightType.INFORMATION" },
    isApplicable = { ktCall: KtValueArgument ->
      ktCall.participatingTypes()?.let { (subtype: KotlinType, supertype: KotlinType) ->
        ktCall.ctx().areTypesCoerced(subtype, supertype)
      } ?: false
    },
    applyTo = { ktCall: KtValueArgument, _, _ ->
      ktCall.ctx()?.explicit(ktCall)
    }
  )