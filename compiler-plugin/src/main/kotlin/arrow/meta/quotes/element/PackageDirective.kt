package arrow.meta.quotes.element

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

/**
 * <code>""" $`package` """.`package`</code>
 *
 * A template destructuring [Scope] for a [KtPackageDirective].
 *
 * ``kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.packageDirective
 *
 * val Meta.reformatPackage: Plugin
 *  get() =
 *  "ReformatPackage" {
 *   meta(
 *    packageDirective({ true }) { e ->
 *     Transform.replace(
 *      replacing = e,
 *      newDeclaration = """ $`package` """.`package`
 *     )
 *    }
 *   )
 *  }
 *```
 */
data class PackageDirective(
  override val value: KtPackageDirective?,
  val `package`: Scope<KtElement> = Scope(value?.packageNameExpression),
  val packages: ScopedList<KtSimpleNameExpression> = ScopedList(value?.packageNames ?: listOf()),
  val lastPackage: Scope<KtSimpleNameExpression> = Scope(value?.lastReferenceExpression)
) : Scope<KtPackageDirective>(value)