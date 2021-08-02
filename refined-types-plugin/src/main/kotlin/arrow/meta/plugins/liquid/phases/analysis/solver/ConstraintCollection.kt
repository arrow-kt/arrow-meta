package arrow.meta.plugins.liquid.phases.analysis.solver

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.liquid.smt.Solver
import arrow.meta.plugins.liquid.smt.intDivide
import arrow.meta.plugins.liquid.smt.intEquals
import arrow.meta.plugins.liquid.smt.intGreaterThan
import arrow.meta.plugins.liquid.smt.intGreaterThanOrEquals
import arrow.meta.plugins.liquid.smt.intLessThan
import arrow.meta.plugins.liquid.smt.intLessThanOrEquals
import arrow.meta.plugins.liquid.smt.intMinus
import arrow.meta.plugins.liquid.smt.intMultiply
import arrow.meta.plugins.liquid.smt.intPlus
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotated
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.fir.builder.toFirOperation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.callExpressionRecursiveVisitor
import org.jetbrains.kotlin.psi.expressionRecursiveVisitor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.annotations.argumentValue
import org.jetbrains.kotlin.resolve.calls.callUtil.getReceiverExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.constants.ArrayValue
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.constants.StringValue
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isAnyOrNullableAny
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.jetbrains.kotlin.types.typeUtil.isInt
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FormulaType

// PHASE 1: COLLECTION OF CONSTRAINTS
// ==================================

/**
 * Looks up in the solver state previously collected constraints and
 * returns the constraints associated to this [resolvedCall] resulting descriptor if any
 */
internal fun SolverState.constraintsFromSolverState(resolvedCall: ResolvedCall<*>): DeclarationConstraints? =
  constraintsFromSolverState(resolvedCall.resultingDescriptor)

/**
 * Looks up in the solver state previously collected constraints and
 * returns the constraints associated to this [descriptor] if any
 */
internal fun SolverState.constraintsFromSolverState(descriptor: DeclarationDescriptor): DeclarationConstraints? =
  callableConstraints.firstOrNull {
    descriptor.fqNameSafe == it.descriptor.fqNameSafe
  }

/**
 * Collects constraints from all declarations and adds them to the solver state
 */
internal fun CompilerContext.collectDeclarationsConstraints(
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

/**
 * Gather constraints from the local module by inspecting
 * - Custom DSL elements (pre and post)
 * - Ad-hoc constraints over third party types TODO
 * - Annotated declarations in compiled third party dependency modules TODO
 */
internal fun KtDeclaration.constraints(
  solver: Solver,
  context: BindingContext
): List<Pair<ResolvedCall<*>, BooleanFormula>> =
  constraintsDSLElements().filterIsInstance<KtElement>().mapNotNull {
    val call = it.getResolvedCall(context)
    if (call != null && call.preOrPostCall()) {
      val f = solver.formula(call, context)
      f
    } else null
  }

/**
 * Recursively walks [this] element for calls to [arrow.refinement.pre] and [arrow.refinement.post]
 * that hold preconditions
 */
private fun KtElement.constraintsDSLElements(): Set<PsiElement> {
  val results = hashSetOf<PsiElement>()
  val visitor = callExpressionRecursiveVisitor {
    if (it.calleeExpression?.text == "pre" || it.calleeExpression?.text == "post") {
      results.add(it)
    }
  }
  accept(visitor)
  acceptChildren(visitor)
  return results
}

/**
 * Adds gathered [constraints] as an association to this [descriptor]
 * in the [SolverState]
 */
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
      DeclarationConstraints(descriptor, preConstraints, postConstraints)
    )
  }
}

private fun Annotated.preAnnotation(): AnnotationDescriptor? =
  annotations.firstOrNull { it.fqName == FqName("arrow.refinement.Pre") }

private fun Annotated.postAnnotation(): AnnotationDescriptor? =
  annotations.firstOrNull { it.fqName == FqName("arrow.refinement.Post") }


