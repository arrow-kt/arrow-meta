package arrow.meta.plugins.liquid.phases.analysis.solver.collect

import arrow.meta.internal.filterNotNull
import arrow.meta.internal.mapNotNull
import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.liquid.phases.analysis.solver.check.RESULT_VAR_NAME
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.model.DeclarationConstraints
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages.Parsing.unexpectedFieldInitBlock
import arrow.meta.plugins.liquid.phases.analysis.solver.primitiveFormula
import arrow.meta.plugins.liquid.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.liquid.smt.ObjectFormula
import arrow.meta.plugins.liquid.smt.Solver
import arrow.meta.plugins.liquid.smt.boolAnd
import arrow.meta.plugins.liquid.smt.boolAndList
import arrow.meta.plugins.liquid.smt.boolOr
import arrow.meta.plugins.liquid.smt.boolOrList
import arrow.meta.plugins.liquid.smt.isFieldCall
import arrow.meta.plugins.liquid.smt.isSingleVariable
import arrow.meta.plugins.liquid.types.PrimitiveType
import arrow.meta.plugins.liquid.types.asFloatingLiteral
import arrow.meta.plugins.liquid.types.asIntegerLiteral
import arrow.meta.plugins.liquid.types.primitiveType
import arrow.meta.plugins.liquid.types.unwrapIfNullable
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.AnnotatedExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.BinaryExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ConstantExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Constructor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.DeclarationWithBody
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.DeclarationWithInitializer
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Function
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.IfExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ExpressionLambdaArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.LambdaExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.NameReferenceExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ParenthesizedExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ReturnExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ThisExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.AnalysisResult
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.Annotated
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.AnnotationDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ExpressionValueArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.FunctionDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.PropertyDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FormulaManager
import org.sosy_lab.java_smt.api.FormulaType
import org.sosy_lab.java_smt.api.FunctionDeclaration
import org.sosy_lab.java_smt.api.visitors.FormulaTransformationVisitor

// PHASE 1: COLLECTION OF CONSTRAINTS
// ==================================

/**
 * Collects constraints from all declarations and adds them to the solver state
 */
internal fun CompilerContext.collectDeclarationsConstraints(
  context: ResolutionContext,
  declaration: Declaration,
  descriptor: DeclarationDescriptor
) {
  val solverState = get<SolverState>(SolverState.key(context.module))
  if (solverState != null && (solverState.isIn(SolverState.Stage.Init) || solverState.isIn(SolverState.Stage.CollectConstraints))) {
    solverState.collecting()
    declaration.constraints(solverState, context, descriptor)
  }
}

/**
 * Gather constraints from the local module by inspecting
 * - Custom DSL elements (pre and post)
 * - Ad-hoc constraints over third party types TODO
 * - Annotated declarations in compiled third party dependency modules TODO
 */
internal fun Declaration.constraints(
  solverState: SolverState,
  context: ResolutionContext,
  descriptor: DeclarationDescriptor
) = when (this) {
  is Constructor<*> ->
    constraintsFromConstructor(solverState, context)
  is DeclarationWithBody, is DeclarationWithInitializer ->
    constraintsFromFunctionLike(solverState, context)
  else -> Pair(arrayListOf(), arrayListOf())
}.let { (preConstraints, postConstraints) ->
  if (preConstraints.isNotEmpty() || postConstraints.isNotEmpty() || descriptor.isField()) {
    solverState.addConstraints(
      descriptor, preConstraints, postConstraints,
      context
    )
  }
}

/**
 * Obtain all the function calls and corresponding formulae
 * to 'pre', 'post', and 'require'
 */
private fun Declaration.constraintsFromDeclaration(
  solverState: SolverState,
  context: ResolutionContext
): List<Pair<ResolvedCall, NamedConstraint>> = context.run {
  constraintsDSLElements().toList().mapNotNull {
    it.elementToConstraint(solverState, context)
  }
}

/**
 * Gather constraints for anything which is not a constructor
 */
