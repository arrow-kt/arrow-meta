package arrow.meta.plugins.patternMatching.phases.analysis

import arrow.meta.plugins.patternMatching.phases.analysis.PatternExpression.Param.Captured
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingContext.CALL
import org.jetbrains.kotlin.resolve.BindingContext.EXPRESSION_TYPE_INFO
import org.jetbrains.kotlin.resolve.BindingContext.LEXICAL_SCOPE
import org.jetbrains.kotlin.resolve.BindingContext.PROCESSED
import org.jetbrains.kotlin.resolve.BindingContext.REFERENCE_TARGET
import org.jetbrains.kotlin.resolve.BindingContext.RESOLVED_CALL
import org.jetbrains.kotlin.resolve.DelegatingBindingTrace
import org.jetbrains.kotlin.resolve.calls.components.InferenceSession
import org.jetbrains.kotlin.resolve.calls.model.DataFlowInfoForArgumentsImpl
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCallImpl
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.tasks.ExplicitReceiverKind
import org.jetbrains.kotlin.resolve.calls.tasks.ResolutionCandidate
import org.jetbrains.kotlin.resolve.calls.tasks.TracingStrategy
import org.jetbrains.kotlin.resolve.calls.util.CallMaker
import org.jetbrains.kotlin.resolve.lazy.FileScopeProvider
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.expressions.ExpressionTypingServices
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo
import org.jetbrains.kotlin.util.slicedMap.ReadOnlySlice
import org.jetbrains.kotlin.util.slicedMap.WritableSlice

fun PatternResolutionContext.fillCapturedParameters(entry: KtWhenEntry, pattern: PatternExpression): List<KtSimpleNameExpression> {
  val entryBody = entry.expression!!
  val trace = TransientBindingTrace(bindingTrace.bindingContext)
  val filledParams = mutableListOf<KtSimpleNameExpression>()
  val fileScopeProvider = compilerContext.componentProvider!!.get<FileScopeProvider>()
  val typingServices = compilerContext.componentProvider!!.get<ExpressionTypingServices>()

  entryBody.accept(
    object : KtTreeVisitorVoid() {
      override fun visitExpression(expr: KtExpression) {
        if (expr !is KtNameReferenceExpression || pattern.capturedParameterIndex(expr) == -1) {
          trace.clear(PROCESSED, expr)
          trace.clear(CALL, expr)
          trace.clear(EXPRESSION_TYPE_INFO, expr)
          if (expr is KtReferenceExpression) {
            trace.clear(REFERENCE_TARGET, expr)
          }
          super.visitExpression(expr)
        }
      }

      override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        if (trace.getType(expression) == null) {
          val param = resolveBodyParameter(expression, pattern)
          if (param != null) {
            filledParams += param
          }
        }
        super.visitSimpleNameExpression(expression)
      }
    }
  )

  var scope: LexicalScope? = null
  var node: KtElement = entry.psiOrParent
  while (scope == null) {
    scope = bindingTrace[LEXICAL_SCOPE, node]
    println("Searching for scope in $node")
    node = node.parent as KtElement

    if (node is KtFile) {
      scope = fileScopeProvider.getFileResolutionScope(node)
    }
  }

  typingServices.getTypeInfo(
    scope,
    entryBody,
    TypeUtils.NO_EXPECTED_TYPE,
    DataFlowInfo.EMPTY,
    InferenceSession.default,
    trace,
    false
  )

  trace.addOwnDataTo(bindingTrace)

  return filledParams
}

private fun PatternExpression.capturedParameterIndex(expr: KtSimpleNameExpression): Int {
  var resultIndex = -1

  // todo: be smarter
  for (i in parameters.indices) {
    val argumentExpr = paramExpression(i)
    val param = parameters[i]

    val condition =
      param is Captured
      && !param.isWildcard
      && argumentExpr is KtSimpleNameExpression
      && argumentExpr.getReferencedName() == expr.getReferencedName()
    if (condition) {
      resultIndex = i
      break
    }
  }

  return resultIndex
}

fun PatternResolutionContext.resolveBodyParameter(
  paramExpr: KtSimpleNameExpression,
  pattern: PatternExpression
): KtSimpleNameExpression? {
  val paramIndex = pattern.capturedParameterIndex(paramExpr)
  if (paramIndex == -1) return null

  val paramType = pattern.constructor.valueParameters[paramIndex].returnType
  bindingTrace.record(EXPRESSION_TYPE_INFO, paramExpr, KotlinTypeInfo(paramType, DataFlowInfo.EMPTY))

  referPlaceholder(paramExpr, paramIndex)

  return paramExpr
}

fun PatternResolutionContext.referPlaceholder(
  expression: KtExpression,
  index: Int
) {
  val call = CallMaker.makeCall(
    expression,
    null,
    null,
    expression,
    emptyList()
  )
  val candidateCall = ResolvedCallImpl.create(
    ResolutionCandidate.create(
      call,
      PlaceholderPropertyDescriptor(
        index,
        expression,
        bindingTrace,
        bindingTrace.getType(expression) ?: module.builtIns.nothingType
      ),
      null,
      ExplicitReceiverKind.DISPATCH_RECEIVER,
      null
    ),
    DelegatingBindingTrace(bindingTrace.bindingContext, "Synthetic"),
    TracingStrategy.EMPTY,
    DataFlowInfoForArgumentsImpl(DataFlowInfo.EMPTY, call)
  )

  bindingTrace.record(CALL, expression, call)
  bindingTrace.record(RESOLVED_CALL, call, candidateCall)
}

private class TransientBindingTrace(
  bindingContext: BindingContext
) : DelegatingBindingTrace(bindingContext, name = "test", allowSliceRewrite = true) {
  private val cleared = hashMapOf<ReadOnlySlice<*, *>, MutableSet<Any?>>()

  override fun <K, V> get(slice: ReadOnlySlice<K, V>, key: K): V? {
    println("requested $slice and ${key.processed()}")
    println("cleared contains slice = ${cleared[slice] != null}")
    if (cleared[slice]?.contains(key) == true) {
      println("return null")
      println()
      return null
    }
    val superValue = super.get(slice, key)
    println("return super for $slice and $key with value of ${superValue.processed()}")
    println()
    return superValue
  }

  override fun <K, V> record(slice: WritableSlice<K, V>, key: K, value: V) {
    println("recording ${value.processed()} for $slice and ${key.processed()}")
    println()
    cleared[slice]?.remove(key)
    super.record(slice, key, value)
  }

  override fun <K> record(slice: WritableSlice<K, Boolean>, key: K) {
    println("recording boolean for $slice and ${key.processed()}")
    println()
    cleared[slice]?.remove(key)
    super.record(slice, key)
  }

  fun <K, V> clear(slice: ReadOnlySlice<K, V>, key: K) {
    println("cleared $slice for ${key.processed()}")
    println()
    cleared.getOrPut(slice) { hashSetOf() }.add(key)
  }

  private fun Any?.processed() =
    when (this) {
      is PsiElement -> text
      is KotlinTypeInfo -> type?.toString()
      else -> toString()
    }
}

