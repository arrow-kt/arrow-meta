package arrow.meta.plugins.liquid.phases.errors

import arrow.meta.plugins.liquid.phases.analysis.solver.NamedConstraint
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticParameterRenderer
import org.jetbrains.kotlin.diagnostics.rendering.RenderingContext
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.sosy_lab.java_smt.api.Formula

@JvmField
val RenderFormula: DiagnosticParameterRenderer<List<Formula>> =
  object : DiagnosticParameterRenderer<List<Formula>> {
    override fun render(obj: List<Formula>, renderingContext: RenderingContext): String =
      obj.toString()
  }

@JvmField
val RenderNamedConstraint: DiagnosticParameterRenderer<List<NamedConstraint>> =
  object : DiagnosticParameterRenderer<List<NamedConstraint>> {
    override fun render(obj: List<NamedConstraint>, renderingContext: RenderingContext): String =
      obj.joinToString { "${it.msg} : ${it.formula}" }
  }

@JvmField
val RenderCall: DiagnosticParameterRenderer<ResolvedCall<*>> =
  object : DiagnosticParameterRenderer<ResolvedCall<*>> {
    override fun render(obj: ResolvedCall<*>, renderingContext: RenderingContext): String =
      "`${obj.call.callElement.text}`"
  }

@JvmField
val RenderDeclaration: DiagnosticParameterRenderer<KtDeclaration> =
  object : DiagnosticParameterRenderer<KtDeclaration> {
    override fun render(obj: KtDeclaration, renderingContext: RenderingContext): String =
      "`${obj.name ?: "<unknown>"}`"
  }
