package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.dsl.platform.cli
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.sequence
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.google.common.collect.ImmutableMap
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.util.slicedMap.ReadOnlySlice
import java.io.File
import java.util.*

/**
 * ### Typed Quote Templates DSL
 *
 * Arrow Meta offers a high level DSL for compiler tree transformations with a deep type information.
 * This DSL enables a compiler plugin to rewrite the user source code before is considered for compilation.
 * These code transformations take the form of tree transformations.
 *
 * The Quote DSL accepts a filter function that will intercept any node during tree traversal based on a Boolean predicate.
 * In the following example the Quote system matches all user declared `fun` named `"helloWorld"`.
 * ```kotlin
 * val Meta.helloWorld: CliPlugin get() =
 *   "Hello World" {
 *     meta(
 *       namedFunction(this, { name == "helloWorld" }) { c ->  // <-- namedFunction(...) {...}
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
 *       namedFunction(this, { name == "helloWorld" }) { c ->  // <-- namedFunction(...) {...}
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
interface TypedQuote<P : KtElement, K : KtElement, D : DeclarationDescriptor, S> : QuoteProcessor<TypedQuoteTemplate<K, D>, K, S> {

  val containingDeclaration: P

  interface Factory<P : KtElement, K : KtElement, D : DeclarationDescriptor, S> {
    operator fun invoke(
      containingDeclaration: P,
      match: TypedQuoteTemplate<K, D>.() -> Boolean,
      map: S.(quoteTemplate: TypedQuoteTemplate<K, D>) -> Transform<K>
    ): TypedQuote<P, K, D, S>
  }
}

data class TypedQuoteTemplate<out K : KtElement, out D : DeclarationDescriptor>(
  val element: K,
  val descriptor: D?
)

class TypedQuoteFactory<K : KtElement, D : DeclarationDescriptor, S : TypedScope<K, D>>(
  val transform: (TypedQuoteTemplate<K, D>) -> S
): TypedQuote.Factory<KtElement, K, D, S> {
  override fun invoke(
    containingDeclaration: KtElement,
    match: TypedQuoteTemplate<K, D>.() -> Boolean,
    map: S.(quoteTemplate: TypedQuoteTemplate<K, D>) -> Transform<K>
  ): TypedQuote<KtElement, K, D, S> =
    object : TypedQuote<KtElement, K, D, S> {
      override fun TypedQuoteTemplate<K, D>.match(): Boolean = match(this)
      override fun S.map(quoteTemplate: TypedQuoteTemplate<K, D>): Transform<K> = map(quoteTemplate)
      override val containingDeclaration: KtElement = containingDeclaration
      override fun transform(quoteTemplate: TypedQuoteTemplate<K, D>): S = this@TypedQuoteFactory.transform(quoteTemplate)
    }
}

inline fun <reified K : KtElement, reified D : DeclarationDescriptor, S : TypedScope<K, D>> Meta.typedQuote(
  ctx: CompilerContext,
  noinline match: TypedQuoteTemplate<K, D>.() -> Boolean,
  noinline map: S.(TypedQuoteTemplate<K, D>) -> Transform<K>,
  noinline mapDescriptor: List<DeclarationDescriptor>.(K) -> D?,
  noinline transform: (TypedQuoteTemplate<K, D>) -> S
): ExtensionPhase =
  typedQuote(ctx, TypedQuoteFactory(transform), match, map, mapDescriptor)

@Suppress("UNCHECKED_CAST")
inline fun <P : KtElement, reified K : KtElement, reified D : DeclarationDescriptor, S : TypedScope<K, D>> Meta.typedQuote(
  ctx: CompilerContext,
  quoteFactory: TypedQuote.Factory<P, K, D, S>,
  noinline match: TypedQuoteTemplate<K, D>.() -> Boolean,
  noinline map: S.(TypedQuoteTemplate<K, D>) -> Transform<K>,
  noinline mapDescriptor: List<DeclarationDescriptor>.(K) -> D?
): ExtensionPhase {
  return cli {
    ctx.analysisPhaseCanBeRewind.set(true)
    analysis(
      doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
        if (!ctx.analysisPhaseWasRewind.get()) return@analysis null
        files as ArrayList
        println("START quote.doAnalysis: $files")
        val fileMutations = processFiles(files, quoteFactory, match, map, mapDescriptor, analysedDescriptors)
        updateFiles(files, fileMutations, match.let { m -> { element: K -> m(TypedQuoteTemplate(element, analysedDescriptors.mapDescriptor(element))) } })
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
      analysisCompleted = { project, module, bindingTrace, files ->
        if (!analysisPhaseWasRewind.get()) {
          analysedDescriptors.addAll(BindingContext.DECLARATIONS_TO_DESCRIPTORS.flatMap {
            it.makeRawValueVersion().let<ReadOnlySlice<Any, Any>, ImmutableMap<Any, Any>>(bindingTrace.bindingContext::getSliceContents).values.map { it as DeclarationDescriptor }
          })
          analysisPhaseWasRewind.set(true)
          AnalysisResult.RetryWithAdditionalRoots(bindingTrace.bindingContext, module, additionalJavaRoots = listOf(), additionalKotlinRoots = listOf())
        } else null
      }
    )
  } ?: ExtensionPhase.Empty
}

@Suppress("UNCHECKED_CAST")
inline fun <reified K : KtElement, reified D : DeclarationDescriptor, P : KtElement, S : TypedScope<K, D>> processFiles(
  files: Collection<KtFile>,
  quoteFactory: TypedQuote.Factory<P, K, D, S>,
  noinline match: TypedQuoteTemplate<K, D>.() -> Boolean,
  noinline map: S.(TypedQuoteTemplate<K, D>) -> Transform<K>,
  noinline mapDescriptor: List<DeclarationDescriptor>.(K) -> D?,
  descriptors: List<DeclarationDescriptor>
): List<Pair<KtFile, List<Transform<K>>>> =
  files.map { file ->
    processKtFile(file, quoteFactory, match, map, mapDescriptor, descriptors)
  }

@Suppress("UNCHECKED_CAST")
inline fun <reified K : KtElement, reified D : DeclarationDescriptor, P : KtElement, S : TypedScope<K, D>> processKtFile(
  file: KtFile,
  quoteFactory: TypedQuote.Factory<P, K, D, S>,
  noinline match: TypedQuoteTemplate<K, D>.() -> Boolean,
  noinline map: S.(TypedQuoteTemplate<K, D>) -> Transform<K>,
  noinline mapDescriptor: List<DeclarationDescriptor>.(K) -> D?,
  descriptors: List<DeclarationDescriptor>
): Pair<KtFile, List<Transform<K>>> =
  processKtFile(file, K::class.java, quoteFactory, match, map, mapDescriptor, descriptors)

@Suppress("UNCHECKED_CAST")
fun <K : KtElement, D : DeclarationDescriptor, P : KtElement, S : TypedScope<K, D>> processKtFile(
  file: KtFile,
  on: Class<K>,
  quoteFactory: TypedQuote.Factory<P, K, D, S>,
  match: TypedQuoteTemplate<K, D>.() -> Boolean,
  map: S.(TypedQuoteTemplate<K, D>) -> Transform<K>,
  mapDescriptor: List<DeclarationDescriptor>.(K) -> D?,
  descriptors: List<DeclarationDescriptor>
): Pair<KtFile, List<Transform<K>>> =
  file to file.viewProvider.document?.run {
    file.sequence(on).mapNotNull { element: K ->
      quoteFactory(
        containingDeclaration = element.psiOrParent as P,
        match = match,
        map = map
      ).process(TypedQuoteTemplate(element, descriptors.mapDescriptor(element)))
    }
  }.orEmpty()