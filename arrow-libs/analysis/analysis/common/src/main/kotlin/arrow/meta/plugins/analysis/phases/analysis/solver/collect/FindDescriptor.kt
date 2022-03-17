package arrow.meta.plugins.analysis.phases.analysis.solver.collect

import arrow.meta.plugins.analysis.phases.analysis.solver.allArgumentExpressions
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.withAliasUnwrapped
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CallExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DotQualifiedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Function
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NameReferenceExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ReturnExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ThisExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.DeclarationConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorIds
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages
import arrow.meta.plugins.analysis.phases.analysis.solver.isALaw
import arrow.meta.plugins.analysis.phases.analysis.solver.isCompatibleWith
import arrow.meta.plugins.analysis.phases.analysis.solver.isLawsType
import arrow.meta.plugins.analysis.phases.analysis.solver.isLooselyCompatibleWith
import arrow.meta.plugins.analysis.phases.analysis.solver.renameConditions
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState

/**
 * Depending on the source of the [descriptor] we might need to attach the information to different
 * places. The main case is when you declare a @Law, in which case you have to look for the proper
 * place.
 */
internal fun SolverState.addConstraints(
  descriptor: DeclarationDescriptor,
  preConstraints: ArrayList<NamedConstraint>,
  postConstraints: ArrayList<NamedConstraint>,
  notLookConstraints: ArrayList<NamedConstraint>,
  bindingContext: ResolutionContext
) {
  val lawSubject =
    findDescriptorFromRemoteLaw(descriptor, bindingContext)
      ?: findDescriptorFromLocalLaw(descriptor, bindingContext)
  if (lawSubject is CallableDescriptor && lawSubject.fqNameSafe == FqName("arrow.analysis.post"))
    throw Exception("trying to attach to post, this is wrong!")
  if (lawSubject != null) {
    val renamed =
      solver.renameConditions(
        DeclarationConstraints(descriptor, preConstraints, postConstraints, notLookConstraints),
        lawSubject.withAliasUnwrapped
      )
    callableConstraints.add(
      renamed.descriptor,
      ArrayList(renamed.pre),
      ArrayList(renamed.post),
      ArrayList(renamed.doNotLookAtArgumentsWhen)
    )
  }
  callableConstraints.add(descriptor, preConstraints, postConstraints, notLookConstraints)
}

/** Finds the target of a particular law by looking up its [arrow.analysis.Subject] annotation */
private fun findDescriptorFromRemoteLaw(
  descriptor: DeclarationDescriptor,
  context: ResolutionContext
): DeclarationDescriptor? =
  descriptor.annotations().findAnnotation(FqName("arrow.analysis.Subject"))?.let { lawSubject ->
    val name = lawSubject.argumentValueAsString("fqName")
    val result = name?.let { context.obtainDeclaration(FqName(it), descriptor) }
    result.also {
      if (it == null) {
        if (name == null)
          throw Exception(ErrorMessages.Parsing.subjectWithoutName(descriptor.fqNameSafe.name))
        else
          throw Exception(
            ErrorMessages.Parsing.couldNotResolveSubject(name, descriptor.fqNameSafe.name)
          )
      }
    }
  }

private fun ResolutionContext.obtainDeclaration(
  fqName: FqName,
  compatibleWith: DeclarationDescriptor
): DeclarationDescriptor? {
  val current = descriptorFor(fqName)
  // the type either strictly checks
  // or we need to look for the best match
  return current.firstOrNull { it.isCompatibleWith(compatibleWith) }
    ?: current.filter { it.isLooselyCompatibleWith(compatibleWith) }.minWithOrNull { o1, o2 ->
      when {
        o1.isLooselyCompatibleWith(o2) -> -1
        o2.isLooselyCompatibleWith(o1) -> 1
        else -> 0
      }
    }
}

/**
 * Finds the target of a particular law by looking at its last return, if marked with a
 * [arrow.analysis.Law] annotation.
 */
public fun SolverState.findDescriptorFromLocalLaw(
  descriptor: DeclarationDescriptor,
  bindingContext: ResolutionContext
): DeclarationDescriptor? {
  if (!descriptor.isALaw()) return null

  if (descriptor !is CallableDescriptor) return null

  val lawCall =
    (descriptor.element() as? Function)?.let {
      getReturnedExpressionWithoutPostcondition(it, bindingContext)
    }
  if (lawCall == null) {
    descriptor.element()?.let { elt ->
      val msg = ErrorMessages.Parsing.lawMustCallFunction()
      bindingContext.handleError(ErrorIds.Laws.LawMustCallFunction, elt, msg)
      signalParseErrors()
    }
    return null
  }

  val parameters = descriptor.allParameters.filter { !it.type.descriptor.isLawsType() }
  val arguments = lawCall.allArgumentExpressions(bindingContext).map { it.expression }
  val check =
    parameters.zip(arguments).all { (param, arg) ->
      (param is ReceiverParameterDescriptor &&
        (arg.isEmpty() || arg.singleOrNull() is ThisExpression)) ||
        (param is ValueParameterDescriptor &&
          arg.singleOrNull() is NameReferenceExpression &&
          (arg.single() as NameReferenceExpression).getReferencedNameAsName() == param.name)
    }
  if (!check) {
    descriptor.element()?.let { elt ->
      val msg = ErrorMessages.Parsing.lawMustHaveParametersInOrder()
      bindingContext.handleError(ErrorIds.Laws.LawMustHaveParametersInOrder, elt, msg)
      signalParseErrors()
    }
    return null
  }

  return lawCall.resultingDescriptor
}

private fun getReturnedExpressionWithoutPostcondition(
  function: Function,
  bindingContext: ResolutionContext
): ResolvedCall? {
  val lastElementWithoutReturn =
    when (val lastElement = function.body()?.lastBlockStatementOrThis()) {
      is ReturnExpression -> lastElement.returnedExpression
      else -> lastElement
    }
  // remove outer layer of postcondition
  val veryLast =
    when (lastElementWithoutReturn) {
      is DotQualifiedExpression -> {
        val selector = lastElementWithoutReturn.selectorExpression
        val callee = (selector as? CallExpression)?.calleeExpression
        if (callee != null && callee.text == "post") {
          lastElementWithoutReturn.receiverExpression
        } else {
          null
        }
      }
      else -> null
    }
      ?: lastElementWithoutReturn

  return veryLast?.getResolvedCall(bindingContext)
}

/**
 * This function is the one really adding the constraints to the list, keeping into consideration
 * possible duplicate [FqName]s.
 */
private fun MutableMap<FqName, MutableList<DeclarationConstraints>>.add(
  descriptor: DeclarationDescriptor,
  pre: ArrayList<NamedConstraint>,
  post: ArrayList<NamedConstraint>,
  doNotLookAtArgumentsWhen: ArrayList<NamedConstraint>
) {
  val fqName = descriptor.fqNameSafe
  // create a new one if not existent
  if (!containsKey(fqName)) this[fqName] = mutableListOf()
  val list = this[fqName]!!
  // see if there's any compatible
  when (val ix = list.indexOfFirst { it.descriptor.isCompatibleWith(descriptor) }) {
    -1 -> list.add(DeclarationConstraints(descriptor, pre, post, doNotLookAtArgumentsWhen))
    else -> {
      val previous = list[ix]
      list[ix] =
        DeclarationConstraints(
          descriptor,
          previous.pre + pre,
          previous.post + post,
          previous.doNotLookAtArgumentsWhen + doNotLookAtArgumentsWhen
        )
    }
  }
}
