package arrow.meta.ide.plugins.purity

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.inspection.ExtendedReturnsCheck
import arrow.meta.invoke
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.codegen.coroutines.isSuspendLambdaOrLocalFunction
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.util.nameIdentifierTextRangeInThis
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.calls.tower.isSynthesized
import org.jetbrains.kotlin.util.ReturnsCheck

val IdeMetaPlugin.purity: Plugin
  get() = "PurityPlugin" {
    meta(
      addApplicableInspection(
        defaultFixText = "Suspend",
        inspectionHighlightType = { ProblemHighlightType.ERROR },
        kClass = KtNamedFunction::class.java,
        highlightingRange = { f -> f.nameIdentifierTextRangeInThis() },
        inspectionText = { f -> "Function should be suspended" },
        applyTo = { f, project, editor ->
          f.addModifier(KtTokens.SUSPEND_KEYWORD)
        },
        isApplicable = { f: KtNamedFunction ->
          f.nameIdentifier != null && !f.hasModifier(KtTokens.SUSPEND_KEYWORD) &&
            f.resolveToDescriptorIfAny()?.run {
              !isSuspend && !isSynthesized && !isSuspendLambdaOrLocalFunction() && (ReturnsCheck.ReturnsUnit.check(this) || ExtendedReturnsCheck.ReturnsNothing.check(this)
                || ExtendedReturnsCheck.ReturnsNullableNothing.check(this))
            } == true
        },
        level = HighlightDisplayLevel.ERROR,
        groupPath = ArrowPath + "PurityPlugin"
      )
    )
  }