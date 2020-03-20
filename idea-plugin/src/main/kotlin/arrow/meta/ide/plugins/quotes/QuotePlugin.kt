package arrow.meta.ide.plugins.quotes

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.invoke

val IdeMetaPlugin.quotes: Plugin
  get() = "Quote Ide Plugin" {
    meta(
      metaSyntheticPackageFragmentProvider
    )
  }