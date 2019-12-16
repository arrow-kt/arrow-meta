package arrow.meta.quotes.scope.expressions

const val typeAlias = """
                         | //metadebug
                         |
                         | typealias IntegerPredicate = (Int) -> Boolean
                         | """

const val typeAliasWithConstraintsProperties = """
                         | //metadebug
                         |
                         | typealias Predicate<Int> = (Int) -> Boolean
                         | """

const val typeAliasWithGenerics = """
                         | //metadebug
                         |
                         | typealias Predicate<T> = (T) -> Boolean
                         | """

val typeAliasExpressions =
  arrayOf(
    typeAlias,
    typeAliasWithConstraintsProperties,
    typeAliasWithGenerics
  )