package arrow.meta.quotes.scope.expressions

const val destructuringDeclarationExpression =
  """
  | //metadebug
  | 
  | class Wrapper {
  |   data class Test(val x: String = "X", val y: Int = 1)
  |    
  |   fun whatever() {
  |     val (x, y) = Test()
  |   } 
  | }
  | """
