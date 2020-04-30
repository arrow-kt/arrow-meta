package arrow.meta.ide.plugins.proofs.inspections

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.IdeSyntax
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.psi.KtValueArgument

/**
 * [explicitCoercionArgumentInspection]: for implicit coercion on arguments to make them explicit
 */
val IdeMetaPlugin.explicitCoercionArgumentInspection: ExtensionPhase
  get() = addLocalInspection(
    inspection = explicitCoercionKtValueArgument,
    level = HighlightDisplayLevel.WEAK_WARNING,
    groupPath = ProofPath + arrayOf("Coercion")
  )

const val COERCION_EXPLICIT_ARGS = "Coercion_explicit_args"

val IdeSyntax.explicitCoercionKtValueArgument: AbstractApplicabilityBasedInspection<KtValueArgument>
  get() = applicableInspection(
    defaultFixText = COERCION_EXPLICIT_ARGS,
    inspectionHighlightType = { ProblemHighlightType.INFORMATION },
    kClass = KtValueArgument::class.java,
    inspectionText = { "Not used at the moment because the highlight type used is ProblemHighlightType.INFORMATION" },
    isApplicable = { ktCall: KtValueArgument ->
      ktCall.ctx()?.let { compilerContext ->
        ktCall.participatingTypes()?.let { (subtype, supertype) ->
          compilerContext.areTypesCoerced(subtype, supertype)
        }
      } ?: false
    },
    applyTo = { ktCall: KtValueArgument, _, _ ->
      ktCall.ctx()?.let { compilerContext ->
        ktCall.explicit(compilerContext)
      }
    }
  )