private val skipPackages = setOf(
  FqName("com.apple"),
  FqName("com.oracle"),
  FqName("org.omg"),
  FqName("com.sun"),
  FqName("META-INF"),
  FqName("jdk"),
  FqName("apple"),
  FqName("java"),
  FqName("javax"),
  FqName("kotlin"),
  FqName("sun")
)

internal tailrec fun ModuleDescriptor.declarationsWithConstraints(
  acc: List<DeclarationDescriptor> = emptyList(),
  packages: List<FqName> = listOf(FqName.ROOT),
  skipPacks: Set<FqName> = skipPackages
): List<DeclarationDescriptor> =
  when {
    packages.isEmpty() -> acc
    else -> {
      val current = packages.first()
      val topLevelDescriptors = getPackage(current).memberScope.getContributedDescriptors { true }.toList()
      val memberDescriptors = topLevelDescriptors.filterIsInstance<ClassDescriptor>().flatMap {
        it.unsubstitutedMemberScope.getContributedDescriptors { true }.toList()
      }
      val allPackageDescriptors = topLevelDescriptors + memberDescriptors
      val packagedProofs = allPackageDescriptors
        .filter {
          it.preAnnotation() != null || it.postAnnotation() != null
        }
      val remaining = (getSubPackagesOf(current) { true } + packages.drop(1)).filter { it !in skipPacks }
      declarationsWithConstraints(acc + packagedProofs.asSequence(), remaining)
    }
  }

internal fun SolverState.addClassPathConstraintsToSolverState(
  descriptor: DeclarationDescriptor
) {
  val constraints = descriptor.annotations.mapNotNull {
    if (it.fqName == FqName("arrow.refinement.Pre")) {
      val formulae = it.argumentValue("formulae") as? ArrayValue
      if (formulae != null) "pre" to formulae.value.filterIsInstance<StringValue>().map { solver.parse(it.value) }
      else null
    } else if (it.fqName == FqName("arrow.refinement.Post")) {
      val formulae = it.argumentValue("formulae") as? ArrayValue
      if (formulae != null) "post" to formulae.value.filterIsInstance<StringValue>().map { solver.parse(it.value) }
      else null
    } else null
  }
  if (constraints.isNotEmpty()) {
    val preConstraints = arrayListOf<BooleanFormula>()
    val postConstraints = arrayListOf<BooleanFormula>()
    constraints.forEach { (call, formula) ->
      if (call == "pre") preConstraints.addAll(formula)
      if (call == "post") postConstraints.addAll(formula)
    }
    callableConstraints.add(
      DeclarationConstraints(descriptor, preConstraints, postConstraints)
    )
  }
}

/**
 * Instructs the compiler analysis phase that we have finished collecting constraints
 * and its time to Rewind analysis for phase 2 [arrow.meta.plugins.liquid.phases.analysis.solver.checkDeclarationConstraints]
 */
internal fun CompilerContext.finalizeConstraintsCollection(
  module: ModuleDescriptor,
  bindingTrace: BindingTrace
): AnalysisResult? {
  val solverState = get<SolverState>(SolverState.key(module))
  return if (solverState != null && solverState.isIn(SolverState.Stage.CollectConstraints)) {
    module.declarationsWithConstraints().forEach {
      solverState.addClassPathConstraintsToSolverState(it)
    }
    solverState.collectionEnds()
    AnalysisResult.RetryWithAdditionalRoots(bindingTrace.bindingContext, module, emptyList(), emptyList())
  } else null
}

/**
 * returns true if [this] resolved call is calling [arrow.refinement.pre]
 */
internal fun ResolvedCall<out CallableDescriptor>.preCall(): Boolean =
  resultingDescriptor.fqNameSafe == FqName("arrow.refinement.pre")

/**
 * returns true if [this] resolved call is calling [arrow.refinement.post]
 */
internal fun ResolvedCall<out CallableDescriptor>.postCall(): Boolean =
  resultingDescriptor.fqNameSafe == FqName("arrow.refinement.post")

/**
 * returns true if [this] resolved call is calling [arrow.refinement.pre] or  [arrow.refinement.post]
 */
