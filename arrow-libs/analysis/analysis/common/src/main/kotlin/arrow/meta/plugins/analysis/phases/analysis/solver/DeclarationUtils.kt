package arrow.meta.plugins.analysis.phases.analysis.solver

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Class
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ClassOrObject
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DeclarationContainer
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.PureClassOrObject
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ReturnExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.check.stableBody

internal fun DeclarationContainer?.singleFieldGroups(
  context: ResolutionContext
): Set<Set<DeclarationDescriptor>> = listOfNotNull(this).singleFieldGroups(context)

internal fun Iterable<DeclarationContainer>.singleFieldGroups(
  context: ResolutionContext
): Set<Set<DeclarationDescriptor>> {
  val result = mutableSetOf<Set<DeclarationDescriptor>>()
  forEach {
    it.declarations.forEach { decl ->
      val descriptor = context.descriptorFor(decl)
      if (descriptor?.isField() == true) {
        result.joinElements(setOfNotNull(descriptor, decl.asSingleField(context)))
      }
    }
  }
  return result
}

internal fun MutableSet<Set<DeclarationDescriptor>>.joinElements(
  elements: Set<DeclarationDescriptor>
) {
  val inElement = { s: DeclarationDescriptor -> elements.any { e -> s.fqNameSafe == e.fqNameSafe } }
  val wanted = filter { set -> set.any(inElement) }
  removeAll(wanted.toSet())
  add((wanted.flatten().filterNot(inElement) + elements).toSet())
}

/**
 * Finds whether a declaration is a simple field reference, like in
 *
 * class A(val n: Int) { fun getValue() = n }
 *
 * This is specially relevant for Java getters
 */
internal fun Declaration.asSingleField(context: ResolutionContext): CallableDescriptor? =
  when (val body = stableBody()) {
    is BlockExpression -> {
      val filtered =
        body.statements.mapNotNull { stmt ->
          val call = stmt.getResolvedCall(context)
          when (call?.specialKind) {
            null -> stmt
            SpecialKind.Post -> call.getReceiverOrThisNamedArgument()
            else -> null // drop the other special calls
          }
        }
      filtered.singleOrNull()?.let { single ->
        when {
          single is ReturnExpression -> single.returnedExpression?.findSingleField(context)
          body.implicitReturnFromLast -> single.findSingleField(context)
          else -> null
        }
      }
    }
    is ReturnExpression -> body.returnedExpression?.findSingleField(context)
    else -> null
  }

private fun Expression.findSingleField(context: ResolutionContext): CallableDescriptor? =
  getResolvedCall(context)?.resultingDescriptor?.takeIf { it.isField() }

private fun ClassOrObject.isInterfaceOrEnum(): Boolean =
  when (this) {
    is Class -> this.isInterface() || this.isEnum()
    else -> false
  }

private fun ClassOrObject.isCompanionObject(): Boolean =
  fqName != null &&
    this.parents.any { parent ->
      parent is PureClassOrObject &&
        parent.companionObjects.any { companion -> fqName == companion?.fqName }
    }

internal fun ClassOrObject.hasImplicitPrimaryConstructor(): Boolean =
  !isInterfaceOrEnum() &&
    !isCompanionObject() &&
    hasPrimaryConstructor() &&
    primaryConstructor == null
