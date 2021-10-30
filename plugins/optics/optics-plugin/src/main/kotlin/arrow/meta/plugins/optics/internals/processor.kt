package arrow.meta.plugins.optics.internals

import arrow.meta.internal.sealedSubclasses
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.filebase.File
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtClass

fun CompilerContext.process(elements: List<ADT>): List<File> =
  elements.flatMap { ele ->
    ele
      .snippets()
      .groupBy(Snippet::fqName)
      .values
      .map {
        it.reduce { acc, snippet ->
          acc.copy(
            imports = acc.imports + snippet.imports,
            content = "${acc.content}\n${snippet.content}"
          )
        }
      }
      .map { snippet -> snippet.asFileText().file(snippet.name) }
  }

internal fun ADT.snippets(): List<Snippet> =
  targets.map {
    when (it) {
      is IsoTarget -> generateIsos(this, it)
      is PrismTarget -> generatePrisms(this, it)
      is LensTarget -> generateLenses(this, it)
      is OptionalTarget -> generateOptionals(this, it)
      is SealedClassDsl -> generatePrismDsl(this, it)
      is DataClassDsl -> generateOptionalDsl(this, it) + generateLensDsl(this, it)
    }
  }

internal fun CompilerContext.evalAnnotatedPrismElement(element: KtClass): List<Focus> =
  when (element.classType) {
    ClassType.SEALED_CLASS ->
      element.sealedSubclassFqNameList().map {
        Focus(it, it.substringAfterLast(".").decapitalize())
      }
    else -> {
      knownError(element.nameAsSafeName.asString().prismErrorMessage, element)
      emptyList()
    }
  }

internal fun KtClass.sealedSubclassFqNameList(): List<String> =
  sealedSubclasses().mapNotNull { it.fqName?.asString() }

internal fun CompilerContext.evalAnnotatedDataClass(
  element: KtClass,
  errorMessage: String
): List<Focus> =
  when (element.classType) {
    ClassType.DATA_CLASS ->
      element
        .getConstructorTypesNames()
        .zip(element.getConstructorParamNames(), Focus.Companion::invoke)
    else -> {
      knownError(errorMessage, element)
      emptyList()
    }
  }

internal fun CompilerContext.evalAnnotatedDslElement(element: KtClass): Target =
  when (element.classType) {
    ClassType.DATA_CLASS ->
      DataClassDsl(
        element
          .getConstructorTypesNames()
          .zip(element.getConstructorParamNames(), Focus.Companion::invoke)
      )
    ClassType.SEALED_CLASS -> SealedClassDsl(evalAnnotatedPrismElement(element))
  }

internal fun CompilerContext.evalAnnotatedIsoElement(element: KtClass): List<Focus> =
  when (element.classType) {
    ClassType.DATA_CLASS ->
      element
        .getConstructorTypesNames()
        .zip(element.getConstructorParamNames(), Focus.Companion::invoke)
        .takeIf { it.size <= 22 }
        ?: run {
          knownError(element.nameAsSafeName.asString().isoTooBigErrorMessage, element)
          emptyList()
        }
    else -> {
      knownError(element.nameAsSafeName.asString().isoErrorMessage, element)
      emptyList()
    }
  }

internal fun KtClass.getConstructorTypesNames(): List<String> =
  primaryConstructor?.valueParameters?.mapNotNull { it.typeReference?.text }.orEmpty()

internal fun KtClass.getConstructorParamNames(): List<String> =
  primaryConstructor?.valueParameters?.mapNotNull { it.name }.orEmpty()

private enum class ClassType {
  DATA_CLASS,
  SEALED_CLASS
}

private val KtClass.classType: ClassType
  get() =
    when {
      isData() -> ClassType.DATA_CLASS
      isSealed() -> ClassType.SEALED_CLASS
      else -> TODO("Impossible case reached on $this")
    }

internal fun CompilerContext.knownError(message: String, element: KtAnnotated? = null): Unit =
  ctx.messageCollector?.report(CompilerMessageSeverity.ERROR, message, null) ?: Unit

internal fun KtClass.targets(): List<OpticsTarget> =
  if (isSealed()) listOf(OpticsTarget.PRISM, OpticsTarget.DSL)
  else listOf(OpticsTarget.ISO, OpticsTarget.LENS, OpticsTarget.OPTIONAL, OpticsTarget.DSL)
