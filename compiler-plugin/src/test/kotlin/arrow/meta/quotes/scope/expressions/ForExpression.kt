package arrow.meta.quotes.scope.expressions

const val forExpression =
  """
  | //metadebug
  | 
  | fun someFunction() {
  |   for (i in 1..10) {
  |     println(i)
  |   }
  | }
  | """

const val forExpressionSingleLine =
  """
  | //metadebug
  | 
  | class Wrapper {
  |   fun singleLineFunction() {
  |     for (i in 1..10) println(i)
  |   } 
  | }
  | """

const val forExpressionDestructuringDeclaration =
  """
  | //metadebug
  | 
  | class Wrapper {
  |   fun destructuringDeclarationFunction() {
  |     for ((index, value) in listOf("a", "b", "c").withIndex()) {
  |       println("index: " + index)
  |       println("value: " + value)
  |     }
  |   }
  | }
  | """

val forExpressions =
  arrayOf(
    forExpression,
    forExpressionSingleLine,
    forExpressionDestructuringDeclaration
  )