package arrow.meta.plugins.analysis.phases.errors

import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticParameterRenderer
import org.jetbrains.kotlin.diagnostics.rendering.RenderingContext

@JvmField
val RenderString: DiagnosticParameterRenderer<String> =
  object : DiagnosticParameterRenderer<String> {
    override fun render(obj: String, renderingContext: RenderingContext): String =
      obj
  }
