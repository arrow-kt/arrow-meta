package arrow.meta.plugins.analysis.java

import com.google.testing.compile.CompilationSubject
import com.google.testing.compile.CompilationSubject.assertThat
import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjects

public operator fun String.invoke(
  withPlugin: CompilationSubject.() -> Unit,
  withoutPlugin: CompilationSubject.() -> Unit
) = ("Example" to this)(withPlugin, withoutPlugin)

public operator fun Pair<String, String>.invoke(
  withPlugin: CompilationSubject.() -> Unit,
  withoutPlugin: CompilationSubject.() -> Unit
) = mapOf(this)(withPlugin, withoutPlugin)

public operator fun Map<String, String>.invoke(
  withPlugin: CompilationSubject.() -> Unit,
  withoutPlugin: CompilationSubject.() -> Unit
) {
  worker(config = { withOptions("-parameters") }, files = this, check = withoutPlugin)
  worker(
    config = {
      withOptions(
          "-parameters",
          "-Xplugin:" +
            AnalysisJavaPlugin.NAME +
            " generatedSrcOutputDir=${System.getProperty("arrow.meta.generate.source.dir")}"
        )
        .withProcessors(AnalysisJavaProcessor())
    },
    files = this,
    check = withPlugin
  )
}

private fun worker(
  config: Compiler.() -> Compiler = { this },
  files: Map<String, String>,
  check: CompilationSubject.() -> Unit
): Unit {
  val compiler = Compiler.javac().config()
  val compilation = compiler.compile(files.map { (k, v) -> JavaFileObjects.forSourceString(k, v) })
  assertThat(compilation).check()
}
