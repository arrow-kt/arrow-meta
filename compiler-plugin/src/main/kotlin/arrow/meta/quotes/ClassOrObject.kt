package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * [classOrObject] is a function that intercepts all [KtClass] elements that [match]
 * allowing a [Transform] to change the intercepted AST tree before compilation.
 *
 * An extension function of [Meta] and inheriting from [ExtensionPhase], [classOrObject] was designed to to feed in
 * virtually any kind of [KtClass] predicate, followed by mapping a function that takes the desired [Scope] of our
 * [KtClass] to change whatever PSI elements desired.
 *
 * For example, the [LensPlugin] and the [HigherKindPlugin] favors enabling ad-hoc polymorphism and more for the
 *
 * In the [LensPlugin], we check to see if a type is a product type.  To add context, the cardinality of a product
 * type is the product of its contained properties:
 *
 * ```
 *      |(a, b)| = |a| x |b|
 * ```
 *
 * In particular, the [LensPlugin] is passing a function to whatever class or object type is a product type:
 *
 * ```
 *  val Meta.lenses: Plugin
 *    get() =
 *      "lenses" {
 *        meta {
 *          classOrObject(::isProductOrType) { c -> ... }
 *        }
 *      }
 * ```
 *
 * Where the function type for [::isProductType] is written as:
 *
 * ```
 * fun isProductType(ktClass: KtClass): Boolean =
 *      ktClass.isData() &&
 *        ktClass.primaryConstructorParameters.isNotEmpty() &&
 *        ktClass.primaryConstructorParameters.all { !it.isMutable } &&
 *        ktClass.typeParameters.isEmpty()
 * ```
 *
 * For a [KtClass] to be a product type, we check to see if the [KtClass] is an ADT, and that the cardinality of
 * the constructor parameters is greater than one and non-mutable. For the [LensPlugin], we've just demonstrated how we
 * can check for the product type of a [KtClass] by accessing the properties made available by the Kotlin PSI tree.
 *
 * While it is not necessary to build [KtClass] predicates for [Meta.classOrObject] does not necessarily have to serve
 * as a check for functional proofs in your compiler plugin.  It is relevant to highlight that the `match` predicate
 * written gives access to the Kotlin PSI when analyzing written code via the written compiler plugin.
 *
 * The second parameter `map` is a function that allows the resulting action from matching on the transformation at
 * the PSI level. The following example plugin illustrates how a Class is intercepted and transformed given `name == "Test" filter. Once matched it's then transformed by [Transform.replace], a transformation that will replace the intercepted class by a new user-declared synthetic replacement. In this example we can observe how the [ClassScope] is available to destructure the class template in full and allows us to reconstruct it back into a [KtClass] by using the [ElementScope.`class`] function.
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.classOrObject
 * import org.jetbrains.kotlin.psi.KtClass
 * import com.intellij.psi.PsiElement
 *
 * val Meta.example: Plugin
 *   get() =
 *     "Example" {
 *       meta(
 *         /** Intercepts all classes named 'Test' **/
 *         classOrObject({ name == "Test" }) { classOrObject ->
 *           Transform.replace<KtClass>(
 *             replacing = classOrObject,
 *             newDeclaration =
 *               """|$`@annotationEntries` $kind $name $`(typeParameters)` $`(valueParameters)` : $supertypes"} {
 *                  |  $body
 *                  |  fun void test(): Unit =
 *                  |    println("Implemented by ΛRROW Meta!")
 *                  |}
 *                  |""".`class`.synthetic
 *           )
 *         }
 *       )
 *     }
 * ```
 *
 * After analyzing the PSI elements available, we pass a resulting [KtClass] matching the predicate (in our case,
 * we pass the resulting [KtClass] whose name is "Test") and replace the entire object with the string block, which is
 * then wrapped as a [ClassScope] and wrapped so the `newDeclaration` is of the type [Scope]<[ClassScope]> to match
 * match compatibility of the intercepted classes wrapped in some kind of [Scope]. To see more, see
 * [ClassScope].
 *
 * @param match designed to to feed in any kind of [KtClass] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.classOrObject(
  match: KtClass.() -> Boolean,
  map: ClassScope.(KtClass) -> Transform<KtClass>
): ExtensionPhase =
  quote(match, map) { ClassScope(it) }

/**
 * [classOrObject] is a function that intercepts all [KtClass] elements that [match]
 * allowing a [Transform] to change the intercepted AST tree before compilation.
 *
 * The [ClassScope] is projected over the template to allow destructuring of the different parts of the
 * [KtClass]. The scope enables template syntax where the user may add new members or modify the class structure
 * before it's compiled
 *
 * After analyzing the PSI elements available, we pass a resulting [KtClass] matching the predicate (in our case,
 * we pass the resulting [KtClass] whose name is "Test") and replace the entire object with the string block, which is
 * then wrapped as a [ClassScope] and wrapped so the `newDeclaration` is of the type [Scope]<[ClassScope]> to match
 * match compatibility of the intercepted classes wrapped in some kind of [Scope]. Too see more, scroll down to
 * [ClassScope].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.classOrObject
 *
 * val Meta.example: Plugin
 *   get() =
 *     "Example" {
 *       meta(
 *         /** Intercepts all classes named 'Test' **/
 *         classOrObject({ name == "Test" }) { classOrObject ->
 *           Transform.replace(
 *             replacing = classOrObject,
 *             newDeclaration =
 *               """|$`@annotationEntries` $kind $name $`(typeParameters)` $`(valueParameters)` : $supertypes"} {
 *                  |  $body
 *                  |  fun void test(): Unit =
 *                  |    println("Implemented by ΛRROW Meta!")
 *                  |}
 *                  |""".`class`.synthetic
 *           )
 *         }
 *       )
 *     }
 * ```
 *
 * @param value the PSI element being fed in for the compiler plugin.
 * @param annotationEntries searches for marked annotations associated with the class.
 * @param modality  Modifier keyword is a keyword that can be used in annotation position as part of modifier list
 * @param visibility is the class public, private, protected? etc.
 * @oaram kind denotes certain classes as sealed class types or data class types.
 */
class ClassScope(
  override val value: KtClass,
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries),
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val kind: Name? =
    (when {
      value.isSealed() -> "sealed "
      value.isData() -> "data "
      else -> "/* empty? */"
    } + value.getClassOrInterfaceKeyword()?.text).let(Name::identifier),
  val name: Name? = value.nameAsName,
  val `(typeParameters)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value.typeParameters, postfix = ">"),
  val `(params)`: ScopedList<KtParameter> = ScopedList(prefix = "public constructor (", value = value.getValueParameters(), postfix = ")"),
  val supertypes: ScopedList<KtSuperTypeListEntry> = ScopedList(value.superTypeListEntries),
  val body: ClassBodyScope = ClassBodyScope(value.body)
) : Scope<KtClass>(value)

data class ClassBodyScope(val value: KtClassBody?) {
  override fun toString(): String =
    value?.text?.drop(1)?.dropLast(1) ?: ""
}
