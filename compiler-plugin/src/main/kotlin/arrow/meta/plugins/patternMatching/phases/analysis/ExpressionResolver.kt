package arrow.meta.plugins.patternMatching.phases.analysis

import org.jetbrains.kotlin.backend.common.descriptors.propertyIfAccessor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DelegatingBindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.getCall
import org.jetbrains.kotlin.resolve.calls.model.DataFlowInfoForArgumentsImpl
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCallImpl
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.tasks.ExplicitReceiverKind
import org.jetbrains.kotlin.resolve.calls.tasks.ResolutionCandidate
import org.jetbrains.kotlin.resolve.calls.tasks.TracingStrategy
import org.jetbrains.kotlin.resolve.calls.util.CallMaker
import org.jetbrains.kotlin.resolve.constants.CompileTimeConstant
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo

fun BindingTrace.resolvePatternExpression(resolution: (BindingTrace) -> Unit) =
  resolution(this)

val BindingTrace.wildcards
  get() = wildcardTypeInfoEntries.forEach { entry ->
    recordType(entry.key, bindingContext.targetType?.type)
    record<KtExpression, CompileTimeConstant<*>>(
      BindingContext.COMPILE_TIME_VALUE,
      entry.key,
      bindingContext.targetExpression
    )
    resolveParam(entry.key)
  }

private val BindingTrace.wildcardTypeInfoEntries: List<MutableMap.MutableEntry<KtExpression, KotlinTypeInfo>>
  get() = bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO).entries
    .filter { it.value.type == null && it.key.text == "_" }

private val BindingContext.targetType
  get() = getSliceContents(BindingContext.EXPRESSION_TYPE_INFO).entries
    .find { it.key.textMatches(""""Matt"""") }?.value

private val BindingContext.targetExpression
  get() = getSliceContents(BindingContext.COMPILE_TIME_VALUE).entries
    .find { it.key.text == """"Matt"""" }?.value

private fun BindingTrace.resolveParam(expr: KtExpression) {
  if (expr.text != "_") return

  // FIXME here I am expecting a lot

  // descriptor of subject of when expression
  val whenExpr = expr.getParentOfType<KtWhenExpression>(false)!!
  val subj = (whenExpr.subjectExpression!! as KtReferenceExpression)
  val subjDescriptor = bindingContext[BindingContext.REFERENCE_TARGET, subj]!! as PropertyDescriptor
  val subjClass = subjDescriptor.type.constructor.declarationDescriptor as ClassDescriptor

  // call of person with underscore
  val valueArgument = expr.parent as KtValueArgument
  val valueArgumentList = valueArgument.parent as KtValueArgumentList
  val paramIndex = valueArgumentList.arguments.indexOf(valueArgument)

  // getting nth property which gets replaced with underscore
  val param =
      subjClass.unsubstitutedPrimaryConstructor!!.valueParameters[paramIndex].propertyIfAccessor
  val property =
      subjClass.unsubstitutedMemberScope.getContributedVariables(
          param.name,
          NoLookupLocation.FROM_TEST
      ).first()

  // adding call to underscore reference with person.(name of nth property)
  val origCall = subj.getCall(bindingContext)!!
  val call = CallMaker.makeCall(
      origCall.callElement,
      origCall.explicitReceiver,
      origCall.callOperationNode,
      origCall.calleeExpression,
      origCall.valueArguments,
      origCall.callType
  )
  val receiver = ExpressionReceiver.create(
      subj,
      subjDescriptor.type,
      bindingContext
  )
  val candidateCall = ResolvedCallImpl.create(
      ResolutionCandidate.create(call, property, receiver, ExplicitReceiverKind.DISPATCH_RECEIVER, null),
      DelegatingBindingTrace(bindingContext, "Compute type predicates for unresolved call arguments"),
      TracingStrategy.EMPTY,
      DataFlowInfoForArgumentsImpl(DataFlowInfo.EMPTY, call)
  )

  record(BindingContext.CALL, expr, call)
  record(BindingContext.RESOLVED_CALL, call, candidateCall)
}
