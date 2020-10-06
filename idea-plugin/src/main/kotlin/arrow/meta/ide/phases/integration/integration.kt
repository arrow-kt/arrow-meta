package arrow.meta.ide.phases.integration

import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.project.Project

interface SyntheticResolver : ExtensionPhase {
  fun syntheticResolver(project: Project): arrow.meta.phases.resolve.synthetics.SyntheticResolver?
}

interface PackageProvider : ExtensionPhase {
  fun packageFragmentProvider(project: Project): arrow.meta.phases.resolve.PackageProvider?
}

interface SyntheticScopeProvider : ExtensionPhase {
  fun syntheticScopeProvider(project: Project): arrow.meta.phases.resolve.synthetics.SyntheticScopeProvider?
}

interface IRGeneration : ExtensionPhase {
  fun irGeneration(project: Project): arrow.meta.phases.codegen.ir.IRGeneration?
}

interface DeclarationAttributeAlterer : ExtensionPhase {
  fun declarationAttributeAlterer(project: Project): arrow.meta.phases.resolve.DeclarationAttributeAlterer?
}

interface Codegen : ExtensionPhase {
  fun codegen(project: Project): arrow.meta.phases.codegen.asm.Codegen?
}

interface ClassBuilder : ExtensionPhase {
  fun classBuilder(project: Project): arrow.meta.phases.codegen.asm.ClassBuilder?
}

interface AnalysisHandler : ExtensionPhase {
  fun analysisHandler(project: Project): arrow.meta.phases.analysis.AnalysisHandler?
}

interface StorageComponentContainer : ExtensionPhase {
  fun storageComponentContainer(project: Project): arrow.meta.phases.config.StorageComponentContainer?
}

interface PreprocessedVirtualFileFactory : ExtensionPhase {
  fun preprocessedVirtualFileFactory(project: Project): arrow.meta.phases.analysis.PreprocessedVirtualFileFactory?
}

interface ExtraImports : ExtensionPhase {
  fun extraImports(project: Project): arrow.meta.phases.analysis.ExtraImports?
}

interface Config : ExtensionPhase {
  fun config(project: Project): arrow.meta.phases.config.Config?
}

interface CollectAdditionalSources : ExtensionPhase {
  fun collectAdditionalSources(project: Project): arrow.meta.phases.analysis.CollectAdditionalSources?
}