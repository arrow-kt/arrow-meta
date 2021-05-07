package arrow.meta.quotes

import arrow.meta.ArrowMetaConfigurationKeys
import arrow.meta.Meta
import arrow.meta.dsl.platform.cli
import arrow.meta.dsl.platform.ide
import arrow.meta.internal.kastree.ast.MutableVisitor
import arrow.meta.internal.kastree.ast.Node
import arrow.meta.internal.kastree.ast.Writer
import arrow.meta.internal.kastree.ast.psi.Converter
import arrow.meta.internal.kastree.ast.psi.ast
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.DefaultElementScope.Companion.DEFAULT_BASE_DIR
import arrow.meta.phases.analysis.MetaFileViewProvider
import arrow.meta.phases.analysis.sequence
import arrow.meta.phases.analysis.traverseFilter
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.google.common.collect.ImmutableMap
import org.jetbrains.kotlin.com.intellij.openapi.vfs.local.CoreLocalFileSystem
import org.jetbrains.kotlin.com.intellij.openapi.vfs.local.CoreLocalVirtualFile
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.util.slicedMap.ReadOnlySlice
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Date

const val META_DEBUG_COMMENT = "//metadebug"

/**
 * ### Quote Templates DSL
 *
 * Arrow Meta offers a high level DSL for compiler tree transformations.
 * This DSL enables a compiler plugin to rewrite the user source code before is considered for compilation.
 * These code transformations take the form of tree transformations.
 *
 * The Quote DSL accepts a filter function that will intercept any node during tree traversal based on a Boolean predicate.
 * In the following example the Quote system matches all user declared `fun` named `"helloWorld"`.
 * ```kotlin
 * val Meta.helloWorld: CliPlugin get() =
 *   "Hello World" {
 *     meta(
 *       namedFunction(this, { element.name == "helloWorld" }) { (c, _) ->  // <-- namedFunction(...) {...}
 *         ...
 *       }
 *     )
 *   }
 * ```
 * Once we get a hold of the nodes that we want to intercept we can return the kind of [Transform] that we want to apply
 * over the tree.
 *
 * In the example below we are using [replace] to change the intercepted function for a new declaration that when invoked prints `"Hello ΛRROW Meta!"`
 *
 * ```kotlin
 * val Meta.helloWorld: CliPlugin get() =
 *   "Hello World" {
 *     meta(
 *       namedFunction(this, { element.name == "helloWorld" }) { (c, _) ->  // <-- namedFunction(...) {...}
 *         Transform.replace(
 *           replacing = c,
 *           newDeclaration = """|fun helloWorld(): Unit =
 *                               |  println("Hello ΛRROW Meta!")
 *                               |""".function.syntheticScope
 *         )
 *       }
 *     )
 *   }
 * ```
 *
 * The Quote system automatically takes care of the internal tree transformations necessary before feeding the new sources
 * to the compiler.
 *
 * The [Quote] system acts as an intermediate layer between PSI Elements and AST Node type checking.
 * More namely, A declaration quasi quote matches tree previous to the analysis and synthetic resolution and gives the
 * compiler plugin the chance to transform the source tree before they are processed by the Kotlin compiler.
 */

interface Quote<P : KtElement, K : KtElement, S> : QuoteProcessor<K, K, S> {

  val containingDeclaration: P

  interface Factory<P : KtElement, K : KtElement, S> {
    operator fun invoke(
      containingDeclaration: P,
      match: K.() -> Boolean,
      map: S.(quoteTemplate: K) -> Transform<K>
    ): Quote<P, K, S>
  }
}

class QuoteFactory<K : KtElement, S : Scope<K>>(
  val transform: (K) -> S
) : Quote.Factory<KtElement, K, S> {
  override operator fun invoke(
    containingDeclaration: KtElement,
    match: K.() -> Boolean,
    map: S.(quotedTemplate: K) -> Transform<K>
  ): Quote<KtElement, K, S> =
    object : Quote<KtElement, K, S> {
      override fun K.match(): Boolean = match(this)
      override fun S.map(quoteTemplate: K): Transform<K> = map(quoteTemplate)
      override val containingDeclaration: KtElement = containingDeclaration
      override fun transform(quoteTemplate: K): S = this@QuoteFactory.transform(quoteTemplate)
    }
}

