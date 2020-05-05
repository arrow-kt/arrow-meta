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
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.types.KotlinType

/**
 * [localExplicitCoercionOnKtProperty]: adds an explict call for implicit coercions on properties
 */
val IdeMetaPlugin.localExplicitCoercionOnKtProperty: ExtensionPhase
  get() = addLocalInspection(
    inspection = explicitCoercionKtProperty,
    groupPath = ProofPath + arrayOf(MetaIdeBundle.message("proofs.coercion")),
    groupDisplayName = MetaIdeBundle.message("proofs.coercion"),
    level = HighlightDisplayLevel.WEAK_WARNING
  )

const val COERCION_EXPLICIT_PROP = "CoercionExplicitProp"

val IdeMetaPlugin.explicitCoercionKtProperty: AbstractApplicabilityBasedInspection<KtProperty>
  get() = applicableInspection(
    defaultFixText = COERCION_EXPLICIT_PROP,
    staticDescription = MetaIdeBundle.message("proofs.coercions.explicit.property.static.description"),
    inspectionHighlightType = { ProblemHighlightType.INFORMATION },
    kClass = KtProperty::class.java,
    inspectionText = { MetaIdeBundle.message("proofs.coercions.explicit.property.inspection.text") },
    isApplicable = { ktCall: KtProperty ->
      ktCall.participatingTypes()?.let { (subtype: KotlinType, supertype: KotlinType) ->
        ktCall.ctx().areTypesCoerced(subtype, supertype)
      } ?: false
    },
    applyTo = { ktCall: KtProperty, _, _ ->
      ktCall.ctx()?.explicit(ktCall)
    }
  )
