package arrow.meta.quotes.scope.expressions

const val returnExpression =
  """
  | //metadebug
  |
  | class Wrapper {
  |   fun whatTimeIsIt(): Long {
  |     return System.currentTimeMillis()
  |   }
  | }
  | """

const val returnLabeledExpression =
  """
  | //metadebug
  |
  | class Wrapper {
  |   fun foo() {
  |     run loop@{
  |       listOf(1, 2, 3, 4, 5).forEach {
  |         if (it == 3) return@loop
  |         print(it)
  |       }
  |     }
  |   }
  | }
  | """

val returnExpressions =
  arrayOf(
    returnExpression,
    returnLabeledExpression
  )