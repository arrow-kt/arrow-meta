package arrow.meta.quotes.scope.expressions

const val catchClauseExpression =
  """
  | //metadebug
  | 
  | fun measureTimeMillis(block: () -> Unit): Unit {
  |    try {
  |      block()
  |    } catch (throwable: Throwable) { println(throwable) }
  |  }
  | """