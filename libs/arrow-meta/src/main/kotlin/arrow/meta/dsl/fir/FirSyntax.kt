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

  fun CompilerContext.fir(
    additionalCheckers: (FirContext.() -> FirAdditionalCheckersExtension)? = null,
    declarationGeneration: (FirContext.() -> FirDeclarationGenerationExtension)? = null,
    statusTransformer: (FirContext.() -> FirStatusTransformerExtension)? = null,
    supertypeGeneration: (FirContext.() -> FirSupertypeGenerationExtension)? = null,
    typeAttribute: (FirContext.() -> FirTypeAttributeExtension)? = null,
  ): FirMetaExtension =
    object : FirMetaExtension {

      override fun CompilerContext.configureFirStatusTransformerExtension(
        firSession: FirSession
      ): FirStatusTransformerExtension =
        statusTransformer?.invoke(FirContext(firSession))
          ?: object : FirStatusTransformerExtension(firSession) {

            override fun needTransformStatus(declaration: FirDeclaration): Boolean = false
          }

      override fun CompilerContext.configureFirDeclarationGenerationExtension(
        firSession: FirSession
      ): FirDeclarationGenerationExtension =
        declarationGeneration?.invoke(FirContext(firSession))
          ?: object : FirDeclarationGenerationExtension(firSession) {}

      override fun CompilerContext.configureFirAdditionalCheckersExtension(
        firSession: FirSession
      ): FirAdditionalCheckersExtension =
        additionalCheckers?.invoke(FirContext(firSession))
          ?: object : FirAdditionalCheckersExtension(firSession) {}

      override fun CompilerContext.configureFirSupertypeGenerationExtension(
        firSession: FirSession
      ): FirSupertypeGenerationExtension =
        supertypeGeneration?.invoke(FirContext(firSession))
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
        typeAttribute?.invoke(FirContext(firSession))
          ?: object : FirTypeAttributeExtension(firSession) {

            override fun convertAttributeToAnnotation(attribute: ConeAttribute<*>): FirAnnotation? =
              null

            override fun extractAttributeFromAnnotation(
              annotation: FirAnnotation
            ): ConeAttribute<*>? = null
          }
    }
}
