package arrow.meta.ide.internal.registry

import arrow.meta.dsl.platform.ide
import arrow.meta.ide.phases.analysis.MetaIdeAnalyzer
import arrow.meta.ide.phases.editor.AddClassExtension
import arrow.meta.ide.phases.editor.AddExtension
import arrow.meta.ide.phases.editor.AddFileTypeExtension
import arrow.meta.ide.phases.editor.AddLanguageExtension
import arrow.meta.ide.phases.editor.ExtensionProvider
import arrow.meta.ide.phases.editor.RegisterBaseExtension
import arrow.meta.ide.phases.editor.RegisterExtension
import arrow.meta.ide.phases.resolve.LOG
import arrow.meta.internal.registry.InternalRegistry
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
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
import com.intellij.core.CoreApplicationEnvironment
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.KotlinLanguage

internal interface IdeInternalRegistry : InternalRegistry {

  override fun registerMetaAnalyzer(): ExtensionPhase =
    ide {
      storageComponent(
        registerModuleComponents = { container, moduleDescriptor ->
          //println("Registering meta analyzer")
          container.useImpl<MetaIdeAnalyzer>()
          //
        },
        check = { declaration, descriptor, context ->
        }
      )
    } ?: ExtensionPhase.Empty

  override fun CompilerContext.registerIdeExclusivePhase(currentPhase: ExtensionPhase) =
    when (currentPhase) {
      is ExtensionPhase.Empty, is CollectAdditionalSources, is Composite, is Config, is ExtraImports,
      is PreprocessedVirtualFileFactory, is StorageComponentContainer, is AnalysisHandler, is ClassBuilder,
      is Codegen, is DeclarationAttributeAlterer, is PackageProvider, is SyntheticResolver,
      is IRGeneration, is SyntheticScopeProvider -> Unit // filter out ExtensionPhases which happen in the compiler
      is ExtensionProvider<*> -> registerExtensionProvider(currentPhase)

      else -> LOG.error("Unsupported ide extension phase: $currentPhase")
    }


  fun <E> registerExtensionProvider(phase: ExtensionProvider<E>, dispose: Disposable = Disposer.newDisposable()): Unit =
    when (phase) {
      is AddExtension -> phase.run {
        println("ADDED ${phase.EP_NAME.name}")
        Extensions.getRootArea().getExtensionPoint(EP_NAME).registerExtension(impl, loadingOrder, dispose)
      }
      is AddLanguageExtension -> phase.run {
        println("ADDED LanguageExtension: ${LE.name}")
        LE.addExplicitExtension(KotlinLanguage.INSTANCE, impl)
      }
      is AddFileTypeExtension -> phase.run { FE.addExplicitExtension(KotlinFileType.INSTANCE, impl) }
      is AddClassExtension -> phase.run { CE.addExplicitExtension(forClass, impl) }
      is RegisterBaseExtension -> phase.run { CoreApplicationEnvironment.registerExtensionPoint(Extensions.getRootArea(), EP_NAME, aClass) }
      is RegisterExtension -> phase.run { CoreApplicationEnvironment.registerExtensionPoint(Extensions.getRootArea(), EP_NAME, aClass) }
    }
}
