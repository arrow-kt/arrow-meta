package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtImportAlias
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportInfo
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.resolve.ImportPath

/**
 * A [KtImportDirective] [Quote] with a custom template destructuring [ImportDirective]
 *
 * @param match designed to to feed in any kind of [KtImportDirective] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.importDirective(
  match: KtImportDirective.() -> Boolean,
  map: ImportDirective.(KtImportDirective) -> Transform<KtImportDirective>
) : ExtensionPhase =
  quote(match, map) { ImportDirective(it) }

/**
 * A template destructuring [Scope] for a [KtLambdaExpression]
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