private fun ResolvedCall<out CallableDescriptor>.preOrPostCall(): Boolean =
  preCall() || postCall()

/**
 * Translates a [resolvedCall] into an smt [BooleanFormula]
 * Ex.
 * ```kotlin
 * x > 2
 * ```
 * ```smt
 * (x > 2)
 * ```
 */
private fun Solver.formula(
  resolvedCall: ResolvedCall<out CallableDescriptor>,
  bindingContext: BindingContext,
): Pair<ResolvedCall<out CallableDescriptor>, BooleanFormula>? =
  ints {
    val callable = resolvedCall.resultingDescriptor
    val call = callable.builtIns.run {
      call(resolvedCall, bindingContext)
    }
    when (call) {
      is BooleanFormula -> resolvedCall to call
      else -> null  // TODO: report error
    }
  }

/**
 * Recursively resolves all symbols in [resolvedCall] translating it into
 * an smt [Formula]
 */
private fun Solver.call(
  resolvedCall: ResolvedCall<out CallableDescriptor>,
  bindingContext: BindingContext,
): Formula? {
  val args = argsFormulae(bindingContext, resolvedCall.call.callElement)
  val descriptor = resolvedCall.resultingDescriptor
  return formulaWithArgs(descriptor, args, resolvedCall)
}

/**
 * Given a list of [args] and a [resolvedCall] whose
 * resulting descriptor points to a supported operation
 * we translate that supported operation such as [Int.plus] into a [Formula]
 */
private fun Solver.formulaWithArgs(
  descriptor: CallableDescriptor,
  args: List<Formula>,
  resolvedCall: ResolvedCall<out CallableDescriptor>
): Formula? = when (descriptor.fqNameSafe) {
  FqName("arrow.refinement.pre") -> {
    //recursion ends here
    args[0] //TODO apparently we don't get called for composed predicates with && or ||
  }
  FqName("arrow.refinement.post") -> {
    //recursion ends here
    args[0] //TODO apparently we don't get called for composed predicates with && or ||
  }
  FqName("kotlin.Int.equals") -> intEquals(args)
  FqName("kotlin.Int.plus") -> intPlus(args)
  FqName("kotlin.Int.minus") -> intMinus(args)
  FqName("kotlin.Int.times") -> intMultiply(args)
  FqName("kotlin.Int.div") -> intDivide(args)
  FqName("kotlin.Int.compareTo") -> {
    val op = (resolvedCall.call.callElement as? KtBinaryExpression)?.operationToken?.toFirOperation()?.operator
    when (op) {
      ">" -> intGreaterThan(args)
      ">=" -> intGreaterThanOrEquals(args)
      "<" -> intLessThan(args)
      "<=" -> intLessThanOrEquals(args)
      else -> null
    }
  }
  else -> null
}

/**
 * Given an [element] and [bindingContext] we traverse all
 * element subexpression resolving the [element] nested calls
 * and recursively transforming them into a list of smt [Formula]
 */
private fun Solver.argsFormulae(
  bindingContext: BindingContext,
  element: KtElement
): List<Formula> {
  val results = arrayListOf<Formula>()
  val visitor = expressionRecursiveVisitor {
    val resolvedCall = it.getResolvedCall(bindingContext)
    if (resolvedCall != null && !resolvedCall.preOrPostCall()) { // not match on the parent call
      val args = argsFormulae(resolvedCall, bindingContext)
      val descriptor = resolvedCall.resultingDescriptor
      val expressionFormula = formulaWithArgs(descriptor, args.map { it.third }, resolvedCall)
      expressionFormula?.also { results.add(it) }
    }
  }
  element.accept(visitor)
  return results.distinct()
}

/**
 * Given a [resolvedCall] and [bindingContext] we traverse all
 * arguments subexpressions
 * and recursively get their [Formula] and associated name and type information
 */
private fun <D : CallableDescriptor> Solver.argsFormulae(
  resolvedCall: ResolvedCall<D>,
  bindingContext: BindingContext,
): List<Triple<KotlinType, String, Formula>> =
  ints {
    booleans {
      val argsExpressions = resolvedCall.allArgumentExpressions()
      argsExpressions.mapNotNull { (name, type, maybeEx) ->
        expressionToFormulae(maybeEx, type, name, bindingContext)
      }
    }
  }

