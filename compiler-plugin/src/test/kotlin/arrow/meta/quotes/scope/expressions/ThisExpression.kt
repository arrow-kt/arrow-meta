package arrow.meta.quotes.scope.expressions

const val thisWithLabeledQualifiers =
  """
  | //metadebug
  |
  | class A {
  |   inner class B {
  |     fun Int.foo() {
  |       val a = this@A
  |       val b = this@B
  |       val c1 = this@foo
  |     }
  |   }
  | }
  | """

const val thisNoLabeledTarget =
  """
  | //metadebug
  |
  | class A {
  |   inner class B {
  |     val funLit2 = { s: String ->
  |       val d1 = this
  |     }
  |   }
  | }
  | """

const val thisWithReflectionTarget =
  """
  | //metadebug
  |
  | class A {
  |   inner class B {
  |     fun stringToSentence(input: String): String = input + " is in a sentence."
  |     val funLit2 = { s: String -> this::stringToSentence }
  |   }
  | }
  | """

val thisExpressions =
  arrayOf(
    thisNoLabeledTarget,
    thisWithLabeledQualifiers,
    thisWithReflectionTarget
  )