package arrow.meta.ide.plugins.external.ui.tooltip.util

private const val META_TAG = "<META>"

fun String?.isArrowMetaTooltip() = this?.containsMetaTag() ?: false

fun String.containsMetaTag() = this.startsWith("<META>")

fun String.applyMetaStyles() = "$META_TAG$this"

fun String.removeMetaTag() = this.removePrefix(META_TAG)