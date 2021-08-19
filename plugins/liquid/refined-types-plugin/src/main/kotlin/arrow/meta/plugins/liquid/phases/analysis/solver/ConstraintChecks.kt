package arrow.meta.plugins.liquid.phases.analysis.solver

import arrow.meta.continuations.ContSeq
import arrow.meta.continuations.andThen
import arrow.meta.continuations.andThenSideEffect
import arrow.meta.continuations.doNothing
import arrow.meta.continuations.doOnlyWhen
import arrow.meta.continuations.ensure
import arrow.meta.continuations.flatMap
import arrow.meta.continuations.goOn
import arrow.meta.continuations.guard
import arrow.meta.continuations.map
import arrow.meta.continuations.sequence
import arrow.meta.continuations.sideEffect
import arrow.meta.internal.mapNotNull
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.body
import arrow.meta.plugins.liquid.phases.solver.collector.renameDeclarationConstraints
import org.jetbrains.kotlin.codegen.kotlinType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.fir.lightTree.converter.nameAsSafeName
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtDeclarationWithInitializer
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLabeledExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import org.jetbrains.kotlin.psi.KtWhenConditionWithExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.sosy_lab.java_smt.api.BooleanFormula

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

internal const val RESULT_VAR_NAME = "${'$'}result"

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
        // clear the solverTrace (for debugging purposes only)
        solverState.solverTrace.clear()
        // now go on and check the body
        declaration.stableBody()?.let { body ->
            solverState.checkDeclarationWithBody(
                constraints, context,
                resultVarName, declaration, body
            ).drain()
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
): ContSeq<Unit> =
    continuationBracket.map {
        val inconsistentPreconditions =
            checkPreconditionsInconsistencies(constraints, context, declaration)
        ensure(!inconsistentPreconditions)
    }.flatMap {
        checkExpressionConstraints(resultVarName, body, context, ReturnPoints(resultVarName, emptyMap()))
    }.onEach {
        checkPostConditionsImplication(constraints, context, declaration)
    }

// 2.2: expressions
// ----------------

/**
 * Maps return points to the SMT variables representing that place.
 */
data class ReturnPoints(
    val topMostReturnPointVariableName: String,
    val namedReturnPointVariableNames: Map<String, String>
) {

    // fun replaceTopMost(newVariableName: String) =
    //   ReturnPoints(newVariableName, namedReturnPointVariableNames)

    fun add(returnPoint: String, variableName: String) =
        ReturnPoints(
            topMostReturnPointVariableName,
            namedReturnPointVariableNames + (returnPoint to variableName)
        )
}

/**
 * Produces a continuation that when invoked
 * recursively checks an [expression] set of constraints
 */
private fun SolverState.checkExpressionConstraints(
    associatedVarName: String,
    expression: KtExpression?,
    context: DeclarationCheckerContext,
    returnPoints: ReturnPoints
): ContSeq<Unit> =
    when (expression) {
        is KtParenthesizedExpression ->
            checkExpressionConstraints(associatedVarName, expression.expression, context, returnPoints)
        is KtBlockExpression ->
            checkBlockExpression(associatedVarName, expression.statements, context, returnPoints)
        is KtReturnExpression ->
            checkReturnConstraints(expression, context, returnPoints)
        is KtConstantExpression ->
            checkConstantExpression(associatedVarName, expression)
        is KtSimpleNameExpression ->
            checkNameExpression(associatedVarName, expression)
        is KtLabeledExpression ->
            checkExpressionConstraints(
                associatedVarName, expression.baseExpression, context,
                // add the return point to the list and recur
                returnPoints.add(expression.name!!, associatedVarName)
            )
        is KtDeclaration ->
            doOnlyWhen(!expression.isVar()) {
                val declName = when (expression) {
                    // use the given name if available
                    is KtNamedDeclaration -> expression.nameAsSafeName.asString()
                    else -> names.newName("decl")
                }
                checkDeclarationExpression(declName, expression, context, returnPoints)
            }
        is KtIfExpression ->
            checkSimpleConditional(associatedVarName, expression.computeSimpleConditions(), context, returnPoints)
        is KtWhenExpression ->
            if (expression.subjectExpression != null) {
                ContSeq.unit // TODO: handle `when` with subject
            } else {
                checkSimpleConditional(associatedVarName, expression.computeSimpleConditions(), context, returnPoints)
            }
        else -> {
            // fall-through case
            // try to treat it as a function call (for +, -, and so on)
            val resolvedCall = expression?.getResolvedCall(context.trace.bindingContext)
            doOnlyWhen(resolvedCall != null) {
                checkCallExpression(associatedVarName, expression!!, resolvedCall!!, context, returnPoints)
            }
        }
    }

private fun KtDeclaration.isVar(): Boolean = when (this) {
    is KtVariableDeclaration -> this.isVar
    else -> false
}

/**
 * Checks each statement in a block expression in order.
 * We need our own function because only the *last* statement
 * is the one assigned as the "return value" of the block.
 */
