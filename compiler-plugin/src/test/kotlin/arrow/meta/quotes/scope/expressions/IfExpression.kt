package arrow.meta.quotes.scope.expressions

// TODO: Do not validate if expression without else scope properties
const val ifExpression =
  """
  | //metadebug
  |
  | class Wrapper {
  |   fun whatever() {
  |     if (2 == 3) {
  |       println("FAKE NEWS")
  |     } else {
  |       println("success!")
  |     }
  |  }
  | }
  | """

const val ifExpressionSingleLine =
  """
  | //metadebug
  |
  | class Wrapper {
  |   fun whatever() {
  |     if (2 == 3) println("FAKE NEWS") else println("success")
  |   }
  |  }
  | """

val ifExpressions =
  arrayOf(
    ifExpression,
    ifExpressionSingleLine
  )
