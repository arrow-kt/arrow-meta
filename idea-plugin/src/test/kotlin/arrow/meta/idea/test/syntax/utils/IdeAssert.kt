package arrow.meta.idea.test.syntax.utils

import arrow.meta.idea.test.syntax.Assert
import arrow.meta.idea.test.syntax.IdeTest

fun assertThis(ideTest: IdeTest): Unit =
  interpreter(ideTest)

val interpreter: (IdeTest) -> Unit
  get() {

  }


private fun <A> assertResolves(compilationResult: Assert.IdeResolution<A>): Unit {
  assertThat(compilationResult.actualStatus).isEqualTo(IdeRes)
}

private fun assertFails(compilationResult: CompilationResult): Unit {
  assertThat(compilationResult.actualStatus).isNotEqualTo(CompilationStatus.OK)
}

private fun assertFailsWith(compilationResult: CompilationResult, check: (String) -> Boolean): Unit {
  assertFails(compilationResult)
  assertThat(check(compilationResult.log)).isTrue()
}