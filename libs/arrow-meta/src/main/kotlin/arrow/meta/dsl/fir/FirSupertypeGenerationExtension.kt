package arrow.meta.dsl.fir

import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef

fun FirContext.superTypeGeneration(
  computeAdditionalSupertypes:
    ((
      classLikeDeclaration: FirClassLikeDeclaration,
      resolvedSupertypes: List<FirResolvedTypeRef>) -> List<FirResolvedTypeRef>)? =
    null,
  needTransformSupertypes: ((declaration: FirClassLikeDeclaration) -> Boolean)? = null,
): FirSupertypeGenerationExtension =
  object : FirSupertypeGenerationExtension(firSession) {
    override fun computeAdditionalSupertypes(
      classLikeDeclaration: FirClassLikeDeclaration,
      resolvedSupertypes: List<FirResolvedTypeRef>
    ): List<FirResolvedTypeRef> =
      if (computeAdditionalSupertypes == null) resolvedSupertypes
      else computeAdditionalSupertypes(classLikeDeclaration, resolvedSupertypes)

    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean =
      if (needTransformSupertypes == null) true else needTransformSupertypes(declaration)
  }
