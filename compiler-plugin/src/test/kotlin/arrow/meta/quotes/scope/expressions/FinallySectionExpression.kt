package arrow.meta.quotes.scope.expressions

// TODO implement convertFinally in Converter to support FINALLY in AST

const val finallySectionExpression =
  """
  | package 47deg.arrow-meta
  | 
  | //metadebug
  | 
  | fun measureTimeMillis(block: () -> Unit): Long {
  |    val start = System.currentTimeMillis()
  |    try {
  |      block()
  |    } finally {
  |      return System.currentTimeMillis() - start
  |    }
  |  }
  | """