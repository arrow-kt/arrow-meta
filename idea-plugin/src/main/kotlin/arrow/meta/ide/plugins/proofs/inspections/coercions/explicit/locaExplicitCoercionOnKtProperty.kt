package arrow.meta.ide.plugins.proofs.inspections.coercions.explicit

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.explicit
import arrow.meta.ide.plugins.proofs.participatingTypes
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
    groupPath = ProofPath + arrayOf("Coercion"),
    groupDisplayName = "Coercion",
    level = HighlightDisplayLevel.WEAK_WARNING
  )

const val COERCION_EXPLICIT_PROP = "CoercionExplicitProp"

val IdeMetaPlugin.explicitCoercionKtProperty: AbstractApplicabilityBasedInspection<KtProperty>
  get() = applicableInspection(
    defaultFixText = COERCION_EXPLICIT_PROP,
    staticDescription = "Make coercion explicit for properties",
    fixText = { "Make coercion explicit" },
    inspectionHighlightType = { ProblemHighlightType.INFORMATION },
    kClass = KtProperty::class.java,
    inspectionText = { "Not used at the moment because the highlight type used is ProblemHighlightType.INFORMATION" },
    isApplicable = { ktProperty: KtProperty ->
      ktProperty.participatingTypes()?.let { (subtype: KotlinType, supertype: KotlinType) ->
        ktProperty.ctx().areTypesCoerced(subtype, supertype)
      } ?: false
    },
    applyTo = { ktProperty: KtProperty, _, _ ->
      ktProperty.ctx()?.explicit(ktProperty)
    }
  )
