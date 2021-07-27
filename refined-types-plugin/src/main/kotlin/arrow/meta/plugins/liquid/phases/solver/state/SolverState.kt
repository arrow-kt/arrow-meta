package arrow.meta.plugins.liquid.phases.solver.state

import arrow.meta.Meta
import arrow.meta.continuations.*
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.body
import arrow.meta.plugins.liquid.errors.MetaErrors.*
import arrow.meta.plugins.liquid.phases.solver.Solver
import arrow.meta.plugins.liquid.phases.solver.collector.*
import arrow.meta.plugins.liquid.phases.solver.collector.constraints
import arrow.meta.plugins.liquid.phases.solver.collector.postCall
import arrow.meta.plugins.liquid.phases.solver.collector.preCall
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.codegen.kotlinType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.fir.lightTree.converter.nameAsSafeName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.typeUtil.isInt
import org.sosy_lab.java_smt.api.*
import java.util.concurrent.atomic.AtomicReference

data class SolverState(
  val solver: Solver = Solver(),
  val prover: ProverEnvironment = solver.newProverEnvironment(
    SolverContext.ProverOptions.GENERATE_MODELS,
    SolverContext.ProverOptions.GENERATE_UNSAT_CORE),
  val callableConstraints: MutableList<DeclarationConstraints> = mutableListOf(),
  val names: NameProvider = NameProvider()
) {

  private var stage = Stage.Init

  fun currentStage(): Stage = stage

  fun collecting(): Unit {
    stage = Stage.CollectConstraints
  }

  fun collectionEnds(): Unit {
    stage = Stage.Prove
  }

  fun <A> bracket(f: () -> A): A {
    prover.push()
    val result = f()
    prover.pop()
    return result
  }

  fun isIn(that: Stage) = stage == that

  companion object {

    fun key(moduleDescriptor: ModuleDescriptor): String =
      "SolverState-${moduleDescriptor.name}"
  }

  enum class Stage {
    Init, CollectConstraints, Prove
  }
}

class NameProvider {
  private val counter = AtomicReference(0)

  fun newName(prefix: String): String {
    val n = counter.getAndUpdate { it + 1 }
    return "${prefix}${n}"
  }
}

internal fun Meta.solverStateAnalysis(): ExtensionPhase =
  Composite(
    listOf(
      analysis(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          ensureSolverStateInitialization(module)
          null
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          finalizeConstraintsCollection(module, bindingTrace)
        },
      ),
      declarationChecker { declaration, descriptor, context ->
        collectDeclarationsConstraints(context, declaration, descriptor)
      },
      declarationChecker { declaration, descriptor, context ->
        checkDeclarationConstraints(context, declaration, descriptor)
      }
    )
  )

// PHASE 1: COLLECTION OF CONSTRAINTS
// ==================================

internal fun SolverState.constraintsFromSolverState(resolvedCall: ResolvedCall<*>): DeclarationConstraints? =
  callableConstraints.firstOrNull {
    resolvedCall.resultingDescriptor.fqNameSafe == it.descriptor.fqNameSafe
  }

internal fun SolverState.constraintsFromSolverState(descriptor: DeclarationDescriptor): DeclarationConstraints? =
  callableConstraints.firstOrNull {
    descriptor.fqNameSafe == it.descriptor.fqNameSafe
  }

private fun CompilerContext.collectDeclarationsConstraints(
  context: DeclarationCheckerContext,
  declaration: KtDeclaration,
  descriptor: DeclarationDescriptor
) {
  val solverState = get<SolverState>(SolverState.key(context.moduleDescriptor))
  if (solverState != null && (solverState.isIn(SolverState.Stage.Init) || solverState.isIn(SolverState.Stage.CollectConstraints))) {
    solverState.collecting()
    val solver = solverState.solver
    val constraints = declaration.constraints(solver, context.trace.bindingContext)
    solverState.addConstraintsToSolverState(constraints, descriptor, declaration)
  }
}

private fun SolverState.addConstraintsToSolverState(
  constraints: List<Pair<ResolvedCall<*>, BooleanFormula>>,
  descriptor: DeclarationDescriptor,
  declaration: KtDeclaration
) {
  if (constraints.isNotEmpty()) {
    val preConstraints = arrayListOf<BooleanFormula>()
    val postConstraints = arrayListOf<BooleanFormula>()
    constraints.forEach { (call, formula) ->
      if (call.preCall()) preConstraints.add(formula)
      if (call.postCall()) postConstraints.add(formula)
    }
    callableConstraints.add(
      DeclarationConstraints(descriptor, declaration, preConstraints, postConstraints)
    )
  }
}

