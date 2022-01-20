package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

enum class TypeCastExpresionKind {
  POSITIVE_TYPE_CAST,
  QUESTION_TYPE_CAST
}

interface TypeCastExpression : OperationExpression {
  /** The token as appears in the source code (+, -, ...) */
  val operationToken: String

  val left: Expression?
  val right: TypeReference?
  val kind: TypeCastExpresionKind
}
