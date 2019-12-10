package arrow.meta.ide.phases.editor.service

import arrow.meta.phases.ExtensionPhase

/**
 * Services are in a higher-kinded position and can be either be simplified to
 * `Id<S>` or provide a type constructor, which can be used for further polymorphic abstractions, on [F].
 * isomorphic signature: Kind<F, S> where [S] is the concrete Service type.
 */
sealed class ServiceProvider : ExtensionPhase {
  @Suppress("UNCHECKED_CAST")
  data class RegisterApplicationService<S>(
    val service: S,
    val serviceClass: Class<S> // somehow express this = service::class.java as Class<F>
  )

  data class RegisterApplicationServiceF<F, S>(
    val service: Class<S>,
    val ctx: Class<F>
  )
}