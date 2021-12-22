package arrow.meta.plugins.analysis.phases.analysis.solver

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableMemberDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ConstructorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.FunctionDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.MemberScope
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PropertyDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeAliasDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.DeclarationConstraints
import arrow.meta.plugins.analysis.smt.Solver
import arrow.meta.plugins.analysis.smt.renameDeclarationConstraints
import java.util.LinkedList

/** Obtain the descriptors which have been overridden by a declaration, if they exist */
fun DeclarationDescriptor.overriddenDescriptors(): Collection<DeclarationDescriptor>? =
  when (this) {
    is CallableMemberDescriptor -> this.overriddenDescriptors
    else -> null
  }

internal val DeclarationDescriptor.topMostOverriden: DeclarationDescriptor
  get() = overriddenDescriptors()?.first { it.overriddenDescriptors().isNullOrEmpty() } ?: this

/**
 * returns true if we have declared something with a @Law or lives inside an object that inherits
 * Laws
 */
fun DeclarationDescriptor.isALaw(): Boolean =
  annotations().hasAnnotation(FqName("arrow.analysis.Law"))

/** returns true if the type inherits Laws */
internal fun DeclarationDescriptor?.isLawsType(): Boolean =
  this?.annotations()?.hasAnnotation(FqName("arrow.analysis.Laws")) ?: false

/**
 * check if a descriptor is compatible with other, in the sense that they refer to a function with
 * the same signature
 */
fun DeclarationDescriptor.isCompatibleWith(other: DeclarationDescriptor): Boolean =
  when {
    this is CallableDescriptor && other is CallableDescriptor -> {
      // we have to ignore the parameters which come from Laws
      val params1 =
        this.allParameters.filter { param ->
          !param.type.descriptor.isLawsType() &&
            !(this is ConstructorDescriptor && param is ReceiverParameterDescriptor)
        }
      val params2 =
        other.allParameters.filter { param ->
          !param.type.descriptor.isLawsType() &&
            !(other is ConstructorDescriptor && param is ReceiverParameterDescriptor)
        }
      params1.size == params2.size &&
        params1.zip(params2).all { (p1, p2) -> p1.type.isEqualTo(p2.type) }
    }
    else -> true
  }

/**
 * check if a descriptor is compatible with other, in the sense that the arguments are (possibly)
 * supertypes
 */
fun DeclarationDescriptor.isLooselyCompatibleWith(other: DeclarationDescriptor): Boolean =
  when {
    this is CallableDescriptor && other is CallableDescriptor -> {
      // we have to ignore the parameters which come from Laws
      val params1 =
        this.allParameters.filter { param ->
          !param.type.descriptor.isLawsType() &&
            !(this is ConstructorDescriptor && param is ReceiverParameterDescriptor)
        }
      val params2 =
        other.allParameters.filter { param ->
          !param.type.descriptor.isLawsType() &&
            !(other is ConstructorDescriptor && param is ReceiverParameterDescriptor)
        }
      params1.size == params2.size &&
        params1.zip(params2).all { (p1, p2) -> p2.type.isSubtypeOf(p1.type) }
    }
    else -> true
  }

/** should we treat a node as a field and create 'field(name, x)'? */
fun DeclarationDescriptor.isField(): Boolean =
  when (this) {
    is PropertyDescriptor -> hasOneReceiver() && !(returnType?.descriptor?.isFun ?: false)
    is FunctionDescriptor ->
      valueParameters.isEmpty() && hasOneReceiver() && !(returnType?.descriptor?.isFun ?: false)
    else -> false
  }

/** has a dispatch or extension receiver, but not both */
internal fun CallableDescriptor.hasOneReceiver(): Boolean =
  (extensionReceiverParameter != null && dispatchReceiverParameter == null) ||
    (extensionReceiverParameter == null && dispatchReceiverParameter != null)

