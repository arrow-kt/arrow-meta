package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface CatchClause : Element {
  val parameterList: ParameterList?
  val catchParameter: Parameter?
  val catchBody: Expression?
}
