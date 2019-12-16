package arrow.meta.quotes.scope.expressions

const val whileExpression =
  """
  | //metadebug
  | 
  | fun power(x: Int) {
  |   var y = 0
  |   while (y++ < x) {
  |     println("INFINITE POWER")
  |   }
  | }
  | """