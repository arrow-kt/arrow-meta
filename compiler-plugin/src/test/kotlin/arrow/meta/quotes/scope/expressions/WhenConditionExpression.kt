package arrow.meta.quotes.scope.expressions

const val whenConditionExpression =
  """
  | //metadebug
  | 
  | class Wrapper {
  |   fun doMaths(x: Int) {
  |     when {
  |       x + 2 == 4 -> println("I can do maths")
  |       else -> println("I cannot do maths")
  |     }
  |   }
  | }
  | """