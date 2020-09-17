package arrow.meta.phases.analysis.diagnostic

import arrow.meta.phases.analysis.fqNameOrShortName
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.asString
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticParameterRenderer
import org.jetbrains.kotlin.diagnostics.rendering.RenderingContext
import org.jetbrains.kotlin.types.KotlinType

@JvmField
val RenderTypeWithFqName: DiagnosticParameterRenderer<KotlinType> =
  object : DiagnosticParameterRenderer<KotlinType> {
    override fun render(obj: KotlinType, renderingContext: RenderingContext): String =
      obj.fqNameOrShortName()
  }

@JvmField
val RenderProof: DiagnosticParameterRenderer<Proof> =
  object : DiagnosticParameterRenderer<Proof> {
    override fun render(obj: Proof, renderingContext: RenderingContext): String =
      obj.asString()
  }

@JvmField
val RenderProofs: DiagnosticParameterRenderer<Collection<Proof>> =
  object : DiagnosticParameterRenderer<Collection<Proof>> {
    override fun render(obj: Collection<Proof>, renderingContext: RenderingContext): String =
      obj.joinToString(separator = ",\n") { it.asString() }
  }
