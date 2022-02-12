package arrow.meta.phases.analysis.fir

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirTypeAttributeExtension

interface FirMetaExtension : ExtensionPhase {

  fun CompilerContext.configureFirStatusTransformerExtension(
    firSession: FirSession
  ): FirStatusTransformerExtension

  fun CompilerContext.configureFirDeclarationGenerationExtension(
    firSession: FirSession
  ): FirDeclarationGenerationExtension

  fun CompilerContext.configureFirAdditionalCheckersExtension(
    firSession: FirSession
  ): FirAdditionalCheckersExtension

  fun CompilerContext.configureFirSupertypeGenerationExtension(
    firSession: FirSession
  ): FirSupertypeGenerationExtension

  fun CompilerContext.configureFirTypeAttributeExtension(
    firSession: FirSession
  ): FirTypeAttributeExtension
}
