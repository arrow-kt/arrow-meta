package arrow.meta.plugins.liquid.phases.analysis.solver.collect

import arrow.meta.internal.mapNotNull
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.body
import arrow.meta.phases.resolve.unwrappedNotNullableType
import arrow.meta.plugins.liquid.errors.MetaErrors
import arrow.meta.plugins.liquid.phases.analysis.solver.check.RESULT_VAR_NAME
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.model.DeclarationConstraints
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages
import arrow.meta.plugins.liquid.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.liquid.smt.ObjectFormula
import arrow.meta.plugins.liquid.smt.Solver
import arrow.meta.plugins.liquid.smt.boolAnd
import arrow.meta.plugins.liquid.smt.boolAndList
import arrow.meta.plugins.liquid.smt.boolEquivalence
import arrow.meta.plugins.liquid.smt.boolNot
import arrow.meta.plugins.liquid.smt.boolOr
import arrow.meta.plugins.liquid.smt.intDivide
import arrow.meta.plugins.liquid.smt.intEquals
import arrow.meta.plugins.liquid.smt.intGreaterThan
import arrow.meta.plugins.liquid.smt.intGreaterThanOrEquals
import arrow.meta.plugins.liquid.smt.intLessThan
import arrow.meta.plugins.liquid.smt.intLessThanOrEquals
import arrow.meta.plugins.liquid.smt.intMinus
import arrow.meta.plugins.liquid.smt.intMultiply
import arrow.meta.plugins.liquid.smt.intPlus
import arrow.meta.plugins.liquid.smt.isFieldCall
import arrow.meta.plugins.liquid.smt.isSingleVariable
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotated
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.fir.builder.toFirOperation
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.callExpressionRecursiveVisitor
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.annotations.argumentValue
import org.jetbrains.kotlin.resolve.calls.callUtil.getParentResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getReceiverExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.calls.model.ExpressionValueArgument
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.constants.ArrayValue
import org.jetbrains.kotlin.resolve.constants.StringValue
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isAnyOrNullableAny
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.jetbrains.kotlin.types.typeUtil.isDouble
import org.jetbrains.kotlin.types.typeUtil.isFloat
import org.jetbrains.kotlin.types.typeUtil.isInt
import org.jetbrains.kotlin.types.typeUtil.isLong
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
    val constraints = declaration.constraints(solverState, context)
    solverState.addConstraintsToSolverState(constraints, descriptor, context.trace.bindingContext)
  }
}

/**
 * Gather constraints from the local module by inspecting
 * - Custom DSL elements (pre and post)
 * - Ad-hoc constraints over third party types TODO
 * - Annotated declarations in compiled third party dependency modules TODO
 */
