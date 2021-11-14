package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface BinaryExpression : OperationExpression {
  /** The token as appears in the source code (+, -, ...) */
  val operationToken: String

  /** One of the codes from [org.jetbrains.kotlin.lexer.KtTokens] (PLUS, MINUS, ...) */
  val operationTokenRpr: String
  val left: Expression?
  val right: Expression?
}
