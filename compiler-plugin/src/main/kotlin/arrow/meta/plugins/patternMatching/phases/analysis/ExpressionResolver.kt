package arrow.meta.plugins.patternMatching.phases.analysis

import org.jetbrains.kotlin.backend.common.descriptors.propertyIfAccessor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.KtWhenCondition
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DelegatingBindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.getCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.components.InferenceSession
import org.jetbrains.kotlin.resolve.calls.model.DataFlowInfoForArgumentsImpl
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCallImpl
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.tasks.ExplicitReceiverKind
import org.jetbrains.kotlin.resolve.calls.tasks.ResolutionCandidate
import org.jetbrains.kotlin.resolve.calls.tasks.TracingStrategy
import org.jetbrains.kotlin.resolve.calls.util.CallMaker
import org.jetbrains.kotlin.resolve.lazy.FileScopeProvider
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.expressions.ExpressionTypingServices
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo
import org.jetbrains.kotlin.util.slicedMap.ReadOnlySlice
import org.jetbrains.kotlin.util.slicedMap.WritableSlice

fun BindingTrace.resolvePatternExpression(resolution: (BindingTrace) -> Unit) =
  resolution(this)

fun BindingTrace.wildcards(
  typingServices: ExpressionTypingServices,
  fileScopeProvider: FileScopeProvider
) {
  val entries = wildcardTypeInfoEntries
  entries.forEach { (expr, _) ->
    expr as KtSimpleNameExpression
    if (expr.getParentOfType<KtWhenCondition>(false, KtWhenEntry::class.java) != null) {
      // condition
      resolveParamInCondition(expr)
    } else {
      // body
      resolveParamInBody(expr)

      val trace = object : DelegatingBindingTrace(bindingContext, name = "test", allowSliceRewrite = true) {
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

      val parent = expr.getParentOfType<KtWhenEntry>(true)!!
      val visitor = object : KtTreeVisitorVoid() {
        override fun visitExpression(expression: KtExpression) {
          if (entries.none { it.key == expression }) {
            trace.clear(BindingContext.PROCESSED, expression)
            trace.clear(BindingContext.CALL, expression)
            trace.clear(BindingContext.EXPRESSION_TYPE_INFO, expression)
            if (expression is KtReferenceExpression) {
              trace.clear(BindingContext.REFERENCE_TARGET, expression)
            }
            super.visitExpression(expression)
          }
        }
      }

      parent.accept(visitor)

      var scope: LexicalScope? = null
      var node: KtElement = parent
      while (scope == null) {
        scope = bindingContext[BindingContext.LEXICAL_SCOPE, node]
        println("Searching for scope in $node")
        node = node.parent as KtElement

        if (node is KtFile) {
          scope = fileScopeProvider.getFileResolutionScope(node)
        }
      }

      val type = typingServices.getTypeInfo(
        scope,
        parent.children.first { it is KtExpression } as KtExpression,
        TypeUtils.NO_EXPECTED_TYPE,
        DataFlowInfo.EMPTY,
        InferenceSession.default,
        trace,
        false
      )

      trace.addOwnDataTo(this)
      println(type.type)
    }
  }
}

private fun BindingTrace.resolveParamInCondition(expr: KtSimpleNameExpression) {
  // descriptor of subject of when expression
  val subject = whenSubject(expr)
  val subjDescriptor = bindingContext[BindingContext.REFERENCE_TARGET, subject]!! as CallableDescriptor
  val subjClass = subjDescriptor.returnType!!.constructor.declarationDescriptor as ClassDescriptor
  // Property which matches underscore
  val targetProperty = subjClass.targetProperty(expr)

  record(BindingContext.EXPRESSION_TYPE_INFO, expr, KotlinTypeInfo(targetProperty.type, DataFlowInfo.EMPTY))
  resolveParam(
    expr,
    subject,
    subjDescriptor,
    targetProperty
  )
}

private fun BindingTrace.resolveParamInBody(expr: KtSimpleNameExpression) {
  val whenEntry = expr.getParentOfType<KtWhenEntry>(false)!!
  val condition = whenEntry.conditions.first()
  val definition = condition.findDescendantOfType<KtNameReferenceExpression> { it.getReferencedName() == expr.getReferencedName() }!!

  val (call, resolvedCall) =
    if (definition.getResolvedCall(bindingContext) != null) {
      Pair(definition.getCall(bindingContext)!!, definition.getResolvedCall(bindingContext)!!)
    } else {
      // condition is not resolved, so do it first
      resolveParamInCondition(definition)
      Pair(definition.getCall(bindingContext)!!, definition.getResolvedCall(bindingContext)!!)
    }
  resolvedCall as ResolvedCallImpl<*>

  val clonedCall = call.clone()
  val clonedResolvedCall = resolvedCall.clone(bindingContext, clonedCall)
  record(BindingContext.EXPRESSION_TYPE_INFO, expr, KotlinTypeInfo(resolvedCall.candidateDescriptor.returnType, DataFlowInfo.EMPTY))
  record(BindingContext.CALL, expr, clonedCall)
  record(BindingContext.RESOLVED_CALL, clonedCall, clonedResolvedCall)
}

private val BindingTrace.wildcardTypeInfoEntries: List<MutableMap.MutableEntry<KtExpression, KotlinTypeInfo>>
  get() = bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO).entries
    .filter { it.value.type == null && (it.key.text == "_" || it.key.text.startsWith("captured")) }

