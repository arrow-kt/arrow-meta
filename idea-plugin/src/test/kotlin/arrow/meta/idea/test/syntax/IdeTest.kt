package arrow.meta.idea.test.syntax

import arrow.meta.plugin.testing.Source

data class IdeTest(
  val code: Source,
  val assert: Assert
) {
  companion object : Assert.Syntax by Assert
}

typealias IdeTestInterpreter = (IdeTest) -> Unit


sealed class Assert {
  sealed class IdeResolution : Assert() {
    object Resolves : IdeResolution()
    object Fails : IdeResolution()
    data class FailsWith(val f: (String) -> Boolean) : IdeResolution()
  }

  object Empty : Assert()
  data class ElementsInCode(val elements: Int) : Assert()
  interface Syntax {
    val emptyAssert: Assert
    val resolves: Assert
    val fails: Assert
    fun failsWith(f: (String) -> Boolean): Assert = IdeResolution.FailsWith(f)
    fun elementsInCode(elements: Int): Assert = ElementsInCode(elements)
  }

  companion object : Syntax {
  }
}