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

interface IdeSyntheticResolver : ExtensionPhase {
  fun syntheticResolver(project: Project): SyntheticResolver?
}

interface IdePackageProvider : ExtensionPhase {
  fun packageFragmentProvider(project: Project): PackageProvider?
}

interface IdeSyntheticScopeProvider : ExtensionPhase {
  fun syntheticScopeProvider(project: Project): SyntheticScopeProvider?
}

interface IdeIRGeneration : ExtensionPhase {
  fun irGeneration(project: Project): IRGeneration?
}

interface IdeDeclarationAttributeAlterer : ExtensionPhase {
  fun declarationAttributeAlterer(project: Project): DeclarationAttributeAlterer?
}

interface IdeCodegen : ExtensionPhase {
  fun codegen(project: Project): Codegen?
}

interface IdeClassBuilder : ExtensionPhase {
  fun classBuilder(project: Project): ClassBuilder?
}

interface IdeAnalysisHandler : ExtensionPhase {
  fun analysisHandler(project: Project): AnalysisHandler?
}

interface IdeStorageComponentContainer : ExtensionPhase {
  fun storageComponentContainer(project: Project): StorageComponentContainer?
}

interface IdePreprocessedVirtualFileFactory : ExtensionPhase {
  fun preprocessedVirtualFileFactory(project: Project): PreprocessedVirtualFileFactory?
}

interface IdeExtraImports : ExtensionPhase {
  fun extraImports(project: Project): ExtraImports?
}

interface IdeConfig : ExtensionPhase {
  fun config(project: Project): Config?
}

interface IdeCollectAdditionalSources : ExtensionPhase {
  fun collectAdditionalSources(project: Project): CollectAdditionalSources?
}