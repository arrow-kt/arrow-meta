package arrow.meta.plugins.analysis.phases.analysis.solver.check

import arrow.meta.continuations.ContSeq
import arrow.meta.continuations.cont
import arrow.meta.continuations.doOnlyWhen
import arrow.meta.continuations.doOnlyWhenNotNull
import arrow.meta.continuations.sequence
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.CheckData
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.CurrentVarInfo
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.NoReturn
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.ReturnPoints
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.constraintsFromSolverState
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkPostConditionsImplication
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkPreconditionsInconsistencies
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.isALaw
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.immediateConstraintsFromSolverState
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.overriddenConstraintsFromSolverState
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkLiskovStrongerPostcondition
import arrow.meta.plugins.analysis.phases.analysis.solver.state.checkLiskovWeakerPrecondition
import arrow.meta.plugins.analysis.smt.ObjectFormula
import arrow.meta.plugins.analysis.smt.substituteDeclarationConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnonymousInitializer
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ClassOrObject
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Constructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DeclarationWithBody
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DelegatedSuperTypeEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.EnumEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NamedDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.PrimaryConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Property
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SecondaryConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SuperTypeCallEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SuperTypeListEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ConstructorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.CurrentBranch
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.VarInfo
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.noReturn

// 2.1: declarations
// -----------------
/**
 * When the solver is in the prover state
 * check this [declaration] body and constraints for
 * - pre-condition inconsistencies,
 * - whether the body satisfy all the pre-conditions in calls,
 * - whether the post-condition really holds.
 */
internal fun <A> SolverState.checkTopLevel(
  context: ResolutionContext,
  descriptor: DeclarationDescriptor,
  declaration: Declaration,
  resultName: ObjectFormula,
  bodyCheck: (data: CheckData, checkPost: (finalData: CheckData) -> Unit) -> ContSeq<A>
): ContSeq<A> {
  // bring the constraints in (if there are any)
  val constraints =
    constraintsFromSolverState(descriptor)?.let {
      solver.substituteDeclarationConstraints(it, mapOf(RESULT_VAR_NAME to resultName))
    }
  // initialize the check data
  val initialVarInfo = if (descriptor is CallableDescriptor) {
    val thisParam = (descriptor.extensionReceiverParameter ?: descriptor.dispatchReceiverParameter)
      ?.let { ParamInfo(THIS_VAR_NAME, THIS_VAR_NAME, it.type, declaration) }
    val valueParams = descriptor.valueParameters.map { param ->
      val element =
        (declaration as? DeclarationWithBody)?.valueParameters
          ?.firstOrNull { it?.name == param.name.value }
      ParamInfo(param.name.value, param.name.value, param.type, element)
    }
    val returnParam = descriptor.returnType
      ?.takeIf { it !is ConstructorDescriptor }
      ?.let { ParamInfo(RESULT_VAR_NAME, RESULT_VAR_NAME, it, declaration) }
    // additional for 'this@info'
    val additional = if (declaration is NamedDeclaration) {
      // Add 'this@functionName'
      declaration.nameAsName?.let { name ->
        VarInfo("this@$name", THIS_VAR_NAME, declaration)
      }
    } else null
    // introduce non-nullability and invariants of parameters
    initialParameters(context, thisParam, valueParams, returnParam) + listOfNotNull(additional)
  } else emptyList()
  val data = CheckData(
    context, ReturnPoints.new(declaration, resultName),
    CurrentVarInfo(initialVarInfo), CurrentBranch.new())
  // perform the checks
  return continuationBracket.map {
    // check consistency of pre-conditions
    val inconsistentPreconditions =
      checkPreconditionsInconsistencies(constraints, data.context, declaration)
    ensure(!inconsistentPreconditions)
  }.map {
    // check Liskov conditions
    val liskovOk = checkLiskovConditions(declaration, descriptor, data.context)
    ensure(liskovOk)
  }.flatMap {
    // check the body
    bodyCheck(data) { finalData ->
      // and finally check the post-conditions
      checkPostConditionsImplication(constraints, finalData.context, declaration, finalData.branch.get())
    }
  }
}

internal fun SolverState.checkTopLevelDeclarationWithBody(
  context: ResolutionContext,
  descriptor: DeclarationDescriptor,
  declaration: Declaration
): ContSeq<Unit> =
  checkTopLevel(context, descriptor, declaration, solver.resultVariable) { data, checkPost ->
    // only check body when we are not in a @Law
    doOnlyWhen(!descriptor.isALaw()) {
      checkExpressionConstraints(solver.resultVariable, declaration.stableBody(), data).map { finalState ->
        checkPost(finalState.data)
      }
    }
  }

internal fun SolverState.checkPrimaryConstructor(
  context: ResolutionContext,
  descriptor: DeclarationDescriptor,
  declaration: PrimaryConstructor
): ContSeq<Unit> =
  checkTopLevel(context, descriptor, declaration, solver.thisVariable) { data, checkPost ->
    val klass = declaration.getContainingClassOrObject()
    ContSeq.unit.flatMap {
      // introduce 'val' and 'var' from the constructor
      introduceImplicitProperties(klass)
    }.flatMap {
      // call the superclass constructors
      // (this will ultimately check the Liskov for classes)
      checkSuperTypeEntries(context, klass.superTypeListEntries, data)
    }.flatMap {
      checkExpressionConstraints(solver.thisVariable, declaration.bodyExpression, data)
    }.flatMap { finalState ->
      checkClassDeclarationInConstructorContext(solver.thisVariable, klass.declarations, finalState.data).onEach {
        checkPost(finalState.data)
      }
    }
  }

