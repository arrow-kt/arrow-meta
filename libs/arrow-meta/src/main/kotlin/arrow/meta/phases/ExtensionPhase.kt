package arrow.meta.phases

/**
 * An [ExtensionPhase] represents a subscription to one of the Kotlin compiler phases.
 *
 * This reified representation enables the [arrow.meta.dsl.MetaPluginSyntax] as a functional interface.
 * Once a [arrow.meta.Plugin] declares the [List] of [ExtensionPhase] Meta receives a value that can use
 * in the [arrow.meta.internal.registry.InternalRegistry] to register to the different compiler phases.
 */
interface ExtensionPhase {

  /**
   * Empty identity performs no registration
   */
  object Empty : ExtensionPhase
}
