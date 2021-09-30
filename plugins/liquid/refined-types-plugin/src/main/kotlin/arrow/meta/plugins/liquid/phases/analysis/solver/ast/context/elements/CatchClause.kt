package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface CatchClause : Element {
  val parameterList: ParameterList?
  val catchParameter: Parameter?
  val catchBody: Expression?
}
