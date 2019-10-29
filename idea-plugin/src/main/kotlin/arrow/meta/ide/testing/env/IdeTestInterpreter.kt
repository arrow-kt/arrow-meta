package arrow.meta.ide.testing.env

import arrow.meta.ide.testing.IdeTest

fun <A> IdeTest<A>.runTest(): Unit = interpreter(this)

fun <A> interpreter(test: IdeTest<A>): Unit = TODO()

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
