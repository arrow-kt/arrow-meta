package arrow.meta.plugins.analysis.phases.analysis.solver.collect

import arrow.meta.plugins.analysis.phases.analysis.solver.RESULT_VAR_NAME
import arrow.meta.plugins.analysis.phases.analysis.solver.SpecialKind
import arrow.meta.plugins.analysis.phases.analysis.solver.arg
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Constructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DeclarationWithBody
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DeclarationWithInitializer
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LambdaExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages
import arrow.meta.plugins.analysis.phases.analysis.solver.isRequireCall
import arrow.meta.plugins.analysis.phases.analysis.solver.specialKind
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.smt.Solver
import arrow.meta.plugins.analysis.smt.extractSingleVariable
import kotlin.collections.ArrayList
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FunctionDeclaration
import org.sosy_lab.java_smt.api.visitors.FormulaTransformationVisitor

/**
 * Gather constraints from the local module by inspecting
 * - Custom DSL elements (pre and post)
 * - Ad-hoc constraints over third party types TODO
 */
public fun Declaration.collectConstraintsFromDSL(
  solverState: SolverState,
  context: ResolutionContext,
  descriptor: DeclarationDescriptor
) =
  when (this) {
    is Constructor<*> -> constraintsFromConstructor(solverState, context, valueParameters)
    is DeclarationWithBody ->
      constraintsFromFunctionLike(solverState, context, valueParameters.filterNotNull())
    is DeclarationWithInitializer -> constraintsFromFunctionLike(solverState, context, emptyList())
    else -> Pair(arrayListOf(), arrayListOf())
  }.let { (preConstraints, postConstraints) ->
    if (preConstraints.isNotEmpty() || postConstraints.isNotEmpty()) {
      solverState.addConstraints(descriptor, preConstraints, postConstraints, context)
    }
  }

/**
 * Obtain all the function calls and corresponding formulae to 'pre', 'post', and 'require' This
 * function is used by more specific instances like [constraintsFromConstructor] and
 * [constraintsFromFunctionLike]
 */
private fun Declaration.constraintsFromGenericDeclaration(
  solverState: SolverState,
  context: ResolutionContext,
  parameters: List<Parameter>
): List<Pair<ResolvedCall, NamedConstraint>> =
  context.run {
    constraintsDSLElements().toList().mapNotNull {
      it.elementToConstraint(solverState, context, parameters)
    }
  }

/** Gather constraints for anything which is not a constructor */
private fun Declaration.constraintsFromFunctionLike(
  solverState: SolverState,
  context: ResolutionContext,
  parameters: List<Parameter>
): Pair<ArrayList<NamedConstraint>, ArrayList<NamedConstraint>> {
  val preConstraints = arrayListOf<NamedConstraint>()
  val postConstraints = arrayListOf<NamedConstraint>()
  constraintsFromGenericDeclaration(solverState, context, parameters).forEach { (call, formula) ->
    when (call.specialKind) {
      SpecialKind.Pre -> preConstraints.add(formula)
      SpecialKind.Post -> postConstraints.add(formula)
      else -> {} // do nothing
    }
  }
  return Pair(preConstraints, postConstraints)
}

/**
 * Constructors have some additional requirements, namely the pre- and post-conditions of init
 * blocks should be added to their own list
 */
private fun <A : Constructor<A>> Constructor<A>.constraintsFromConstructor(
  solverState: SolverState,
  context: ResolutionContext,
  parameters: List<Parameter>
): Pair<ArrayList<NamedConstraint>, ArrayList<NamedConstraint>> {
  val preConstraints = arrayListOf<NamedConstraint>()
  val postConstraints = arrayListOf<NamedConstraint>()
  (getContainingClassOrObject().getAnonymousInitializers() + listOf(this))
    .flatMap { it.constraintsFromGenericDeclaration(solverState, context, parameters) }
    .forEach { (call, formula) ->
      if (call.specialKind == SpecialKind.Pre) {
        rewritePrecondition(solverState, context, !call.isRequireCall(), call, formula.formula)
          ?.let { preConstraints.add(NamedConstraint(formula.msg, it)) }
      }
      // in constructors 'require' has a double duty
      if (call.specialKind == SpecialKind.Post || call.isRequireCall()) {
        rewritePostcondition(solverState, formula.formula).let {
          postConstraints.add(NamedConstraint(formula.msg, it))
        }
      }
    }
  return Pair(preConstraints, postConstraints)
}

/** Turn references to 'field(x, this)' into references to parameter 'x' */
private fun <A : Constructor<A>> Constructor<A>.rewritePrecondition(
  solverState: SolverState,
  context: ResolutionContext,
  raiseErrorWhenUnexpected: Boolean,
  call: ResolvedCall,
  formula: BooleanFormula
): BooleanFormula? {
  val mgr = solverState.solver.formulaManager
  var errorSignaled = false
  val result =
    mgr.transformRecursively(
      formula,
      object : FormulaTransformationVisitor(mgr) {
        override fun visitFunction(
          f: Formula?,
          args: MutableList<Formula>?,
          fn: FunctionDeclaration<*>?
        ): Formula =
          if (fn?.name == Solver.FIELD_FUNCTION_NAME) {
            val fieldName = args?.getOrNull(0)?.let { mgr.extractSingleVariable(it) }
            val thisName = args?.getOrNull(1)?.let { mgr.extractSingleVariable(it) }
            val paramName =
              this@rewritePrecondition.valueParameters
                .firstOrNull { param ->
                  fieldName?.endsWith(".${param.nameAsName?.value}")
                    ?: (param.nameAsName?.value == fieldName)
                }
                ?.nameAsName
                ?.value
            if (fieldName != null && thisName == "this") {
              if (paramName != null) {
                solverState.solver.makeObjectVariable(paramName)
              } else {
                // error case, we have a reference to a field
                // which is not coming from an argument
                errorSignaled = true
                if (raiseErrorWhenUnexpected) {
                  val msg = ErrorMessages.Parsing.unexpectedFieldInitBlock(fieldName)
                  context.reportErrorsParsingPredicate(call.callElement, msg)
                }
                super.visitFunction(f, args, fn)
              }
            } else {
              super.visitFunction(f, args, fn)
            }
          } else {
            super.visitFunction(f, args, fn)
          }
      }
    )
  return result.takeIf { !errorSignaled }
}

/** Turn references to 'this' into references to '$result' */
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
    }
  )
}

/** Turns an [Element] into a formula */
private fun Element.elementToConstraint(
  solverState: SolverState,
  context: ResolutionContext,
  parameters: List<Parameter>
): Pair<ResolvedCall, NamedConstraint>? {
  val call = getResolvedCall(context)
  val kind = call?.specialKind
  return if (kind == SpecialKind.Pre || kind == SpecialKind.Post) {
    val predicateArg = call.arg("predicate", context) ?: call.arg("value", context)
    val result = solverState.topLevelExpressionToFormula(predicateArg, context, parameters, false)
    if (result == null) {
      val msg = ErrorMessages.Parsing.errorParsingPredicate(predicateArg)
      context.reportErrorsParsingPredicate(this, msg)
      solverState.signalParseErrors()
      null
    } else {
      val msgBody = call.arg("msg", context) ?: call.arg("lazyMessage", context)
      val msg =
        if (msgBody is LambdaExpression) msgBody.bodyExpression?.firstStatement?.text?.trim('"')
        else msgBody?.text ?: predicateArg?.text
      msg?.let { call to NamedConstraint(it, result) }
    }
  } else {
    null
  }
}