private fun whenSubject(expr: KtExpression): KtReferenceExpression {
  val whenExpr = expr.getParentOfType<KtWhenExpression>(false)!!
  return whenExpr.subjectExpression!! as KtReferenceExpression
}

private fun ClassDescriptor.targetProperty(expr: KtExpression): PropertyDescriptor {
  val valueArgument = expr.parent as KtValueArgument
  val valueArgumentList = valueArgument.parent as KtValueArgumentList
  val paramIndex = valueArgumentList.arguments.indexOf(valueArgument)

  // getting nth property which gets replaced with underscore
  val param = unsubstitutedPrimaryConstructor!!.valueParameters[paramIndex].propertyIfAccessor

  return unsubstitutedMemberScope.getContributedVariables(
    param.name,
    NoLookupLocation.FROM_TEST
  ).first()
}

private fun BindingTrace.resolveParam(
  expr: KtExpression,
  subject: KtReferenceExpression,
  subjectDescriptor: CallableDescriptor,
  targetPropertyDescriptor: PropertyDescriptor
) {
  // FIXME here I am expecting a lot

  // adding call to underscore reference with person.(name of nth property)
  val origCall = subject.getCall(bindingContext)!!
  val call = origCall.clone()
  val receiver = ExpressionReceiver.create(
    subject,
    subjectDescriptor.returnType!!,
    bindingContext
  )
  val candidateCall = ResolvedCallImpl.create(
    ResolutionCandidate.create(
      call,
      targetPropertyDescriptor,
      receiver,
      ExplicitReceiverKind.DISPATCH_RECEIVER,
      null
    ),
    DelegatingBindingTrace(bindingContext, "Synthetic"),
    TracingStrategy.EMPTY,
    DataFlowInfoForArgumentsImpl(DataFlowInfo.EMPTY, call)
  )

  record(BindingContext.EXPRESSION_TYPE_INFO, expr, KotlinTypeInfo(subjectDescriptor.returnType!!, DataFlowInfo.EMPTY))
  record(BindingContext.CALL, expr, call)
  record(BindingContext.RESOLVED_CALL, call, candidateCall)
}

private fun Call.clone(): Call =
  CallMaker.makeCall(
    callElement,
    explicitReceiver,
    callOperationNode,
    calleeExpression,
    valueArguments,
    callType
  )

private fun <T : CallableDescriptor> ResolvedCallImpl<T>.clone(bindingContext: BindingContext, newCall: Call): ResolvedCall<T> =
  ResolvedCallImpl.create(
    ResolutionCandidate.create(
      newCall,
      resultingDescriptor,
      dispatchReceiver,
      ExplicitReceiverKind.DISPATCH_RECEIVER,
      null
    ),
    DelegatingBindingTrace(bindingContext, "Synthetic"),
    TracingStrategy.EMPTY,
    DataFlowInfoForArgumentsImpl(DataFlowInfo.EMPTY, newCall)
  )
