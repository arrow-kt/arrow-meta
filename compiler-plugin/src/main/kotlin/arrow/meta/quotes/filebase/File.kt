package arrow.meta.quotes.filebase

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.element.PackageDirective
import org.jetbrains.kotlin.com.intellij.openapi.fileTypes.FileType
import org.jetbrains.kotlin.com.intellij.psi.PsiClass
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFileAnnotationList
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtScript
import org.jetbrains.kotlin.psi.stubs.KotlinFileStub

/**
 * <code>file(name, """ $packageDirective $importList $classes """)</code>
 *
 * A template destructuring [Scope] for a [File]. See below:
 *
 * ```
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.file
 *
 * val Meta.reformatFile: CliPlugin
 *   get() =
 *     "ReformatFile" {
 *       meta(
 *        file({ true }) { file ->
 *          Transform.replace(
 *            replacing = file,
 *            newDeclaration = {
 *              """$packageDirective$importList$classes""".file(name)
 *            }
 *          )
 *        }
 *      )
 *    }
 * ```
 */
class File(
  override val value: KtFile,
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries),
  val name: String = value.name,
  val importList: Scope<KtImportList> = Scope(value.importList), // TODO KtImportList scope and quote template
  val fileAnnotationList: Scope<KtFileAnnotationList>? = Scope(value.fileAnnotationList), // TODO KtFileAnnotationList scope and quote template
  val importDirectives: ScopedList<KtImportDirective> = ScopedList(value = value.importDirectives, postfix = ", "),
  val packageDirective: PackageDirective = PackageDirective(value.packageDirective),
  val packageFqName: FqName = value.packageFqName,
  val packageFqNameByTree: FqName = value.packageFqNameByTree,
  val script: Scope<KtScript>? = Scope(value.script), // TODO KtScript scope and quote template
  val danglingAnnotations: ScopedList<KtAnnotationEntry> = ScopedList(value = value.danglingAnnotations, postfix = ", "),
  val fileType: FileType = value.fileType,
  val declarations: ScopedList<KtDeclaration> = ScopedList(value = value.declarations, postfix = ", "),
  val stub: KotlinFileStub? = value.stub,
  val classes: Array<PsiClass> = value.classes,
  val sourcePath: FqName? = null
): Scope<KtFile>(value)