private fun SolverState.checkBlockExpression(
    associatedVarName: String,
    expressions: List<KtExpression>,
    context: DeclarationCheckerContext,
    returnPoints: ReturnPoints
): ContSeq<Unit> =
    when (expressions.size) {
        0 -> ContSeq.unit
        1 -> // this is the last element, so it's the return value of the expression
            checkExpressionConstraints(associatedVarName, expressions[0], context, returnPoints)
        else -> {
            val first = expressions[0]
            val remainder = expressions.drop(1)
            val inventedName = names.newName("stmt")
            checkExpressionConstraints(inventedName, first, context, returnPoints).flatMap {
                checkBlockExpression(associatedVarName, remainder, context, returnPoints)
            }
        }
    }

/**
 * Checks a 'return' or 'return@label' statement.
 * At the end it aborts the current computation, because
 * after a return there's nothing else to be checked.
 */
private fun SolverState.checkReturnConstraints(
    expression: KtReturnExpression,
    context: DeclarationCheckerContext,
    returnPoints: ReturnPoints
): ContSeq<Unit> {
    // figure out the right variable to assign
    // - if 'return@label', find the label in the recorded return points
    // - otherwise, it should be the top-most one
    val returnVarName = expression.getLabelName()?.let {
        returnPoints.namedReturnPointVariableNames[it]
    } ?: returnPoints.topMostReturnPointVariableName
    // and now assign it
    return checkExpressionConstraints(returnVarName, expression.returnedExpression, context, returnPoints)
        .flatMap { ContSeq.abort } // Stop the computation after this
}

/**
 * Produces a continuation that when invoked
 * recursively checks the call [resolvedCall]
 * starting from its arguments
 */
private fun SolverState.checkCallExpression(
    associatedVarName: String,
    expression: KtExpression,
    resolvedCall: ResolvedCall<out CallableDescriptor>,
    context: DeclarationCheckerContext,
    returnPoints: ReturnPoints
): ContSeq<Unit> =
    when (val specialCase = specialCasingForResolvedCalls(resolvedCall)) {
        null ->
            checkCallArguments(resolvedCall, context, returnPoints)
                .map { it.toMap() }
                .map { argVars ->
                    val callConstraints = constraintsFromSolverState(resolvedCall)?.let {
                        val completeRenaming = argVars + (RESULT_VAR_NAME to associatedVarName)
                        solver.renameDeclarationConstraints(it, completeRenaming)
                    }
                    // check pre-conditions and post-conditions
                    checkCallPreConditionsImplication(callConstraints, context, expression, resolvedCall)
                    val inconsistentPostConditions =
                        checkCallPostConditionsInconsistencies(callConstraints, context, expression, resolvedCall)
                    // there's no point in continuing if we are in an inconsistent position
                    ensure(!inconsistentPostConditions)
                }
        else ->
            checkCallArguments(resolvedCall, context, returnPoints)
                .map { argVars ->
                    val result =
                        if (expression.kotlinType(context.trace.bindingContext)?.isBoolean() == true)
                            solver.makeBooleanObjectVariable(associatedVarName)
                        else
                            solver.makeIntegerObjectVariable(associatedVarName)
                    val arg1 = solver.makeIntegerObjectVariable(argVars[0].second)
                    val arg2 = solver.makeIntegerObjectVariable(argVars[1].second)
                    specialCase(result, arg1, arg2)?.let { addConstraint(it) }
                }.void()
    }

/**
 * Recursively perform check on arguments,
 * including extension receiver and dispatch receiver
 *
 * [NOTE: argument renaming]
 *   this function creates a new name for each argument,
 *   based on the formal parameter name;
 *   this creates a renaming for the original constraints
 */
private fun SolverState.checkCallArguments(
    resolvedCall: ResolvedCall<out CallableDescriptor>,
    context: DeclarationCheckerContext,
    returnPoints: ReturnPoints
): ContSeq<List<Pair<String, String>>> =
    resolvedCall.allArgumentExpressions().map { (name, _, expr) ->
        val argUniqueName =
            if (expr != null && solver.isResultReference(expr, context.trace.bindingContext)) {
                RESULT_VAR_NAME
            } else {
                names.newName(name)
            }
        checkExpressionConstraints(argUniqueName, expr, context, returnPoints)
            .map { name to argUniqueName }
    }.sequence()

/**
 * This function produces a constraint that makes the desired variable name
 * equal to the value encoded in the constant and adds it to the
 * [SolverState.prover] constraints.
 */
