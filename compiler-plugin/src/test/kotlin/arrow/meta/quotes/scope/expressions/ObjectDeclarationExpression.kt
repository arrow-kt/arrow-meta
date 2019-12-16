package arrow.meta.quotes.scope.expressions

const val objectDeclarationExpression =
  """
  | //metadebug
  | 
  | @Deprecated("Test") object Test {
  |   fun test() { println("Test") }
  |   fun test2() { println("Test2") }
  | }"""