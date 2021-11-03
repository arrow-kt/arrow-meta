@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.types

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.TypeProjection
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Variance
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.WildcardType

public class JavaTypeProjection(private val ctx: AnalysisContext, private val ty: TypeMirror) :
  TypeProjection {
  override val projectionKind: Variance = Variance.Invariant
  override val type: Type = JavaType(ctx, ty)
  override val isStarProjection: Boolean =
    ty.visit(
      object : OurTypeVisitor<Boolean>(false) {
        override fun visitWildcard(t: WildcardType?, p: TypeMirror?): Boolean = true
      }
    )
}
