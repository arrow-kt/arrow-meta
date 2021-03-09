package arrow.meta.dsl.config

import arrow.meta.Meta
import arrow.meta.dsl.platform.cli
import arrow.meta.dsl.platform.ide
import arrow.meta.internal.Noop
import arrow.meta.internal.registry.setFinalStatic
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.*
import arrow.meta.phases.config.Config
import arrow.meta.plugins.proofs.phases.resolve.ProofTypeChecker
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.calls.ArgumentTypeResolver
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker

/**
 * The configuration phase allows changing the compiler configuration prior to compilation.
 * In this phase, we can programmatically activate and change all compiler flags and system
 * properties the compiler uses to enable/disable the different features in compilation.
 */
interface ConfigSyntax {

  /**
   * The [updateConfig] function provides access to the [CompilerConfiguration] that contains the map of properties used
   * to enable/disable the different features in compilation.
   *
   * @updateConfiguration enables a DSL using [CompilerContext], and it can update [CompilerConfiguration] through its mutable API.
   * @return [Config] [ExtensionPhase].
   */
  fun updateConfig(updateConfiguration: CompilerContext.(configuration: CompilerConfiguration) -> Unit): Config =
    object : Config {
      override fun CompilerContext.updateConfiguration(configuration: CompilerConfiguration) {
        updateConfiguration(configuration)
      }
    }

  /**
   * The [storageComponent] function allows access to the [StorageComponentContributor].
   * This is the Dependency Injector and service registry the compiler uses in all phases.
   * In this function, you can register new services or modify existing ones before the container is composed and sealed prior to compilation.
   *
   * @registerModuleComponents enables a DSL using [CompilerContext], and it can update the [StorageComponentContainer] and [ModuleDescriptor] to
   * update the DI configuration of the compiler.
   */
  fun storageComponent(
    registerModuleComponents: CompilerContext.(container: StorageComponentContainer, moduleDescriptor: ModuleDescriptor) -> Unit,
    check: CompilerContext.(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) -> Unit = Noop.effect4
  ): arrow.meta.phases.config.StorageComponentContainer =
    object : arrow.meta.phases.config.StorageComponentContainer {
      override fun CompilerContext.check(
        declaration: KtDeclaration,
        descriptor: DeclarationDescriptor,
        context: DeclarationCheckerContext
      ) {
        check(declaration, descriptor, context)
      }

      override fun CompilerContext.registerModuleComponents(container: StorageComponentContainer, moduleDescriptor: ModuleDescriptor) {
        registerModuleComponents(container, moduleDescriptor)
      }
    }

  fun declarationChecker(
    check: CompilerContext.(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) -> Unit
  ): arrow.meta.phases.config.StorageComponentContainer =
    storageComponent(Noop.effect3, check)

  /**
   * The [enableIr] function enables the Intermediate Representation Backend.
   * The IR Backend is a part of the code generation phase and emits code in the IR format.
   * The IR Format is a tree structure with significant indentation that contains all the information needed to generate bytecode
   * for all platforms the Kotlin programming language targets.
   * When the IR backend is disabled, which is the current default in the Kotlin Compiler, the [JVM ASM Backend] is used instead.
   *
   * [IR Example]
   */
  fun enableIr(): ExtensionPhase =
    cli {
      updateConfig { configuration ->
        configuration.put(JVMConfigurationKeys.IR, true)
      }
    } ?: ExtensionPhase.Empty

  /**
   * The [typeChecker] function allows the user to provide a custom implementation of the [KotlinTypeChecker].
   * With a custom [KotlinTypeChecker], we can redefine what subtyping and type equality means.
   */
  fun Meta.typeChecker(replace: (KotlinTypeChecker) -> NewKotlinTypeChecker): ExtensionPhase =
    Composite(storageComponent(
      registerModuleComponents = { container, moduleDescriptor ->
        evaluateDependsOnRewindableAnalysisPhase { evaluateTypeChecker(replace) }
      },
      check = Noop.effect4
    ), registerArgumentTypeResolver())

  private fun evaluateTypeChecker(replace: (KotlinTypeChecker) -> NewKotlinTypeChecker) {
    val defaultTypeChecker = KotlinTypeChecker.DEFAULT
    val replacement = replace(defaultTypeChecker)
    if (replacement != defaultTypeChecker) {
      val defaultTypeCheckerField = KotlinTypeChecker::class.java.getDeclaredField("DEFAULT")
      setFinalStatic(defaultTypeCheckerField, replacement)
    }
  }

  fun callChecker(
    check: CompilerContext.(resolvedCall: ResolvedCall<*>, reportOn: org.jetbrains.kotlin.com.intellij.psi.PsiElement, context: CallCheckerContext) -> Unit
  ): arrow.meta.phases.config.StorageComponentContainer =
    storageComponent(
      registerModuleComponents = { container, _ ->
        val ctx = this
        container.useInstance(
          object : CallChecker {
            override fun check(resolvedCall: ResolvedCall<*>, reportOn: org.jetbrains.kotlin.com.intellij.psi.PsiElement, context: CallCheckerContext): Unit =
              ctx.check(resolvedCall, reportOn, context)
          }
        )
      }
    )

  private fun Meta.registerArgumentTypeResolver(): ExtensionPhase =
    cli {
      analysis(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          if (!ctx.analysisPhaseWasRewind.get()) return@analysis null
          Log.Verbose({ "analysis.registerArgumentTypeResolver.initializeProofCache + replace type checker" }) {
            replaceArgumentTypeResolverTypeChecker(componentProvider)
            null
          }
        }
      )
    } ?: ide {
      packageFragmentProvider { project, module, storageManager, trace, moduleInfo, lookupTracker ->
        componentProvider?.let { replaceArgumentTypeResolverTypeChecker(it) }
        null
      }
    } ?: ExtensionPhase.Empty

  private fun CompilerContext.replaceArgumentTypeResolverTypeChecker(componentProvider: ComponentProvider) {
    val argumentTypeResolver: ArgumentTypeResolver = componentProvider.get()
    replaceTypeChecker(argumentTypeResolver)
  }

  private fun CompilerContext.replaceTypeChecker(argumentTypeResolver: ArgumentTypeResolver) =
    Log.Verbose({ "replaceArgumentTypeResolverTypeChecker $argumentTypeResolver" }) {
      val typeCheckerField = ArgumentTypeResolver::class.java.getDeclaredField("kotlinTypeChecker").also { it.isAccessible = true }
      typeCheckerField.set(argumentTypeResolver, ProofTypeChecker(this))
    }

}
