package arrow.meta.quotes.element.whencondition

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtWhenCondition

/**
 * <code>condition.whenCondition</code>
 *
 * A template destructuring [Scope] for a [KtWhenCondition].
 *
 *  ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.whenCondition
 *
 * val Meta.reformatWhenCondition: Plugin
 *  get() =
 *   "ReformatWhenCondition" {
 *    meta(
 *     whenCondition({ true }) { c ->
 *      Transform.replace(
 *       replacing = c,
 *       newDeclaration = condition.whenCondition
 *      )
 *     }
 *    )
 *   }
 *```
 */
class WhenCondition(
  override val value: KtWhenCondition?,
  val condition: String = value?.text ?: ""
) : Scope<KtWhenCondition>(value)