private fun Declaration.constraintsFromFunctionLike(
  solverState: SolverState,
  context: ResolutionContext
): Pair<ArrayList<NamedConstraint>, ArrayList<NamedConstraint>> {
  val preConstraints = arrayListOf<NamedConstraint>()
  val postConstraints = arrayListOf<NamedConstraint>()
  constraintsFromDeclaration(solverState, context).forEach { (call, formula) ->
    if (call.preCall()) preConstraints.add(formula)
    if (call.postCall()) postConstraints.add(formula)
  }
  return Pair(preConstraints, postConstraints)
}

/**
 * Constructors have some additional requirements,
 * namely the pre- and post-conditions of init blocks
 * should be added to their own list
 */
private fun <A : Constructor<A>> Constructor<A>.constraintsFromConstructor(
  solverState: SolverState,
  context: ResolutionContext
): Pair<ArrayList<NamedConstraint>, ArrayList<NamedConstraint>> {
  val preConstraints = arrayListOf<NamedConstraint>()
  val postConstraints = arrayListOf<NamedConstraint>()
  (this.getContainingClassOrObject()?.let { klass ->
    (klass.getAnonymousInitializers() + listOf(this)).flatMap {
      it.constraintsFromDeclaration(solverState, context)
    }
  } ?: emptyList()).forEach { (call, formula) ->
    if (call.preCall()) {
      rewritePrecondition(solverState, context, !call.requireCall(), call, formula.formula)?.let {
        preConstraints.add(NamedConstraint(formula.msg, it))
      }
    }
    // in constructors 'require' has a double duty
    if (call.postCall() || call.requireCall()) {
      rewritePostcondition(solverState, formula.formula).let {
        postConstraints.add(NamedConstraint(formula.msg, it))
      }
    }
  }
  return Pair(preConstraints, postConstraints)
}

/**
 * Turn references to 'field(x, this)'
 * into references to parameter 'x'
 */
private fun <A : Constructor<A>> Constructor<A>.rewritePrecondition(
  solverState: SolverState,
  context: ResolutionContext,
  raiseErrorWhenUnexpected: Boolean,
  call: ResolvedCall,
  formula: BooleanFormula
): BooleanFormula? {
  val mgr = solverState.solver.formulaManager
  var errorSignaled = false
  val result = mgr.transformRecursively(
    formula,
    object : FormulaTransformationVisitor(mgr) {
      override fun visitFunction(f: Formula?, args: MutableList<Formula>?, fn: FunctionDeclaration<*>?): Formula =
        if (fn?.name == Solver.FIELD_FUNCTION_NAME) {
          val fieldName = args?.getOrNull(0)?.let { mgr.extractSingleVariable(it) }
          val thisName = args?.getOrNull(1)?.let { mgr.extractSingleVariable(it) }
          val paramName = this@rewritePrecondition.valueParameters.firstOrNull { param ->
            fieldName?.endsWith(".${param.nameAsName?.value}") ?: (param.nameAsName?.value == fieldName)
          }?.nameAsName?.value
          if (fieldName != null && thisName == "this") {
            if (paramName != null) {
              solverState.solver.makeObjectVariable(paramName)
            } else {
              // error case, we have a reference to a field
              // which is not coming from an argument
              errorSignaled = true
              if (raiseErrorWhenUnexpected) {
                val msg = unexpectedFieldInitBlock(fieldName)
                context.reportUnsatCallPre(call.callElement, msg)
              }
              super.visitFunction(f, args, fn)
            }
          } else {
            super.visitFunction(f, args, fn)
          }
        } else {
          super.visitFunction(f, args, fn)
        }
    })
  return result.takeIf { !errorSignaled }
}

/**
 * Turn references to 'this'
 * into references to '$result'
 */
private fun rewritePostcondition(
  solverState: SolverState,
  formula: BooleanFormula
): BooleanFormula {
  val mgr = solverState.solver.formulaManager
  return mgr.transformRecursively(
    formula,
    object : FormulaTransformationVisitor(mgr) {
      override fun visitFreeVariable(f: Formula?, name: String?): Formula =
        if (name == "this") {
          solverState.solver.makeObjectVariable(RESULT_VAR_NAME)
        } else {
          super.visitFreeVariable(f, name)
        }
    })
}

private fun FormulaManager.extractSingleVariable(
  formula: Formula
): String? =
  extractVariables(formula)
    .takeIf { it.size == 1 }
    ?.toList()?.getOrNull(0)?.first

