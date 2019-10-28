package arrow.meta.ide.testing.env

import arrow.meta.ide.testing.IdeTest

fun assertThis(ideTest: IdeTest): Unit =
  println(ideTest)

val interpreter: (IdeTest) -> Unit = {}

/*

private fun <A> assertResolves(compilationResult: Assert.IdeResolution<A>): Unit {
  assertThat(compilationResult.actualStatus).isEqualTo(IdeRes)
}

private fun assertFails(compilationResult: CompilationResult): Unit {
  assertThat(compilationResult.actualStatus).isNotEqualTo(CompilationStatus.OK)
}

private fun assertFailsWith(compilationResult: CompilationResult, check: (String) -> Boolean): Unit {
  assertFails(compilationResult)
  assertThat(check(compilationResult.log)).isTrue()
}*/