private fun SolverState.introduceImplicitProperties(
  klass: ClassOrObject
): ContSeq<Unit> = cont {
  // if we have 'var' or 'var' in the parameters,
  // we need to assign them to fields
  // when the constructor is primary
  klass.primaryConstructorParameters
    .filter { it.hasValOrVar() }
    .forEach { param ->
      val paramName = param.nameAsName?.value ?: THIS_VAR_NAME
      val fieldName = klass.fqName?.let { "$it.$paramName" } ?: THIS_VAR_NAME
      addConstraint(NamedConstraint(
        "definition of property $paramName",
        solver.objects {
          equal(
            solver.makeObjectVariable(paramName),
            solver.field(fieldName, solver.thisVariable)
          )
        }
      ))
    }
}

private fun SolverState.checkSuperTypeEntries(
  context: ResolutionContext,
  superTypeListEntries: List<SuperTypeListEntry>,
  data: CheckData
): ContSeq<Unit> =
  superTypeListEntries.mapNotNull { entry ->
    when (entry) {
      is DelegatedSuperTypeEntry -> entry.delegateExpression
      is SuperTypeCallEntry -> entry.calleeExpression
      else -> null
    }
  }.map { expr ->
    doOnlyWhenNotNull(expr.getResolvedCall(context), Unit) { call ->
      checkRegularFunctionCall(solver.thisVariable, call, expr, data)
    }
  }.sequence().void()

private fun SolverState.checkClassDeclarationInConstructorContext(
  thisRef: ObjectFormula,
  declarations: List<Declaration>,
  data: CheckData
): ContSeq<Unit> = declarations.map { decl ->
  when (decl) {
    is Constructor<*> -> ContSeq.unit // do not check any other constructor
    is AnonymousInitializer ->
      checkExpressionConstraints(thisRef, decl.body, data)
    is Property ->
      doOnlyWhenNotNull(decl.fqName?.name, NoReturn) { fieldName ->
        val result = solver.field(fieldName, solver.makeObjectVariable(THIS_VAR_NAME))
        checkExpressionConstraints(result, decl.stableBody(), data)
      }
    else -> ContSeq.unit
  }
}.sequence().void()

internal fun SolverState.checkSecondaryConstructor(
  context: ResolutionContext,
  descriptor: DeclarationDescriptor,
  declaration: SecondaryConstructor
): ContSeq<Unit> =
  checkTopLevel(context, descriptor, declaration, solver.thisVariable) { data, checkPost ->
    ContSeq.unit.flatMap {
      // delegate into the primary constructor, if available
      doOnlyWhenNotNull(declaration.getDelegationCall(), data.noReturn()) { delegation ->
        doOnlyWhenNotNull(delegation.getResolvedCall(context), data.noReturn()) { call ->
          doOnlyWhenNotNull(delegation.calleeExpression, data.noReturn()) { calleeExpression ->
            checkRegularFunctionCall(solver.thisVariable, call, calleeExpression, data)
          }
        }
      }
    }.flatMap { stateAfterPrimaryConstructorCall ->
      checkExpressionConstraints(solver.thisVariable, declaration.bodyExpression, stateAfterPrimaryConstructorCall.data).map { finalState ->
        checkPost(finalState.data)
      }
    }
  }

internal fun SolverState.checkEnumEntry(
  context: ResolutionContext,
  descriptor: DeclarationDescriptor,
  entry: EnumEntry
): ContSeq<Unit> =
  checkTopLevel(context, descriptor, entry, solver.thisVariable) { data, checkPost ->
    ContSeq.unit.flatMap {
      // the supertype entries in an enumeration is the enum itself
      checkSuperTypeEntries(context, entry.superTypeListEntries, data)
    }.flatMap {
      checkClassDeclarationInConstructorContext(solver.thisVariable, entry.declarations, data)
    }.map {
      checkPost(data)
    }
  }

/**
 * Check that the constraints respect subtyping,
 * in particular the Liskov Substitution Principle
 */
private fun SolverState.checkLiskovConditions(
  declaration: Declaration,
  descriptor: DeclarationDescriptor,
  context: ResolutionContext
): Boolean {
  val immediateConstraints = immediateConstraintsFromSolverState(descriptor)
  val overriddenConstraints = overriddenConstraintsFromSolverState(descriptor)
  return if (immediateConstraints != null && overriddenConstraints != null) {
    // pre-conditions should be weaker,
    // so the immediate ones should be implied by the overridden ones
    val liskovPreOk = bracket {
      overriddenConstraints.pre.forEach { addConstraint(it) }
      immediateConstraints.pre.all {
        checkLiskovWeakerPrecondition(it, context, declaration)
      }
    }
    // post-conditions should be stronger,
    // so the overridden ones should be implied by the immediate ones
    val liskovPostOk = bracket {
      immediateConstraints.post.forEach { addConstraint(it) }
      overriddenConstraints.post.all {
        checkLiskovStrongerPostcondition(it, context, declaration)
      }
    }
    // check that both things are OK
    liskovPreOk && liskovPostOk
  } else {
    true
  }
}
