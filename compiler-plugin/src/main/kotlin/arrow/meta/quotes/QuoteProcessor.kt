package arrow.meta.quotes

import arrow.meta.dsl.platform.cli
import arrow.meta.dsl.platform.ide
import arrow.meta.internal.kastree.ast.MutableVisitor
import arrow.meta.internal.kastree.ast.Node
import arrow.meta.internal.kastree.ast.Writer
import arrow.meta.internal.kastree.ast.psi.Converter
import arrow.meta.internal.kastree.ast.psi.ast
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.MetaFileViewProvider
import arrow.meta.phases.analysis.traverseFilter
import arrow.meta.phases.evaluateDependsOn
import org.jetbrains.kotlin.com.intellij.openapi.vfs.local.CoreLocalFileSystem
import org.jetbrains.kotlin.com.intellij.openapi.vfs.local.CoreLocalVirtualFile
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpressionCodeFragment
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.nio.file.Paths
import java.util.ArrayList

/**
 * Quote processor defines how quotes should behave related with its processing phase
 */
interface QuoteProcessor<T, K, S> {

  /**
   * Returns a String representation of what a match for a tree may look like. For example:
   * ```
   * "fun <$typeArgs> $name($params): $returnType = $body"
   * ```
   */
  fun T.match(): Boolean

  /**
   * Given real matches of a [quoteTemplate] the user is then given a chance to replace them with new trees
   * where also uses code as a template
   */
  fun S.map(quoteTemplate: T): Transform<K>


  /**
   * Given a [quoteTemplate] value, provides a [S] value
   */
  fun transform(quoteTemplate: T): S

  fun process(quoteTemplate: T): Transform<K>? {
    return if (quoteTemplate.match()) {
      // a new scope is transformed
      val transformedScope = transform(quoteTemplate)
      // the user transforms the expression into a new list of declarations
      transformedScope.map(quoteTemplate)
    } else null
  }
}

inline fun <reified K : KtElement> CompilerContext.updateFiles(
  result: ArrayList<KtFile>,
  fileMutations: List<Pair<KtFile, List<Transform<K>>>>,
  noinline match: K.() -> Boolean
) {
  fileMutations.forEach { (file, mutations) ->
    val newFile = updateFile(mutations, file, match)
    result.replaceFiles(file, newFile)
  }
}

inline fun <reified K : KtElement> CompilerContext.updateFile(
  mutations: List<Transform<K>>,
  file: KtFile,
  noinline match: K.() -> Boolean
): List<KtFile> =
  if (mutations.isNotEmpty()) {
    transformFile(file, mutations, match)
  } else listOf(file)

inline fun <reified K : KtElement> CompilerContext.transformFile(
  ktFile: KtFile,
  mutations: List<Transform<K>>,
  noinline match: K.() -> Boolean
): List<KtFile> {
  val newSource: List<Pair<KtFile, Node.File>> = ktFile.sourceWithTransformationsAst(mutations, this, match).map {
    (it.first ?: ktFile) to it.second
  }
  val newFile = newSource.map { source -> changeSource(source.first, Writer.write(source.second), ktFile, sourcePath = source.second.path) }
  println("Transformed file: $ktFile. New contents: \n$newSource")
  return newFile
}

inline fun <reified K : KtElement> KtFile.sourceWithTransformationsAst(
  mutations: List<Transform<K>>,
  compilerContext: CompilerContext,
  noinline match: K.() -> Boolean
): List<Pair<KtFile?, Node.File>> {
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
          compilerContext.evaluateDependsOn(
            noRewindablePhase = { newSource.addAll(it.second) },
            rewindablePhase = { wasRewind -> if (wasRewind) newSource.addAll(it.second) }
          )
        }
      }
      is Transform.NewSource -> {
        transform.newSource().let {
          compilerContext.evaluateDependsOn(
            noRewindablePhase = { newSource.addAll(it) },
            rewindablePhase = { wasRewind -> if (wasRewind) newSource.addAll(it) }
          )
        }
      }
      Transform.Empty -> Unit
    }
  }
  return (newSource + dummyFile).map { it.first to it.second }
}

fun <K : KtElement> Transform.NewSource<K>.newSource(): List<Pair<KtFile, Node.File>> =
  files.map {
    it.value to
      if (it.value.text.contains(META_DEBUG_COMMENT))
        Converter
          .convertFile(it.value, it.sourcePath)
          .copy(commands = listOf(Node.Command(name = META_DEBUG_COMMENT)))
      else Converter.convertFile(it.value, it.sourcePath)
  }

inline fun <reified K : KtElement> Transform.Many<K>.many(ktFile: KtFile, compilerContext: CompilerContext, noinline match: K.() -> Boolean): Pair<Node.File, MutableList<Pair<KtFile, Node.File>>> {
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
  (context ?: scope.value?.context)?.let { context ->
    MutableVisitor.preVisit(context.ast) { element, _ ->
      if (element != null && element.psiElement?.text?.trim() == scope.value?.text?.trim()) element.also {
        it.psiElement?.let { psi -> psiElements.add(psi) }
      } else element
    }
  }
  psiElements
}

inline fun <reified K : KtElement> processContext(source: KtFile, noinline match: K.() -> Boolean): K? =
  source.traverseFilter(K::class.java) { it.takeIf(match) }.firstOrNull()

fun ArrayList<KtFile>.replaceFiles(file: KtFile, newFile: List<KtFile>) {
  when (val x = indexOf(file)) {
    -1 -> Unit
    else -> {
      removeAt(x)
      addAll(x, newFile)
    }
  }
}

fun CompilerContext.changeSource(file: KtFile, newSource: String, rootFile: KtFile, sourcePath: String? = null): KtFile {
  var virtualFile = rootFile.virtualFile
  if (file.name != DEFAULT_META_FILE_NAME) {
    val path = sourcePath ?: System.getProperty("arrow.meta.generated.source.output", DEFAULT_SOURCE_PATH)
    val directory = Paths.get("", *path.split("/").toTypedArray()).toFile()
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