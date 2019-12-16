package arrow.meta.quotes.scope.expressions

val continueExpressions =
  arrayOf (
    """
    | //metadebug
    | 
    | class Wrapper {
    |   fun whatever() {
    | loop@ for (i in 1..100) {
    |   for (j in 1..100) {
    |     if (j > 30) continue@loop
    |   }
    | } 
    |   }
    | }
    | """
  )