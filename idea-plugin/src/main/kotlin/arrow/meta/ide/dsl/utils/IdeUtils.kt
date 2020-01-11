package arrow.meta.ide.dsl.utils

import arrow.meta.phases.analysis.Eq
import arrow.meta.phases.analysis.intersect
import com.intellij.psi.PsiElement
import com.intellij.psi.SyntaxTraverser
import org.celtric.kotlin.html.BlockElement
import org.celtric.kotlin.html.InlineElement
import org.celtric.kotlin.html.code
import org.celtric.kotlin.html.text
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

object IdeUtils {
  fun <A> isNotNull(a: A?): Boolean = a?.let { true } ?: false
}

/**
 * traverse and filters starting from the root node [receiver] down to all it's children and applying [f]
 */
fun <A : PsiElement, B> PsiElement.traverseFilter(on: Class<A>, f: (A) -> B): List<B> =
  SyntaxTraverser.psiTraverser(this).filter(on).map(f).toList()

/**
 * collects all Calls
 */
val KtElement.callElements: List<KtCallElement>
  get() = traverseFilter(KtCallElement::class.java) { it }

val KtCallElement.returnType: KotlinType?
  get() = resolveToCall()?.resultingDescriptor?.returnType

/**
 * returns all ReturnTypes of each call starting from the receiver
 */
val KtElement.callReturnTypes: List<KotlinType>
  get() = callElements.mapNotNull { it.returnType }

/**
 * this extension traverses and collects intersecting [KotlinType]s given [eq]
 * with the returnType of [F] and all it's calls in the function body.
 * [intersectFunction] implements a traversal of depth 1.
 * TODO: add returns with a traversal of depth n by virtue of recursion
 */
fun <F : CallableDescriptor> F.intersectFunction(
  eq: Eq<KotlinType>,
  ktFunction: KtNamedFunction,
  types: KotlinBuiltIns.() -> List<KotlinType>
): List<KotlinType> =
  intersect(eq, types) + intersect(eq, ktFunction.callReturnTypes, types)


/**
 * this extension traverses and collects intersecting [KotlinType]s given [eq]
 * with the returnType of [F] and all it's calls in the initializer of [prop].
 * [intersectProperty] implements a traversal of depth 1.
 * TODO: add returns with a traversal of depth n by virtue of recursion
 */
fun <F : CallableDescriptor> F.intersectProperty(
  eq: Eq<KotlinType>,
  prop: KtProperty,
  types: KotlinBuiltIns.() -> List<KotlinType>
): List<KotlinType> =
  intersect(eq, types) + intersect(eq, prop.callReturnTypes, types)

/**
 * reified PsiElement replacement
 */
inline fun <reified K : PsiElement> K.replace(f: (K) -> K): K? =
  replace(f(this)).safeAs()

fun <A> List<A?>.toNotNullable(): List<A> = fold(emptyList()) { acc: List<A>, r: A? -> if (r != null) acc + r else acc }

fun <A> kotlin(a: A): InlineElement = code(other = mapOf("lang" to "kotlin")) { "\t$a\n" }
fun kotlin(a: String): InlineElement = code(other = mapOf("lang" to "kotlin")) { "\t${text(a).content}\n" }
fun <A> h1(a: A): BlockElement = org.celtric.kotlin.html.h1("$a")
fun <A> code(a: A): InlineElement = code("\n\t$a\n")
