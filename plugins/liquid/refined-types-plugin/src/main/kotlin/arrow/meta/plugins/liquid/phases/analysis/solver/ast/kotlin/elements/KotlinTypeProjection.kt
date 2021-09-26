package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ProjectionKind
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeProjection
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtProjectionKind
import org.jetbrains.kotlin.psi.KtProjectionKind.*
import org.jetbrains.kotlin.psi.KtTypeProjection

fun interface KotlinTypeProjection: TypeProjection {
  fun impl(): KtTypeProjection
  override val projectionKind: ProjectionKind
    get() = when (impl().projectionKind) {
      IN -> ProjectionKind.IN
      OUT -> ProjectionKind.OUT
      STAR -> ProjectionKind.STAR
      NONE -> ProjectionKind.NONE
    }
  override val typeReference: TypeReference?
    get() = impl().typeReference?.model()
}
