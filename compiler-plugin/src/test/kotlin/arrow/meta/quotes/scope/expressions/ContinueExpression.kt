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
    | """,
    """
    | //metadebug
    | 
    | class Wrapper {
    |   fun whatever() {
    | for(i in 0 until 100 step 3) {
    |   if (i == 6) continue
    |   if (i == 60) break
    |   println(i)
    | }
    |   }
    | }
    | """
  )