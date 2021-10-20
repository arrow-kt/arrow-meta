package arrow.meta.plugins.analysis.phases.analysis.solver.collect

import arrow.meta.plugins.analysis.phases.analysis.solver.RESULT_VAR_NAME
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.AnalysisResult
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.AnnotationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.gather
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import org.sosy_lab.java_smt.api.BooleanFormula

/**
 * Finishes the collection of constraints from CLASSPATH.
 * In addition, it signals that we have finished collecting
 * constraints, and we should now proceed to checking
 * declarations.
 */
public fun SolverState.collectConstraintsFromClasspath(
  localDeclarations: List<DeclarationDescriptor>,
  module: ModuleDescriptor,
  bindingTrace: ResolutionContext
): Pair<AnalysisResult, Set<FqName>> {
  // check local declarations for @Laws
  localDeclarations.flatMap {
    it.gather { it.hasPreOrPostAnnotation }
  }.forEach {
    addConstraintsFromAnnotations(it, bindingTrace)
  }
  // check rest of the CLASSPATH for @Laws
  val skipClasspath =
    System.getProperty("ARROW_ANALYSIS_SKIP_CLASSPATH", "false").toBooleanStrictOrNull() ?: false
  if (!skipClasspath) {
    module.gather {
      it.hasPreOrPostAnnotation
    }.forEach {
      addConstraintsFromAnnotations(it, bindingTrace)
    }
  }

  return if (hadParseErrors()) {
    Pair(AnalysisResult.ParsingError, emptySet())
  } else {
    // figure out which is the set of local elements
    // whose packages should be added in the hints
    val interesting = localDeclarations.flatMap {
      it.gather { callableConstraints.containsKey(it.fqNameSafe) }
    }.mapNotNull {
      it.containingPackage
    }.toSet()
    Pair(AnalysisResult.Retry, interesting)
  }
}

internal fun SolverState.addConstraintsFromAnnotations(
  descriptor: DeclarationDescriptor,
  bindingContext: ResolutionContext
) {
  val constraints = descriptor.annotations().iterable().mapNotNull { ann ->
    when (ann.fqName) {
      FqName("arrow.analysis.Pre") -> "pre"
      FqName("arrow.analysis.Post") -> "post"
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