internal fun KtDeclaration.constraints(
  solverState: SolverState,
  context: DeclarationCheckerContext
): List<Pair<ResolvedCall<*>, NamedConstraint>> =
  constraintsDSLElements().filterIsInstance<KtElement>().mapNotNull { element ->
    val bindingCtx = context.trace.bindingContext
    val call = element.getResolvedCall(bindingCtx)
    if (call?.preOrPostCall() == true) {
      val predicateArg = call.arg("predicate")
      val result = solverState.solver.expressionToFormula(predicateArg, bindingCtx) as? BooleanFormula
      if (result == null) {
        context.trace.report(
          MetaErrors.ErrorParsingPredicate.on(element, ErrorMessages.Parsing.errorParsingPredicate(predicateArg))
        )
        solverState.signalParseErrors()
        null
      } else {
        val msgBody = call.arg("msg")
        val msg = if (msgBody is KtLambdaExpression) msgBody.bodyExpression?.firstStatement?.text?.trim('"')
        else msgBody?.text ?: predicateArg?.text
        msg?.let { call to NamedConstraint(it, result) }
      }
    } else {
      null
    }
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
 * returns true if [this] resolved call is calling [arrow.refinement.invariant]
 */
internal fun ResolvedCall<out CallableDescriptor>.invariantCall(): Boolean =
  resultingDescriptor.fqNameSafe == FqName("arrow.refinement.invariant")

/**
 * returns true if we have declared something with a @Law
 */
fun DeclarationDescriptor.hasLawAnnotation(): Boolean =
  annotations.hasAnnotation(FqName("arrow.refinement.Law"))

/**
 * Adds gathered [constraints] as an association to this [descriptor]
 * in the [SolverState]
 */
private fun SolverState.addConstraintsToSolverState(
  constraints: List<Pair<ResolvedCall<*>, NamedConstraint>>,
  descriptor: DeclarationDescriptor,
  bindingContext: BindingContext
) {
  if (constraints.isNotEmpty()) {
    val preConstraints = arrayListOf<NamedConstraint>()
    val postConstraints = arrayListOf<NamedConstraint>()
    constraints.forEach { (call, formula) ->
      if (call.preCall()) preConstraints.add(formula)
      if (call.postCall()) postConstraints.add(formula)
    }
    addConstraints(descriptor, preConstraints, postConstraints, bindingContext)
  }
}

/**
 * Depending on the source of the [descriptor] we might
 * need to attach the information to different places.
 * The main case is when you declare a @Law, in which
 * case you have to look for the proper place.
 */
private fun SolverState.addConstraints(
  descriptor: DeclarationDescriptor,
  preConstraints: ArrayList<NamedConstraint>,
  postConstraints: ArrayList<NamedConstraint>,
  bindingContext: BindingContext
) {
  val remoteDescriptorFromRemoteLaw =
    descriptor.annotations.findAnnotation(FqName("arrow.refinement.Subject"))?.let { lawSubject ->
      val subjectFqName = (lawSubject.argumentValue("fqName") as? StringValue)?.value?.let { FqName(it) }
      if (subjectFqName != null) {
        val pck = subjectFqName.parent()
        val fn = subjectFqName.pathSegments().lastOrNull()
        descriptor.module.getPackage(pck).memberScope.getContributedDescriptors { it == fn }.firstOrNull()
      } else null
    }
  val targetDescriptorFromLocalLaw =
    if (descriptor.hasLawAnnotation()) {
      getReturnedExpressionWithoutPostcondition(descriptor, bindingContext)?.resultingDescriptor
    } else null
  val lawSubject = remoteDescriptorFromRemoteLaw ?: targetDescriptorFromLocalLaw
  if (lawSubject != null) {
    callableConstraints.add(
      DeclarationConstraints(lawSubject, preConstraints, postConstraints)
    )
  }
  callableConstraints.add(
    DeclarationConstraints(descriptor, preConstraints, postConstraints)
  )
}

private fun getReturnedExpressionWithoutPostcondition(
  descriptor: DeclarationDescriptor,
  bindingContext: BindingContext
): ResolvedCall<out CallableDescriptor>? {
  val lastElement = (descriptor.findPsi() as? KtFunction)?.body()?.lastBlockStatementOrThis()
  val lastElementWithoutReturn = when (lastElement) {
    is KtReturnExpression -> lastElement.returnedExpression
    else -> lastElement
  }
  // remove outer layer of postcondition
  return lastElementWithoutReturn?.getResolvedCall(bindingContext)?.let {
    if (it.postCall()) {
      it.arg("this")?.getResolvedCall(bindingContext)
    } else {
      it
    }
  }
}
//  ((descriptor.findPsi() as? KtFunction)?.body()
//    ?.lastBlockStatementOrThis() as? KtReturnExpression)?.returnedExpression?.getResolvedCall(bindingContext)?.resultingDescriptor

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

/**
 * Get all the pre- and post- conditions for declarations
 * in the CLASSPATH, by looking at the annotations.
 */
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
  descriptor: DeclarationDescriptor,
  bindingContext: BindingContext
) {
  val constraints = descriptor.annotations.mapNotNull { ann ->
    when (ann.fqName) {
      FqName("arrow.refinement.Pre") -> "pre"
      FqName("arrow.refinement.Post") -> "post"
      else -> null
    }?.let { element -> parseFormula(element, ann, descriptor) }
  }
  if (constraints.isNotEmpty()) {
    val preConstraints = arrayListOf<NamedConstraint>()
    val postConstraints = arrayListOf<NamedConstraint>()
    constraints.forEach { (call, formula) ->
      if (call == "pre") preConstraints.addAll(formula)
      if (call == "post") postConstraints.addAll(formula)
    }
    addConstraints(descriptor, preConstraints, postConstraints, bindingContext)
  }
}

/**
 * Parse constraints from annotations.
 */
private fun SolverState.parseFormula(
  element: String,
  annotation: AnnotationDescriptor,
  descriptor: DeclarationDescriptor
): Pair<String, List<NamedConstraint>> {
  fun getArg(arg: String) =
    (annotation.argumentValue(arg) as? ArrayValue)?.value?.filterIsInstance<StringValue>()?.map { it.value }

  val dependencies = getArg("dependencies") ?: emptyList()
  val formulae = getArg("formulae") ?: emptyList()
  val messages = getArg("messages") ?: emptyList()
  return element to messages.zip(formulae).map { (msg, formula) ->
    NamedConstraint(msg, parseFormula(descriptor, formula, dependencies))
  }
}