/** Gather all descriptors which satisfy a predicate, going inside every element recursively. */
fun DeclarationDescriptor.gather(
  predicate: (DeclarationDescriptor) -> Boolean
): List<DeclarationDescriptor> {
  // we fake the initial scope
  val fakeMemberScope =
    object : MemberScope {
      override fun getClassifierNames(): Set<Name> =
        throw IllegalStateException("not available here")
      override fun getFunctionNames(): Set<Name> = throw IllegalStateException("not available here")
      override fun getVariableNames(): Set<Name> = throw IllegalStateException("not available here")
      override fun getContributedDescriptors(
        filter: (name: String) -> Boolean
      ): List<DeclarationDescriptor> =
        listOf(this@gather).filter { decl -> filter(decl.name.value) }
    }
  val scopesWorklist = LinkedList<MemberScope>(listOf(fakeMemberScope))
  // initialize place for results
  val result = mutableListOf<DeclarationDescriptor>()

  // the work
  while (scopesWorklist.isNotEmpty()) {
    // work to do in a member scope
    val scope = scopesWorklist.remove()
    // 1. get all descriptors
    val descriptors = scope.getContributedDescriptors { true }
    // 2. add the interesting ones to the result
    result.addAll(descriptors.filter(predicate))
    // 3. add all new member scopes to the worklist
    scopesWorklist.addAll(
      descriptors.filterIsInstance<ClassDescriptor>().map { it.completeUnsubstitutedScope }
    )
    scopesWorklist.addAll(
      descriptors.filterIsInstance<TypeAliasDescriptor>().mapNotNull {
        it.classDescriptor?.completeUnsubstitutedScope
      }
    )
  }

  return result.toList()
}

/** Gather all descriptors which satisfy a predicate, going inside every element recursively. */
fun ModuleDescriptor.gather(
  initialPackages: List<FqName> = listOf(FqName("")),
  addSubPackages: Boolean,
  predicate: (DeclarationDescriptor) -> Boolean
): List<DeclarationDescriptor> {
  // initialize worklists
  val packagesWorklist = LinkedList(initialPackages)
  val scopesWorklist = LinkedList<MemberScope>()
  // initialize place for results
  val result = mutableListOf<DeclarationDescriptor>()

  // the work
  while (true) {
    if (scopesWorklist.isNotEmpty()) {
      // work to do in a member scope
      val scope = scopesWorklist.remove()
      // 1. get all descriptors
      val descriptors = scope.getContributedDescriptors { true }
      // 2. add the interesting ones to the result
      result.addAll(descriptors.filter(predicate))
      // 3. add all new member scopes to the worklist
      scopesWorklist.addAll(
        descriptors.filterIsInstance<ClassDescriptor>().map { it.completeUnsubstitutedScope }
      )
      scopesWorklist.addAll(
        descriptors.filterIsInstance<TypeAliasDescriptor>().mapNotNull {
          it.classDescriptor?.completeUnsubstitutedScope
        }
      )
    } else if (packagesWorklist.isNotEmpty()) {
      // work to do in a package
      val pkg = packagesWorklist.remove()
      // 1. add the scope to the worklist
      getPackage(pkg.name)?.memberScope?.let { scopesWorklist.add(it) }
      // 2. add the subpackages to the worklist
      if (addSubPackages) packagesWorklist.addAll(getSubPackagesOf(pkg))
    } else {
      break
    }
  }

  return result.toList()
}

/** Rename the conditions from one descriptor to the names of another */
internal fun Solver.renameConditions(
  constraints: DeclarationConstraints,
  to: DeclarationDescriptor
): DeclarationConstraints {
  val fromParams =
    (constraints.descriptor as? CallableDescriptor)?.valueParameters?.map { it.name.value }
  val toParams = (to as? CallableDescriptor)?.valueParameters?.map { it.name.value }
  return if (fromParams != null && toParams != null) {
    val renamed = renameDeclarationConstraints(constraints, fromParams.zip(toParams).toMap())
    DeclarationConstraints(to, renamed.pre, renamed.post, renamed.doNotLookAtArgumentsWhen)
  } else {
    DeclarationConstraints(
      to,
      constraints.pre,
      constraints.post,
      constraints.doNotLookAtArgumentsWhen
    )
  }
}
