package arrow.meta.plugins.analysis.phases.analysis.solver.check

import arrow.meta.continuations.cont
import arrow.meta.continuations.doOnlyWhen
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CallableDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Class
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ClassOrObject
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.EnumEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.PrimaryConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SecondaryConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState

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

/** When the solver is in the prover state check this [declaration] body constraints */
public fun SolverState.checkDeclarationConstraints(
  context: ResolutionContext,
  declaration: Declaration,
  descriptor: DeclarationDescriptor
) {
  if (!hadParseErrors() && declaration.shouldBeAnalyzed()) {
    // trace
    solverTrace.add("CHECKING ${descriptor.fqNameSafe.name}")
    // now go on and check the body
    when (declaration) {
      is PrimaryConstructor -> checkPrimaryConstructor(context, descriptor, declaration)
      is SecondaryConstructor -> checkSecondaryConstructor(context, descriptor, declaration)
      is EnumEntry -> checkEnumEntry(context, descriptor, declaration)
      is ClassOrObject ->
        doOnlyWhen(
          !declaration.isInterfaceOrEnum() &&
            declaration.hasPrimaryConstructor() &&
            declaration.primaryConstructor == null &&
            !descriptor.hasPackageWithLawsAnnotation
        ) {
          cont {
            val msg = ErrorMessages.Unsupported.unsupportedImplicitPrimaryConstructor(declaration)
            context.reportUnsupported(declaration, msg)
          }
        }
      else -> checkTopLevelDeclarationWithBody(context, descriptor, declaration)
    }.drain()
    // trace
    solverTrace.add("FINISH ${descriptor.fqNameSafe.name}")
  }
}

private fun ClassOrObject.isInterfaceOrEnum(): Boolean =
  when (this) {
    is Class -> this.isInterface() || this.isEnum()
    else -> false
  }

/**
 * Only elements which are not
 * - inside another "callable declaration" (function, property, etc.) (b/c this is not yet
 * supported)
 * - or constructors (b/c they are handled at the level of class)
 */
private fun Declaration.shouldBeAnalyzed() = !(this.parents.any { it is CallableDeclaration })
