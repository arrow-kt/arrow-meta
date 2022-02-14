package arrow.meta.dsl.fir

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.impl.FirDeclarationStatusImpl
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension

fun FirContext.statusTransformer(
  needTransformStatus: (FirDeclaration) -> Boolean = { true },
  transformStatus: (FirDeclarationStatus, FirDeclaration) -> FirDeclarationStatus,
): FirStatusTransformerExtension =
  object : FirStatusTransformerExtension(firSession) {

    override fun needTransformStatus(declaration: FirDeclaration): Boolean =
      needTransformStatus(declaration)

    override fun transformStatus(
      status: FirDeclarationStatus,
      declaration: FirDeclaration
    ): FirDeclarationStatus = transformStatus(status, declaration)
  }

fun declarationStatus(visibility: Visibility, modality: Modality): FirDeclarationStatus =
  FirDeclarationStatusImpl(visibility, modality)
