package arrow.meta.ide.plugins.purity

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.intersectFunction
import arrow.meta.ide.dsl.utils.intersectProperty
import arrow.meta.ide.dsl.utils.replace
import arrow.meta.invoke
import arrow.meta.phases.analysis.resolveFunctionTypeEq
import arrow.meta.phases.analysis.typeConstructorEq
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.codegen.coroutines.isSuspendLambdaOrLocalFunction
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.util.nameIdentifierTextRangeInThis
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.createExpressionByPattern
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
        inspectionText = { f -> "Function: ${f.name} should be suspended" },
        applyTo = { f, project, editor ->
          f.addModifier(KtTokens.SUSPEND_KEYWORD)
        },
        isApplicable = { f: KtNamedFunction ->
          f.nameIdentifier != null && !f.hasModifier(KtTokens.SUSPEND_KEYWORD) &&
            f.resolveToDescriptorIfAny()?.run {
              !isSuspend && !isSynthesized && !isSuspendLambdaOrLocalFunction() &&
                intersectFunction(resolveFunctionTypeEq(), f) { impureTypes }.isNotEmpty()
            } == true
        },
        level = HighlightDisplayLevel.ERROR,
        groupPath = ArrowPath + "PurityPlugin"
      ),
      addApplicableInspection(
        defaultFixText = "ImpureProperty",
        inspectionHighlightType = { ProblemHighlightType.ERROR },
        kClass = KtProperty::class.java,
        highlightingRange = { prop -> prop.nameIdentifierTextRangeInThis() },
        inspectionText = { prop -> "Property: ${prop.name} has an impure initializer" },
        applyTo = { prop, project, editor ->
          // TODO: Find a refactoring strategy which works for some cases; for now just warn the user
          /*prop.initializer?.replace { body: KtExpression ->
              body.safeAs<KtLambdaExpression>()?.let {
                createExpressionByPattern("$1$0", it.functionLiteral, createIdentifier("suspend"))
              } ?: createExpressionByPattern("suspend { $0 }", body)
          }*/
        },
        isApplicable = { prop: KtProperty ->
          prop.resolveToDescriptorIfAny()?.run {
            intersectProperty(typeConstructorEq(), prop) { suspendedFunctionTypes }.isEmpty() &&
              intersectProperty(resolveFunctionTypeEq(), prop) { impureTypes }.isNotEmpty()
          } == true
        },
        level = HighlightDisplayLevel.ERROR,
        groupPath = ArrowPath + "PurityPlugin"
      )
    )
  }

private val KotlinBuiltIns.impureTypes: List<KotlinType>
  get() = listOf(unitType, nothingType, nullableNothingType)

/**
 * FunctionTypes have an arity of up to 42 https://kotlinlang.org/docs/reference/whatsnew13.html#functions-with-big-arity
 */
private val KotlinBuiltIns.suspendedFunctionTypes: List<KotlinType>
  get() = (0..42).toList().map { getSuspendFunction(it).defaultType }
