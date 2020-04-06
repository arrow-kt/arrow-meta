package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement

fun IdeMetaPlugin.implicitCoercionIntention(compilerContext: CompilerContext): ExtensionPhase =
  addApplicableInspection(
    defaultFixText = "Make_coercion_implicit",
    kClass = KtElement::class.java,
    enabledByDefault = true,
    isApplicable = { ktCall: KtElement ->
      ktCall.implicitParticipatingTypes().any { (subtype, supertype) ->
        compilerContext.areTypesCoerced(subtype, supertype)
      }
    },
    applyTo = { ktCall: KtElement, _, _ ->
      when (ktCall) {
        is KtDotQualifiedExpression -> {
          ktCall.replace(ktCall.receiverExpression)
        }
      }
    },
    inspectionText = { "TODO impl" },
    inspectionHighlightType = { ProblemHighlightType.INFORMATION },
    groupPath = ArrowPath + arrayOf("Coercion")
  )
