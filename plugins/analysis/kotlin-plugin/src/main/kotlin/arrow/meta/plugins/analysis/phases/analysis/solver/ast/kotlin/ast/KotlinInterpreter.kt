package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast

/* ktlint-disable no-wildcard-imports */
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors.*
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.LazyClassReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.impl.LocalVariableDescriptor
import org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isNull

/* ktlint-enable no-wildcard-imports */

private fun <A : org.jetbrains.kotlin.descriptors.DeclarationDescriptor,
  B : DeclarationDescriptor> A.repr(): B =
  this as B

private fun <A : DeclarationDescriptor,
  B : DeclarationDescriptor> A.repr(unit: Unit = Unit): B =
  this as B

fun <A : Element,
  B : Element> A.repr(): B =
  this as B

fun <A : org.jetbrains.kotlin.descriptors.DeclarationDescriptor,
  B : DeclarationDescriptor> A.model(): B =
  when (this) {
    is SimpleFunctionDescriptor -> KotlinSimpleFunctionDescriptor(this).repr()
    is TypeAliasConstructorDescriptor -> KotlinTypeAliasConstructorDescriptor(this).repr()
    is ConstructorDescriptor -> KotlinConstructorDescriptor(this).repr()
    is FunctionDescriptor -> (object : KotlinFunctionDescriptor(this) { }).repr()
    is PropertyAccessorDescriptor -> KotlinPropertyAccessorDescriptor(this).repr()
    is PropertyDescriptor -> KotlinPropertyDescriptor(this).repr()
    is PackageViewDescriptor -> KotlinPackageViewDescriptor(this).repr()
    is ClassDescriptor -> KotlinClassDescriptor(this).repr()
    is TypeAliasDescriptor -> KotlinTypeAliasDescriptor(this).repr()
    is ValueParameterDescriptor -> KotlinValueParameterDescriptor(this).repr()
    is ClassConstructorDescriptor -> KotlinConstructorDescriptor(this).repr()
    is ModuleDescriptor -> KotlinModuleDescriptor(this).repr()
    is LazyClassReceiverParameterDescriptor -> KotlinReceiverParameterDescriptor(this).repr()
    is ReceiverParameterDescriptor -> KotlinReceiverParameterDescriptor(this).repr()
    is LocalVariableDescriptor -> KotlinLocalVariableDescriptor(this).repr()
    is PackageFragmentDescriptor -> KotlinPackageFragmentDescriptor(this).repr()
    else -> TODO("Missing impl for $this (${this.javaClass.name})")
  }

fun <A : KtElement,
  B : Element> A.model(): B =
  when (this) {
    is KtNamedFunction -> KotlinNamedFunction(this).repr()
    is KtProperty -> KotlinProperty(this).repr()
    is KtParameter -> KotlinParameter(this).repr()
    is KtBinaryExpression -> KotlinBinaryExpression(this).repr()
    is KtNameReferenceExpression -> KotlinNameReferenceExpression(this).repr()
    is KtConstantExpression ->
      if (this.isNull()) KotlinNullExpression(this).repr()
      else KotlinConstantExpression(this).repr()
    is KtCallExpression -> KotlinCallExpression(this).repr()
    is KtEnumEntry -> KotlinEnumEntry(this).repr()
    is KtClass -> KotlinClass(this).repr()
    is KtObjectDeclaration -> KotlinObjectDeclaration(this).repr()
    is KtClassBody -> KotlinClassBody(this).repr()
    is KtLambdaExpression -> KotlinLambdaExpression(this).repr()
    is KtValueArgument -> KotlinValueArgument(this).repr()
    is KtValueArgumentList -> KotlinValueArgumentList(this).repr()
    is KtBlockExpression -> KotlinBlockExpression(this).repr()
    is KtStringTemplateExpression -> KotlinDefaultExpression(this).repr()
    is KtReturnExpression -> KotlinReturnExpression(this).repr()
    is KtParenthesizedExpression -> KotlinParenthesizedExpression(this).repr()
    is KtFunctionLiteral -> KotlinFunctionLiteral(this).repr()
    is KtDotQualifiedExpression -> KotlinDotQualifiedExpression(this).repr()
    is KtPrefixExpression -> KotlinDefaultExpression(this).repr()
    is KtThisExpression -> KotlinThisExpression(this).repr()
    is KtIfExpression -> KotlinIfExpression(this).repr()
    is KtPrimaryConstructor -> KotlinPrimaryConstructor(this).repr()
    is KtClassInitializer -> KotlinClassInitializer(this).repr()
    is KtParameterList -> KotlinParameterList(this).repr()
    is KtIsExpression -> KotlinIsExpression(this).repr()
    is KtTypeReference -> KotlinTypeReference(this).repr()
    is KtTryExpression -> KotlinTryExpression(this).repr()
    is KtCatchClause -> KotlinCatchClause(this).repr()
    is KtSuperTypeCallEntry -> KotlinSuperTypeCallEntry(this).repr()
    is KtConstructorCalleeExpression -> KotlinConstructorCalleeExpression(this).repr()
    is KtSuperTypeEntry -> KotlinSuperTypeEntry(this).repr()
    is KtContainerNode -> KotlinDefaultElement(this).repr()
    is KtWhileExpression -> KotlinWhileExpression(this).repr()
    is KtArrayAccessExpression -> KotlinArrayAccessExpression(this).repr()
    is KtForExpression -> KotlinForExpression(this).repr()
    is KtWhenExpression -> KotlinWhenExpression(this).repr()
    is KtWhenEntry -> KotlinWhenEntry(this).repr()
    is KtWhenConditionWithExpression -> KotlinWhenConditionWithExpression(this).repr()
    is KtSecondaryConstructor -> KotlinSecondaryConstructor(this).repr()
    is KtConstructorDelegationCall -> KotlinConstructorDelegationCall(this).repr()
    is KtConstructorDelegationReferenceExpression -> KotlinConstructorDelegationReferenceExpression(this).repr()
    is KtSafeQualifiedExpression -> KotlinSafeQualifiedExpression(this).repr()
    is KtSuperTypeList -> KotlinSuperTypeList(this).repr()
    is KtInitializerList -> KotlinInitializerList(this).repr()
    // is KtFile -> KotlinFile(this).repr()
    else -> TODO("Missing impl for $this (${this.javaClass.name})")
  }

fun <A : Element,
  B : KtElement> A.element(): B =
  impl() as B
