package arrow.meta.ide

import com.intellij.ide.ApplicationInitializedListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.impl.ProjectLifecycleListener
import org.jetbrains.kotlin.config.CompilerConfiguration

class MetaPluginRegistrar : ApplicationInitializedListener {
  companion object {
    val metaPlugin = IdeMetaPlugin()
    private val LOG = Logger.getInstance("#arrow.metaRegistrar")
  }

  override fun componentsInitialized() {
    val app = ApplicationManager.getApplication()

    // register application-level extensions
    // TODO support registration of application extensions
    // IdeMetaPlugin.Instance.registerMetaApplicationComponents(app, ...)

    // register a project lifecycle listener to register project components when necessary
    // the listener registers project-level extensions
    app.messageBus.connect(app).subscribe(ProjectLifecycleListener.TOPIC, object : ProjectLifecycleListener {
      override fun beforeProjectLoaded(project: Project) {
        LOG.info("beforeProjectLoaded(${project.name})")

        val start = System.currentTimeMillis()
        val configuration = CompilerConfiguration()
        // TODO: only register project extensions here
        metaPlugin.registerMetaComponents(project, configuration)
        LOG.info("beforeProjectLoaded(${project.name}) took ${System.currentTimeMillis() - start} ms")
      }

      override fun projectComponentsInitialized(project: Project) {
        LOG.info("projectComponentsInitialized(${project.name})")
      }

      override fun afterProjectClosed(project: Project) {
        LOG.info("afterProjectClosed(${project.name})")
      }
    })
  }
}
