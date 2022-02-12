package arrow.meta.dsl.fir

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.fir.FirMetaExtension
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirTypeAttributeExtension
import org.jetbrains.kotlin.fir.types.ConeAttribute
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef

interface FirSyntax {

  fun fir(
    firAdditionalCheckersExtension:
      (CompilerContext.(firSession: FirSession) -> FirAdditionalCheckersExtension)? =
      null,
    firDeclarationGenerationExtension:
      (CompilerContext.(firSession: FirSession) -> FirDeclarationGenerationExtension)? =
      null,
    firStatusTransformerExtension:
      (CompilerContext.(firSession: FirSession) -> FirStatusTransformerExtension)? =
      null,
    firSupertypeGenerationExtension:
      (CompilerContext.(firSession: FirSession) -> FirSupertypeGenerationExtension)? =
      null,
    firTypeAttributeExtension:
      (CompilerContext.(firSession: FirSession) -> FirTypeAttributeExtension)? =
      null,
  ): FirMetaExtension =
    object : FirMetaExtension {

      override fun CompilerContext.configureFirStatusTransformerExtension(
        firSession: FirSession
      ): FirStatusTransformerExtension =
        firStatusTransformerExtension?.invoke(ctx, firSession)
          ?: object : FirStatusTransformerExtension(firSession) {

            override fun needTransformStatus(declaration: FirDeclaration): Boolean = false
          }

      override fun CompilerContext.configureFirDeclarationGenerationExtension(
        firSession: FirSession
      ): FirDeclarationGenerationExtension =
        firDeclarationGenerationExtension?.invoke(ctx, firSession)
          ?: object : FirDeclarationGenerationExtension(firSession) {}

      override fun CompilerContext.configureFirAdditionalCheckersExtension(
        firSession: FirSession
      ): FirAdditionalCheckersExtension =
        firAdditionalCheckersExtension?.invoke(ctx, firSession)
          ?: object : FirAdditionalCheckersExtension(firSession) {}

      override fun CompilerContext.configureFirSupertypeGenerationExtension(
        firSession: FirSession
      ): FirSupertypeGenerationExtension =
        firSupertypeGenerationExtension?.invoke(ctx, firSession)
          ?: object : FirSupertypeGenerationExtension(firSession) {

            override fun computeAdditionalSupertypes(
              classLikeDeclaration: FirClassLikeDeclaration,
              resolvedSupertypes: List<FirResolvedTypeRef>
            ): List<FirResolvedTypeRef> = resolvedSupertypes

            override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean =
              false
          }

      override fun CompilerContext.configureFirTypeAttributeExtension(
        firSession: FirSession
      ): FirTypeAttributeExtension =
        firTypeAttributeExtension?.invoke(ctx, firSession)
          ?: object : FirTypeAttributeExtension(firSession) {

            override fun convertAttributeToAnnotation(attribute: ConeAttribute<*>): FirAnnotation? =
              null

            override fun extractAttributeFromAnnotation(
              annotation: FirAnnotation
            ): ConeAttribute<*>? = null
          }
    }
}
