package arrow.meta.dsl.config

import arrow.meta.Meta
import arrow.meta.dsl.platform.cli
import arrow.meta.dsl.platform.ide
import arrow.meta.internal.Noop
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.*
import arrow.meta.plugins.proofs.phases.resolve.ProofTypeChecker
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.resolve.calls.ArgumentTypeResolver
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker
import java.lang.reflect.Field
import java.lang.reflect.Modifier

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

/**
 * The nastier bits
 */
@Throws(Exception::class)
internal fun setFinalStatic(field: Field, newValue: Any) {
  field.isAccessible = true

  val modifiersField = Field::class.java.getDeclaredField("modifiers")
  modifiersField.isAccessible = true
  modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

  field.set(null, newValue)
}