/**
 * Parse constraints from annotations.
 */
internal fun SolverState.parseFormula(
  descriptor: DeclarationDescriptor,
  formula: String,
  dependencies: List<String>
): BooleanFormula {
  val VALUE_TYPE = "Int"
  val FIELD_TYPE = "Int"
  // build the parameters environment
  val params = (descriptor as? CallableDescriptor)?.let { function ->
    function.valueParameters.joinToString(separator = "\n") { param ->
      "(declare-fun ${param.name} () $VALUE_TYPE)"
    }
  } ?: ""
  // build the dependencies
  val deps = dependencies.joinToString(separator = "\n") {
    "(declare-fun $it () $FIELD_TYPE)"
  }
  // build the rest of the environment
  val rest = """
    (declare-fun this () $VALUE_TYPE)
    (declare-fun $RESULT_VAR_NAME () $VALUE_TYPE)
  """.trimIndent()
  val fullString = "$params\n$deps\n$rest\n(assert $formula)"
  return solver.parse(fullString)
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
      solverState.addClassPathConstraintsToSolverState(it, bindingTrace.bindingContext)
    }
    solverState.introduceFieldNamesInSolver()
    // solverState.introduceFieldAxiomsInSolver() // only if we introduce a solver with quantifiers
    solverState.collectionEnds()
    return if (!solverState.hadParseErrors()) {
      AnalysisResult.RetryWithAdditionalRoots(bindingTrace.bindingContext, module, emptyList(), emptyList())
    } else null
  } else null
}

/**
 * Transform a [KtExpression] into a [Formula]
 */
internal fun Solver.expressionToFormula(
  ex: KtExpression?,
  bindingContext: BindingContext
): Formula? {
  val argCall = ex?.getResolvedCall(bindingContext)
  return when {
    ex is KtLambdaExpression ->
      expressionToFormula(ex.bodyExpression, bindingContext)
    ex is KtBlockExpression ->
      ex.statements
        .mapNotNull { expressionToFormula(it, bindingContext) as? BooleanFormula }
        .let { conditions -> boolAndList(conditions) }
    ex is KtBinaryExpression &&
      ex.operationToken.toString() == "EQEQ" &&
      ex.right is KtConstantExpression && ex.right?.text == "null" ->
      ex.left?.let { expressionToFormula(it, bindingContext) as? ObjectFormula }?.let { isNull(it) }
    ex is KtBinaryExpression &&
      ex.operationToken.toString() == "EXCLEQ" &&
      ex.right is KtConstantExpression && ex.right?.text == "null" ->
      ex.left?.let { expressionToFormula(it, bindingContext) as? ObjectFormula }?.let { isNotNull(it) }
    ex is KtConstantExpression ->
      ex.getType(bindingContext)?.let { ty -> makeConstant(ty, ex) }
    ex is KtThisExpression -> // reference to this
      makeObjectVariable("this")
    ex is KtNameReferenceExpression && argCall?.resultingDescriptor is ParameterDescriptor ->
      makeObjectVariable(formulaVariableName(ex, bindingContext))
    argCall != null -> { // fall-through case
      val descriptor = argCall.resultingDescriptor
      val args = argCall.allArgumentExpressions().map { (_, ty, e) -> Pair(ty, expressionToFormula(e, bindingContext)) }
      // is it one of our well-known functions?
      val special = when {
        args.all { it.second != null } -> specialFormula(argCall, args.map { (ty, e) -> wrap(e!!, ty) })
        else -> null
      }
      when {
        special != null -> special
        descriptor.isField() -> {
          // create a field, the 'this' may be missing
          val thisExpression =
            (args.getOrNull(0)?.second as? ObjectFormula) ?: makeObjectVariable("this")
          field(descriptor.fqNameSafe.asString(), thisExpression)
        }
        else -> null
      }
    }
    else -> null
  }
}

