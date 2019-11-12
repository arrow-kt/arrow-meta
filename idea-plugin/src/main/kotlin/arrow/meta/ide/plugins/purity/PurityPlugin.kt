package arrow.meta.ide.plugins.purity

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.invoke
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.codegen.coroutines.isSuspendLambdaOrLocalFunction
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.core.dropBraces
import org.jetbrains.kotlin.idea.refactoring.changeSignature.getDeclarationBody
import org.jetbrains.kotlin.idea.refactoring.removeTemplateEntryBracesIfPossible
import org.jetbrains.kotlin.idea.util.nameIdentifierTextRangeInThis
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBlockStringTemplateEntry
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.createDeclarationByPattern
import org.jetbrains.kotlin.psi.createExpressionByPattern
import org.jetbrains.kotlin.psi.psiUtil.isLambdaOutsideParentheses
import org.jetbrains.kotlin.psi2ir.deparenthesize
import org.jetbrains.kotlin.resolve.calls.tower.isSynthesized
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.purity: Plugin
  get() = "PurityPlugin" {
    meta(
      addApplicableInspection(
        defaultFixText = "SuspendFunction",
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
              !isSuspend && !isSynthesized && !isSuspendLambdaOrLocalFunction() &&
                returns(f) { impureTypes }
            } == true
        },
        level = HighlightDisplayLevel.ERROR,
        groupPath = ArrowPath + "PurityPlugin"
      ),
      addApplicableInspection(
        defaultFixText = "SuspendProperty",
        inspectionHighlightType = { ProblemHighlightType.ERROR },
        kClass = KtProperty::class.java,
        highlightingRange = { prop -> prop.nameIdentifierTextRangeInThis() },
        inspectionText = { prop -> "Property:${prop.name} should be suspended" },
        applyTo = { prop, project, editor ->
          modify(prop) { p: KtProperty ->
            p.initializer?.safeAs<KtBlockStringTemplateEntry>()
              ?.dropBraces()?.safeAs<KtExpression>()?.let { body ->
              modify(body) { b ->
                createExpressionByPattern("suspend { $0 }", b)
              }.safeAs()
            }
          }
        },
        isApplicable = { prop: KtProperty ->
          prop.resolveToDescriptorIfAny()?.run {
            returns { impureTypes } && returns(prop) { impureTypes }
          } == true
        },
        level = HighlightDisplayLevel.ERROR,
        groupPath = ArrowPath + "PurityPlugin"
      )
    )
  }

private val KotlinBuiltIns.impureTypes: List<KotlinType>
  get() = listOf(unitType, nothingType, nullableNothingType)
