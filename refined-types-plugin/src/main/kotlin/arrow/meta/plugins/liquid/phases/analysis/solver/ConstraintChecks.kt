package arrow.meta.plugins.liquid.phases.analysis.solver

import arrow.meta.continuations.Cont
import arrow.meta.continuations.SimpleCont
import arrow.meta.continuations.contEach
import arrow.meta.continuations.continueWith
import arrow.meta.continuations.forget
import arrow.meta.continuations.guard
import arrow.meta.continuations.orDoNothing
import arrow.meta.continuations.orElse
import arrow.meta.continuations.runCont
import arrow.meta.continuations.then
import arrow.meta.continuations.wrap
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.body
import arrow.meta.plugins.liquid.errors.MetaErrors
import arrow.meta.plugins.liquid.phases.solver.collector.renameDeclarationConstraints
import org.jetbrains.kotlin.codegen.kotlinType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.fir.lightTree.converter.nameAsSafeName
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtDeclarationWithInitializer
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.types.typeUtil.isInt
import org.sosy_lab.java_smt.api.FormulaType

// PHASE 2: CHECKING OF CONSTRAINTS
// ================================

/* [NOTE: which do we use continuations?]
 * It might look odd that we create continuations when checking
 * the body, instead of simply performing the steps.
 *
 * The reason to do so is to have the ability to decide whether
 * and how to execute the "remainder of the analysis". For example:
 * - if we find a `return`, we ought to stop, since no further
 *   statement is executed;
 * - if we are in a condition, the "remainder of the analysis"
 *   ought to be executed more than once; in fact once per possible
 *   execution flow.
 *
 * What makes the problem complicated, and brings in continuations,
 * is that these decisions may have to be made within an argument:
 *
 * ```
 * f(if (x > 0) 3 else 2)
 * ```
 *
 * yet they must affect the global ongoing computations. Continuations
 * allow us to do so by saying that checking `if (x > 0) 3 else 2`
 * is the current computation, and checking `f(...)` the "remainder".
 * Thus, the conditional `if` can duplicate the check of the "remainder".
 */

private val RESULT_VAR_NAME = "${'$'}result"

// 2.0: entry point
/**
 * When the solver is in the prover state
 * check this [declaration] body constraints
 */
internal fun CompilerContext.checkDeclarationConstraints(
  context: DeclarationCheckerContext,
  declaration: KtDeclaration,
  descriptor: DeclarationDescriptor
) {
  val solverState = get<SolverState>(SolverState.key(context.moduleDescriptor))
  val constraints = solverState?.constraintsFromSolverState(descriptor)
  if (solverState != null && solverState.isIn(SolverState.Stage.Prove)) {
    // choose a good name for the result
    // should we change it for 'val' declarations?
    val resultVarName = RESULT_VAR_NAME
    // now go on and check the body
    declaration.stableBody()?.let { body ->
      solverState.checkDeclarationWithBody(
        constraints, context,
        resultVarName, declaration, body
      ).runCont()
    }
  }
}


// 2.1: declarations
// -----------------
/**
 * When the solver is in the prover state
 * check this [declaration] body and constraints for
 * - pre-condition inconsistencies,
 * - whether the body satisfy all the pre-conditions in calls,
 * - whether the post-condition really holds.
 */
private fun SolverState.checkDeclarationWithBody(
  constraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  resultVarName: String,
  declaration: KtDeclaration,
  body: KtExpression?
): SimpleCont<Unit> = bracket {
  // assert preconditions (if available)
  checkPreconditionsInconsistencies(
    constraints,
    context,
    declaration
  ).then { inconsistentPreconditions ->
    // if we are inconsistent, there's no point in going on, just stop early
    guard(!inconsistentPreconditions)
  }.then {
    checkExpressionConstraints(resultVarName, body, context)
  }.then {
    // check the post-conditions
    checkPostConditionsImplication(constraints, context, declaration)
  }
}

/**
 * Checks that this [declaration] does not contain logical inconsistencies in its preconditions.
 * For example:
 * - `(x > 0)`
 * - `(x < 0)`
 *
 * If any inconsistencies are found report them through the [context] trace diagnostics
 */
