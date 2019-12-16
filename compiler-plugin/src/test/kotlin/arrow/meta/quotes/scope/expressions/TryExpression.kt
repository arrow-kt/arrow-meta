package arrow.meta.quotes.scope.expressions

const val tryExpression =
  """
  | //metadebug
  | 
  | fun measureTimeMillis(block: () -> Unit): Unit {
  |    try {
  |      block()
  |    } catch (throwable: Throwable) { println(throwable) }
  |  }
  | """