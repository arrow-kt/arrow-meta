package arrow.meta.quotes

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
import arrow.meta.phases.analysis.MetaFileViewProvider
import arrow.meta.phases.analysis.dfs
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
import org.jetbrains.kotlin.psi.KtExpressionCodeFragment
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.io.File
import java.nio.file.Paths
import java.util.*

const val META_DEBUG_COMMENT = "//metadebug"
const val DEFAULT_META_FILE_NAME = "Source.kt"

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
 * val Meta.helloWorld: Plugin get() =
 *   "Hello World" {
 *     meta(
 *       namedFunction({ name == "helloWorld" }) { c ->  // <-- namedFunction(...) {...}
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
 * val Meta.helloWorld: Plugin get() =
 *   "Hello World" {
 *     meta(
 *       namedFunction({ name == "helloWorld" }) { c ->  // <-- namedFunction(...) {...}
 *         Transform.replace(
 *           replacing = c,
 *           newDeclaration = """|fun helloWorld(): Unit =
 *                               |  println("Hello ΛRROW Meta!")
 *                               |""".function.synthetic
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
interface Quote<P : KtElement, K : KtElement, S> {

  val containingDeclaration: P

  /**
   * Returns a String representation of what a match for a tree may look like. For example:
   * ```
   * "fun <$typeArgs> $name($params): $returnType = $body"
   * ```
   */
  fun K.match(): Boolean

  /**
   * Given real matches of a [quotedTemplate] the user is then given a chance to replace them with new trees
   * where also uses code as a template
   */
  fun S.map(quotedTemplate: K): Transform<K>

  interface Factory<P : KtElement, K : KtElement, S> {
    operator fun invoke(
      containingDeclaration: P,
      match: K.() -> Boolean,
      map: S.(quotedTemplate: K) -> Transform<K>
    ): Quote<P, K, S>
  }

  fun transform(ktElement: K): S

  fun process(ktElement: K): Transform<K>? {
    return if (ktElement.match()) {
      // a new scope is transformed
      val transformedScope = transform(ktElement)
      // the user transforms the expression into a new list of declarations
      transformedScope.map(ktElement)
    } else null
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
      override fun S.map(quotedTemplate: K): Transform<K> = map(quotedTemplate)
      override val containingDeclaration: KtElement = containingDeclaration
      override fun transform(ktElement: K): S = this@QuoteFactory.transform(ktElement)
    }
}

inline fun <reified K : KtElement> Meta.quote(
  noinline match: K.() -> Boolean,
  noinline map: Scope<K>.(K) -> Transform<K>
): ExtensionPhase =
  quote(match, map) { Scope(it) }

inline fun <reified K : KtElement, S : Scope<K>> Meta.quote(
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> Transform<K>,
  noinline transform: (K) -> S
): ExtensionPhase =
  quote(QuoteFactory(transform), match, map)

@Suppress("UNCHECKED_CAST")
inline fun <P : KtElement, reified K : KtElement, S> Meta.quote(
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
      analysisCompleted = { project, module, bindingTrace, files ->
        null
      }
    )
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
inline fun <reified K : KtElement, P : KtElement, S> processFiles(
  files: Collection<KtFile>,
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> Transform<K>
): List<Pair<KtFile, ArrayList<Transform<K>>>> {
  return files.map { file ->
    processKtFile(file, quoteFactory, match, map)
  }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified K : KtElement, P : KtElement, S> processKtFile(
  file: KtFile,
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> Transform<K>
): Pair<KtFile, ArrayList<Transform<K>>> {
  val mutatingDocument = file.viewProvider.document
  val mutations = arrayListOf<Transform<K>>()
  if (mutatingDocument != null) {
    val matches: List<KtElement> = file.dfs { element ->
      val result = K::class.java.isAssignableFrom(element.javaClass)
      result
    }
    matches.forEach { element ->
      val transformation = quoteFactory(
        containingDeclaration = element.psiOrParent as P,
        match = match,
        map = map
      ).process(element as K)
      transformation?.let { mutations.add(it) }
    }
  }
  return file to mutations
}

inline fun <reified K : KtElement> CompilerContext.updateFiles(
  result: java.util.ArrayList<KtFile>,
  fileMutations: List<Pair<KtFile, java.util.ArrayList<Transform<K>>>>,
  noinline match: K.() -> Boolean
) {
  fileMutations.forEach { (file, mutations) ->
    val newFile = updateFile(mutations, file, match)
    result.replaceFiles(file, newFile)
  }
}

inline fun <reified K : KtElement> CompilerContext.updateFile(
  mutations: java.util.ArrayList<Transform<K>>,
  file: KtFile,
  noinline match: K.() -> Boolean
): List<KtFile> =
  if (mutations.isNotEmpty()) {
    transformFile(file, mutations, match)
  } else listOf(file)

inline fun <reified K : KtElement> CompilerContext.transformFile(
  ktFile: KtFile,
  mutations: java.util.ArrayList<Transform<K>>,
  noinline match: K.() -> Boolean
): List<KtFile> {
  val newSource: List<Pair<KtFile, String>> = ktFile.sourceWithTransformationsAst(mutations, this, match).map { (it.first ?: ktFile) to it.second }
  val newFile = newSource.map { source -> changeSource(source.first, source.second, ktFile) }
  println("Transformed file: $ktFile. New contents: \n$newSource")
  return newFile
}

inline fun <reified K : KtElement> KtFile.sourceWithTransformationsAst(
  mutations: ArrayList<Transform<K>>,
  compilerContext: CompilerContext,
  match: K.() -> Boolean
): List<Pair<KtFile?, String>> {
  var dummyFile: Pair<KtFile?, Node.File> = null to Converter.convertFile(this)
  val newSource: MutableList<Pair<KtFile, Node.File>> = mutableListOf()
  val saveTransformation: (Node.File) -> Unit = { nodeFile -> dummyFile = null to nodeFile }
  mutations.forEach { transform ->
    when (transform) {
      is Transform.Replace -> saveTransformation(transform.replace(dummyFile.second))
      is Transform.Remove -> saveTransformation(transform.remove(dummyFile.second))
      is Transform.Many -> {
        transform.many(this, compilerContext, match).let {
          saveTransformation(it.first)
          newSource.addAll(it.second)
        }
      }
      is Transform.NewSource -> newSource.addAll(transform.newSource())
      Transform.Empty -> Unit
    }
  }
  return (newSource + dummyFile).map { it.first to Writer.write(it.second) }
}

fun <K : KtElement> Transform.NewSource<K>.newSource(): List<Pair<KtFile, Node.File>> =
  files.filter { it.value != null }.map { it.value!! to if (it.value.text.contains(META_DEBUG_COMMENT)) Converter.convertFile(it.value).copy(commands = listOf(Node.Command(name = META_DEBUG_COMMENT))) else Converter.convertFile(it.value) }

inline fun <reified K : KtElement> Transform.Many<K>.many(ktFile: KtFile, compilerContext: CompilerContext, match: K.() -> Boolean): Pair<Node.File, MutableList<Pair<KtFile, Node.File>>> {
  var dummyFile: KtFile = ktFile
  val newSource: MutableList<Pair<KtFile, Node.File>> = mutableListOf()
  var context: K? = null
  val changeSource: (Node.File) -> KtFile = { compilerContext.changeSource(dummyFile, Writer.write(it), ktFile) }
  transforms.forEach { transform ->
    context = processContext(dummyFile, match)
    when (transform) {
      is Transform.Replace -> dummyFile = changeSource(transform.replace(Converter.convertFile(dummyFile), context))
      is Transform.Remove -> dummyFile = changeSource(transform.remove(Converter.convertFile(dummyFile), context))
      is Transform.NewSource -> newSource.addAll(transform.newSource())
    }
  }
  return Converter.convertFile(dummyFile) to newSource
}

fun <K : KtElement> Transform.Replace<K>.replace(file: Node.File, context: PsiElement? = null): Node.File = MutableVisitor.preVisit(file) { element, _ ->
    if (element != null && element == (context?.ast ?: replacing.ast)) {
        val newContents = newDeclarations.joinToString("\n") { it.value?.text ?: "" }
        println("Replacing ${element.javaClass} with ${newDeclarations.map { it.value?.javaClass }}: newContents: \n$newContents")
        element.dynamic = newContents
        element
    } else element
}

fun <K : KtElement> Transform.Remove<K>.remove(file: Node.File, context: PsiElement? = null): Node.File {
    val elementsToRemove = declarations.elementsFromItsContexts(context)
    return MutableVisitor.preVisit(file) { element, _ ->
        if (element != null && elementsToRemove.any { it.textRange == element.psiElement?.textRange }) element.also {
            println("Removing ${element.javaClass}")
            it.dynamic = ""
        } else element
    }
}

private fun List<Scope<KtExpressionCodeFragment>>.elementsFromItsContexts(context: PsiElement? = null): List<PsiElement> = flatMap { scope ->
    val psiElements = mutableListOf<PsiElement>()
    (context ?: scope.value?.context)?.let { context -> MutableVisitor.preVisit(context.ast) { element, _ ->
        if (element != null && element.psiElement?.text?.trim() == scope.value?.text?.trim()) element.also {
            it.psiElement?.let { psi -> psiElements.add(psi) }
        } else element
    }}
    psiElements
}

inline fun <reified K : KtElement> processContext(source: KtFile, match: K.() -> Boolean): K? = source.dfs {
    K::class.java.isAssignableFrom(it.javaClass)
}.firstOrNull { (it as K).match() } as K

fun java.util.ArrayList<KtFile>.replaceFiles(file: KtFile, newFile: List<KtFile>) {
  val fileIndex = indexOf(file)
  removeAt(fileIndex)
  addAll(fileIndex, newFile)
}

fun CompilerContext.changeSource(file: KtFile, newSource: String, rootFile: KtFile): KtFile {
  var virtualFile = rootFile.virtualFile
  if (file.name != DEFAULT_META_FILE_NAME) {
      val directory = Paths.get("build", "generated", "source", "kapt", "main").toFile()
      directory.mkdirs()
      virtualFile = CoreLocalVirtualFile(CoreLocalFileSystem(), File(directory, file.name).apply {
          writeText(file.text)
      })
  }
  return cli {
    KtFile(
      viewProvider = MetaFileViewProvider(file.manager, virtualFile) {
        it?.also {
          it.setText(newSource)
        }
      },
      isCompiled = false
    )
  } ?: ide {
    ktPsiElementFactory.createAnalyzableFile("_meta_${file.name}", newSource, file)
  }!!
}

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
