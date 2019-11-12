package arrow.meta.ide.dsl.editor.inspection

import arrow.meta.ide.dsl.utils.toNotNullable
import arrow.meta.ide.dsl.utils.traverseFilter
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
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
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.createByPattern
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.types.KotlinType

interface InspectionUtilitySyntax {
  fun KtDeclaration.withExpectedActuals(): List<KtDeclaration> {
    val expect = liftToExpected() ?: return listOf(this)
    val actuals = expect.actualsForExpected()
    return listOf(expect) + actuals
  }

  /**
   * More here [org.jetbrains.kotlin.types.TypeUtils]
   */
  fun <F : CallableDescriptor> F.returns(type: KotlinBuiltIns.() -> List<KotlinType>): Boolean =
    builtIns.type().let { type: List<KotlinType> ->
      returnType?.let { result: KotlinType ->
        if (result.isBuiltinFunctionalType) type.contains(result.getReturnTypeFromFunctionType()) else type.contains(result)
      }
    } ?: false

  fun <F : DeclarationDescriptor> F.returns(list: List<KotlinType>, types: KotlinBuiltIns.() -> List<KotlinType>): Boolean =
    builtIns.types().let {
      list.any { type: KotlinType -> if (type.isBuiltinFunctionalType) it.contains(type.getReturnTypeFromFunctionType()) else it.contains(type) }
    }

  /**
   * first degree traversal of Function [f] and all it's calls in the body
   * TODO: add returns with a traversal of depth n by virtue of recursion
   */
  fun <F : CallableDescriptor> F.returns(f: KtNamedFunction, types: KotlinBuiltIns.() -> List<KotlinType>): Boolean =
    returns(types) || returns(f.callReturnTypes, types)

  /**
   * first degree traversal of Property [prop] and all it's calls in the body
   * TODO: add returns with a traversal of depth n by virtue of recursion
   */
  fun <F : CallableDescriptor> F.returns(prop: KtProperty, types: KotlinBuiltIns.() -> List<KotlinType>): Boolean =
    returns(types) || returns(prop.callReturnTypes, types)

  val KtElement.callReturnTypes: List<KotlinType>
    get() = callElements.map { it.returnType }.toNotNullable()

  val KtElement.callElements: List<KtCallElement>
    get() = traverseFilter(KtCallElement::class.java) { it }

  val KtCallElement.returnType: KotlinType?
    get() = resolveToCall()?.resultingDescriptor?.returnType

  val ArrowPath: Array<String>
    get() = arrayOf("Kotlin", "Λrrow")

  val Project.ktPsiFactory: KtPsiFactory
    get() = KtPsiFactory(this)

  fun <K : KtElement> KtPsiFactory.modify(element: K, f: KtPsiFactory.(K) -> K?): PsiElement? =
    f(this, element)?.run { element.replace(this) }
}