package arrow.meta.quotes.scope.expressions

const val dotQualified =
  """
  | //metadebug
  | 
  | class Wrapper {
  |   fun whatever() {
  | val list = listOf("12", "33", "65")
  |     list.flatMap { it.toList() }
  |   }
  |  }
  | """

const val multipleDotQualified =
  """
  | //metadebug
  | 
  | class Wrapper {
  |   fun whatever() {
  |    "Shortest".plus("sentence").plus("ever")
  |   }
  |  }
  | """

val dotQualifiedExpressions =
  arrayOf(
    dotQualified,
    multipleDotQualified
  )