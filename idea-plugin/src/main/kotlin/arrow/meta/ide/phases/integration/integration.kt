package arrow.meta.ide.phases.integration

import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.CollectAdditionalSources
import arrow.meta.phases.analysis.ExtraImports
import arrow.meta.phases.analysis.PreprocessedVirtualFileFactory
import arrow.meta.phases.codegen.asm.ClassBuilder
import arrow.meta.phases.codegen.asm.Codegen
import arrow.meta.phases.codegen.ir.IRGeneration
import arrow.meta.phases.config.Config
import arrow.meta.phases.config.StorageComponentContainer
import arrow.meta.phases.resolve.DeclarationAttributeAlterer
import arrow.meta.phases.resolve.PackageProvider
import arrow.meta.phases.resolve.synthetics.SyntheticResolver
import arrow.meta.phases.resolve.synthetics.SyntheticScopeProvider
import com.intellij.openapi.project.Project

interface IDESyntheticResolver : ExtensionPhase {
  fun syntheticResolver(project: Project): SyntheticResolver?
}

interface IDEPackageProvider : ExtensionPhase {
  fun packageFragmentProvider(project: Project): PackageProvider?
}

interface IDESyntheticScopeProvider : ExtensionPhase {
  fun syntheticScopeProvider(project: Project): SyntheticScopeProvider?
}

interface IDEIRGeneration : ExtensionPhase {
  fun irGeneration(project: Project): IRGeneration?
}

interface IDEDeclarationAttributeAlterer : ExtensionPhase {
  fun declarationAttributeAlterer(project: Project): DeclarationAttributeAlterer?
}

interface IDECodegen : ExtensionPhase {
  fun codegen(project: Project): Codegen?
}

interface IDEClassBuilder : ExtensionPhase {
  fun classBuilder(project: Project): ClassBuilder?
}

interface IDEAnalysisHandler : ExtensionPhase {
  fun analysisHandler(project: Project): AnalysisHandler?
}

interface IDEStorageComponentContainer : ExtensionPhase {
  fun storageComponentContainer(project: Project): StorageComponentContainer?
}

interface IDEPreprocessedVirtualFileFactory : ExtensionPhase {
  fun preprocessedVirtualFileFactory(project: Project): PreprocessedVirtualFileFactory?
}

interface IDEExtraImports : ExtensionPhase {
  fun extraImports(project: Project): ExtraImports?
}

interface IDEConfig : ExtensionPhase {
  fun config(project: Project): Config?
}

interface IDECollectAdditionalSources : ExtensionPhase {
  fun collectAdditionalSources(project: Project): CollectAdditionalSources?
}