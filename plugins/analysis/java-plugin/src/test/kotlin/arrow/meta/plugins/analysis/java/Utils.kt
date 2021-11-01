package arrow.meta.plugins.analysis.java

import com.google.testing.compile.CompilationSubject
import com.google.testing.compile.CompilationSubject.assertThat
import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjects

public operator fun Pair<String, String>.invoke(
  withPlugin: CompilationSubject.() -> Unit,
  withoutPlugin: CompilationSubject.() -> Unit
) {
  worker(
    config = { withOptions("-Xplugin:" + AnalysisPlugin.NAME) },
    file = this,
    check = withPlugin
  )
  worker(file = this, check = withoutPlugin)
}

private fun worker(
  config: Compiler.() -> Compiler = { this },
  file: Pair<String, String>,
  check: CompilationSubject.() -> Unit
): Unit {
  val compiler = Compiler.javac().config()
  val compilation = compiler.compile(JavaFileObjects.forSourceString(file.first, file.second))
  assertThat(compilation).check()
}
