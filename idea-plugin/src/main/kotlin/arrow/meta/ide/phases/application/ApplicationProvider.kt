package arrow.meta.ide.phases.application

import arrow.meta.ide.dsl.application.ApplicationSyntax
import arrow.meta.ide.dsl.application.ProjectLifecycle
import arrow.meta.ide.dsl.application.ServiceKind
import arrow.meta.phases.ExtensionPhase
import com.intellij.ide.AppLifecycleListener
import com.intellij.ide.plugins.ContainerDescriptor
import com.intellij.openapi.application.ApplicationListener

/**
 * @see ApplicationSyntax
 */
sealed class ApplicationProvider : ExtensionPhase {

  data class Service<A : Any>(val service: Class<A>, val instance: A, val kind: ServiceKind) : ApplicationProvider()

  data class OverrideService(val from: Class<*>, val to: Class<*>, val override: Boolean) : ApplicationProvider()

  data class ReplaceService<A : Any>(val service: Class<A>, val instance: A) : ApplicationProvider()

  data class Listener(val listener: ApplicationListener) : ApplicationProvider()

  data class AppListener(val listener: AppLifecycleListener) : ApplicationProvider()

  data class ProjectListener(val listener: ProjectLifecycle) : ApplicationProvider()

  object StopServicePreloading : ApplicationProvider()

  /**
   * TODO: create an extension for Meta API
   */
  data class UnloadServices(val container: ContainerDescriptor) : ApplicationProvider()
}