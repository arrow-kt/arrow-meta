package arrow.meta.quotes.element

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.resolve.ImportPath

/**
 * <code>importDirective(ImportPath(importedFqName, isAllUnder, alias))</code>
 *
 * A template destructuring [Scope] for a [KtImportDirective].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.importDirective
 * import org.jetbrains.kotlin.resolve.ImportPath
 *
 * val Meta.reformatImportDirective: Plugin
 *  get() =
 *   "ReformatImportDirective" {
 *    meta(
 *     importDirective({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = importDirective(ImportPath(importedFqName, isAllUnder, alias))
 *      )
 *      }
 *     )
 *    }
 * ```
 */
class ImportDirective(
  override val value: KtImportDirective?,
  val importedFqName: FqName = value?.importedFqName!!,
  val isAllUnder: Boolean = value?.isAllUnder == true,
  val alias: Name? = value?.aliasName?.let(Name::identifier)
) : Scope<KtImportDirective>(value) {
  override fun ElementScope.identity(): ImportDirective =
    importDirective(ImportPath(importedFqName, isAllUnder, alias))
}