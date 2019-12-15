package arrow.meta.quotes.scope.expressions

// TODO: Validate is? expression scope properties
// TODO: Validate as? expression scope properties

const val isExpression =
  """
  | //metadebug
  |
  | class Wrapper {
  |   fun whatever() {
  |     if (2 is Int) {
  |       println("2 is a number")
  |     } else {
  |       println("2 is not a number")
  |     }
  |   }
  | }
  | """

const val trueExpression =
  """
  | //metadebug
  |
  | class Wrapper {
  |   fun whatever() {
  |     if (true) {
  |       println("2 is a number")
  |     } else {
  |       println("2 is not a number")
  |     }
  |   }
  | }
  | """

const val asExpression =
  """
  | //metadebug
  |
  | class Wrapper {
  |   fun whatever() {
  |     val e = System.currentTimeMillis() as Number
  |   }
  | }
  | """

val isExpressions =
  arrayOf(
    isExpression,
    trueExpression,
    asExpression
  )