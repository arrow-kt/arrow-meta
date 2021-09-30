package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface DeclarationWithBody : Declaration {
  val bodyExpression: Expression?
  fun hasBlockBody(): Boolean
  fun hasBody(): Boolean
  fun hasDeclaredReturnType(): Boolean
  fun body(): Expression?

  val valueParameters: List<Parameter?>
  val bodyBlockExpression: BlockExpression?
}
