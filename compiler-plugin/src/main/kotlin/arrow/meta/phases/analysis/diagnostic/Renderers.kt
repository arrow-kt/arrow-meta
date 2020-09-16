package arrow.meta.phases.analysis.diagnostic

import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticParameterRenderer
import org.jetbrains.kotlin.diagnostics.rendering.RenderingContext
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.KotlinType

@JvmField
val RenderTypeWithFqName: DiagnosticParameterRenderer<KotlinType> =
  object : DiagnosticParameterRenderer<KotlinType> {
    override fun render(obj: KotlinType, renderingContext: RenderingContext): String =
      obj.constructor.declarationDescriptor?.fqNameOrNull()?.asString()
        ?: DescriptorRenderer.FQ_NAMES_IN_TYPES.renderType(obj)
  }