private fun SolverState.checkPreconditionsInconsistencies(
  constraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  declaration: KtDeclaration
): SimpleCont<Boolean> = wrap {
  constraints?.pre?.let {
    addAndCheckConsistency(it) { unsatCore ->
      context.trace.report(
        MetaErrors.InconsistentBodyPre.on(declaration.psiOrParent, declaration, unsatCore)
      )
    }
  } ?: false // if there are no preconditions, they are consistent
}

/**
 * Checks that this [declaration] constraints post conditions hold
 * according to the declaration body in the current solver state
 */
private fun SolverState.checkPostConditionsImplication(
  constraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  declaration: KtDeclaration
): SimpleCont<Unit> = wrap {
  constraints?.post?.forEach { postCondition ->
    checkImplicationOf(postCondition) {
      context.trace.report(
        MetaErrors.UnsatBodyPost.on(declaration.psiOrParent, declaration, listOf(postCondition))
      )
    }
  }
}

// 2.2: expressions
// ----------------
/**
 * Produces a continuation that when invoked
 * recursively checks an [expression] set of constraints
 */
private fun SolverState.checkExpressionConstraints(
  associatedVarName: String,
  expression: KtExpression?,
  context: DeclarationCheckerContext
): SimpleCont<Unit> =
  when (expression) {
    // TODO: fix block expressions!
    is KtBlockExpression ->
      expression.statements.contEach {
        checkExpressionConstraints(associatedVarName, it, context)
      }.forget()
    is KtCallExpression ->
      checkCallExpression(associatedVarName, expression, context)
    is KtConstantExpression ->
      checkConstantExpression(associatedVarName, expression)
    is KtSimpleNameExpression ->
      checkNameExpression(associatedVarName, expression, context)
    is KtNamedDeclaration ->
      checkDeclarationExpression(expression.nameAsSafeName.asString(), expression, context)
    is KtDeclaration -> { // declaration without names, make up a new one
      val madeUpName = names.newName("decl")
      checkDeclarationExpression(madeUpName, expression, context)
    }
    else -> continueWith(Unit)
    /*expression?.getChildrenOfType<KtExpression>()?.toList()?.contEach {
      checkExpressionConstraints(associatedVarName, it, context)
    }?.forget().orDoNothing()*/
  }

/**
 * Produces a continuation that when invoked
 * recursively checks a call [expression] set of constraints
 */
private fun SolverState.checkCallExpression(
  associatedVarName: String,
  expression: KtCallExpression,
  context: DeclarationCheckerContext
): SimpleCont<Unit> =
  expression.getResolvedCall(context.trace.bindingContext).let { resolvedCall ->
    // recursively perform check on arguments
    // including extension receiver and dispatch receiver
    //
    // [NOTE: argument renaming]
    //   this function creates a new name for each argument,
    //   based on the formal parameter name;
    //   this creates a renaming for the original constraints
    resolvedCall?.allArgumentExpressions()?.contEach { (name, _, expr) ->
      val argUniqueName = names.newName(name)
      checkExpressionConstraints(argUniqueName, expr, context).then {
        continueWith(Pair(name, argUniqueName))
      }
    }?.then { continueWith(it.toMap()) }
      .orElse(continueWith<Unit, Map<String, String>>(emptyMap()))
      // obtain and rename the pre- and post-conditions
      .then { argVars ->
        wrap {
          checkCallPreAndPostConditions(resolvedCall, argVars, associatedVarName, context, expression)
        }
      }
  }

/**
 * Checks this [resolvedCall] pre and post conditions based on the solver state constraints
 */
private fun SolverState.checkCallPreAndPostConditions(
  resolvedCall: ResolvedCall<out CallableDescriptor>?,
  argVars: Map<String, String>,
  associatedVarName: String,
  context: DeclarationCheckerContext,
  expression: KtCallExpression
) {
  val callConstraints = resolvedCall?.let { constraintsFromSolverState(it) }?.let {
    val completeRenaming = argVars + (RESULT_VAR_NAME to associatedVarName)
    solver.renameDeclarationConstraints(it, completeRenaming)
  }
  if (resolvedCall != null) {
    // check pre-conditions
    checkCallPreconditionsImplication(callConstraints, context, expression, resolvedCall)
    // assert post-conditions (inconsistent means unreachable code)
    checkCallPostConditionsInconsistencies(callConstraints, context, expression, resolvedCall)
  }
}

