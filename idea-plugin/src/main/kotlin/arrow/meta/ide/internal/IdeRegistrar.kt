package arrow.meta.ide.internal

import arrow.meta.ide.MetaIde
import com.intellij.ide.ApplicationInitializedListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import kotlin.contracts.ExperimentalContracts

/**
 * entry point of Meta in the Ide
 */
class IdeRegistrar : ApplicationInitializedListener {
  val LOG = Logger.getInstance("#arrow.AppRegistrar")

  @ExperimentalContracts
  override fun componentsInitialized(): Unit {
    LOG.info("componentsInitialized")
    ApplicationManager.getApplication()?.let { app ->
      app.getService(MetaIde::class.java)?.registerMetaIdeComponents(app)
    }
  }
}