private fun Element.elementToConstraint(
  solverState: SolverState,
  context: ResolutionContext
): Pair<ResolvedCall, NamedConstraint>? {
  val call = getResolvedCall(context)
  return if (call?.preOrPostCall() == true) {
    val predicateArg = call.arg("predicate", context) ?: call.arg("value", context)
    val result = solverState.solver.expressionToFormula(predicateArg, context) as? BooleanFormula
    if (result == null) {
      val msg = ErrorMessages.Parsing.errorParsingPredicate(predicateArg)
      context.reportErrorsParsingPredicate(this, msg)
      solverState.signalParseErrors()
      null
    } else {
      val msgBody = call.arg("msg", context) ?: call.arg("lazyMessage", context)
      val msg = if (msgBody is LambdaExpression) msgBody.bodyExpression?.firstStatement?.text?.trim('"')
      else msgBody?.text ?: predicateArg?.text
      msg?.let { call to NamedConstraint(it, result) }
    }
  } else {
    null
  }
}

/**
 * returns true if [this] resolved call is calling [arrow.refinement.pre]
 */
internal fun ResolvedCall.preCall(): Boolean =
  resultingDescriptor.fqNameSafe == FqName("arrow.refinement.pre") ||
    requireCall() // require is taken as precondition

/**
 * returns true if [this] resolved call is calling [arrow.refinement.post]
 */
internal fun ResolvedCall.postCall(): Boolean =
  resultingDescriptor.fqNameSafe == FqName("arrow.refinement.post")

/**
 * returns true if [this] resolved call is calling [kotlin.require]
 */
internal fun ResolvedCall.requireCall(): Boolean =
  resultingDescriptor.fqNameSafe == FqName("kotlin.require")

/**
 * returns true if [this] resolved call is calling [arrow.refinement.pre] or  [arrow.refinement.post]
 */
private fun ResolvedCall.preOrPostCall(): Boolean =
  preCall() || postCall()

/**
 * returns true if [this] resolved call is calling [arrow.refinement.invariant]
 */
internal fun ResolvedCall.invariantCall(): Boolean =
  resultingDescriptor.fqNameSafe == FqName("arrow.refinement.invariant")

/**
 * returns true if we have declared something with a @Law
 */
fun DeclarationDescriptor.hasLawAnnotation(): Boolean =
  annotations().hasAnnotation(FqName("arrow.refinement.Law"))

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
  bindingContext: ResolutionContext
) {
  val remoteDescriptorFromRemoteLaw =
    descriptor.annotations().findAnnotation(FqName("arrow.refinement.Subject"))?.let { lawSubject ->
      val subjectFqName = (lawSubject.argumentValueAsString("fqName"))?.let { FqName(it) }
      if (subjectFqName != null) {
        val pck = subjectFqName.name.substringBeforeLast(".")
        val fn = subjectFqName.name.split(".").lastOrNull()
        descriptor.module.getPackage(pck)?.getMemberScope()?.getContributedDescriptors { it == fn }?.firstOrNull()
      } else null
    }
  val targetDescriptorFromLocalLaw =
    if (descriptor.hasLawAnnotation()) {
      getReturnedExpressionWithoutPostcondition(descriptor, bindingContext)?.resultingDescriptor
    } else null
  val lawSubject = remoteDescriptorFromRemoteLaw ?: targetDescriptorFromLocalLaw
  if (lawSubject != null) {
    callableConstraints.add(lawSubject, preConstraints, postConstraints)
  }
  callableConstraints.add(descriptor, preConstraints, postConstraints)
}

private fun MutableList<DeclarationConstraints>.add(
  descriptor: DeclarationDescriptor,
  pre: ArrayList<NamedConstraint>,
  post: ArrayList<NamedConstraint>
) {
  val previous = this.firstOrNull { it.descriptor == descriptor }
  if (previous == null) {
    this.add(DeclarationConstraints(descriptor, pre, post))
  } else {
    this.remove(previous)
    this.add(DeclarationConstraints(descriptor, previous.pre + pre, previous.post + post))
  }
}

