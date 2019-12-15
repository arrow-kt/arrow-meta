package arrow.meta.quotes.scope.expressions

const val breakExpression =
  """
  | //metadebug
  | 
  | fun loop() {
  |  loop@ for (i in 1..100) {
  |    for (j in 1..100) {
  |      if (j > 30) break@loop
  |    }
  |  }
  |}
  | """