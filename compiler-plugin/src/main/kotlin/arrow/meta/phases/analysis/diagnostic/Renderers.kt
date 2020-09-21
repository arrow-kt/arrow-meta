package arrow.meta.phases.analysis.diagnostic

import arrow.meta.plugins.proofs.phases.ArrowProofSet
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.asString
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticParameterRenderer
import org.jetbrains.kotlin.diagnostics.rendering.RenderingContext
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.types.KotlinType

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


val ProofRenderer: DescriptorRenderer =
  DescriptorRenderer.FQ_NAMES_IN_TYPES.withOptions {
    annotationFilter = { ArrowProofSet.none { p -> p == it.fqName } }
  }

@JvmField
val RenderTypes: DiagnosticParameterRenderer<KotlinType> =
  object : DiagnosticParameterRenderer<KotlinType> {
  override fun render(obj: KotlinType, renderingContext: RenderingContext): String =
    ProofRenderer.renderType(obj)
}