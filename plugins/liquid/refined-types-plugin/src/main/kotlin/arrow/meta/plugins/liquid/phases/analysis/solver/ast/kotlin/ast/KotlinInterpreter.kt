package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors.*
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors.KotlinExpressionValueArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.types.TypeConstructor


private fun <A, B> A.repr(): B =
  this as B


fun <A : org.jetbrains.kotlin.descriptors.DeclarationDescriptor,
  B : DeclarationDescriptor> A.model(): B =
  when (this) {
    is SimpleFunctionDescriptor -> KotlinSimpleFunctionDescriptor(this).repr()
    is PropertyGetterDescriptor -> KotlinPropertyAccessorDescriptor(this).repr()
    is PropertyDescriptor -> KotlinPropertyDescriptor(this).repr()
    is PackageViewDescriptor -> KotlinPackageViewDescriptor(this).repr()
    is ClassDescriptor -> KotlinClassDescriptor(this).repr()
    is TypeAliasDescriptor -> KotlinTypeAliasDescriptor(this).repr()
    is ValueParameterDescriptor -> KotlinValueParameterDescriptor(this).repr()
    else -> TODO("Missing impl for $this")
  }

fun <A : KtElement,
  B : Element> A.model(): B =
  when (this) {
    is KtNamedFunction -> KotlinNamedFunction(this).repr()
    is KtProperty -> KotlinProperty(this).repr()
    is KtParameter -> KotlinParameter(this).repr()
   // is KtFile -> KotlinFile(this).repr()
    else -> TODO("Missing impl for $this")
  }

fun <A : Element,
  B : KtElement> A.element(): B =
  impl() as B

