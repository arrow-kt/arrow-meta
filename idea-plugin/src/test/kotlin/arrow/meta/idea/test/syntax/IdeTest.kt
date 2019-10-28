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
    object Resolves : Assert()
    object Fails : Assert()
    data class FailsWith(val f: (String) -> Boolean) : Assert()
  }

  object Empty : Assert()

  interface Syntax {
    fun failsWith
  }

  companion object : Syntax {

  }
}