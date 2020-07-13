package arrow.meta.ide.plugins.proofs.inspections.coercions.explicit

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.ctx
import arrow.meta.ide.plugins.proofs.explicit
import arrow.meta.ide.plugins.proofs.participatingTypes
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.types.KotlinType

class CoercionExplicitProperty : AbstractApplicabilityBasedInspection<KtProperty>(KtProperty::class.java) {

  override val defaultFixText: String
    get() = COERCION_EXPLICIT_PROP

  override fun applyTo(element: KtProperty, project: Project, editor: Editor?) {
    element.ctx()?.explicit(element)
  }

  override fun inspectionText(element: KtProperty): String =
    "Not used at the moment because the highlight type used is ProblemHighlightType.INFORMATION"

  override fun isApplicable(element: KtProperty): Boolean =
    element.participatingTypes()?.let { (subtype: KotlinType, supertype: KotlinType) ->
      element.ctx().areTypesCoerced(subtype, supertype)
    } ?: false

  override fun inspectionHighlightType(element: KtProperty): ProblemHighlightType =
    ProblemHighlightType.INFORMATION

  override fun inspectionHighlightRangeInElement(element: KtProperty): TextRange? =
    null

  override fun getStaticDescription(): String? = "Make coercion explicit for properties"

  override fun fixText(element: KtProperty): String =
    "Make coercion explicit"
}

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
    isApplicable = { ktCall: KtProperty ->
      ktCall.participatingTypes()?.let { (subtype: KotlinType, supertype: KotlinType) ->
        ktCall.ctx().areTypesCoerced(subtype, supertype)
      } ?: false
    },
    applyTo = { ktCall: KtProperty, _, _ ->
      ktCall.ctx()?.explicit(ktCall)
    }
  )