private fun SolverState.checkConstantExpression(
    associatedVarName: String,
    expression: KtConstantExpression
): ContSeq<Unit> = cont {
    solver.formulae {
        val mayBoolean = expression.text.toBooleanStrictOrNull()
        val mayInteger = expression.text.toBigIntegerOrNull()
        val mayRational = expression.text.toBigDecimalOrNull()
        when {
            mayBoolean == true ->
                solver.makeBooleanObjectVariable(associatedVarName)
            mayBoolean == false ->
                solver.booleans { not(solver.makeBooleanObjectVariable(associatedVarName)) }
            mayInteger != null ->
                solver.ints {
                    equal(
                        solver.makeIntegerObjectVariable(associatedVarName),
                        makeNumber(mayInteger)
                    )
                }
            mayRational != null ->
                solver.rationals {
                    equal(
                        solver.decimalValue(solver.makeObjectVariable(associatedVarName)),
                        makeNumber(mayRational)
                    )
                }
            else -> null
        }?.let {
            addConstraint(it)
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
    context: DeclarationCheckerContext,
    returnPoints: ReturnPoints
): ContSeq<Unit> {
    val body = declaration.stableBody()
    return doOnlyWhen(body != null) {
        checkExpressionConstraints(newVarName, body, context, returnPoints)
    }
}

/**
 * This function produces a continuation that makes the desired variable name
 * equal to the value encoded in the named expression and adds the resulting boolean formula
 * to the [SolverState.prover] constraints.
 */
private fun SolverState.checkNameExpression(
    associatedVarName: String,
    expression: KtSimpleNameExpression
): ContSeq<Unit> = cont {
    // FIX: add only things in scope
    val referencedName = expression.getReferencedName().nameAsSafeName().asString()
    solver.objects {
        equal(solver.makeObjectVariable(associatedVarName), solver.makeObjectVariable(referencedName))
    }.let(::addConstraint)
}

/**
 * Data type used to handle `if` and `when` without subject uniformly.
 */
data class Condition(val condition: KtExpression?, val body: KtExpression, val whole: KtElement)

private fun KtExpression.computeSimpleConditions(): List<Condition> = when (this) {
    is KtIfExpression ->
        listOf(
            Condition(condition!!, then!!, then!!),
            Condition(null, `else`!!, `else`!!)
        )
    is KtWhenExpression ->
        entries.flatMap { entry ->
            if (entry.conditions.isEmpty()) {
                listOf(Condition(null, entry.expression!!, entry))
            } else {
                entry.conditions.toList().mapNotNull { cond ->
                    when (cond) {
                        is KtWhenConditionWithExpression ->
                            Condition(cond.expression!!, entry.expression!!, entry)
                        else -> null
                    }
                }
            }
        }
    else -> emptyList()
}

/**
 * Check `if` and `when` expressions without subject.
 */
private fun SolverState.checkSimpleConditional(
    associatedVarName: String,
    branches: List<Condition>,
    context: DeclarationCheckerContext,
    returnPoints: ReturnPoints
): ContSeq<Unit> =
    branches.map { cond ->
        val conditionVar = names.newName("cond")
        // introduce the condition
        (cond.condition?.let {
            checkExpressionConstraints(conditionVar, it, context, returnPoints)
        } ?: cont {
            // if we have no condition, it's equivalent to true
            addConstraint(solver.makeBooleanObjectVariable(conditionVar))
        }).map { Pair(cond, conditionVar) }
    }.sequence().flatMap { conditionInformation ->
        yesNo(conditionInformation)
            .asContSeq()
            .flatMap { (cond, correspondingVars) ->
                continuationBracket.map {
                    // assert the variables and check that we are consistent
                    val inconsistentEnvironment =
                        checkConditionsInconsistencies(correspondingVars, context, cond.whole)
                    // it only makes sense to continue if we are not consistent
                    ensure(!inconsistentEnvironment)
                }.flatMap {
                    // check the body
                    checkExpressionConstraints(associatedVarName, cond.body, context, returnPoints)
                }
            }
    }


/**
 * Given a list of names for condition variables,
 * create the boolean conditions for each branch.
 *
 * For example, given [a, b, c], it generates:
 * - a
 * - not a, b
 * - not a, not b, c
 */
private fun <A> SolverState.yesNo(conditionVars: List<Pair<A, String>>): List<Pair<A, List<BooleanFormula>>> {
    fun go(currents: List<Pair<A, String>>, acc: List<BooleanFormula>): List<Pair<A, List<BooleanFormula>>> =
        if (currents.isEmpty()) emptyList()
        else {
            solver.booleans {
                val varName = solver.makeBooleanObjectVariable(currents[0].second)
                val nextValue: List<BooleanFormula> = acc + listOf(varName)
                val nextAcc: List<BooleanFormula> = acc + listOf(not(varName))
                listOf(Pair(currents[0].first, nextValue)) + go(currents.drop(1), nextAcc)
            }
        }
    return go(conditionVars, emptyList())
}

/**
 * TODO I believe here instead of trying to see which fields represent a body we just need to recursively
 * visit the declaration as we do in [argsFormulae] where it uses an expression recursive visitor.
 * That will recursively visit all body element as well and there we can match just in those we care.
 */
private fun KtDeclaration.stableBody(): KtExpression? = when (this) {
    is KtVariableDeclaration -> if (isVar) null else initializer
    is KtDeclarationWithBody -> body()
    is KtDeclarationWithInitializer -> initializer
    else -> null
}
