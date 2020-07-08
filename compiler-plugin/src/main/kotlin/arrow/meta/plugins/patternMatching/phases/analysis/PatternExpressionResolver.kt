package arrow.meta.plugins.patternMatching.phases.analysis

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.traverseFilter
import arrow.meta.plugins.patternMatching.phases.analysis.PatternExpression.Companion.parsePatternExpression
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtWhenCondition
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

fun PatternResolutionContext.resolvePatternExpression(file: KtFile, resolution: PatternResolutionContext.(KtWhenExpression) -> List<PatternExpression>) =
  file.traverseFilter(KtWhenExpression::class.java) { resolution(it) }.flatten()

fun PatternResolutionContext.patternExpressionResolution(expression: KtWhenExpression): List<Pair<KtWhenEntry, PatternExpression>> =
  expression.entries.flatMap { entry ->
    // fixme: for now pattern is supposed to be in the form of `case(Constructor(param1, param2))`
    val matchingConditions = entry.conditions.mapNotNull { patternExpression(it) }
    assert(matchingConditions.size < 2) { "Cannot have multiple pattern expressions in one when entry" }
    matchingConditions.map { entry to it }
  }

private fun PatternResolutionContext.patternExpression(condition: KtWhenCondition): PatternExpression? =
  condition.children.find { callExpr ->
    callExpr is KtCallExpression
      && callExpr.getResolvedCall(bindingTrace.bindingContext)?.candidateDescriptor == caseDescriptor
  }?.let { parsePatternExpression(it as KtCallExpression) }

data class PatternExpression(
  val elementCall: KtCallExpression,
  val classDescriptor: ClassDescriptor,
  val wildcards: List<Parameter>,
  val captured: List<Parameter>,
  val other: List<KtExpression>
) {
  data class Parameter(
    val expr: KtNameReferenceExpression,
    val index: Int
  )

  override fun toString(): String =
    "PatternExpression(" +
      "elementCall=${elementCall.text}, " +
      "wildcards=$wildcards, " +
      "captured=${captured.map { it.expr.text }}, " +
      "other=${other.map { it.text }}" +
    ")"

  companion object {
    fun PatternResolutionContext.parsePatternExpression(caseCallExpr: KtCallExpression): PatternExpression? {
      val elementCall =
        when (val innerCall = caseCallExpr.valueArguments.first().getArgumentExpression()) {
          is KtCallExpression -> innerCall
          is KtDotQualifiedExpression -> innerCall.selectorExpression as? KtCallExpression
          else -> null
        }
      check(elementCall != null) { "Case should contain a call inside" }

      val wildcards = mutableListOf<Parameter>()
      val captured = mutableListOf<Parameter>()
      val other = mutableListOf<KtExpression>()

      elementCall.valueArguments.forEachIndexed { index, valueArgument ->
        when (val argExpression = valueArgument.getArgumentExpression()) {
          is KtNameReferenceExpression ->
            when {
              bindingTrace.getType(argExpression) == null -> {
                val param = Parameter(argExpression, index)
                when (argExpression.getReferencedName()) {
                  "_" -> wildcards.add(param)
                  else -> captured.add(param)
                }
              }
              else -> other.add(argExpression)
            }
          null -> {
            // ignore
          }
          else -> other.add(argExpression)
        }
      }

      val classDescriptor = bindingTrace.getType(elementCall)?.constructor?.declarationDescriptor as? ClassDescriptor
      check(classDescriptor != null) { "Element call should create a class" }

      return PatternExpression(
        elementCall = elementCall,
        classDescriptor = classDescriptor,
        wildcards = wildcards,
        captured = captured,
        other = other
      )
    }
  }
}

class PatternResolutionContext(
  val compilerContext: CompilerContext
) {
  val module = compilerContext.module!!
  val bindingTrace = compilerContext.componentProvider!!.get<BindingTrace>()

  val caseDescriptor by lazy {
    // similar to findClassAcrossDeps
    // todo: adjust to case function coordinate when it is introduced properly
    val pkg = module.getPackage(FqName.ROOT)
    pkg.fragments.mapNotNull { pkgFragment ->
      pkgFragment.getMemberScope().getContributedFunctions(
        Name.identifier("case"),
        NoLookupLocation.FROM_SYNTHETIC_SCOPE
      ).firstOrNull {
        it.valueParameters.size == 1
          && it.valueParameters.first().type == module.builtIns.nullableAnyType
          && it.returnType == module.builtIns.nullableAnyType
      }
    }.single()
  }

  val paramPlaceholder by lazy {
    val pkg = module.getPackage(FqName.ROOT)
    pkg.fragments.mapNotNull { pkgFragment ->
      pkgFragment.getMemberScope().getContributedVariables(
        Name.identifier("todo"),
        NoLookupLocation.FROM_SYNTHETIC_SCOPE
      ).firstOrNull()
    }.single()
  }
}
