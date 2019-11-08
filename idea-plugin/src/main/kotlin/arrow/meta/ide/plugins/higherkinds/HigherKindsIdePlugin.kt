package arrow.meta.ide.plugins.higherkinds


import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.dsl.utils.code
import arrow.meta.ide.dsl.utils.h1
import arrow.meta.ide.dsl.utils.kotlin
import arrow.meta.ide.invoke
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.plugins.higherkind.isHigherKindedType
import arrow.meta.plugins.typeclasses.hasExtensionDefaultValue
import arrow.meta.quotes.FuncScope
import arrow.meta.quotes.ScopedList
import org.celtric.kotlin.html.Node
import org.celtric.kotlin.html.a
import org.celtric.kotlin.html.h2
import org.celtric.kotlin.html.html
import org.celtric.kotlin.html.i
import org.celtric.kotlin.html.render
import org.celtric.kotlin.html.text
import org.jetbrains.kotlin.idea.imports.importableFqName
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.higherKindsIdePlugin: IdePlugin
  get() = "HigherKindsIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.HKT,
        transform = {
          it.safeAs<KtClass>()?.takeIf(::isHigherKindedType)?.identifyingElement
        },
        message = { identifier ->
          """
          |${identifier.text} is a Higher Kinded Type that may be used in polymorphic functions expressed over [Kind<F, A>] and with the type classes.
          |For more info visit [https://arrow-kt.io/docs/patterns/glossary/#type-constructors](https://arrow-kt.io/docs/patterns/glossary/#type-constructors)
          |""".trimMargin()
        }
      ),
      addLineMarkerProvider(
        icon = ArrowIcons.POLY,
        transform = { it.safeAs<KtNamedFunction>()?.takeIf(KtNamedFunction::isKindPolymorphic)?.takeIf(KtNamedFunction::hasExtensionDefaultValues) },
        composite = KtNamedFunction::class.java,
        message = { f: KtNamedFunction ->
          FuncScope(f).run {
            val target = ScopedList(receiver.value, transform = { it.text.escapeHTML() })
            val compose: List<Node> = h1(name) + kotlin(this@run) +
              code(name) + text(" is a polymorphic function that can be applied over $target.") +
              code(name) + text(" requires @extension proofs for ") + code(givenParameters()) + text("Valid ") +
              code("@extension") + text("locations:\n${validExtensionLocations()}") +
              a(href = "https://arrow-kt.io/docs/patterns/polymorphic_programs/") { "Learn more about type class and ad-hoc polymorphism" }
            html(compose).render()
          }
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
    val compose = h2("Type Class: $typeClassPackage") + i("Canonical") +
      kotlin("""
       package $typeClassPackage
       
       @extension fun ${typeClassName?.decapitalize()}(): $extensionType
       @extension val ${typeClassName?.decapitalize()}: $extensionType 
       @extension object ${typeClassName?.capitalize()}: $extensionType 
       @extension class ${typeClassName?.capitalize()}: $extensionType
       """.trimIndent()) +
      h2("Local Overrides: $localPackage") +
      i("Internal, Non-Exportable") +
      kotlin("""package ${value.containingKtFile.packageFqName}
       
       @extension internal fun ${typeClassName?.decapitalize()}(): $extensionType
       @extension internal val ${typeClassName?.decapitalize()}: $extensionType 
       @extension internal object ${typeClassName?.capitalize()}: $extensionType 
       @extension internal class ${typeClassName?.capitalize()}: $extensionType
      """.trimIndent())
    compose.render()
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
