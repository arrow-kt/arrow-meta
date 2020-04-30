package arrow.meta.ide.plugins.proofs.inspections

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.IdeSyntax
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.psi.KtProperty

/**
 * [explicitCoercionPropertyInspection]: for implicit coercion on properties to make them explicit
 */
val IdeMetaPlugin.explicitCoercionPropertyInspection: ExtensionPhase
  get() = addLocalInspection(
    inspection = explicitCoercionKtProperty,
    level = HighlightDisplayLevel.WEAK_WARNING,
    groupPath = ProofPath + arrayOf("Coercion")
  )

const val COERCION_EXPLICIT_PROP = "Coercion_explicit_prop"

val IdeSyntax.explicitCoercionKtProperty: AbstractApplicabilityBasedInspection<KtProperty>
  get() = applicableInspection(
    defaultFixText = COERCION_EXPLICIT_PROP,
    inspectionHighlightType = { ProblemHighlightType.INFORMATION },
    kClass = KtProperty::class.java,
    inspectionText = { "Not used at the moment because the highlight type used is ProblemHighlightType.INFORMATION" },
    isApplicable = { ktCall: KtProperty ->
      ktCall.ctx()?.let { compilerContext ->
        ktCall.participatingTypes()?.let { (subtype, supertype) ->
          compilerContext.areTypesCoerced(subtype, supertype)
        }
      } ?: false
    },
    applyTo = { ktCall: KtProperty, _, _ ->
      ktCall.ctx()?.let { compilerContext ->
        ktCall.explicit(compilerContext)
      }
    }
  )
