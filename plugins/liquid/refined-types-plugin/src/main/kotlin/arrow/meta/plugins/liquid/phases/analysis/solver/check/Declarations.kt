package arrow.meta.plugins.liquid.phases.analysis.solver.check

import arrow.meta.continuations.ContSeq
import arrow.meta.continuations.cont
import arrow.meta.continuations.doOnlyWhen
import arrow.meta.continuations.doOnlyWhenNotNull
import arrow.meta.continuations.sequence
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.CheckData
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.CurrentVarInfo
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.NoReturn
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.Return
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.ReturnPoints
import arrow.meta.plugins.liquid.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.liquid.phases.analysis.solver.check.model.VarInfo
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.constraintsFromSolverState
import arrow.meta.plugins.liquid.phases.analysis.solver.state.checkPostConditionsImplication
import arrow.meta.plugins.liquid.phases.analysis.solver.state.checkPreconditionsInconsistencies
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.hasLawAnnotation
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.immediateConstraintsFromSolverState
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.overriddenConstraintsFromSolverState
import arrow.meta.plugins.liquid.phases.analysis.solver.state.checkLiskovStrongerPostcondition
import arrow.meta.plugins.liquid.phases.analysis.solver.state.checkLiskovWeakerPrecondition
import arrow.meta.plugins.liquid.smt.ObjectFormula
import arrow.meta.plugins.liquid.smt.renameDeclarationConstraints
import org.jetbrains.kotlin.backend.common.descriptors.allParameters
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtDelegatedSuperTypeEntry
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

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
  context: DeclarationCheckerContext,
  descriptor: DeclarationDescriptor,
  declaration: KtDeclaration,
  bodyCheck: (checkPost: () -> Unit) -> ContSeq<A>
): ContSeq<A> {
  // bring the constraints in (if there are any)
  val constraints =
    constraintsFromSolverState(descriptor)?.let {
      when (declaration) {
        // special case for constructors: instead of $result we use this
        is KtConstructor<*> ->
          solver.renameDeclarationConstraints(it, mapOf(RESULT_VAR_NAME to "this"))
        else -> it
      }
    }
  // perform the checks
  return continuationBracket.map {
    // introduce non-nullability of parameters
    if (descriptor is CallableDescriptor) {
      descriptor.allParameters.forEach { param ->
        if (!param.type.isMarkedNullable) {
          val paramName = param.name.asString()
          val notNullFormula = solver.isNotNull(solver.makeObjectVariable(paramName))
          addConstraint(NamedConstraint("$paramName is not null", notNullFormula))
        }
      }
    }
    // check consistency of pre-conditions
    val inconsistentPreconditions =
      checkPreconditionsInconsistencies(constraints, context, declaration)
    ensure(!inconsistentPreconditions)
  }.map {
    // check Liskov conditions
    val liskovOk = checkLiskovConditions(declaration, descriptor, context)
    ensure(liskovOk)
  }.flatMap {
    // check the body
    bodyCheck {
      // and finally check the postconditions
      checkPostConditionsImplication(constraints, context, declaration)
    }
  }
}

internal fun SolverState.checkTopLevelDeclarationWithBody(
  context: DeclarationCheckerContext,
  descriptor: DeclarationDescriptor,
  declaration: KtDeclaration
): ContSeq<Return> =
  checkTopLevel(context, descriptor, declaration) { checkPost ->
    // only check body when we are not in a @Law
    doOnlyWhen(!descriptor.hasLawAnnotation(), NoReturn) {
      val result = solver.makeObjectVariable(RESULT_VAR_NAME)
      val data = CheckData(context, ReturnPoints.new(declaration, result), initializeVarInfo(declaration))
      checkExpressionConstraints(result, declaration.stableBody(), data).onEach {
        checkPost()
      }
    }
  }

