package arrow.meta.ide.dsl.integration

import arrow.meta.ide.MetaIde
import arrow.meta.ide.phases.integration.*
import arrow.meta.ide.phases.integration.indices.KotlinIndicesHelper
import arrow.meta.phases.CompilerContext
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
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.types.KotlinType

/**
 * [IntegrationSyntax] servers as an interoperability algebra to register compiler extensions and kotlin extensions regarding the Ide.
 */
interface IntegrationSyntax {

  fun MetaIde.syntheticResolver(
    f: (Project) -> SyntheticResolver?
  ): ExtensionPhase =
    object : IDESyntheticResolver {
      override fun syntheticResolver(project: Project): SyntheticResolver? = f(project)
    }

  fun MetaIde.packageFragmentProvider(
    f: (Project) -> PackageProvider?
  ): ExtensionPhase =
    object : IDEPackageProvider {
      override fun packageFragmentProvider(project: Project): PackageProvider? = f(project)
    }

  fun MetaIde.syntheticScopeProvider(
    f: (Project) -> SyntheticScopeProvider?
  ): ExtensionPhase =
    object : IDESyntheticScopeProvider {
      override fun syntheticScopeProvider(project: Project): SyntheticScopeProvider? = f(project)
    }

  fun MetaIde.irGeneration(
    f: (Project) -> IRGeneration?
  ): ExtensionPhase =
    object : IDEIRGeneration {
      override fun irGeneration(project: Project): IRGeneration? = f(project)
    }

  fun MetaIde.declarationAttributeAlterer(
    f: (Project) -> DeclarationAttributeAlterer?
  ): ExtensionPhase =
    object : IDEDeclarationAttributeAlterer {
      override fun declarationAttributeAlterer(project: Project): DeclarationAttributeAlterer? = f(project)
    }

  fun MetaIde.codegen(
    f: (Project) -> Codegen?
  ): ExtensionPhase =
    object : IDECodegen {
      override fun codegen(project: Project): Codegen? = f(project)
    }

  fun MetaIde.classBuilder(
    f: (Project) -> ClassBuilder?
  ): ExtensionPhase =
    object : IDEClassBuilder {
      override fun classBuilder(project: Project): ClassBuilder? = f(project)
    }

  fun MetaIde.analysisHandler(
    f: (Project) -> AnalysisHandler?
  ): ExtensionPhase =
    object : IDEAnalysisHandler {
      override fun analysisHandler(project: Project): AnalysisHandler? = f(project)
    }

  fun MetaIde.storageComponentContainer(
    f: (Project) -> StorageComponentContainer?
  ): ExtensionPhase =
    object : IDEStorageComponentContainer {
      override fun storageComponentContainer(project: Project): StorageComponentContainer? = f(project)
    }

  fun MetaIde.preprocessedVirtualFileFactory(
    f: (Project) -> PreprocessedVirtualFileFactory?
  ): ExtensionPhase =
    object : IDEPreprocessedVirtualFileFactory {
      override fun preprocessedVirtualFileFactory(project: Project): PreprocessedVirtualFileFactory? = f(project)
    }

  fun MetaIde.extraImports(
    f: (Project) -> ExtraImports?
  ): ExtensionPhase =
    object : IDEExtraImports {
      override fun extraImports(project: Project): ExtraImports? = f(project)
    }

  fun MetaIde.config(
    f: (Project) -> Config?
  ): ExtensionPhase =
    object : IDEConfig {
      override fun config(project: Project): Config? = f(project)
    }

  fun MetaIde.collectAdditionalSources(
    f: (Project) -> CollectAdditionalSources?
  ): ExtensionPhase =
    object : IDECollectAdditionalSources {
      override fun collectAdditionalSources(project: Project): CollectAdditionalSources? = f(project)
    }

  fun MetaIde.kotlinIndices(
    appendExtensionCallables:
    CompilerContext.(
      project: Project,
      consumer: MutableList<in CallableDescriptor>,
      moduleDescriptor: ModuleDescriptor,
      receiverTypes: Collection<KotlinType>,
      nameFilter: (String) -> Boolean,
      lookupLocation: LookupLocation
    ) -> Unit
  ): ExtensionPhase =
    object : KotlinIndicesHelper {
      override fun CompilerContext.appendExtensionCallables(project: Project, consumer: MutableList<in CallableDescriptor>, moduleDescriptor: ModuleDescriptor, receiverTypes: Collection<KotlinType>, nameFilter: (String) -> Boolean, lookupLocation: LookupLocation): Unit =
        appendExtensionCallables(project, consumer, moduleDescriptor, receiverTypes, nameFilter, lookupLocation)
    }
}