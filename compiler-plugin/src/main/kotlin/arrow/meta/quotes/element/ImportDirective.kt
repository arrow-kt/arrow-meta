package arrow.meta.quotes.element

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtImportAlias
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportInfo
import org.jetbrains.kotlin.resolve.ImportPath

/**
 * <code>""" import $importPath.$importedName """.importDirective</code>
 *
 * A template destructuring [Scope] for a [KtImportDirective].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.importDirective
 *
 * val Meta.reformatImportDirective: Plugin
 *  get() =
 *   "ReformatImportDirective" {
 *    meta(
 *     importDirective({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """ import $importPath.$importedName """.importDirective
 *      )
 *      }
 *     )
 *    }
 * ```
 */
class ImportDirective(
  override val value: KtImportDirective,
  val importedReference: Scope<KtExpression> = Scope(value.importedReference), // TODO KtExpression scope and quote template
  val alias: Scope<KtImportAlias> = Scope(value.alias), // TODO KtImportAlias scope and quote template
  val aliasName: String? = value.aliasName,
  val importContent: KtImportInfo.ImportContent? = value.importContent,
  val importedName: Name? = value.importedName,
  val importedFqName: FqName? = value.importedFqName,
  val importPath: ImportPath? = value.importPath
) : Scope<KtImportDirective>(value)