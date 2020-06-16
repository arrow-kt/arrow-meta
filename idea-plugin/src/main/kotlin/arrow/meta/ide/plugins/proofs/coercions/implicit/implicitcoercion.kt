package arrow.meta.ide.plugins.proofs.coercions.implicit

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.coercions.implicitParticipatingTypes
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import org.celtric.kotlin.html.body
import org.celtric.kotlin.html.html
import org.celtric.kotlin.html.text
import org.jetbrains.kotlin.idea.KotlinQuickDocumentationProvider
import org.jetbrains.kotlin.idea.decompiler.navigation.SourceNavigationHelper
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.idea.kdoc.KDocRenderer
import org.jetbrains.kotlin.idea.kdoc.findKDoc
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression

/**
 * [localImplicitCoercion]: for explicit coercion to make it implicit
 */
val IdeMetaPlugin.localImplicitCoercion: ExtensionPhase
  get() = addLocalInspection(
    inspection = implicitCoercion,
    groupPath = ProofPath + arrayOf("Coercion"),
    groupDisplayName = "Coercion",
    level = HighlightDisplayLevel.WARNING
  )

const val IMPLICIT_COERCION_INSPECTION_ID = "CoercionImplicit"

val IdeMetaPlugin.implicitCoercion: AbstractApplicabilityBasedInspection<KtDotQualifiedExpression>
  get() = applicableInspection(
    defaultFixText = IMPLICIT_COERCION_INSPECTION_ID,
    staticDescription = "Make coercion implicit",
    inspectionHighlightType = { ProblemHighlightType.WARNING },
    kClass = KtDotQualifiedExpression::class.java,
    inspectionText = { ktCall: KtDotQualifiedExpression ->
      ktCall.implicitParticipatingTypes()?.let { (subtype, supertype) ->
        ktCall.ctx()?.coerceProof(subtype, supertype)?.let { proof ->
          html {
            body {
              text("Apply implicit coercion available by") +
                text(proof.through.containingDeclaration.findKDoc { proof.through.findPsi() }?.let { kDocTag: KDocTag ->
                  KDocRenderer.renderKDocContent(kDocTag)
                }.orEmpty()) +
                //declarationRenderer.render(proof.through)
                text(KotlinQuickDocumentationProvider().getQuickNavigateInfo(
                  proof.through.findPsi(),
                  SourceNavigationHelper.getNavigationElement((proof.through.findPsi() as KtDeclaration))
                ).orEmpty())
            }
          }.render()
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