inline fun <reified K : KtElement> Meta.quote(
  ctx: CompilerContext,
  noinline match: K.() -> Boolean,
  noinline map: Scope<K>.(K) -> Transform<K>
): ExtensionPhase =
  quote(ctx, match, map) { Scope(it) }

inline fun <reified K : KtElement, S : Scope<K>> Meta.quote(
  ctx: CompilerContext,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> Transform<K>,
  noinline transform: (K) -> S
): ExtensionPhase =
  quote(ctx, QuoteFactory(transform), match, map)

data class QuoteDefinition<P : KtElement, K : KtElement, S : Scope<K>>(
  val on: Class<K>,
  val quoteFactory: Quote.Factory<P, K, S>,
  val match: K.() -> Boolean,
  val map: S.(K) -> Transform<K>
)

@Suppress("UNCHECKED_CAST")
inline fun <P : KtElement, reified K : KtElement, S : Scope<K>> Meta.quote(
  ctx: CompilerContext,
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> Transform<K>
): ExtensionPhase =
  cli {
    analysis(
      doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
        files as ArrayList
        println("START quote.doAnalysis: $files")
        val fileMutations = processFiles(files, quoteFactory, match, map)
        updateFiles(files, fileMutations, match)
        println("END quote.doAnalysis: $files")
        files.forEach {
          val fileText = it.text
          if (fileText.contains(META_DEBUG_COMMENT)) {
            File(it.virtualFilePath + ".meta").writeText(it.text.replaceFirst(META_DEBUG_COMMENT, "//meta: ${Date()}"))
            println("""|
              |ktFile: $it
              |----
              |${it.text}
              |----
              """.trimMargin())
          }
        }
        null
      },
      analysisCompleted = { _, module, bindingTrace, _ ->
        null
      }
    )
  }.apply {
    ctx.quotes.add(QuoteDefinition(K::class.java, quoteFactory, match, map))
  } ?: ExtensionPhase.Empty

fun PackageViewDescriptor.declarations(): Collection<DeclarationDescriptor> =
  memberScope.getContributedDescriptors { true }

fun DeclarationDescriptor.ktFile(): KtFile? =
  findPsi()?.containingFile.safeAs()

fun ClassDescriptor.ktClassOrObject(): KtClassOrObject? =
  findPsi() as? KtClassOrObject

fun KtClassOrObject.nestedClassNames(): List<String> =
  declarations.filterIsInstance<KtClassOrObject>().mapNotNull { it.name }

fun KtFile.ktClassNamed(name: String?): KtClass? =
  name?.let {
    findDescendantOfType { d -> d.name == it }
  }

fun KtClassOrObject.functionNames(): List<Name> =
  declarations.filterIsInstance<KtFunction>().mapNotNull { it.name }.map(Name::identifier)

@Suppress("UNCHECKED_CAST")
inline fun <reified K : KtElement, P : KtElement, S : Scope<K>> processFiles(
  files: Collection<KtFile>,
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> Transform<K>
): List<Pair<KtFile, List<Transform<K>>>> =
  files.map { file ->
    processKtFile(file, quoteFactory, match, map)
  }

@Suppress("UNCHECKED_CAST")
inline fun <reified K : KtElement, P : KtElement, S : Scope<K>> processKtFile(
  file: KtFile,
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> Transform<K>
): Pair<KtFile, List<Transform<K>>> =
  processKtFile(file, K::class.java, quoteFactory, match, map)

@Suppress("UNCHECKED_CAST")
fun <K : KtElement, P : KtElement, S : Scope<K>> processKtFile(
  file: KtFile,
  on: Class<K>,
  quoteFactory: Quote.Factory<P, K, S>,
  match: K.() -> Boolean,
  map: S.(K) -> Transform<K>
): Pair<KtFile, List<Transform<K>>> =
  file to file.viewProvider.document?.run {
    file.sequence(on).mapNotNull { element: K ->
      quoteFactory(
        containingDeclaration = element.psiOrParent as P,
        match = match,
        map = map
      ).process(element)
    }
  }.orEmpty()

@Suppress("UNCHECKED_CAST")
inline operator fun <reified A, B> A.get(field: String): B {
  val clazz = A::class.java
  return try {
    clazz.getDeclaredField(field).also { it.isAccessible = true }.get(this) as B
  } catch (e: Exception) {
    clazz.getField(field).also { it.isAccessible = true }.get(this) as B
  }
}

fun KtFile.isMetaFile(): Boolean =
  name.startsWith("_meta_")
