package arrow.meta.plugins.liquid.phases.analysis.solver.check

import arrow.meta.continuations.ContSeq
import arrow.meta.continuations.cont
import arrow.meta.continuations.doOnlyWhen
import arrow.meta.continuations.doOnlyWhenNotNull
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
import org.jetbrains.kotlin.backend.common.descriptors.allParameters
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtNamedDeclaration
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
internal fun SolverState.checkTopLevelDeclarationWithBody(
  context: DeclarationCheckerContext,
  descriptor: DeclarationDescriptor,
  declaration: KtDeclaration
): ContSeq<Return> {
  // bring the constraints in (if there are any)
  val constraints = constraintsFromSolverState(descriptor)
  // choose a good name for the result
  // should we change it for 'val' declarations?
  val resultVarName = RESULT_VAR_NAME
  // perform the actual check
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
    // only check body when we are not in a @Law
    doOnlyWhenNotNull(declaration.stableBody(), NoReturn) { body ->
      doOnlyWhen(!descriptor.hasLawAnnotation(), NoReturn) {
        val data = CheckData(context, ReturnPoints.new(declaration, resultVarName), initializeVarInfo(declaration))
        checkExpressionConstraints(resultVarName, body, data).onEach {
          checkPostConditionsImplication(constraints, context, declaration)
        }
      }
    }
  }
}

internal fun <A : KtConstructor<A>> SolverState.checkConstructor(
  context: DeclarationCheckerContext,
  declaration: KtConstructor<A>
): ContSeq<Return> = cont {
  NoReturn
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
