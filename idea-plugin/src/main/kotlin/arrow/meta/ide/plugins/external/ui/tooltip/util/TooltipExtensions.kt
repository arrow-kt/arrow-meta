package arrow.meta.ide.plugins.external.ui.tooltip.util

private const val META_TAG = "<META>"

fun String?.isArrowMetaTooltip() = this?.containsMetaTag() ?: false

fun String.containsMetaTag() = this.contains(META_TAG)

fun String.applyMetaStyles() = "$META_TAG$this"

fun String.removeMetaTags() = this.replace(META_TAG, "")