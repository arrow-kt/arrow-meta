package arrow.meta.quotes.classorobject

import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * <code>```"""|$`@annotations` $kind $name $`(typeParameters)` $`(params)` : $supertypes"} { $body } """.`class````</code>
 *
 * The [ClassDeclaration] is projected over the template to allow destructuring of the different parts of the
 * [KtClass]. The scope enables template syntax where the user may add new members or modify the class structure
 * before it's compiled.
 *
 *  * [`class`] is a function that intercepts all [KtClass] elements that [match]
 * then uses a [Transform] to change the intercepted AST tree before compilation.
 *
 * An extension function of [Meta] and inheriting from [ExtensionPhase], [classDeclaration] was designed to feed in
 * virtually any kind of [KtClass] predicate, followed by a mapping function that takes the desired [Scope] of our
 * [KtClass] to change whatever PSI elements desired.
 *
 * For example, the [LensPlugin] and the [HigherKindPlugin] favor easy update of immutable data structures and ad-hoc polymorphism that does not require inheritance.
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
 *  val Meta.lenses: CliPlugin
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
 * For a [KtClass] to be a product type, we check to see if the [KtClass] is an ADT, and that the arity of
 * the constructor parameters is greater than one and non-mutable. For the [LensPlugin], we've just demonstrated how we
 * can check for the product type of a [KtClass] by accessing the properties made available by the Kotlin PSI tree.
 *
 * In the example provided, we favor and talk about writing compiler plugins for functional stuff,
 * which is great - We'd want to emphasize that folks really don't need to worry about writing predicates
 * (the [match] parameter) that are functional proofs. They can be for any boolean predicate!
 *
 * The second parameter [map] is a function that allows the resulting action from matching on the transformation at
 * the PSI level. The following example plugin illustrates how a Class is intercepted and transformed given `name == "Test"` filter. Once matched it's then transformed by [Transform.replace], a transformation that will replace the intercepted class by a new user-declared synthetic replacement. In this example we can observe how the [ClassDeclaration] is available to destructure the class template in full and allows us to reconstruct it back into a [KtClass] by using the [ElementScope.`class`] function.
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.classDeclaration
 *
 * val Meta.example: CliPlugin
 *   get() =
 *     "Example" {
 *       meta(
 *         /** Intercepts all classes named 'Test' **/
 *         classDeclaration(this, { name == "Test" }) { classElement ->
 *           Transform.replace(
 *             replacing = classElement,
 *             newDeclaration =
 *               """|$`@annotations` $kind $name $`(typeParameters)` $`(params)` : $supertypes"} {
 *                  |  $body
 *                  |  fun void test(): Unit =
 *                  |    println("Implemented by ΛRROW Meta!")
 *                  |}
 *                  |""".`class`.syntheticScope
 *           )
 *         }
 *       )
 *     }
 * ```
 *
 * After analyzing the PSI elements available, we pass a resulting [KtClass] matching the predicate (in our case,
 * we pass the resulting [KtClass] whose name is "Test") and replace the entire object with the string block, which is
 * then wrapped as a [ClassDeclaration] and wrapped so the `newDeclaration` is of the type [Scope]<[ClassDeclaration]> to match
 * match compatibility of the intercepted classes wrapped in some kind of [Scope]. To see more, see
 * [ClassDeclaration].
 *
 * @param value scoped [KtClass] being destructured in the template
 * @param visibility is the class public, private, protected? etc.
 * @param kind denotes certain classes as sealed class types or data class types.
 */
class ClassDeclaration(
  override val value: KtClass,
  override val descriptor: ClassDescriptor?,
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val kind: Name? =
    (when {
      value.isSealed() -> "sealed "
      value.isData() -> "data "
      else -> "/* empty? */"
    } + value.getClassOrInterfaceKeyword()?.text).let(Name::identifier),
  val `(typeParameters)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value.typeParameters, postfix = ">"),
  val `(params)`: ScopedList<KtParameter> = ScopedList(prefix = "public constructor (", value = value.getValueParameters(), postfix = ")"),
  val supertypes: ScopedList<KtSuperTypeListEntry> = ScopedList(value.superTypeListEntries)
) : ClassOrObjectScope<KtClass>(value, descriptor)
