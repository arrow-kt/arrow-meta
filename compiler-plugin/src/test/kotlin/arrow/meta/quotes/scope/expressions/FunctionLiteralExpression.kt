package arrow.meta.quotes.scope.expressions

const val functionLiteral =
  """
  | //metadebug
  |
  | class Wrapper {
  |   fun whatever() {
  |    val a = { i: Int -> i + 1 }
  |   }
  |  }
  | """

const val functionLiteralAsAnonymousFunction =
  """
   | //metadebug
   |
   | class Wrapper {
   |   fun whatever() {
   |    val increment: (Int) -> Unit = fun(x) { x + 1 }
   |   }
   |  }
   | """

const val functionLiteralAsALambdaExpression =
  """
  | //metadebug
  |
  | class Wrapper {
  |   fun whatever() {
  |    val sum: (Int, Int) -> Int = { x: Int, y: Int -> x + y }
  |   }
  |  }
  | """

val functionalLiteralExpressions =
  arrayOf(
    functionLiteral,
    functionLiteralAsAnonymousFunction,
    functionLiteralAsALambdaExpression
  )