/**
 * Get all argument expressions for [this] call including extension receiver, dispatch receiver, and all
 * value arguments
 */
internal fun <D : CallableDescriptor> ResolvedCall<D>.allArgumentExpressions(): List<Triple<String, KotlinType, KtExpression?>> =
  listOfNotNull((dispatchReceiver ?: extensionReceiver)?.type?.let { Triple("this", it, getReceiverExpression()) }) +
    valueArguments.flatMap { (param, resolvedArg) ->
      val containingType =
        if (param.type.isTypeParameter() || param.type.isAnyOrNullableAny())
          (param.containingDeclaration.containingDeclaration as? ClassDescriptor)?.defaultType
            ?: param.builtIns.nothingType
        else param.type
      resolvedArg.arguments.mapIndexed { n, it ->
        Triple(param.name.asString(), containingType, it.getArgumentExpression())
      }
    }

/**
 * Transform a [KtExpression] into a [Formula]
 */
private fun Solver.expressionToFormulae(
  maybeEx: KtExpression?,
  type: KotlinType,
  name: String,
  bindingContext: BindingContext
): Triple<KotlinType, String, Formula>? =
  //TODO(change name for actual argument name instead of param name
  // change also `it` or whatever named first lambda argument of post condition gets renamed to `result`)
  when (val ex = maybeEx) {
    is KtConstantExpression ->
      makeConstant(type, ex.text, ex)
    is KtNameReferenceExpression ->
      //TODO(here we can introduce that any data type member or property extension with no arguments should
      // be lifted as an object field in z3 using the UF utilities in Solver)
      makeVariable(ex, type, ex.text)
    is KtElement -> {
      makeExpression(ex, bindingContext, type, ex.text)
    }
    else -> null
  }

/**
 * Uses the same resolution infra in [argsFormulae] to turn a complex
 * [KtExpression] into a [Formula] by resolving its nested calls
 * recursively
 */
private fun Solver.makeExpression(
  ex: KtExpression?,
  bindingContext: BindingContext,
  type: KotlinType,
  name: String
): Triple<KotlinType, String, Formula>? {
  val argCall = ex.getResolvedCall(bindingContext)
  val expResCall = argCall?.let { argsFormulae(it, bindingContext) }
  val descriptor = argCall?.resultingDescriptor
  val formula = descriptor?.let { formulaWithArgs(it, expResCall?.map { it.third }.orEmpty(), argCall) }
  return formula?.let { Triple(type, name, formula) }
}

/**
 * Turns a named referenced expression into a smt [Formula]
 * represented as a variable declared in the correct theory
 * given this [type].
 *
 * For example if [type] refers to [Int] the variable will have as
 * formula type [FormulaType.IntegerType]
 */
private fun Solver.makeVariable(
  ex: KtNameReferenceExpression,
  type: KotlinType,
  name: String
): Triple<KotlinType, String, Formula>? =
  ex.getReferencedName().let {
    when {
      type.isInt() ->
        Triple(type, name, makeIntegerObjectVariable(it))
      type.isBoolean() ->
        Triple(type, name, makeBooleanObjectVariable(it))
      else -> null
    }
  }

/**
 * Turns a named constant expression into a smt [Formula]
 * represented as a constant declared in the correct theory
 * given this [type].
 *
 * For example if [type] refers to [Int] the constant smt value will have as
 * formula type [FormulaType.IntegerType]
 */
private fun Solver.makeConstant(
  type: KotlinType,
  name: String,
  ex: KtConstantExpression
): Triple<KotlinType, String, Formula>? = when {
  type.isInt() ->
    Triple(type, name, integerFormulaManager.makeNumber(ex.text))
  type.isBoolean() ->
    Triple(type, name, booleanFormulaManager.makeBoolean(ex.text.toBooleanStrict()))
  else -> null
}

