package arrow.meta.plugins.analysis.phases.analysis.solver.state

import arrow.meta.continuations.ContSeq
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.DeclarationConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorIds
import arrow.meta.plugins.analysis.phases.analysis.solver.search.typeInvariants
import arrow.meta.plugins.analysis.sarif.ReportedError
import arrow.meta.plugins.analysis.sarif.SeverityLevel
import arrow.meta.plugins.analysis.sarif.sarifFileContent
import arrow.meta.plugins.analysis.smt.ObjectFormula
import arrow.meta.plugins.analysis.smt.Solver
import arrow.meta.plugins.analysis.smt.fieldNames
import arrow.meta.plugins.analysis.smt.utils.FieldProvider
import arrow.meta.plugins.analysis.smt.utils.NameProvider
import arrow.meta.plugins.analysis.smt.utils.ReferencedElement
import java.io.File
import java.util.Locale
import org.sosy_lab.java_smt.api.ProverEnvironment
import org.sosy_lab.java_smt.api.SolverContext

data class SolverState(
  val names: NameProvider = NameProvider(),
  val solver: Solver = Solver(names),
  val prover: ProverEnvironment =
    solver.newProverEnvironment(
      SolverContext.ProverOptions.GENERATE_MODELS,
      SolverContext.ProverOptions.GENERATE_UNSAT_CORE
    ),
  val callableConstraints: MutableMap<FqName, MutableList<DeclarationConstraints>> = mutableMapOf(),
  val solverTrace: MutableList<String> = mutableListOf(),
  val fieldProvider: FieldProvider = FieldProvider(solver, prover),
  private val reportedErrors: MutableSet<ReportedError> = mutableSetOf()
) {

  fun notifySarifReport(id: ErrorIds, element: Element, msg: String) {
    reportedErrors.add(ReportedError(id.id, id, element, msg, SeverityLevel.Error, emptyList()))
  }

  private var parseErrors = false

  fun signalParseErrors() {
    parseErrors = true
  }

  fun hadParseErrors(): Boolean = parseErrors

  inline fun <A> bracket(f: () -> A): A {
    solverTrace.add("PUSH")
    prover.push()
    val result = f()
    prover.pop()
    solverTrace.add("POP")
    return result
  }

  /** This signals that the rest of the computation happens inside a push/pop bracket */
  val continuationBracket: ContSeq<Unit> = ContSeq { bracket { yield(Unit) } }

  /** This signals that part of the computation happens inside a push/pop bracket */
  fun <A> scopedBracket(cont: () -> ContSeq<A>) =
    ContSeq.unit
      .map {
        solverTrace.add("PUSH (scoped)")
        prover.push()
      }
      .flatMap { cont() }
      .onEach {
        prover.pop()
        solverTrace.add("POP (scoped)")
      }

  fun addConstraint(constraint: NamedConstraint, context: ResolutionContext) {
    prover.addConstraint(constraint.formula)
    // introduce the field names
    solver.formulaManager.fieldNames(constraint.formula).map { (fieldName, _) ->
      context.descriptorFor(FqName(fieldName)).getOrNull(0)?.let { descriptor ->
        fieldProvider.introduce(descriptor)
      }
    }
    solverTrace.add("${constraint.msg} : ${constraint.formula}")
  }

  fun newName(context: ResolutionContext, prefix: String, element: Element?): String =
    newName(context, prefix, element, null)

  fun newName(
    context: ResolutionContext,
    prefix: String,
    element: Element?,
    reference: Pair<ValueParameterDescriptor, ResolvedValueArgument>?
  ): String {
    val type = (element as? Expression)?.type(context)
    val info = element?.let { ReferencedElement(it, reference, type) }
    val newName = names.recordNewName(prefix, info)
    if (type != null && !type.isNullable()) {
      typeInvariants(type, newName, context).forEach { addConstraint(it, context) }
    }
    return newName
  }

  fun field(field: DeclarationDescriptor, formula: ObjectFormula): ObjectFormula {
    fieldProvider.introduce(field)
    return solver.field(field.fqNameSafe.asField, formula)
  }

  fun notifyModuleProcessed(moduleDescriptor: ModuleDescriptor) {
    updateSarifFile(moduleDescriptor)
  }

  private fun updateSarifFile(moduleDescriptor: ModuleDescriptor) {
    if (reportedErrors.isNotEmpty()) {
      val content = sarifFileContent("1.0.0", reportedErrors.toList())
      val sarifReportFolder = File(moduleDescriptor.getBuildDirectory(), "/reports/sarif")
      sarifReportFolder.mkdirs()
      val moduleName = moduleDescriptor.fqNameSafe.toString().ifBlank { "default" }
      val sarifFile = File(sarifReportFolder, "arrow.analysis.${moduleName}.sarif")
      println("Generating sarif file: $sarifFile")
      sarifFile.writeText(content)
    }
  }
}

val FqName.asField: String
  get() {
    val parts = this.name.split('.')
    return if (parts.isEmpty()) {
      this.name
    } else {
      var lastPart = parts.last()
      if (lastPart.startsWith("get")) {
        lastPart = lastPart.drop(3).replaceFirstChar { it.lowercase(Locale.getDefault()) }
      }
      return (parts.dropLast(1) + listOf(lastPart)).joinToString(separator = ".")
    }
  }
