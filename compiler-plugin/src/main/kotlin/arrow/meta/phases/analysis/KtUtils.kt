package arrow.meta.phases.analysis

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.getReturnTypeFromFunctionType
import org.jetbrains.kotlin.builtins.isBuiltinFunctionalType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.astReplace
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.TypeProjection

fun KtFunction.body(): KtExpression? =
  bodyExpression ?: bodyBlockExpression

fun KtExpression.bodySourceAsExpression(): String? =
  when (this) {
    is KtBlockExpression -> statements.map {
      when (it) {
        is KtReturnExpression -> it.returnedExpression?.text
        else -> text
      }
    }.joinToString("\n").drop(1).dropLast(1)
    else -> text
  }

fun KtElement.transform(f: (KtElement) -> KtElement?): KtElement {
  accept(object : KtTreeVisitorVoid() {
    override fun visitKtElement(element: KtElement) {
      val result = f(element)
      if (result != null) {
        element.astReplace(result)
      }
      super.visitKtElement(element)
    }
  })
  return this
}

fun KtElement.dfs(f: (KtElement) -> Boolean): List<KtElement> {
  val found = arrayListOf<KtElement>()
  accept(object : KtTreeVisitorVoid() {
    override fun visitKtElement(element: KtElement) {
      val result = f(element)
      if (result) found.add(element)
      super.visitKtElement(element)
    }
  })
  return found
}

/**
 * true if [types] contains the returnType of receiver [C]
 * [f] defines on what property two Types are equal
 * e.g.: [TypeConstructor], [MemberScope] or typeArguments List<[TypeProjection]>, etc.
 * One concrete example for equality on [TypeConstructor] may look like this:
 * ```kotlin:ank
 * import org.jetbrains.kotlin.descriptors.CallableDescriptor
 * import org.jetbrains.kotlin.builtins.KotlinBuiltIns
 * import org.jetbrains.kotlin.types.KotlinType
 * import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
 *
 * fun <F : CallableDescriptor> F.returnsOnTypeConstructor(
 *   types: KotlinBuiltIns.() -> List<KotlinType>
 * ): Boolean =
 *   returnType?.let { result: KotlinType ->
 *    builtIns.types().map{ it.constructor }.contains(result.constructor)
 *   } ?: false
 * ```
 * More abstractions are here [org.jetbrains.kotlin.types.TypeUtils]
 */
@Suppress("UNCHECKED_CAST")
fun <C : CallableDescriptor, A> C.returns(
  f: (KotlinType) -> A = { it as A },
  types: KotlinBuiltIns.() -> List<KotlinType>
): Boolean =
  returnType?.let { result: KotlinType ->
    builtIns.types().map(f).contains(f(result))
  } ?: false

/**
 * true if any type of [list] is in [types]
 * [f] defines on what property two Types are equal
 * e.g.: [TypeConstructor], [MemberScope] or typeArguments List<[TypeProjection]>, etc...
 * @see [returns]
 */
@Suppress("UNCHECKED_CAST")
fun <D : DeclarationDescriptor, A> D.returns(
  f: (KotlinType) -> A = { it as A },
  list: List<KotlinType>,
  types: KotlinBuiltIns.() -> List<KotlinType>
): Boolean =
  list.any { type: KotlinType -> builtIns.types().map(f).contains(f(type)) }

/**
 * resolves FunctionType to it's returnType
 */
val resolveFunctionType: (KotlinType) -> KotlinType
  get() = { if (it.isBuiltinFunctionalType) it.getReturnTypeFromFunctionType() else it }
