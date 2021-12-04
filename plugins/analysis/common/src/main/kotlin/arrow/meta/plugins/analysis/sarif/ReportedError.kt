package arrow.meta.plugins.analysis.sarif

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorIds

data class ReportedError(
  val id: String,
  val errorsId: ErrorIds,
  val element: Element,
  val msg: String,
  val severity: SeverityLevel,
  val references: List<Element>
)
