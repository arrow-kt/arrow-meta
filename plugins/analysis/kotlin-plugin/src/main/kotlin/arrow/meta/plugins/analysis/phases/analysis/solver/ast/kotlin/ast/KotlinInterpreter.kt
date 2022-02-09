package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast

/* ktlint-disable no-wildcard-imports */
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors.*
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.LocalVariableDescriptor
import org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isNull

/* ktlint-enable no-wildcard-imports */

@Suppress("UNUSED_PARAMETER", "UNCHECKED_CAST")
private fun <A : DeclarationDescriptor, B : DeclarationDescriptor> A.repr(unit: Unit = Unit): B =
  this as B

@Suppress("UNCHECKED_CAST") fun <A : Element, B : Element> A.repr(): B = this as B

fun <
  A : org.jetbrains.kotlin.descriptors.DeclarationDescriptor, B : DeclarationDescriptor> A.model():
  B =
  when (this) {
    is SimpleFunctionDescriptor -> KotlinSimpleFunctionDescriptor(this).repr()
    is TypeAliasConstructorDescriptor -> KotlinTypeAliasConstructorDescriptor(this).repr()
    is ConstructorDescriptor -> KotlinConstructorDescriptor(this).repr()
    is PropertyAccessorDescriptor -> KotlinPropertyAccessorDescriptor(this).repr()
    is PropertyDescriptor -> KotlinPropertyDescriptor(this).repr()
    is PackageViewDescriptor -> KotlinPackageViewDescriptor(this).repr()
    is ClassDescriptor -> KotlinClassDescriptor(this).repr()
    is TypeAliasDescriptor -> KotlinTypeAliasDescriptor(this).repr()
    is ValueParameterDescriptor -> KotlinValueParameterDescriptor(this).repr()
    is ModuleDescriptor -> KotlinModuleDescriptor(this).repr()
    is ReceiverParameterDescriptor -> KotlinReceiverParameterDescriptor(this).repr()
    is LocalVariableDescriptor -> KotlinLocalVariableDescriptor(this).repr()
    is PackageFragmentDescriptor -> KotlinPackageFragmentDescriptor(this).repr()
    // fallback cases: we sometimes find unknown descriptors
    // and for those cases we need nothing else than what the
    // abstract classes provide
    is FunctionDescriptor -> KotlinDefaultFunctionDescriptor(this).repr()
    is VariableDescriptor -> KotlinDefaultVariableDescriptor(this).repr()
    is DeclarationDescriptorWithVisibility ->
      KotlinDefaultDeclarationDescriptorWithVisibility(this).repr()
    else -> KotlinDefaultDeclarationDescriptor(this).repr()
  }

