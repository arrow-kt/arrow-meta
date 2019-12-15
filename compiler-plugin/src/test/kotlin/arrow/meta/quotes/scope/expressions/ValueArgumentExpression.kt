package arrow.meta.quotes.scope.expressions

const val valueArgument =
  """
  | //metadebug
  |
  | class Wrapper {
  |   init {
  |    addNumbers(x = 3, y = 4)
  |   }
  |   fun addNumbers(x: Int, y: Int): Int = x + y
  |  }
  | """

const val valueArgumentWithoutArgumentNames =
  """
  | //metadebug
  |
  | class Wrapper {
  |   init {
  |    addNumbers(3, 4)
  |   }
  |   fun addNumbers(x: Int, y: Int): Int = x + y
  |  }
  | """

val valueArgumentExpressions =
  arrayOf(
    valueArgument,
    valueArgumentWithoutArgumentNames
  )