package arrow.meta.quotes.scope.expressions

val classBodyExpressions =
  arrayOf(
    """
    | //metadebug
    |
    | class ClassBodyScopeTest {
    |
    |   private val x = "x"
    |   private val y = "y"
    |   private val z = "z"
    |
    |   companion object {
    |     fun init() = ClassBodyScopeTest()
    |   }
    |
    |   fun x() = x
    |   fun y() = y
    |   fun z() = z
    | }
    """,
    """
    | //metadebug
    |
    | enum class EnumBodyScopeTest {
    |   FOO, BAR;
    |
    |   fun foo() = 0
    | }
    """,
    """
    | //metadebug
    |
    | object ObjectBodyScopeTest {
    |
    |   private val x = "x"
    |   private val y = "y"
    |   private val z = "z"
    |
    |   fun x() = x
    |   fun y() = y
    |   fun z() = z
    | }
    """
  )