package arrow.meta.ide.plugins.purity

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.dsl.utils.intersectFunction
import arrow.meta.ide.invoke
import arrow.meta.phases.analysis.returnTypeEq
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.codegen.coroutines.isSuspendLambdaOrLocalFunction
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.idea.util.nameIdentifierTextRangeInThis
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.calls.tower.isSynthesized
import org.jetbrains.kotlin.types.KotlinType

val IdeMetaPlugin.purity: IdePlugin
  get() = "PurityPlugin" {
    meta(
      addLocalInspection(
        inspection = purityInspection,
        level = HighlightDisplayLevel.ERROR,
        groupPath = ArrowPath + "PurityPlugin"
      )
    )
  }

val IdeMetaPlugin.purityInspection: AbstractApplicabilityBasedInspection<KtNamedFunction>
  get() = applicableInspection(
    defaultFixText = "Suspend",
    inspectionHighlightType = { ProblemHighlightType.ERROR },
    kClass = KtNamedFunction::class.java,
    highlightingRange = { f -> f.nameIdentifierTextRangeInThis() },
    inspectionText = { f -> "Function: ${f.name} should be suspended" },
    applyTo = { f, project, editor ->
      f.addModifier(KtTokens.SUSPEND_KEYWORD)
    },
    isApplicable = { f: KtNamedFunction ->
      f.nameIdentifier != null && !f.hasModifier(KtTokens.SUSPEND_KEYWORD) &&
        f.resolveToDescriptorIfAny()?.run {
          !isSuspend && !isSynthesized && !isSuspendLambdaOrLocalFunction() &&
            intersectFunction(returnTypeEq, f) { impureTypes }.isNotEmpty()
        } == true
    }
  )

private val KotlinBuiltIns.impureTypes: List<KotlinType>
  get() = listOf(unitType, nothingType, nullableNothingType)