private fun getReturnedExpressionWithoutPostcondition(
  descriptor: DeclarationDescriptor,
  bindingContext: ResolutionContext
): ResolvedCall? {
  val lastElement = (descriptor.element() as? Function)?.body()?.lastBlockStatementOrThis()
  val lastElementWithoutReturn = when (lastElement) {
    is ReturnExpression -> lastElement.returnedExpression
    else -> lastElement
  }
  // remove outer layer of postcondition
  return lastElementWithoutReturn?.getResolvedCall(bindingContext)?.let {
    if (it.postCall()) {
      it.arg("this", bindingContext)?.getResolvedCall(bindingContext)
    } else {
      it
    }
  }
}
//  ((descriptor.findPsi() as? KtFunction)?.body()
//    ?.lastBlockStatementOrThis() as? KtReturnExpression)?.returnedExpression?.getResolvedCall(bindingContext)?.resultingDescriptor

private fun Annotated.preAnnotation(): AnnotationDescriptor? =
  annotations().findAnnotation(FqName("arrow.refinement.Pre"))

private fun Annotated.postAnnotation(): AnnotationDescriptor? =
  annotations().findAnnotation(FqName("arrow.refinement.Post"))

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
  packages: List<FqName> = listOf(FqName("")),
  skipPacks: Set<FqName> = skipPackages
): List<DeclarationDescriptor> =
  when {
    packages.isEmpty() -> acc
    else -> {
      val current = packages.first()
      val topLevelDescriptors = getPackage(current.name)?.getMemberScope()?.getContributedDescriptors { true }?.toList().orEmpty()
      val memberDescriptors = topLevelDescriptors.filterIsInstance<ClassDescriptor>().flatMap {
        it.getUnsubstitutedMemberScope().getContributedDescriptors { true }.toList()
      }
      val allPackageDescriptors = topLevelDescriptors + memberDescriptors
      val packagedProofs = allPackageDescriptors
        .filter {
          it.preAnnotation() != null || it.postAnnotation() != null || it.isField()
        }
      val remaining = (getSubPackagesOf(current) + packages.drop(1)).filter { it !in skipPacks }
      declarationsWithConstraints(acc + packagedProofs.asSequence(), remaining)
    }
  }

