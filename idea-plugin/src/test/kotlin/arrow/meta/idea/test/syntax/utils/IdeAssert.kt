package arrow.meta.idea.test.syntax.utils

private fun assertResolves(compilationResult: CompilationResult): Unit {
  assertThat(compilationResult.actualStatus).isEqualTo(CompilationStatus.OK)
}

private fun assertFails(compilationResult: CompilationResult): Unit {
  assertThat(compilationResult.actualStatus).isNotEqualTo(CompilationStatus.OK)
}

private fun assertFailsWith(compilationResult: CompilationResult, check: (String) -> Boolean): Unit {
  assertFails(compilationResult)
  assertThat(check(compilationResult.log)).isTrue()
}