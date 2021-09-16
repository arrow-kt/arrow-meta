package arrow.meta.plugins.liquid.phases.analysis.solver.check

import arrow.meta.continuations.cont
import arrow.meta.continuations.doOnlyWhen
import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.liquid.errors.MetaErrors
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages.Parsing.unsupportedImplicitPrimaryConstructor
import arrow.meta.plugins.liquid.phases.analysis.solver.state.SolverState
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

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
internal const val THIS_VAR_NAME = "this"

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
  if ((solverState != null) &&
    solverState.isIn(SolverState.Stage.Prove) &&
    !solverState.hadParseErrors() &&
    declaration.shouldBeAnalyzed()
  ) {
    // trace
    solverState.solverTrace.add("CHECKING ${descriptor.fqNameSafe.asString()}")
    // now go on and check the body
    when (declaration) {
      is KtPrimaryConstructor ->
        solverState.checkPrimaryConstructor(context, descriptor, declaration)
      is KtSecondaryConstructor ->
        solverState.checkSecondaryConstructor(context, descriptor, declaration)
      is KtClassOrObject ->
        doOnlyWhen(declaration.hasPrimaryConstructor() && declaration.primaryConstructor == null) {
          cont {
            val msg = unsupportedImplicitPrimaryConstructor(declaration)
            context.trace.report(MetaErrors.ErrorParsingPredicate.on(declaration, msg))
          }
        }
      else ->
        solverState.checkTopLevelDeclarationWithBody(context, descriptor, declaration)
    }.drain()
    // trace
    solverState.solverTrace.add("FINISH ${descriptor.fqNameSafe.asString()}")
  }
}

/**
 * Only elements which are not
 * - inside another "callable declaration" (function, property, etc.)
 *   (b/c this is not yet supported)
 * - or constructors (b/c they are handled at the level of class)
 */
private fun KtDeclaration.shouldBeAnalyzed() =
  !(this.parents.any { it is KtCallableDeclaration })