private fun CompilerContext.finalizeConstraintsCollection(
  module: ModuleDescriptor,
  bindingTrace: BindingTrace
): AnalysisResult? {
  val solverState = get<SolverState>(SolverState.key(module))
  return if (solverState != null && solverState.isIn(SolverState.Stage.CollectConstraints)) {
    solverState.collectionEnds()
    AnalysisResult.RetryWithAdditionalRoots(bindingTrace.bindingContext, module, emptyList(), emptyList())
  } else null
}

private fun CompilerContext.ensureSolverStateInitialization(
  module: ModuleDescriptor
) {
  val solverState = get<SolverState>(SolverState.key(module))
  if (solverState == null) {
    val state = SolverState()
    set(SolverState.key(module), state)
  }
}

// PHASE 2: CHECKING OF CONSTRAINTS
// ================================

val RESULT_VAR_NAME = "${'$'}result"

// 2.0: entry point

private fun CompilerContext.checkDeclarationConstraints(
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

private fun SolverState.checkPreconditionsInconsistencies(
  constraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  declaration: KtDeclaration
): SimpleCont<Boolean> = wrap {
  constraints?.pre?.let {
    addAndCheckConsistency(it) { unsatCore ->
      context.trace.report(
        InconsistentBodyPre.on(declaration.psiOrParent, declaration, unsatCore)
      )
    }
  } ?: false // if there are no preconditions, they are consistent
}

private fun SolverState.checkPostConditionsImplication(
  constraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  declaration: KtDeclaration
): SimpleCont<Unit> = wrap {
  constraints?.post?.forEach { postCondition ->
    checkImplicationOf(postCondition) {
      context.trace.report(
        UnsatBodyPost.on(declaration.psiOrParent, declaration, listOf(postCondition))
      )
    }
  }
}

// 2.2: expressions
// ----------------

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
    else -> expression?.getChildrenOfType<KtExpression>()?.toList()?.contEach {
      checkExpressionConstraints(associatedVarName, it, context)
    }?.forget().orDoNothing()
  }

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
      .orElse { continueWith<Unit, Map<String, String>>(emptyMap()) }
    // obtain and rename the pre- and post-conditions
      .then { argVars ->
        wrap {
          val callConstraints = resolvedCall?.let { constraintsFromSolverState(it) }?.let {
            val completeRenaming = argVars + (RESULT_VAR_NAME to associatedVarName)
            solver.renameDeclarationConstraints(it, completeRenaming)
          }
          // check pre-conditions
          callConstraints?.pre?.forEach { callPreCondition ->
            checkImplicationOf(callPreCondition) {
              context.trace.report(
                UnsatCallPre.on(expression.psiOrParent, resolvedCall, listOf(callPreCondition))
              )
            }
          }
          // assert post-conditions (inconsistent means unreachable code)
          callConstraints?.post?.let {
            addAndCheckConsistency(it) { unsatCore ->
              context.trace.report(
                InconsistentCallPost.on(expression.psiOrParent, resolvedCall, unsatCore)
              )
            }
          }
          // and done!
          Unit
        }
      }
  }

// this function makes the desired variable name
// equal to the value encoded in the constant
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

private fun SolverState.checkDeclarationExpression(
  newVarName: String,
  declaration: KtDeclaration,
  context: DeclarationCheckerContext
): SimpleCont<Unit> = declaration.stableBody()?.let {
    checkExpressionConstraints(newVarName, it, context)
  }.orDoNothing()

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

private fun KtDeclaration.stableBody(): KtExpression?
  = when (this) {
      is KtVariableDeclaration -> if (isVar) null else initializer
      is KtDeclarationWithBody -> body()
      is KtDeclarationWithInitializer -> initializer
      else -> null
    }

// SOLVER INTERACTION
// ==================
// these two functions ultimately call the SMT solver,
// and report errors as desired

private fun SolverState.addAndCheckConsistency(
  constraints: Iterable<BooleanFormula>,
  message: (unsatCore: List<BooleanFormula>) -> Unit
): Boolean {
  constraints.forEach { prover.addConstraint(it) }
  val unsat = prover.isUnsat
  if (unsat) { message(prover.unsatCore) }
  return unsat
}

private fun SolverState.checkImplicationOf(
  constraint: BooleanFormula,
  message: (model: Model) -> Unit
): Boolean =
  bracket {
    solver.booleans { prover.addConstraint(not(constraint)) }
    val unsat = prover.isUnsat
    if (!unsat) { message(prover.model) }
    !unsat
  }