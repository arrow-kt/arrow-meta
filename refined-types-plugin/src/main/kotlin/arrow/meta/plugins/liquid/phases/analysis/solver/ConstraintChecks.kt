package arrow.meta.plugins.liquid.phases.analysis.solver

import arrow.meta.continuations.*
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.body
import arrow.meta.plugins.liquid.errors.MetaErrors
import arrow.meta.plugins.liquid.phases.solver.collector.renameDeclarationConstraints
import arrow.meta.plugins.liquid.smt.*
import org.jetbrains.kotlin.codegen.kotlinType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.fir.builder.toFirOperation
import org.jetbrains.kotlin.fir.lightTree.converter.nameAsSafeName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.jetbrains.kotlin.types.typeUtil.isInt
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FormulaType
import org.sosy_lab.java_smt.api.NumeralFormula

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

private const val RESULT_VAR_NAME = "${'$'}result"

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
    checkExpressionConstraints(resultVarName, body, context, ReturnPoints(resultVarName, emptyMap()))
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
 * Maps return points to the SMT variables representing that place.
 */
data class ReturnPoints(
  val topMostReturnPointVariableName: String,
  val namedReturnPointVariableNames: Map<String, String>) {

  fun replaceTopMost(newVariableName: String) =
    ReturnPoints(newVariableName, namedReturnPointVariableNames)

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
): SimpleCont<Unit> =
  when (expression) {
    is KtBlockExpression ->
      checkBlockExpression(associatedVarName, expression.statements, context, returnPoints)
    is KtReturnExpression ->
      checkReturnConstraints(expression, context, returnPoints)
    is KtConstantExpression ->
      checkConstantExpression(associatedVarName, expression)
    is KtSimpleNameExpression ->
      checkNameExpression(associatedVarName, expression, context)
    is KtLabeledExpression ->
      checkExpressionConstraints(
        associatedVarName, expression.baseExpression, context,
        // add the return point to the list and recur
        returnPoints.add(expression.name!!, associatedVarName))
    is KtDeclaration -> {
      val declName = when (expression) {
        // use the given name if available
        is KtNamedDeclaration -> expression.nameAsSafeName.asString()
        else -> names.newName("decl")
      }
      checkDeclarationExpression(declName, expression, context, returnPoints)
    }
    // fall-through cases
    else -> when {
      // either treat as conditional
      expression?.isConditional() == true ->
        checkConditional(associatedVarName, expression.conditionalBranches()!!, context, returnPoints)
      // or try to treat it as a function call (for +, -, and so on)
      else ->
        expression?.getResolvedCall(context.trace.bindingContext)?.let {
          checkCallExpression(associatedVarName, expression, it, context, returnPoints)
        }.orDoNothing()
    }
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
): SimpleCont<Unit> =
  when (expressions.size) {
    0 -> continueWith(Unit)
    1 -> // this is the last element, so it's the return value of the expression
      checkExpressionConstraints(associatedVarName, expressions[0], context, returnPoints)
    else -> {
      val first = expressions[0]
      val remainder = expressions.drop(1)
      val inventedName = names.newName("stmt")
      checkExpressionConstraints(inventedName, first, context, returnPoints). then {
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
): Cont<Unit, Unit> {
  // figure out the right variable to assign
  // - if 'return@label', find the label in the recorded return points
  // - otherwise, it should be the top-most one
  val returnVarName = expression.getLabelName()?.let {
    returnPoints.namedReturnPointVariableNames[it]
  } ?: returnPoints.topMostReturnPointVariableName
  // and now assign it
  return checkExpressionConstraints(returnVarName, expression.returnedExpression, context, returnPoints).then {
    abortWith(Unit)
  }
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
): SimpleCont<Unit> =
  when (val specialCase = specialCasingForResolvedCalls(resolvedCall)) {
    null ->
      checkCallArguments(resolvedCall, context, returnPoints).thenWrap { it.toMap() }.thenWrap { argVars ->
        constraintsFromSolverState(resolvedCall)?.let {
          val completeRenaming = argVars + (RESULT_VAR_NAME to associatedVarName)
          solver.renameDeclarationConstraints(it, completeRenaming)
        }
      }.then { callConstraints ->
        // check pre-conditions and post-conditions
        checkCallPreConditionsImplication(callConstraints, context, expression, resolvedCall).then {
          checkCallPostConditionsInconsistencies(callConstraints, context, expression, resolvedCall)
        }
      }.then { inconsistentPostConditions ->
        // there's no point in continuing if we are in an inconsistent position
        guard(!inconsistentPostConditions)
      }
    else ->  // this is the special case
      checkCallArguments(resolvedCall, context, returnPoints).thenWrap { argVars ->
        solver.ints {
          solver.booleans {
            var result =
              if (expression.kotlinType(context.trace.bindingContext)?.isBoolean() == true)
                this.makeVariable(associatedVarName)
              else
                this@ints.makeVariable(associatedVarName)
            val arg1 = this@ints.makeVariable(argVars[0].second)
            val arg2 = this@ints.makeVariable(argVars[1].second)
            val cstr = specialCase(result, arg1, arg2)
            prover.addConstraint(cstr)
          }
        }
      }.forget()
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
) = resolvedCall.allArgumentExpressions().contEach { (name, _, expr) ->
  val argUniqueName = names.newName(name)
  checkExpressionConstraints(argUniqueName, expr, context, returnPoints).then {
    continueWith(Pair(name, argUniqueName))
  }
}

/**
 * Checks the pre-conditions in [callConstraints] hold for [resolvedCall]
 */
private fun SolverState.checkCallPreConditionsImplication(
  callConstraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  expression: KtExpression,
  resolvedCall: ResolvedCall<out CallableDescriptor>
): SimpleCont<Unit> = wrap {
  callConstraints?.pre?.forEach { callPreCondition ->
    checkImplicationOf(callPreCondition) {
      context.trace.report(
        MetaErrors.UnsatCallPre.on(expression.psiOrParent, resolvedCall, listOf(callPreCondition))
      )
    }
  }
}

/**
 * Checks the post-conditions in [callConstraints] hold for [resolvedCall]
 */
private fun SolverState.checkCallPostConditionsInconsistencies(
  callConstraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  expression: KtExpression,
  resolvedCall: ResolvedCall<out CallableDescriptor>
): SimpleCont<Boolean> = wrap {
  callConstraints?.post?.let {
    addAndCheckConsistency(it) { unsatCore ->
      context.trace.report(
        MetaErrors.InconsistentCallPost.on(expression.psiOrParent, resolvedCall, unsatCore)
      )
    }
  } ?: false
}

// TODO: remove when we can obtain the laws
private fun SolverState.specialCasingForResolvedCalls(
  resolvedCall: ResolvedCall<out CallableDescriptor>,
): ((result: Formula, arg1: Formula, arg2: Formula) -> BooleanFormula?)? =
  solver.ints {
    solver.booleans {
      when (resolvedCall.resultingDescriptor.fqNameSafe) {
        FqName("kotlin.Int.equals") -> { result, arg1, arg2 ->
          equivalence(result as BooleanFormula, equal(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula))
        }
        FqName("kotlin.Int.plus") -> { result, arg1, arg2 ->
          equal(result as NumeralFormula.IntegerFormula, add(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula))
        }
        FqName("kotlin.Int.minus") -> { result, arg1, arg2 ->
          equal(result as NumeralFormula.IntegerFormula, subtract(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula))
        }
        FqName("kotlin.Int.times") -> { result, arg1, arg2 ->
          equal(result as NumeralFormula.IntegerFormula, multiply(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula))
        }
        FqName("kotlin.Int.div") -> { result, arg1, arg2 ->
          equal(result as NumeralFormula.IntegerFormula, divide(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula))
        }
        FqName("kotlin.Int.compareTo") -> {
          val op = (resolvedCall.call.callElement as? KtBinaryExpression)?.operationToken?.toFirOperation()?.operator
          when (op) {
            ">" -> { result, arg1, arg2 ->
              equivalence(result as BooleanFormula, greaterThan(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula))
            }
            ">=" -> { result, arg1, arg2 ->
              equivalence(result as BooleanFormula, greaterOrEquals(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula))
            }
            "<" -> { result, arg1, arg2 ->
              equivalence(result as BooleanFormula, lessThan(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula))
            }
            "<=" -> { result, arg1, arg2 ->
              equivalence(result as BooleanFormula, lessOrEquals(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula))
            }
            else -> null
          }
        }
        else -> null
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
  context: DeclarationCheckerContext,
  returnPoints: ReturnPoints
): SimpleCont<Unit> = declaration.stableBody()?.let {
  checkExpressionConstraints(newVarName, it, context, returnPoints)
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
): SimpleCont<Unit> = wrap {
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
 * Check each of the branches of a condition.
 */
private fun SolverState.checkConditional(
  associatedVarName: String,
  info: ConditionalBranches,
  context: DeclarationCheckerContext,
  returnPoints: ReturnPoints
): SimpleCont<Unit> = reifyCont<Unit, Unit> { cont ->
  // go over each element
  // we use a recursive function because
  // we need to nest the calls to [bracket]
  fun go(remainingBranches: List<ConditionalBranch>) {
    when (val first = remainingBranches.firstOrNull()) {
      null -> { }  // done
      else -> solver.booleans {
        val newVariables = introduceCondition(first.condition, context, returnPoints)
        // either the things are true
        bracket {
          // assert the variables and check that we are consistent
          val inconsistentEnvironment =
            checkConditionsInconsistencies<Boolean>(
              newVariables.map { makeVariable(it) },
              context, first.whole).runCont()
          // it only makes sense to continue if we are not consistent
          if (!inconsistentEnvironment) {
            // check the body
            checkExpressionConstraints(associatedVarName, first.body, context, returnPoints).runCont()
            // and now execute the rest of the analysis
            // by calling the continuation
            cont(Unit)
          }
        }
        // or they are false
        bracket {
          // assert the negation of the new variables
          newVariables.map {
            prover.addConstraint(not(makeVariable(it)))
          }
          // go on with the rest of the cases
          go(remainingBranches.drop(1))
        }
      }
    }
  }

  go(info.branches)
}

private fun SolverState.introduceCondition(
  expressions: List<Condition>?,
  context: DeclarationCheckerContext,
  returnPoints: ReturnPoints
): List<String> = expressions?.map {
  // create a new variable for the condition
  val conditionVar = names.newName("cond")
  // and now go and check it
  when (it) {
    is BooleanCondition ->
      checkExpressionConstraints(conditionVar, it.expression, context, returnPoints).runCont()
  }
  // return the variable
  conditionVar
} ?: emptyList()

/**
 * Checks the post-conditions in [callConstraints] hold for [resolvedCall]
 */
private fun <R> SolverState.checkConditionsInconsistencies(
  formulae: List<BooleanFormula>,
  context: DeclarationCheckerContext,
  expression: KtElement
): Cont<R, Boolean> = wrap {
  addAndCheckConsistency(formulae) { unsatCore ->
    context.trace.report(
      MetaErrors.InconsistentConditions.on(expression.psiOrParent, unsatCore)
    )
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
