package arrow.meta.phases.analysis

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.getReturnTypeFromFunctionType
import org.jetbrains.kotlin.builtins.isBuiltinFunctionalType
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.SyntaxTraverser
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.astReplace
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun KtDeclarationWithBody.body(): KtExpression? = bodyExpression ?: bodyBlockExpression

fun KtExpression.bodySourceAsExpression(): String? =
  when (this) {
    is KtBlockExpression ->
      statements
        .map {
          when (it) {
            is KtReturnExpression -> it.returnedExpression?.text
            else -> text
          }
        }
        .joinToString("\n")
        .drop(1)
        .dropLast(1)
    else -> text
  }

fun KtElement.transform(f: (KtElement) -> KtElement?): KtElement {
  accept(
    object : KtTreeVisitorVoid() {
      override fun visitKtElement(element: KtElement) {
        val result = f(element)
        if (result != null) {
          element.astReplace(result)
        }
        super.visitKtElement(element)
      }
    }
  )
  return this
}

fun KtElement.dfs(f: (KtElement) -> Boolean): List<KtElement> {
  val found = arrayListOf<KtElement>()
  accept(
    object : KtTreeVisitorVoid() {
      override fun visitKtElement(element: KtElement) {
        val result = f(element)
        if (result) found.add(element)
        super.visitKtElement(element)
      }
    }
  )
  return found
}

/**
 * collects all distinct witnesses of [f] from the receiver, where the return type contains pairs of
 * [A] and a list of other corresponding elements that full fill f
 */
fun <A> List<A>.exists(f: (A, A) -> Boolean): List<Pair<A, List<A>>> =
  fold(emptyList()) { acc: List<Pair<A, List<A>>>, a: A ->
    acc + (a to filter { b: A -> if (a != b) f(a, b) else false })
  }

/**
 * traverse and filters starting from the root node [receiver] down to all it's children and
 * applying [f]
 */
fun <A : PsiElement, B : Any> PsiElement.traverseFilter(on: Class<A>, f: (A) -> B?): List<B> =
  SyntaxTraverser.psiTraverser(this).filter(on).mapNotNull(f).toList()

/**
 * a convenient function that collects all child nodes [A] starting from [receiver] it applies
 * [traverseFilter] with the identity function
 */
fun <A : PsiElement> PsiElement.sequence(on: Class<A>): List<A> = traverseFilter(on) { it }

interface Eq<A> { // from arrow
  fun A.eqv(other: A): Boolean
  fun A.neqv(other: A): Boolean = !eqv(other)

  companion object {
    inline operator fun <A> invoke(crossinline feqv: (A, A) -> Boolean): Eq<A> =
      object : Eq<A> {
        override fun A.eqv(other: A): Boolean = feqv(this, other)
      }
  }
}

/** defines Equality on the type constructor */
fun typeConstructorEq(): Eq<KotlinType> = Eq { t1, t2 -> t1.constructor == t2.constructor }

/** defines Equality on types, where FunctionTypes are reduced to their return type */
fun resolveFunctionTypeEq(): Eq<KotlinType> = Eq { t1, t2 ->
  resolveFunctionType(t1) == resolveFunctionType(t2)
}

/**
 * Given [eq] this function returns [KotlinType]s that [intersect] with the returnType from the list
 * in [types]. One concrete example for equality on [TypeConstructor] may look like this:
 * ```kotlin:ank
 * import org.jetbrains.kotlin.descriptors.CallableDescriptor
 * import org.jetbrains.kotlin.builtins.KotlinBuiltIns
 * import org.jetbrains.kotlin.types.KotlinType
 * import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
 * import arrow.meta.phases.analysis.Eq
 * import arrow.meta.phases.analysis.intersect
 * //sampleStart
 *
 * fun typeConstructorEq(): Eq<KotlinType> =
 *  Eq { t1, t2 ->
 *   t1.constructor == t2.constructor
 *  }
 *
 * /**
 * * this function yields true if the type constructor - short [TC] - of the returnType in F is equal to one TC in [types]
 * */
 * fun <F : CallableDescriptor> F.returns( //
 *   types: KotlinBuiltIns.() -> List<KotlinType>
 * ): Boolean =
 *   intersect(typeConstructorEq(), types).isNotEmpty()
 * //sampleEnd
 * ```
 * @see [org.jetbrains.kotlin.types.TypeUtils] for more abstractions
 * @param eq can be define for e.g.: [TypeConstructor], [MemberScope] or typeArguments List<
 * [TypeProjection]>, etc.
 * @see functionTypeEq
 */
fun <C : CallableDescriptor> C.intersect(
  eq: Eq<KotlinType>,
  types: KotlinBuiltIns.() -> List<KotlinType>
): List<KotlinType> =
  eq.run {
    returnType?.let { result: KotlinType -> builtIns.types().filter { it.eqv(result) } }
      ?: emptyList()
  }

/**
 * given [eq] this function returns a List of [KotlinType] that are contained by both [list] and
 * [other]
 * @param eq can be defined for [TypeConstructor], [MemberScope] or typeArguments List<
 * [TypeProjection]>, etc.
 * @see intersect
 */
fun <D : DeclarationDescriptor> D.intersect(
  eq: Eq<KotlinType>,
  list: List<KotlinType>,
  other: KotlinBuiltIns.() -> List<KotlinType>
): List<KotlinType> =
  eq.run {
    val set = list.toMutableList()
    set.retainAll { t1 -> builtIns.other().any { t2 -> t1.eqv(t2) } }
    set.toList()
  }

/** resolves FunctionType to it's returnType */
val resolveFunctionType: (KotlinType) -> KotlinType
  get() = { if (it.isBuiltinFunctionalType) it.getReturnTypeFromFunctionType() else it }

/** naive type equality where function types are reduced to their return type */
val returnTypeEq: Eq<KotlinType>
  get() = Eq { a, b -> resolveFunctionType(a) == resolveFunctionType(b) }

fun KtAnnotated.isAnnotatedWith(regex: Regex): Boolean =
  annotationEntries.any { it.text.matches(regex) }

val KtClass.companionObject: KtObjectDeclaration?
  get() =
    declarations
      .singleOrNull { it.safeAs<KtObjectDeclaration>()?.isCompanion() == true }
      .safeAs<KtObjectDeclaration>()
