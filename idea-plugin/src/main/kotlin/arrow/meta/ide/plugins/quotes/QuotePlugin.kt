package arrow.meta.ide.plugins.quotes

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.quotes.lifecycle.quoteProjectOpened
import arrow.meta.ide.plugins.quotes.resolve.quoteSyntheticPackageFragmentProvider
import arrow.meta.invoke
import com.intellij.openapi.startup.StartupActivity

/**
 * Please, view the sub directories `cache` , `resolve` and `system` for quotes related IDE features.
 */
val IdeMetaPlugin.quotes: Plugin
  get() = "Quote Ide Plugin" {
    meta(
      quoteSyntheticPackageFragmentProvider,
      addPostStartupActivity(
        StartupActivity.DumbAware {
          quoteProjectOpened(it)
        }
      )
    )
  }