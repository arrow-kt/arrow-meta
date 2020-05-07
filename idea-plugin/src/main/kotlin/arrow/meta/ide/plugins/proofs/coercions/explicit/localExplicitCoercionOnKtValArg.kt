package arrow.meta.ide.plugins.proofs.coercions.explicit

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.coercions.explicit
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.ide.resources.MetaIdeBundle
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
    groupPath = ProofPath + arrayOf(MetaIdeBundle.message("proofs.coercion")),
    groupDisplayName = MetaIdeBundle.message("proofs.coercion"),
    level = HighlightDisplayLevel.WEAK_WARNING
  )

const val COERCION_EXPLICIT_ARGS = "CoercionExplicitArgs"

val IdeMetaPlugin.explicitCoercionKtValArg: AbstractApplicabilityBasedInspection<KtValueArgument>
  get() = applicableInspection(
    defaultFixText = COERCION_EXPLICIT_ARGS,
    staticDescription = MetaIdeBundle.message("proofs.coercions.inspection.explicit.args.static.description"),
    inspectionHighlightType = { ProblemHighlightType.INFORMATION },
    kClass = KtValueArgument::class.java,
    inspectionText = { MetaIdeBundle.message("proofs.coercions.inspection.explicit.property.inspection.text") },
    isApplicable = { ktCall: KtValueArgument ->
      ktCall.participatingTypes()?.let { (subtype: KotlinType, supertype: KotlinType) ->
        ktCall.ctx().areTypesCoerced(subtype, supertype)
      } ?: false
    },
    applyTo = { ktCall: KtValueArgument, _, _ ->
      ktCall.ctx()?.explicit(ktCall)
    }
  )