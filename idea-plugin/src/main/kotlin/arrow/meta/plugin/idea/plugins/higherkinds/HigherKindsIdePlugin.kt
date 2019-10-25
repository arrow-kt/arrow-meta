package arrow.meta.plugin.idea.plugins.higherkinds

import arrow.meta.Plugin
import arrow.meta.dsl.ide.editor.lineMarker.LineMarker
import arrow.meta.invoke
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.resources.ArrowIcons
import arrow.meta.plugins.higherkind.isHigherKindedType
import arrow.meta.plugins.typeclasses.hasExtensionDefaultValue
import arrow.meta.quotes.FuncScope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.idea.imports.importableFqName
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.higherKindsIdePlugin: Plugin
  get() = "HigherKindsIdePlugin" {
    meta(
      lineMarker(
        matchOn = { it.safeAs<KtClass>()?.let(::isHigherKindedType) == true },
        marker = {
          LineMarker(
            icon = ArrowIcons.HKT,
            message = it.safeAs<KtClass>()?.let { classOrInterface ->
              """|${classOrInterface.name} is a Higher Kinded Type that may be used in polymorphic functions expressed over [Kind<F, A>] and with the type classes. 
                 |For more info visit [https://arrow-kt.io/docs/patterns/glossary/#type-constructors](https://arrow-kt.io/docs/patterns/glossary/#type-constructors)
                 |""".trimMargin()
            } ?: ""
          )
        }
      ),
      lineMarker(
        matchOn = { it.safeAs<KtNamedFunction>()?.run { isKindPolymorphic() && hasExtensionDefaultValues() } == true },
        marker = { element ->
          LineMarker(
            icon = ArrowIcons.POLY,
            message = element.safeAs<KtNamedFunction>()?.let { f ->
              FuncScope(f).run {
                val target = ScopedList(receiver.value, transform = { it.text.escapeHTML()})
                """|<h1>$name</h1>
                   |<code lang="kotlin">
                   |  ${this@run} 
                   |</code>
                   |<code>$name</code> is a polymorphic function that can be applied over $target.
                   |<code>$name</code> requires @extension proofs for <code>${givenParameters()}</code>
                   |
                   |Valid <code>@extension</code> locations:
                   |${validExtensionLocations()}
                   |
                   |<a href="https://arrow-kt.io/docs/patterns/polymorphic_programs/">Learn more about type class and ad-hoc polymorphism</a>
                   |""".trimMargin()
              }
            } ?: ""
          )
        }
      )
    )
  }

private fun FuncScope.givenParameters(): ScopedList<KtParameter> =
  ScopedList(`(valueParameters)`.value.filter { it.hasExtensionDefaultValue() }, transform = {
    it.text.escapeHTML()
  })

private fun FuncScope.validExtensionLocations(): String =
  givenParameters().value.joinToString("\n") {
    val type = it.type()
    val extensionType = type.toString().escapeHTML()
    val typeClassDescriptor = type?.constructor?.declarationDescriptor
    val typeClassName = typeClassDescriptor?.name?.asString()
    val typeClassPackage = typeClassDescriptor?.importableFqName?.pathSegments()?.dropLast(1)?.joinToString(".")
    val localPackage = value.containingKtFile.packageFqName
    """|<h2>Type Class: $typeClassPackage</h2>
       |<i>Canonical</i>
       |<code lang="kotlin">
       |package $typeClassPackage
       |
       |@extension fun ${typeClassName?.decapitalize()}(): $extensionType
       |@extension val ${typeClassName?.decapitalize()}: $extensionType 
       |@extension object ${typeClassName?.capitalize()}: $extensionType 
       |@extension class ${typeClassName?.capitalize()}: $extensionType
       |</code>
       |
       |<h2>Local Overrides: $localPackage</h2>
       |<i>Internal, Non-Exportable</i>
       |<code lang="kotlin">
       |package ${value.containingKtFile.packageFqName}
       |
       |@extension internal fun ${typeClassName?.decapitalize()}(): $extensionType
       |@extension internal val ${typeClassName?.decapitalize()}: $extensionType 
       |@extension internal object ${typeClassName?.capitalize()}: $extensionType 
       |@extension internal class ${typeClassName?.capitalize()}: $extensionType
       |</code>
       |""".trimMargin()
  }

private fun String.escapeHTML(): String {
  val text = this@escapeHTML
  if (text.isEmpty()) return text
  return buildString(length) {
    for (element in text) {
      when (element) {
        '\'' -> append("&#x27;")
        '\"' -> append("&quot;")
        '&' -> append("&amp;")
        '<' -> append("&lt;")
        '>' -> append("&gt;")
        else -> append(element)
      }
    }
  }
}

fun KtNamedFunction.isKindPolymorphic(): Boolean =
  (listOfNotNull(receiverTypeReference, typeReference) +
    valueParameters.mapNotNull { it.typeReference }).any {
    it.text.matches("Kind<(.*)>".toRegex())
  }

fun KtNamedFunction.hasExtensionDefaultValues(): Boolean =
  valueParameters.any { it.hasExtensionDefaultValue() }
