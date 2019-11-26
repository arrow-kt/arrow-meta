package arrow.meta.quotes.element

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtWhenCondition

/**
 * A template destructuring [Scope] for a [KtWhenCondition]
 */
class WhenCondition(
  override val value: KtWhenCondition?,
  val condition: String = value?.text ?: ""
) : Scope<KtWhenCondition>(value)