/**
 * Checks the pre conditions in [callConstraints] hold for [resolvedCall]
 */
private fun SolverState.checkCallPreconditionsImplication(
  callConstraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  expression: KtCallExpression,
  resolvedCall: ResolvedCall<out CallableDescriptor>
) {
  callConstraints?.pre?.forEach { callPreCondition ->
    checkImplicationOf(callPreCondition) {
      context.trace.report(
        MetaErrors.UnsatCallPre.on(expression.psiOrParent, resolvedCall, listOf(callPreCondition))
      )
    }
  }
}

/**
 * Checks the post conditions in [callConstraints] hold for [resolvedCall]
 */
private fun SolverState.checkCallPostConditionsInconsistencies(
  callConstraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  expression: KtCallExpression,
  resolvedCall: ResolvedCall<out CallableDescriptor>
) {
  callConstraints?.post?.let {
    addAndCheckConsistency(it) { unsatCore ->
      context.trace.report(
        MetaErrors.InconsistentCallPost.on(expression.psiOrParent, resolvedCall, unsatCore)
      )
    }
  }
}

/**
 * This function produces a constraint that makes the desired variable name
 * equal to the value encoded in the constant and adds it to the
 * [SolverState.prover] constraints.
 */
private fun SolverState.checkConstantExpression(
  associatedVarName: String,
  expression: KtConstantExpression
): SimpleCont<Unit> = wrap {
  solver.formulae {
    val mayBoolean  = expression.text.toBooleanStrictOrNull()
    val mayInteger  = expression.text.toBigIntegerOrNull()
    val mayRational = expression.text.toBigDecimalOrNull()
    when {
      mayBoolean == true ->
        makeVariable(FormulaType.BooleanType, associatedVarName)
      mayBoolean == false ->
        solver.booleans { not(makeVariable(FormulaType.BooleanType, associatedVarName)) }
      mayInteger != null ->
        solver.ints {
          equal(
            makeVariable(FormulaType.IntegerType, associatedVarName),
            makeNumber(mayInteger)
          )
        }
      mayRational != null ->
        solver.rationals {
          equal(
            makeVariable(FormulaType.RationalType, associatedVarName),
            makeNumber(mayRational)
          )
        }
      else -> null
    }?.let {
      prover.addConstraint(it)
    }
  }
}

/**
 * This function produces a continuation that makes the desired variable name
 * equal to the value encoded in the named expression.
 */
private fun SolverState.checkDeclarationExpression(
  newVarName: String,
  declaration: KtDeclaration,
  context: DeclarationCheckerContext
): SimpleCont<Unit> = declaration.stableBody()?.let {
  checkExpressionConstraints(newVarName, it, context)
}.orDoNothing()

/**
 * This function produces a continuation that makes the desired variable name
 * equal to the value encoded in the named expression and adds the resulting boolean formula
 * to the [SolverState.prover] constraints.
 */
private fun SolverState.checkNameExpression(
  associatedVarName: String,
  expression: KtSimpleNameExpression,
  context: DeclarationCheckerContext
): Cont<Unit, Unit> = wrap {
  // FIX: add only things in scope
  val referencedName = expression.getReferencedName().nameAsSafeName().asString()
  // TODO: for now only for integers
  if (expression.kotlinType(context.trace.bindingContext)?.isInt() == true) {
    solver.formulae {
      solver.ints {
        equal(
          makeVariable(FormulaType.IntegerType, associatedVarName),
          makeVariable(FormulaType.IntegerType, referencedName)
        )
      }
    }.let { prover.addConstraint(it) }
  }
}

/**
 * TODO I believe here instead of trying to see which fields represent a body we just need to recursively
 * visit the declaration as we do in [argsFormulae] where it uses an expression recursive visitor.
 * That will recursively visit all body element as well and there we can match just in those we care.
 */
private fun KtDeclaration.stableBody(): KtExpression?
  = when (this) {
  is KtVariableDeclaration -> if (isVar) null else initializer
  is KtDeclarationWithBody -> body()
  is KtDeclarationWithInitializer -> initializer
  else -> null
}
