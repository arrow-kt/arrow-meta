package arrow.meta

import arrow.meta.dsl.MetaPluginSyntax
import arrow.meta.internal.registry.InternalRegistry
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

data class Plugin<A>(
  val name: String,
  val meta: A.() -> List<ExtensionPhase>
)

/**
 * An Arrow Meta [CliPlugin] is a named function that given a [CompilerContext] produces a [List] of [ExtensionPhase].
 *
 * The following plugin named `"Hello World"` returns a single `func` extension phase that produces a transformation
 * on the user tree.
 *
 * In this simple plugin we are replacing all functions named `helloWorld` with a new function that prints
 * "Hello ΛRROW Meta!" when invoked.
 *
 * ```kotlin
 * val Meta.helloWorld: CliPlugin get() =
 *   "Hello World" {
 *     meta(
 *       namedFunction(this, { name == "helloWorld" }) { c ->  // <-- namedFunction(...) {...}
 *         Transform.replace(
 *           replacing = c,
 *           newDeclaration = """|fun helloWorld(): Unit =
 *                               |  println("Hello ΛRROW Meta!")
 *                               |""".function.syntheticScope
 *         )
 *       }
 *     )
 *   }
 * ```
 *
 * `func` is part of the [arrow.meta.quotes.Quote] DSL, a high level DSL built for tree transformations.
 * While most use cases can be covered by the Quote DSL you can also subscribe to the low level compiler phases
 * such as Configuration, Analysis, Resolution and Code generation with the Arrow Meta Compiler DSL [arrow.meta.dsl].
 */
typealias CliPlugin = Plugin<CompilerContext>

/**
 * Enables syntactic sugar for plugin creation via:
 * "pluginId" {
 *   meta(
 *    ..phases
 *   )
 * }
 */
operator fun String.invoke(phases: CompilerContext.() -> List<ExtensionPhase>): CliPlugin =
  Plugin(this, phases)

/**
 * [Meta] is the core entry point and impl of Arrow Meta in terms of its registration system and interface with the
 * Kotlin Compiler
 *
 * Plugin authors are encouraged to define [CliPlugin] extensions by making them part of the [Meta] receiver:
 *
 * ```kotlin
 * val Meta.helloWorld: CliPlugin get() =
 *   "Hello World" {
 *     meta(
 *       ...
 *     )
 *   }
 * ```
 *
 * The [Meta] receiver provides automatic syntax for everything the user needs to build plugins through delegation to
 * [MetaPluginSyntax] and providing the internal implementation to the compiler subscriptions via [InternalRegistry]
 */
interface Meta : ComponentRegistrar, MetaPluginSyntax, InternalRegistry {

  /**
   * The [Meta] plugin supports N numbers of local and remote sub [CliPlugin]  that provide a [List] of [ExtensionPhase]
   * subscriptions. [registerProjectComponents] [Meta] implementation calls [intercept] to deliver subscriptions to
   * the Kotlin Compiler in the right order.
   *
   * As the Kotlin Compiler progresses through its phases it will call the plugin [ExtensionPhase].
   * Depending on the Compiler phase each of the phases of a plugin are called. The compiler invokes the [CliPlugin] in the
   * same ordered as they are returned in [intercept].
   *
   * Users may override [intercept] to add further plugins or programmatically remove some of the predefined ones in their
   * custom [CliPlugin]
   */
  override fun intercept(ctx: CompilerContext): List<CliPlugin>

  /**
   * CLI Compiler [ComponentRegistrar] entry point.
   */
  override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) =
    super.registerProjectComponents(project, configuration)

}
