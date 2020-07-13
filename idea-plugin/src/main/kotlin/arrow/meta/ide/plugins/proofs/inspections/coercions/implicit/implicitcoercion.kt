package arrow.meta.ide.plugins.proofs.inspections.coercions.implicit

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.ctx
import arrow.meta.ide.plugins.proofs.implicitParticipatingTypes
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import org.celtric.kotlin.html.body
import org.celtric.kotlin.html.html
import org.celtric.kotlin.html.text
import org.jetbrains.kotlin.idea.KotlinQuickDocumentationProvider
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression

class CoercionImplicit : AbstractApplicabilityBasedInspection<KtDotQualifiedExpression>(KtDotQualifiedExpression::class.java) {

  override val defaultFixText: String
    get() = IMPLICIT_COERCION_INSPECTION_ID

  override fun applyTo(element: KtDotQualifiedExpression, project: Project, editor: Editor?) {
    element.replace(element.receiverExpression)
  }

  override fun inspectionText(element: KtDotQualifiedExpression): String =
    element.implicitParticipatingTypes()?.let { (subtype, supertype) ->
      element.ctx()?.coerceProof(subtype, supertype)?.let { proof ->
        proof.through.findPsi()?.let { proofPsi ->
          html {
            body {
              text("Apply implicit coercion available by") +
                text(KotlinQuickDocumentationProvider().generateDoc(proofPsi, element).orEmpty())
            }
          }.render()
        }
      }
    } ?: "Proof not found"

  override fun isApplicable(element: KtDotQualifiedExpression): Boolean =
    (element.parent !is KtSafeQualifiedExpression) &&
      element.implicitParticipatingTypes()?.let { (subtype, supertype) ->
        element.ctx().areTypesCoerced(subtype, supertype)
      } ?: false

  override fun inspectionHighlightType(element: KtDotQualifiedExpression): ProblemHighlightType =
    ProblemHighlightType.WARNING

  override fun inspectionHighlightRangeInElement(element: KtDotQualifiedExpression): TextRange? =
    null

  override fun getStaticDescription(): String? = "Make coercion implicit"

  override fun fixText(element: KtDotQualifiedExpression): String =
    "Make coercion implicit"
}

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
    fixText = { "Make coercion implicit" },
    inspectionHighlightType = { ProblemHighlightType.WARNING },
    kClass = KtDotQualifiedExpression::class.java,
    inspectionText = { ktCall: KtDotQualifiedExpression ->
      ktCall.implicitParticipatingTypes()?.let { (subtype, supertype) ->
        ktCall.ctx()?.coerceProof(subtype, supertype)?.let { proof ->
          proof.through.findPsi()?.let { proofPsi ->
            html {
              body {
                text("Apply implicit coercion available by") +
                  text(KotlinQuickDocumentationProvider().generateDoc(proofPsi, ktCall).orEmpty())
              }
            }.render()
          }
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