internal fun SolverState.addClassPathConstraintsToSolverState(
  descriptor: DeclarationDescriptor,
  bindingContext: ResolutionContext
) {
  val constraints = descriptor.annotations().iterable().mapNotNull { ann ->
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
  fun getArg(arg: String) = annotation.argumentValueAsArrayOfString(arg)

  val dependencies = getArg("dependencies")
  val formulae = getArg("formulae")
  val messages = getArg("messages")
  return element to messages.zip(formulae).map { (msg, formula) ->
    NamedConstraint(msg, parseFormula(descriptor, formula, dependencies.toList()))
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
 * and its time to Rewind analysis for phase 2
 * [arrow.meta.plugins.liquid.phases.analysis.solver.check.checkDeclarationConstraints]
 */
internal fun CompilerContext.finalizeConstraintsCollection(
  module: ModuleDescriptor,
  bindingTrace: ResolutionContext
): AnalysisResult {
  val solverState = get<SolverState>(SolverState.key(module))
  return if (solverState != null && solverState.isIn(SolverState.Stage.CollectConstraints)) {
    module.declarationsWithConstraints().forEach {
      solverState.addClassPathConstraintsToSolverState(it, bindingTrace)
    }
    solverState.introduceFieldNamesInSolver()
    // solverState.introduceFieldAxiomsInSolver() // only if we introduce a solver with quantifiers
    solverState.collectionEnds()
    return if (!solverState.hadParseErrors()) {
      AnalysisResult.Retry
    } else AnalysisResult.Completed
  } else AnalysisResult.Completed
}

/**
 * Transform a [Expression] into a [Formula]
 */
internal fun Solver.expressionToFormula(
  ex: Expression?,
  context: ResolutionContext
): Formula? {
  val argCall = ex?.getResolvedCall(context)
  return when {
    // just recur
    ex is ParenthesizedExpression ->
      expressionToFormula(ex.expression, bindingContext)
    ex is AnnotatedExpression ->
      expressionToFormula(ex.baseExpression, bindingContext)
    ex is LambdaExpression ->
      expressionToFormula(ex.bodyExpression, bindingContext)
    // basic blocks
    ex is BlockExpression ->
      ex.statements
        .mapNotNull { expressionToFormula(it, context) as? BooleanFormula }
        .let { conditions -> boolAndList(conditions) }
    ex is ConstantExpression ->
      ex.getType(bindingContext)?.let { ty -> makeConstant(ty, ex) }
    ex is ThisExpression -> // reference to this
      makeObjectVariable("this")
    ex is NameReferenceExpression && argCall?.resultingDescriptor is ParameterDescriptor ->
      makeObjectVariable(formulaVariableName(ex, bindingContext))
    ex is IfExpression -> {
      val cond = expressionToFormula(ex.condition, bindingContext) as? BooleanFormula
      val thenBranch = expressionToFormula(ex.then, bindingContext)
      val elseBranch = expressionToFormula(ex.`else`, bindingContext)
      if (cond != null && thenBranch != null && elseBranch != null) {
        ifThenElse(cond, thenBranch, elseBranch)
      } else {
        null
      }
    }
    ex is KtWhenExpression && ex.subjectExpression == null ->
      ex.entries.foldRight<KtWhenEntry, Formula?>(null) { entry, acc ->
        val conditions: List<BooleanFormula?> = when {
          entry.isElse -> listOf(booleanFormulaManager.makeTrue())
          else -> entry.conditions.map { cond ->
            when (cond) {
              is KtWhenConditionWithExpression ->
                expressionToFormula(cond.expression, bindingContext) as? BooleanFormula
              else -> null
            }
          }
        }
        val body = expressionToFormula(entry.expression, bindingContext)
        when {
          body == null || conditions.any { it == null } ->
            return@foldRight null // error case
          acc != null -> ifThenElse(boolOrList(conditions.filterNotNull()), body, acc)
          entry.isElse -> body
          else -> null
        }
      }
    // special cases which do not always resolve well
    ex is KtBinaryExpression &&
      ex.operationToken.toString() == "EQEQ" &&
      ex.right is ConstantExpression && ex.right?.text == "null" ->
      ex.left?.let { expressionToFormula(it, context) as? ObjectFormula }?.let { isNull(it) }
    ex is BinaryExpression &&
      ex.operationToken.toString() == "EXCLEQ" &&
      ex.right is ConstantExpression && ex.right?.text == "null" ->
      ex.left?.let { expressionToFormula(it, context) as? ObjectFormula }?.let { isNotNull(it) }
    ex is BinaryExpression &&
      ex.operationToken.toString() == "ANDAND" ->
      expressionToFormula(ex.left, context)?.let { leftFormula ->
        expressionToFormula(ex.right, context)?.let { rightFormula ->
          boolAnd(listOf(leftFormula, rightFormula))
        }
      }
    ex is BinaryExpression &&
      ex.operationToken.toString() == "OROR" ->
      expressionToFormula(ex.left, context)?.let { leftFormula ->
        expressionToFormula(ex.right, context)?.let { rightFormula ->
          boolOr(listOf(leftFormula, rightFormula))
        }
      }
    // fall-through case
    argCall != null -> {
      val args = argCall.allArgumentExpressions().map { (_, ty, e) ->
        Pair(ty, expressionToFormula(e, bindingContext))
      }
      val wrappedArgs =
        args.takeIf { args.all { it.second != null } }
          ?.map { (ty, e) -> wrap(e!!, ty) }
      wrappedArgs?.let { primitiveFormula(argCall, it) }
        ?: fieldFormula(argCall.resultingDescriptor, args)
    }
    else -> null
  }
}

private fun Solver.wrap(
  formula: Formula,
  type: Type
): Formula = when {
  // only wrap variables and 'field(name, thing)'
  !formulaManager.isSingleVariable(formula) && !isFieldCall(formula) -> formula
  formula is ObjectFormula -> {
    val unwrapped = if (type.isMarkedNullable) type.unwrappedNotNullableType else type
    when (unwrapped.primitiveType()) {
      PrimitiveType.INTEGRAL -> intValue(formula)
      PrimitiveType.RATIONAL -> decimalValue(formula)
      PrimitiveType.BOOLEAN -> boolValue(formula)
      else -> formula
    }
  }
  else -> formula
}

private fun Solver.fieldFormula(
  descriptor: CallableDescriptor,
  args: List<Pair<KotlinType, Formula?>>
): ObjectFormula? = descriptor.takeIf { it.isField() }?.let {
    // create a field, the 'this' may be missing
    val thisExpression =
      (args.getOrNull(0)?.second as? ObjectFormula) ?: makeObjectVariable("this")
    field(descriptor.fqNameSafe.asString(), thisExpression)
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
): Formula? = when (type.unwrapIfNullable().primitiveType()) {
  PrimitiveType.INTEGRAL ->
    ex.text.asIntegerLiteral()?.let { integerFormulaManager.makeNumber(it) }
  PrimitiveType.RATIONAL ->
    ex.text.asFloatingLiteral()?.let { rationalFormulaManager.makeNumber(it) }
  PrimitiveType.BOOLEAN ->
    booleanFormulaManager.makeBoolean(ex.text.toBooleanStrict())
  else -> null
}

/**
 * Use the special name $result for references to the result.
 */
internal fun formulaVariableName(
  ex: NameReferenceExpression,
  bindingContext: ResolutionContext
): String =
  if (ex.isResultReference(bindingContext)) RESULT_VAR_NAME else ex.getReferencedName()

/**
 * Should we treat a node as a field and create 'field(name, x)'?
 */
internal fun DeclarationDescriptor.isField(): Boolean = when (this) {
  is PropertyDescriptor -> hasOneReceiver()
  is FunctionDescriptor -> valueParameters.size == 0 && hasOneReceiver()
  else -> false
}

private fun CallableDescriptor.hasOneReceiver(): Boolean =
  (extensionReceiverParameter != null && dispatchReceiverParameter == null) ||
    (extensionReceiverParameter == null && dispatchReceiverParameter != null)

/**
 * Get all argument expressions for [this] call including extension receiver, dispatch receiver, and all
 * value arguments
 */
internal fun ResolvedCall.allArgumentExpressions(context: ResolutionContext): List<Triple<String, Type, Expression?>> =
  listOfNotNull((dispatchReceiver ?: extensionReceiver)?.type?.let { Triple("this", it, getReceiverExpression()) }) +
    valueArgumentExpressions(context)

internal fun ResolvedCall.valueArgumentExpressions(context: ResolutionContext): List<Triple<String, Type, Expression?>> =
  valueArguments.flatMap { (param, resolvedArg) ->
    val containingType =
      if (param.type.isTypeParameter() || param.type.isAnyOrNullableAny())
        (param.containingDeclaration?.containingDeclaration as? ClassDescriptor)?.defaultType
          ?: context.types.nothingType
      else param.type
    resolvedArg.arguments.map {
      Triple(param.name.value, containingType, it.argumentExpression)
    }
  }

internal fun ResolvedCall.arg(
  argumentName: String,
  context: ResolutionContext
): Expression? =
  this.allArgumentExpressions(context).find { it.first == argumentName }?.third

internal fun ResolvedCall.resolvedArg(
  argumentName: String
): ResolvedValueArgument? =
  this.valueArguments.toList().find {
    it.first.name.value == argumentName
  }?.second

internal fun Element.isResultReference(bindingContext: ResolutionContext): Boolean =
  getPostOrInvariantParent(bindingContext)?.let { parent ->
    val expArg = parent.resolvedArg("predicate") as? ExpressionValueArgument
    val lambdaArg =
      (expArg?.valueArgument as? ExpressionLambdaArgument)?.getLambdaExpression()
        ?: (expArg?.valueArgument as? arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ExpressionResolvedValueArgument)?.argumentExpression as? LambdaExpression
    val params =
      lambdaArg?.functionLiteral?.valueParameters?.map { it.text }.orEmpty() +
        listOf("it")
    this.text in params.distinct()
  } ?: false

internal fun Element.getPostOrInvariantParent(
  bindingContext:  ResolutionContext
): ResolvedCall? =
  this.parents().mapNotNull {
    it.getResolvedCall(bindingContext)
  }.firstOrNull { call ->
    call.postCall() || call.invariantCall()
  }
