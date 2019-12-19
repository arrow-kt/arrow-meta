package arrow.meta.quotes

import org.jetbrains.kotlin.psi.KtElement

open class WhenScope<out K : KtElement>(override val value: K?): Scope<K>(value) {
  override fun toString(): String =
    if (value != null) "(${value?.text})" ?: "" else "" //
}