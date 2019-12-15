package arrow.meta.quotes.scope.expressions

// TODO:
//const val binaryExpression =
//  """
//  | //metadebug
//  |
//  | class Wrapper {
//  |   init {
//  |     println(2 == 3)
//  |   }
//  | }
//  | """

const val binaryExpression =
  """
  | //metadebug
  | 
  | fun binaryExpression(): Unit =
  |     println(2 == 3)
  | """