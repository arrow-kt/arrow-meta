package arrow.meta.dsl.fir

import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.extensions.FirTypeAttributeExtension
import org.jetbrains.kotlin.fir.types.ConeAttribute

fun FirContext.typeAttribute(
  convertAttributeToAnnotation: ((attribute: ConeAttribute<*>) -> FirAnnotation?)? = null,
  extractAttributeFromAnnotation: ((annotation: FirAnnotation) -> ConeAttribute<*>?)? = null
): FirTypeAttributeExtension =
  object : FirTypeAttributeExtension(firSession) {
    override fun convertAttributeToAnnotation(attribute: ConeAttribute<*>): FirAnnotation? =
      if (convertAttributeToAnnotation == null) null else convertAttributeToAnnotation(attribute)

    override fun extractAttributeFromAnnotation(annotation: FirAnnotation): ConeAttribute<*>? =
      if (extractAttributeFromAnnotation == null) null
      else extractAttributeFromAnnotation(annotation)
  }
