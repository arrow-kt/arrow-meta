package arrow.meta.ide.testing.env.types

import arrow.meta.ide.testing.Source
import arrow.meta.plugin.testing.CompilationData
import arrow.meta.plugin.testing.CompilationResult
import arrow.meta.plugin.testing.Config
import arrow.meta.plugin.testing.compilationData
import arrow.meta.plugin.testing.compile

object HeavyTestSyntax {
  fun Source.compile(vararg config: Config): CompilationResult {
    val initialCompilationData = CompilationData(source = listOf(this.trimMargin()))
    val compilationData = config.toList().compilationData(initialCompilationData)
    return compile(compilationData)
  }
}