fun <A : KtElement, B : Element> A.model(): B =
  when (this) {
    is KtNamedFunction -> KotlinNamedFunction(this).repr()
    is KtProperty -> KotlinProperty(this).repr()
    is KtPropertyAccessor -> KotlinPropertyAccessor(this).repr()
    is KtParameter -> KotlinParameter(this).repr()
    is KtDestructuringDeclaration -> KotlinDestructuringDeclaration(this).repr()
    is KtDestructuringDeclarationEntry -> KotlinDestructuringDeclarationEntry(this).repr()
    is KtBinaryExpression ->
      when (this.operationToken.toString()) {
        "EQ" -> KotlinAssignmentExpression(this).repr()
        else -> KotlinBinaryExpression(this).repr()
      }
    is KtBinaryExpressionWithTypeRHS ->
      when (this.operationReference.getReferencedName()) {
        "as", "as?" -> KotlinTypeCastExpression(this).repr()
        else -> KotlinDefaultExpression(this).repr()
      }
    is KtNameReferenceExpression -> KotlinNameReferenceExpression(this).repr()
    is KtLabelReferenceExpression -> KotlinLabelReferenceExpression(this).repr()
    is KtOperationReferenceExpression -> KotlinOperationReferenceExpression(this).repr()
    is KtConstantExpression ->
      if (this.isNull()) KotlinNullExpression(this).repr()
      else KotlinConstantExpression(this).repr()
    is KtCallExpression -> KotlinCallExpression(this).repr()
    is KtEnumEntry -> KotlinEnumEntry(this).repr()
    is KtTypeAlias -> KotlinTypeAlias(this).repr()
    is KtClass -> KotlinClass(this).repr()
    is KtObjectDeclaration -> KotlinObjectDeclaration(this).repr()
    is KtObjectLiteralExpression -> KotlinObjectLiteralExpression(this).repr()
    is KtPropertyDelegate -> KotlinPropertyDelegate(this).repr()
    is KtClassBody -> KotlinClassBody(this).repr()
    is KtLambdaExpression -> KotlinLambdaExpression(this).repr()
    is KtValueArgument -> KotlinValueArgument(this).repr()
    is KtValueArgumentList -> KotlinValueArgumentList(this).repr()
    is KtBlockExpression -> KotlinBlockExpression(this).repr()
    is KtStringTemplateExpression ->
      if (!this.hasInterpolation()) KotlinConstantStringExpression(this).repr()
      else KotlinStringTemplateExpression(this).repr()
    is KtReturnExpression -> KotlinReturnExpression(this).repr()
    is KtBreakExpression -> KotlinBreakExpression(this).repr()
    is KtContinueExpression -> KotlinContinueExpression(this).repr()
    is KtParenthesizedExpression -> KotlinParenthesizedExpression(this).repr()
    is KtAnnotatedExpression -> KotlinAnnotatedExpression(this).repr()
    is KtFunctionLiteral -> KotlinFunctionLiteral(this).repr()
    is KtDotQualifiedExpression -> KotlinDotQualifiedExpression(this).repr()
    is KtUnaryExpression -> KotlinUnaryExpression(this).repr()
    is KtThisExpression -> KotlinThisExpression(this).repr()
    is KtSuperExpression -> KotlinSuperExpression(this).repr()
    is KtIfExpression -> KotlinIfExpression(this).repr()
    is KtPrimaryConstructor -> KotlinPrimaryConstructor(this).repr()
    is KtSecondaryConstructor -> KotlinSecondaryConstructor(this).repr()
    is KtClassInitializer -> KotlinClassInitializer(this).repr()
    is KtParameterList -> KotlinParameterList(this).repr()
    is KtIsExpression -> KotlinIsExpression(this).repr()
    is KtTypeReference -> KotlinTypeReference(this).repr()
    is KtThrowExpression -> KotlinThrowExpression(this).repr()
    is KtTryExpression -> KotlinTryExpression(this).repr()
    is KtCatchClause -> KotlinCatchClause(this).repr()
    is KtFinallySection -> KotlinFinallySection(this).repr()
    is KtClassLiteralExpression -> KotlinClassLiteralExpression(this).repr()
    is KtCallableReferenceExpression -> KotlinCallableReferenceExpression(this).repr()
    is KtConstructorCalleeExpression -> KotlinConstructorCalleeExpression(this).repr()
    is KtSuperTypeCallEntry -> KotlinSuperTypeCallEntry(this).repr()
    is KtDelegatedSuperTypeEntry -> KotlinDelegatedSuperTypeEntry(this).repr()
    is KtSuperTypeEntry -> KotlinSuperTypeEntry(this).repr()
    is KtWhileExpression -> KotlinWhileExpression(this).repr()
    is KtDoWhileExpression -> KotlinDoWhileExpression(this).repr()
    is KtArrayAccessExpression -> KotlinArrayAccessExpression(this).repr()
    is KtForExpression -> KotlinForExpression(this).repr()
    is KtWhenExpression -> KotlinWhenExpression(this).repr()
    is KtWhenEntry -> KotlinWhenEntry(this).repr()
    is KtWhenConditionWithExpression -> KotlinWhenConditionWithExpression(this).repr()
    is KtWhenConditionIsPattern -> KotlinWhenConditionIsPattern(this).repr()
    is KtWhenConditionInRange -> KotlinWhenConditionInRange(this).repr()
    is KtConstructorDelegationCall -> KotlinConstructorDelegationCall(this).repr()
    is KtConstructorDelegationReferenceExpression ->
      KotlinConstructorDelegationReferenceExpression(this).repr()
    is KtSafeQualifiedExpression -> KotlinSafeQualifiedExpression(this).repr()
    is KtSuperTypeList -> KotlinSuperTypeList(this).repr()
    is KtInitializerList -> KotlinInitializerList(this).repr()
    is KtTypeParameter -> KotlinTypeParameter(this).repr()
    is KtTypeParameterList -> KotlinTypeParameterList(this).repr()
    is KtTypeConstraint -> KotlinTypeConstraint(this).repr()
    is KtTypeConstraintList -> KotlinTypeConstraintList(this).repr()
    is KtTypeArgumentList -> KotlinTypeArgumentList(this).repr()
    is KtAnnotation -> KotlinAnnotation(this).repr()
    is KtAnnotationEntry -> KotlinAnnotationEntry(this).repr()
    is KtModifierList -> KotlinModifierList(this).repr()
    // fallbacks
    is KtFunction -> KotlinDefaultFunction(this).repr()
    is KtContainerNodeForControlStructureBody -> this.expression?.model()
        ?: KotlinDefaultElement(this).repr()
    is KtContainerNode -> KotlinDefaultElement(this).repr()
    // final fallback for expressions
    is KtExpression -> KotlinDefaultExpression(this).repr()
    // is KtFile -> KotlinFile(this).repr()
    else ->
      TODO(
        "Missing impl for ${this.text} ${this.containingKtFile.virtualFilePath} (${this.javaClass.name})"
      )
  }

@Suppress("UNCHECKED_CAST") fun <A : Element, B : KtElement> A.element(): B = impl() as B
