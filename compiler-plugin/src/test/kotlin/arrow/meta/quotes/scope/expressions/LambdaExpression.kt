package arrow.meta.quotes.scope.expressions

// TODO: Validate lambda expression with multiple parameters

const val lambdaExpression =
  """
  | //metadebug
  |
  | class Wrapper {
  |   init {
  |    val square: (Int) -> Int = { x -> x * x }
  |   }
  |}
  | """

const val lambdaExpressionAsAFunctionLiteral =
  """
  | //metadebug
  |
  | class Wrapper {
  |   init {
  |     fun whenPassingALambdaLiteral_thenCallTriggerLambda() {
  |       fun invokeLambda(lambda: (Double) -> Boolean) : Boolean {
  |           return lambda(4.329)
  |       }
  |
  |       val result = invokeLambda({
  |         true
  |       })
  |     }
  |   }
  |}
  | """

val lambdaExpressions =
  arrayOf(
    lambdaExpression,
    lambdaExpressionAsAFunctionLiteral
  )