internal fun SolverState.checkPrimaryConstructor(
  context: DeclarationCheckerContext,
  descriptor: DeclarationDescriptor,
  declaration: KtPrimaryConstructor
): ContSeq<Return> =
  checkTopLevel(context, descriptor, declaration) { checkPost ->
    val klass = declaration.getContainingClassOrObject()
    val thisRef = solver.makeObjectVariable("this")
    val data = CheckData(context, ReturnPoints.new(declaration, thisRef), initializeVarInfo(declaration))
    ContSeq.unit.flatMap {
      // introduce 'val' and 'var' from the constructor
      introduceImplicitProperties(thisRef, klass)
    }.flatMap {
      // call the superclass constructors
      // (this will ultimately check the Liskov for classes)
      klass.superTypeListEntries.mapNotNull { entry ->
        when (entry) {
          is KtDelegatedSuperTypeEntry -> entry.delegateExpression
          is KtSuperTypeCallEntry -> entry.calleeExpression
          else -> null
        }
      }.map { expr ->
        doOnlyWhenNotNull(expr.getResolvedCall(context.trace.bindingContext), NoReturn) { call ->
          checkRegularFunctionCall(thisRef, call, expr, data)
        }
      }.sequence()
    }.flatMap {
      checkExpressionConstraints(thisRef, declaration.bodyExpression, data)
    }.flatMap {
      checkClassDeclarationInConstructorContext(thisRef, klass, data)
    }.map {
      checkPost() // but we need to replace $RESULT with 'this'!
      NoReturn
    }
  }

private fun SolverState.introduceImplicitProperties(
  thisRef: ObjectFormula,
  klass: KtClassOrObject
): ContSeq<Unit> = cont {
  // if we have 'var' or 'var' in the parameters,
  // we need to assign them to fields
  // when the constructor is primary
  klass.primaryConstructorParameters
    .filter { it.hasValOrVar() }
    .forEach { param ->
      val paramName = param.name ?: "this"
      val fieldName = klass.fqName?.let { "$it.$paramName" } ?: "this"
      addConstraint(NamedConstraint(
        "definition of property $paramName",
        solver.objects {
          equal(
            solver.makeObjectVariable(paramName),
            solver.field(fieldName, thisRef)
          )
        }
      ))
    }
  NoReturn
}

private fun SolverState.checkClassDeclarationInConstructorContext(
  thisRef: ObjectFormula,
  klass: KtClassOrObject,
  data: CheckData
) = klass.declarations.map { decl ->
  when (decl) {
    is KtConstructor<*> ->
      cont { NoReturn } // do not check any other constructor
    is KtAnonymousInitializer ->
      checkExpressionConstraints(thisRef, decl.body, data)
    is KtProperty ->
      doOnlyWhenNotNull(decl.fqName?.asString(), NoReturn) { fieldName ->
        val result = solver.field(fieldName, solver.makeObjectVariable("this"))
        checkExpressionConstraints(result, decl.stableBody(), data)
      }
    else -> cont { NoReturn }
  }
}.sequence()

internal fun SolverState.checkSecondaryConstructor(
  context: DeclarationCheckerContext,
  descriptor: DeclarationDescriptor,
  declaration: KtSecondaryConstructor
): ContSeq<Return> =
  checkTopLevel(context, descriptor, declaration) { checkPost ->
    val thisRef = solver.makeObjectVariable("this")
    val data = CheckData(context, ReturnPoints.new(declaration, thisRef), initializeVarInfo(declaration))
    ContSeq.unit.flatMap {
      // delegate into the primary constructor, if available
      doOnlyWhenNotNull(declaration.getDelegationCallOrNull(), NoReturn) { delegation ->
        doOnlyWhenNotNull(delegation.getResolvedCall(context.trace.bindingContext), NoReturn) { call ->
          doOnlyWhenNotNull(delegation.calleeExpression, NoReturn) { calleeExpression ->
            checkRegularFunctionCall(thisRef, call, calleeExpression, data)
          }
        }
      }
    }.flatMap {
      checkExpressionConstraints(thisRef, declaration.bodyExpression, data)
    }.map {
      checkPost() // but we need to replace $RESULT with 'this'!
      NoReturn
    }
  }

/**
 * Check that the constraints respect subtyping,
 * in particular the Liskov Substitution Principle
 */
private fun SolverState.checkLiskovConditions(
  declaration: KtDeclaration,
  descriptor: DeclarationDescriptor,
  context: DeclarationCheckerContext
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

/**
 * Initialize the names of the variables,
 * the SMT name is initialized to themselves.
 */
private fun initializeVarInfo(declaration: KtDeclaration): CurrentVarInfo {
  val initialVarInfo = mutableListOf<VarInfo>()
  initialVarInfo.add(VarInfo("this", "this", declaration))
  if (declaration is KtNamedDeclaration) {
    // Add 'this@functionName'
    declaration.name?.let { name ->
      initialVarInfo.add(VarInfo("this@$name", "this", declaration))
    }
  }
  if (declaration is KtDeclarationWithBody) {
    declaration.valueParameters.forEach { param ->
      param.name?.let { name ->
        initialVarInfo.add(VarInfo(name, name, param))
      }
    }
  }
  return CurrentVarInfo(initialVarInfo)
}