private fun Solver.specialFormula(
  resolvedCall: ResolvedCall<out CallableDescriptor>,
  args: List<Formula>
): Formula? = when (resolvedCall.resultingDescriptor.fqNameSafe) {
  FqName("kotlin.Int.equals") -> {
    val op = (resolvedCall.call.callElement as? KtBinaryExpression)?.operationToken?.toFirOperation()?.operator
    when (op) {
      "==" -> intEquals(args)
      "!=" -> intEquals(args)?.let { not(it) }
      else -> null
    }
  }
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
  FqName("kotlin.Boolean.equals") -> {
    val op = (resolvedCall.call.callElement as? KtBinaryExpression)?.operationToken?.toFirOperation()?.operator
    when (op) {
      "==" -> boolEquivalence(args)
      "!=" -> boolEquivalence(args)?.let { not(it) }
      else -> null
    }
  }
  FqName("kotlin.Boolean.not") -> boolNot(args)
  FqName("kotlin.Boolean.and") -> boolAnd(args)
  FqName("kotlin.Boolean.or") -> boolOr(args)
  else -> null
}

private fun Solver.wrap(
  formula: Formula,
  type: KotlinType
): Formula = when {
  // only wrap variables and 'field(name, thing)'
  !formulaManager.isSingleVariable(formula) && !isFieldCall(formula) -> formula
  formula is ObjectFormula -> {
    val unwrapped = if (type.isMarkedNullable) type.unwrappedNotNullableType else type
    when {
      unwrapped.isInt() || unwrapped.isLong() -> intValue(formula)
      unwrapped.isBoolean() -> boolValue(formula)
      unwrapped.isFloat() || unwrapped.isDouble() -> decimalValue(formula)
      else -> formula
    }
  }
  else -> formula
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
  ex: KtConstantExpression
): Formula? =
  when {
    type.isInt() ->
      integerFormulaManager.makeNumber(ex.text)
    type.isBoolean() ->
      booleanFormulaManager.makeBoolean(ex.text.toBooleanStrict())
    else -> null
  }

/**
 * Use the special name $result for references to the result.
 */
internal fun formulaVariableName(
  ex: KtNameReferenceExpression,
  bindingContext: BindingContext
): String =
  if (isResultReference(ex, bindingContext)) RESULT_VAR_NAME else ex.getReferencedName()

/**
 * Should we treat a node as a field and create 'field(name, x)'?
 */
internal fun DeclarationDescriptor.isField(): Boolean = when (this) {
  is PropertyDescriptor -> true
  is FunctionDescriptor ->
    valueParameters.size == 0 &&
      ((extensionReceiverParameter != null && dispatchReceiverParameter == null) ||
        (extensionReceiverParameter == null && dispatchReceiverParameter != null))
  else -> false
}

/**
 * Get all argument expressions for [this] call including extension receiver, dispatch receiver, and all
 * value arguments
 */
internal fun <D : CallableDescriptor> ResolvedCall<D>.allArgumentExpressions(): List<Triple<String, KotlinType, KtExpression?>> =
  listOfNotNull((dispatchReceiver ?: extensionReceiver)?.type?.let { Triple("this", it, getReceiverExpression()) }) +
    valueArgumentExpressions()

internal fun <D : CallableDescriptor> ResolvedCall<D>.valueArgumentExpressions(): List<Triple<String, KotlinType, KtExpression?>> =
    valueArguments.flatMap { (param, resolvedArg) ->
      val containingType =
        if (param.type.isTypeParameter() || param.type.isAnyOrNullableAny())
          (param.containingDeclaration.containingDeclaration as? ClassDescriptor)?.defaultType
            ?: param.builtIns.nothingType
        else param.type
      resolvedArg.arguments.map {
        Triple(param.name.asString(), containingType, it.getArgumentExpression())
      }
    }

internal fun <D : CallableDescriptor> ResolvedCall<D>.arg(
  argumentName: String
): KtExpression? =
  this.allArgumentExpressions().find { it.first == argumentName }?.third

internal fun <D : CallableDescriptor> ResolvedCall<D>.resolvedArg(
  argumentName: String
): ResolvedValueArgument? =
  this.valueArguments.toList().find {
    it.first.name.asString() == argumentName
  }?.second

internal fun isResultReference(ex: KtElement, bindingContext: BindingContext): Boolean {
  val parent =
    ex.getParentResolvedCall(bindingContext)?.call?.callElement.getParentResolvedCall(bindingContext)
  return if (parent != null && (parent.postCall() || parent.invariantCall())) {
    val expArg = parent.resolvedArg("predicate") as? ExpressionValueArgument
    val lambdaArg =
      (expArg?.valueArgument as? KtLambdaArgument)?.getLambdaExpression()
        ?: (expArg?.valueArgument as? KtValueArgument)?.getArgumentExpression() as? KtLambdaExpression
    val params =
      lambdaArg?.functionLiteral?.valueParameters?.map { it.text }.orEmpty() +
        listOf("it")
    ex.text in params.distinct()
  } else {
    false
  }
}
