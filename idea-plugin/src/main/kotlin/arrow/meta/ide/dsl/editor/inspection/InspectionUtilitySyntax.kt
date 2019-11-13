package arrow.meta.ide.dsl.editor.inspection

import arrow.meta.ide.dsl.utils.toNotNullable
import arrow.meta.ide.dsl.utils.traverseFilter
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.getReturnTypeFromFunctionType
import org.jetbrains.kotlin.builtins.isBuiltinFunctionalType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.idea.util.actualsForExpected
import org.jetbrains.kotlin.idea.util.liftToExpected
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.TypeProjection

interface InspectionUtilitySyntax {
  fun KtDeclaration.withExpectedActuals(): List<KtDeclaration> {
    val expect = liftToExpected() ?: return listOf(this)
    val actuals = expect.actualsForExpected()
    return listOf(expect) + actuals
  }

  /**
   * convenience function where [f] reduces FunctionReturnTypes of subsequent calls in the initializer to their returnType
   */
  fun <F : CallableDescriptor> F.returns(prop: KtProperty, types: KotlinBuiltIns.() -> List<KotlinType>): Boolean =
    returns({ if (it.isBuiltinFunctionalType) it.getReturnTypeFromFunctionType() else it }, prop, types)

  /**
   * convenience function where [f] reduces FunctionReturnTypes of subsequent calls in the body to their returnType
   */
  fun <F : CallableDescriptor> F.returns(ktFunction: KtNamedFunction, types: KotlinBuiltIns.() -> List<KotlinType>): Boolean =
    returns({ if (it.isBuiltinFunctionalType) it.getReturnTypeFromFunctionType() else it }, ktFunction, types)

  /**
   * true if [types] contains the returnType of [F]
   * [f] defines on what property/ notion two Types are equal
   * e.g.: [TypeConstructor], [MemberScope] or typeArguments List<[TypeProjection]>, etc...
   * More abstractions here [org.jetbrains.kotlin.types.TypeUtils]
   */
  @Suppress("UNCHECKED_CAST")
  fun <F : CallableDescriptor, A> F.returns(
    f: (KotlinType) -> A = { it as A },
    types: KotlinBuiltIns.() -> List<KotlinType>
  ): Boolean =
    returnType?.let { result: KotlinType ->
      builtIns.types().map(f).contains(f(result))
    } ?: false

  /**
   * true if any type of [list] is in [types]
   * [f] defines on what property/ notion two Types are equal
   * e.g.: [TypeConstructor], [MemberScope] or typeArguments List<[TypeProjection]>, etc...
   */
  @Suppress("UNCHECKED_CAST")
  fun <F : DeclarationDescriptor, A> F.returns(
    f: (KotlinType) -> A = { it as A },
    list: List<KotlinType>,
    types: KotlinBuiltIns.() -> List<KotlinType>
  ): Boolean =
    list.any { type: KotlinType -> builtIns.types().map(f).contains(f(type)) }

  /**
   * traversal of depth 1 on returnTypes in Function [ktFunction] and all it's calls in the body
   * [f] defines on what property two Types are equal
   * TODO: add returns with a traversal of depth n by virtue of recursion
   */
  @Suppress("UNCHECKED_CAST")
  fun <F : CallableDescriptor, A> F.returns(
    f: (KotlinType) -> A = { it as A },
    ktFunction: KtNamedFunction,
    types: KotlinBuiltIns.() -> List<KotlinType>
  ): Boolean =
    returns(f, types) || returns(f, ktFunction.callReturnTypes, types)


  /**
   * traversal of depth 1 on returnTypes in Property [prop] and all it's calls in the body
   * [f] defines on what property two Types are equal
   * TODO: add returns with a traversal of depth n by virtue of recursion
   */
  @Suppress("UNCHECKED_CAST")
  fun <F : CallableDescriptor, A> F.returns(
    f: (KotlinType) -> A = { it as A },
    prop: KtProperty,
    types: KotlinBuiltIns.() -> List<KotlinType>
  ): Boolean =
    returns(f, types) || returns(f, prop.callReturnTypes, types)

  /**
   * returns all ReturnTypes of each call starting from the receiver
   */
  val KtElement.callReturnTypes: List<KotlinType>
    get() = callElements.map { it.returnType }.toNotNullable()

  /**
   * collects all Calls
   */
  val KtElement.callElements: List<KtCallElement>
    get() = traverseFilter(KtCallElement::class.java) { it }

  val KtCallElement.returnType: KotlinType?
    get() = resolveToCall()?.resultingDescriptor?.returnType

  val ArrowPath: Array<String>
    get() = arrayOf("Kotlin", "Λrrow")

  val Project.ktPsiFactory: KtPsiFactory
    get() = KtPsiFactory(this)
}
