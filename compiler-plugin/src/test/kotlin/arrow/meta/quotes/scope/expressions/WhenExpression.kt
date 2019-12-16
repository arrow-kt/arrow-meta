package arrow.meta.quotes.scope.expressions

// TODO failing compilation:  Expecting a when-condition, Expecting an expression, is-condition or in-condition

const val whenExpression =
  """
  | //metadebug
  | 
  | class Wrapper {
  |   fun doMaths(x: Int) {
  |     when {
  |       x + 2 == 4 -> { println("I can do maths") }
  |       else -> { println("I cannot do maths") }
  |     }
  |   }
  | }
  | """