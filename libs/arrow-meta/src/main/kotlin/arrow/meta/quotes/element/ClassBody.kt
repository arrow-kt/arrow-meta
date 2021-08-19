package arrow.meta.quotes.element

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty

/**
 * <code>"""{ $properties $companionObjects $functions }""".classBody</code>
 *
 * A template destructuring [Scope] for a [KtClassBody].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.classBody
 *
 * val Meta.classBody: CliPlugin
 *    get() =
 *      "Reformat Class Body" {
 *          meta(
 *              classBody(this, { true }) { element ->
 *                  Transform.replace(
 *                      replacing = element,
 *                      newDeclaration = """
 *                          | {
 *                          |  $properties
 *                          |
 *                          |  $companionObjects
 *                          |
 *                          |  $functions
 *                          | }""".classBody
 *                  )
 *                }
 *              )
 *            }
 * ```
 */
class ClassBody(
  override val value: KtClassBody?,
  val companionObjects: ScopedList<KtObjectDeclaration> = ScopedList(value?.allCompanionObjects ?: listOf(), separator = "\n"),
  val anonymousInitializers: ScopedList<KtAnonymousInitializer> = ScopedList(value?.anonymousInitializers ?: listOf(), separator = "\n"),
  val danglingAnnotations: ScopedList<KtAnnotationEntry> = ScopedList(value?.danglingAnnotations ?: listOf(), separator = "\n"),
  val enumEntries: ScopedList<KtEnumEntry> = ScopedList(value?.enumEntries ?: listOf(), separator = "\n"),
  val functions: ScopedList<KtNamedFunction> = ScopedList(value?.functions ?: listOf(), separator = "\n"),
  val properties: ScopedList<KtProperty> = ScopedList(value?.properties ?: listOf(), separator = "\n")
) : Scope<KtClassBody>(value) {

    override fun toString(): String =
      if (value != null) value.text?.drop(1)?.dropLast(1) ?: "" else ""

    // TODO split up into two different elements to be able to use the identity method
    override fun ElementScope.identity(): Scope<KtClassBody> =
      if (enumEntries.isEmpty()) {
          """
              | {
              |  $properties
              |
              |  $companionObjects
              |
              |  $functions
              | }
              | """.classBody
      } else {
          """
              | {
              |  $enumEntries
              |
              |  $functions
              | }
              | """.classBody
      }
}
