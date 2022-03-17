package arrow.meta.plugins.analysis.phases.analysis.solver.check

import arrow.meta.continuations.ContSeq
import arrow.meta.continuations.doOnlyWhen
import arrow.meta.continuations.doOnlyWhenNotNull
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.MemberDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PropertyDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CallableDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ClassOrObject
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.EnumEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.PrimaryConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Property
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SecondaryConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeAlias
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorIds
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages
import arrow.meta.plugins.analysis.phases.analysis.solver.hasImplicitPrimaryConstructor
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
  if (!hadParseErrors() && declaration.shouldBeAnalyzed() && descriptor.shouldBeAnalyzed()) {
    // trace
    solverTrace.add("CHECKING ${descriptor.fqNameSafe.name}")
    // now go on and check the body
    try {
      when (declaration) {
        is TypeAlias -> ContSeq.unit // type alias have no checks
        is PrimaryConstructor -> checkPrimaryConstructor(context, descriptor, declaration)
        is SecondaryConstructor -> checkSecondaryConstructor(context, descriptor, declaration)
        is EnumEntry -> checkEnumEntry(context, descriptor, declaration)
        is ClassOrObject ->
          doOnlyWhen(
            declaration.hasImplicitPrimaryConstructor() && !descriptor.hasPackageWithLawsAnnotation
          ) {
            descriptor as ClassDescriptor
            checkImplicitPrimaryConstructor(
              context,
              descriptor.constructors.single { it.isPrimary },
              declaration
            )
          }
        is Property ->
          when {
            declaration.stableBody() != null ->
              checkTopLevelDeclarationWithBody(
                context,
                descriptor,
                declaration,
                declaration.stableBody()
              )
            declaration.delegate != null ->
              checkTopLevelDeclarationWithBody(
                context,
                descriptor,
                declaration,
                declaration.delegate?.expression
              )
            declaration.getter != null || declaration.setter != null -> {
              // no body, maybe we have getter and setter
              descriptor as PropertyDescriptor
              ContSeq.unit
                .map {
                  doOnlyWhenNotNull(declaration.getter, Unit) { getterDecl ->
                    doOnlyWhenNotNull(descriptor.getter, Unit) { getterDescr ->
                      checkTopLevelDeclarationWithBody(
                        context,
                        getterDescr,
                        getterDecl,
                        getterDecl.body()
                      )
                    }
                  }
                }
                .map {
                  doOnlyWhenNotNull(declaration.setter, Unit) { setterDecl ->
                    doOnlyWhenNotNull(descriptor.setter, Unit) { setterDescr ->
                      checkTopLevelDeclarationWithBody(
                        context,
                        setterDescr,
                        setterDecl,
                        setterDecl.body()
                      )
                    }
                  }
                }
            }
            else -> ContSeq.unit
          }
        else ->
          checkTopLevelDeclarationWithBody(
            context,
            descriptor,
            declaration,
            declaration.stableBody()
          )
      }.drain()
    } catch (e: IllegalStateException) {
      val msg = ErrorMessages.Exception.illegalState(solverTrace)
      context.handleError(ErrorIds.Exception.IllegalState, declaration, msg)
    } catch (e: Exception) {
      val msg = ErrorMessages.Exception.otherException(e)
      context.handleError(ErrorIds.Exception.OtherException, declaration, msg)
    }
    // trace
    solverTrace.add("FINISH ${descriptor.fqNameSafe.name}")
  }
}

/**
 * Only elements which are not
 * - inside another "callable declaration" (function, property, etc.) (b/c this is not yet
 * supported)
 * - or constructors (b/c they are handled at the level of class)
 */
private fun Declaration.shouldBeAnalyzed() = !(this.parents.any { it is CallableDeclaration })

private fun DeclarationDescriptor.shouldBeAnalyzed() =
  when (this) {
    is MemberDescriptor -> !isExpect && !isExternal
    else -> true
  }
