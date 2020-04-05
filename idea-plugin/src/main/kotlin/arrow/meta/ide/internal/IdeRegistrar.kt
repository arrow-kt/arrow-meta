package arrow.meta.ide.internal

import arrow.meta.ide.IdeMetaPlugin
import com.intellij.ide.ApplicationInitializedListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import kotlin.contracts.ExperimentalContracts

class IdeRegistrar : ApplicationInitializedListener {
  val LOG = Logger.getInstance("#arrow.AppRegistrar")

  @ExperimentalContracts
  override fun componentsInitialized(): Unit {
    LOG.info("componentsInitialized")
    ApplicationManager.getApplication()?.let { app ->
      metaPlugin.registerMetaIdeComponents(app)
    }
  }
}

private val metaPlugin = IdeMetaPlugin()
