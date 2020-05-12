package arrow.meta.ide.phases.application

import arrow.meta.ide.dsl.application.ApplicationSyntax
import arrow.meta.ide.plugins.external.ui.tooltip.MetaEditorMouseHoverPopupManager
import arrow.meta.phases.ExtensionPhase
import com.intellij.ide.AppLifecycleListener
import com.intellij.ide.plugins.ContainerDescriptor
import com.intellij.openapi.application.ApplicationListener
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.ModuleListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.impl.ProjectLifecycleListener

/**
 * @see ApplicationSyntax
 */
sealed class ApplicationProvider : ExtensionPhase {
  /**
   * @see ApplicationSyntax
   */
  data class AppService<A : Any>(val service: Class<A>, val instance: (Any?) -> A?) : ApplicationProvider()

  /**
   * @see ApplicationSyntax
   */
  data class ProjectService<A : Any>(val service: Class<A>, val instance: (Project, Any?) -> A?) : ApplicationProvider()

  /**
   * @see ApplicationSyntax
   */
  data class OverrideService(val from: Class<*>, val to: Class<*>, val override: Boolean) : ApplicationProvider()

  /**
   * @see ApplicationSyntax
   */
  data class ReplaceAppService<A : Any>(val service: Class<A>, val instance: (Any?) -> A) : ApplicationProvider()

  /**
   * @see ApplicationSyntax
   */
  data class ReplaceProjectService<A : Any>(val service: Class<A>, val instance: (Project, Any?) -> A) : ApplicationProvider()

  /**
   * @see ApplicationSyntax
   */
  data class Listener(val listener: ApplicationListener) : ApplicationProvider()

  /**
   * @see ApplicationSyntax
   */
  data class AppListener(val listener: AppLifecycleListener) : ApplicationProvider()

  /**
   * @see ApplicationSyntax
   */
  data class ProjectListener(val listener: ProjectLifecycleListener) : ApplicationProvider()

  /**
   * @see ApplicationSyntax
   */
  data class MetaModuleListener(val listener: ModuleListener) : ApplicationProvider()

  /**
   * @see ApplicationSyntax
   */
  object StopServicePreloading : ApplicationProvider()

  /**
   * TODO: create an extension for Meta API
   */
  data class UnloadServices(val container: ContainerDescriptor) : ApplicationProvider()

  /**
   * @see EditorSyntax
   */
  data class FileEditorListener(val listener: FileEditorManagerListener) : ApplicationProvider()

  data class MouseEditorListener(val listener: MetaEditorMouseHoverPopupManager.MyEditorMouseEventListener) : ApplicationProvider()

  data class MouseMotionEditorListener(val listener: MetaEditorMouseHoverPopupManager.MyEditorMouseMotionEventListener) : ApplicationProvider()
}
