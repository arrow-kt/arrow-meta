package arrow.meta.phases.analysis

import arrow.meta.quotes.*
import arrow.meta.quotes.ValueArgument
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.ImportPath

/**
 * Default impl for element scopes based on the [KtPsiFactory]
 */
class DefaultElementScope(project: Project) : ElementScope {

  private val delegate = KtPsiFactory(project)

  override val valKeyword: PsiElement
    get() = delegate.createValKeyword()

  override val varKeyword: PsiElement
    get() = delegate.createVarKeyword()

  override val String.expression: Scope<KtExpression>
    get() = Scope(delegate.createExpression(trimMargin()))

  override val String.dotQualifiedExpression: Scope<KtDotQualifiedExpression>
    get() = Scope(expression.value as KtDotQualifiedExpression)
  override val String.expressionOrNull: Scope<KtExpression>
    get() = Scope(delegate.createExpressionIfPossible(trimMargin()))

  override val thisExpression: Scope<KtThisExpression>
    get() = Scope(delegate.createThisExpression())

  override val String.thisExpression: Scope<KtThisExpression>
    get() = Scope(delegate.createThisExpression(trimMargin()))

  override val String.callArguments: Scope<KtValueArgumentList>
    get() = Scope(delegate.createCallArguments(trimMargin()))

  override val String.typeArguments: Scope<KtTypeArgumentList>
    get() = Scope(delegate.createTypeArguments(trimMargin()))

  override val String.typeArgument: Scope<KtTypeProjection>
    get() = Scope(delegate.createTypeArgument(trimMargin()))

  override val String.type: TypeReference
    get() = TypeReference(delegate.createType(trimMargin()))

  override val KtTypeElement.type: TypeReference
    get() = TypeReference(delegate.createType(this))

  override val String.typeOrNull: Scope<KtTypeReference>
    get() = Scope(delegate.createTypeIfPossible(trimMargin()))

  override val KtTypeReference.functionTypeReceiver: Scope<KtFunctionTypeReceiver>
    get() = Scope(delegate.createFunctionTypeReceiver(this))

  override val KtTypeReference.functionTypeParameter: Parameter
    get() = Parameter(delegate.createFunctionTypeParameter(this))

  override fun typeAlias(name: String, typeParameters: List<String>, typeElement: KtTypeElement): Scope<KtTypeAlias> =
    Scope(delegate.createTypeAlias(name, typeParameters, typeElement))

  override fun typeAlias(name: String, typeParameters: List<String>, body: String): Scope<KtTypeAlias> =
    Scope(delegate.createTypeAlias(name, typeParameters, body))

  override val star: PsiElement
    get() = delegate.createStar()

  override val comma: PsiElement
    get() = delegate.createComma()

  override val dot: PsiElement
    get() = delegate.createDot()
  override val colon: PsiElement
    get() = delegate.createColon()
  override val eq: PsiElement
    get() = delegate.createEQ()
  override val semicolon: PsiElement
    get() = delegate.createSemicolon()
  override val whiteSpaceAndArrow: Pair<PsiElement, PsiElement>
    get() = delegate.createWhitespaceAndArrow()
  override val whiteSpace: PsiElement
    get() = delegate.createWhiteSpace()
  override val String.whiteSpace: PsiElement
    get() = delegate.createWhiteSpace(trimMargin())
  override val Int.newLine: PsiElement
    get() = delegate.createNewLine()
  override val String.`class`: ClassDeclaration
    get() = ClassDeclaration(delegate.createClass(trimMargin()))
  override val String.`object`: Scope<KtObjectDeclaration>
    get() = Scope(delegate.createObject(trimMargin()))
  override val companionObject: Scope<KtObjectDeclaration>
    get() = Scope(delegate.createCompanionObject())
  override val String.companionObject: Scope<KtObjectDeclaration>
    get() = Scope(delegate.createCompanionObject(trimMargin()))

  override val <A : KtDeclaration> Scope<A>.synthetic: Scope<A>
    get() {
      val synth = "@arrow.synthetic"
      val declaration = value
      return if (value != null) {
        val expression = when (declaration) {
          is KtDeclaration -> delegate.createDeclaration<A>("$synth ${value?.text}")
          else -> value
        }
        Scope(expression)
      } else this
    }

  override fun property(modifiers: String?, name: String, type: String?, isVar: Boolean, initializer: String?): Scope<KtProperty> =
    Scope(delegate.createProperty(modifiers, name, type, isVar, initializer))

  override fun property(name: String, type: String?, isVar: Boolean, initializer: String?): Scope<KtProperty> =
    Scope(delegate.createProperty(name, type, isVar, initializer))

  override fun property(name: String, type: String?, isVar: Boolean): Scope<KtProperty> =
    Scope(delegate.createProperty(name, type, isVar))

  override val String.property: Scope<KtProperty>
    get() = Scope(delegate.createProperty(trimMargin()))

  override fun propertyGetter(expression: KtExpression): Scope<KtPropertyAccessor> =
    Scope(delegate.createPropertyGetter(expression))

  override fun propertySetter(expression: KtExpression): Scope<KtPropertyAccessor> =
    Scope(delegate.createPropertyGetter(expression))

  override fun propertyDelegate(expression: KtExpression): Scope<KtPropertyDelegate> =
    Scope(delegate.createPropertyDelegate(expression))

  override val String.destructuringDeclaration: Scope<KtDestructuringDeclaration>
    get() = Scope(delegate.createDestructuringDeclaration(trimMargin()))

  override val String.destructuringParameter: Parameter
    get() = Parameter(delegate.createDestructuringParameter(trimMargin()))

  override fun <A : KtDeclaration> String.declaration(): Scope<A> =
    Scope(delegate.createDeclaration(trimMargin()))

  override val String.nameIdentifier: PsiElement
    get() = delegate.createNameIdentifier(trimMargin())

  override val String.nameIdentifierIfPossible: PsiElement?
    get() = delegate.createNameIdentifierIfPossible(trimMargin())

  override val String.simpleName: Scope<KtSimpleNameExpression>
    get() = Scope(delegate.createSimpleName(trimMargin()))

  override val String.operationName: Scope<KtSimpleNameExpression>
    get() = Scope(delegate.createOperationName(trimMargin()))

  override val String.identifier: PsiElement
    get() = delegate.createIdentifier(trimMargin())

  override val String.function: NamedFunction
    get() = NamedFunction(delegate.createFunction(trimMargin()))

  override val String.callableReferenceExpression: Scope<KtCallableReferenceExpression>
    get() = Scope(delegate.createCallableReferenceExpression(trimMargin()))

  override val String.secondaryConstructor: Scope<KtSecondaryConstructor>
    get() = Scope(delegate.createSecondaryConstructor(trimMargin()))

  override fun modifierList(modifier: KtModifierKeywordToken): ModifierList =
    ModifierList(delegate.createModifierList(modifier))

  override val String.modifierList: ModifierList
    get() = ModifierList(delegate.createModifierList(trimMargin()))

  override val emptyModifierList: ModifierList
    get() = ModifierList(delegate.createEmptyModifierList())

  override fun modifier(modifier: KtModifierKeywordToken): PsiElement =
    delegate.createModifier(modifier)

  override val String.annotationEntry: Scope<KtAnnotationEntry>
    get() = Scope(delegate.createAnnotationEntry(trimMargin()))

  override val emptyBody: BlockExpression
    get() = BlockExpression(delegate.createEmptyBody())

  override val anonymousInitializer: Scope<KtAnonymousInitializer>
    get() = Scope(delegate.createAnonymousInitializer())

  override val emptyClassBody: Scope<KtClassBody>
    get() = Scope(delegate.createEmptyClassBody())

  override val String.parameter: Parameter
    get() = Parameter(delegate.createParameter(trimMargin()))

  override val String.loopParameter: Parameter
    get() = Parameter(delegate.createLoopParameter(trimMargin()))

  override val String.parameterList: ParameterList
    get() = ParameterList(delegate.createParameterList(trimMargin()))

  override val String.typeParameterList: Scope<KtTypeParameterList>
    get() = Scope(delegate.createTypeParameterList(trimMargin()))

  override val String.typeParameter: Scope<KtTypeParameter>
    get() = Scope(delegate.createTypeParameter(trimMargin()))

  override val String.lambdaParameterListIfAny: ParameterList
    get() = ParameterList(delegate.createLambdaParameterList(trimMargin()))

  override val String.lambdaParameterList: ParameterList
    get() = ParameterList(delegate.createLambdaParameterList(trimMargin()))

  override fun lambdaExpression(parameters: String, body: String): Scope<KtLambdaExpression> =
    Scope(delegate.createLambdaExpression(parameters, body))

  override val String.enumEntry: Scope<KtEnumEntry>
    get() = Scope(delegate.createEnumEntry(trimMargin()))

  override val enumEntryInitializerList: Scope<KtInitializerList>
    get() = Scope(delegate.createEnumEntryInitializerList())

  override val String.whenEntry: WhenEntry
    get() = WhenEntry(delegate.createWhenEntry(trimMargin()))

  override val String.whenCondition: WhenCondition
    get() = WhenCondition(delegate.createWhenCondition(trimMargin()))

  override fun blockStringTemplateEntry(expression: KtExpression): Scope<KtStringTemplateEntryWithExpression> =
    Scope(delegate.createBlockStringTemplateEntry(expression))

  override fun simpleNameStringTemplateEntry(name: String): Scope<KtSimpleNameStringTemplateEntry> =
    Scope(delegate.createSimpleNameStringTemplateEntry(name))

  override fun literalStringTemplateEntry(literal: String): Scope<KtLiteralStringTemplateEntry> =
    Scope(delegate.createLiteralStringTemplateEntry(literal))

  override fun stringTemplate(content: String): Scope<KtStringTemplateExpression> =
    Scope(delegate.createStringTemplate(content))

  override val String.packageDirective: Scope<KtPackageDirective>
    get() = Scope(delegate.createPackageDirective(FqName(trimMargin())))

  override val String.packageDirectiveOrNull: Scope<KtPackageDirective>
    get() = Scope(delegate.createPackageDirectiveIfNeeded(FqName(trimMargin())))

  override fun importDirective(importPath: ImportPath): ImportDirective =
    ImportDirective(delegate.createImportDirective(importPath))

  override fun primaryConstructor(text: String): Scope<KtPrimaryConstructor> =
    Scope(delegate.createPrimaryConstructor(text))

  override val primaryConstructorNoArgs: Scope<KtPrimaryConstructor>
    get() = Scope(delegate.createPrimaryConstructor())

  override fun primaryConstructorWithModifiers(modifiers: String?): Scope<KtPrimaryConstructor> =
    Scope(delegate.createPrimaryConstructorWithModifiers(modifiers))

  override val constructorKeyword: PsiElement
    get() = delegate.createConstructorKeyword()

  override fun labeledExpression(labelName: String): Scope<KtLabeledExpression> =
    Scope(delegate.createLabeledExpression(labelName))

  override fun String.typeCodeFragment(context: PsiElement?): Scope<KtTypeCodeFragment> =
    Scope(delegate.createTypeCodeFragment(trimMargin(), context))

  override fun String.expressionCodeFragment(context: PsiElement?): Scope<KtExpressionCodeFragment> =
    Scope(delegate.createExpressionCodeFragment(trimMargin(), context))

  override fun String.blockCodeFragment(context: PsiElement?): Scope<KtBlockCodeFragment> =
    Scope(delegate.createBlockCodeFragment(trimMargin(), context))

  override fun `if`(condition: KtExpression, thenExpr: KtExpression, elseExpr: KtExpression?): IfExpression =
    IfExpression(delegate.createIf(condition, thenExpr, elseExpr))

  override fun argument(expression: KtExpression?, name: Name?, isSpread: Boolean, reformat: Boolean): ValueArgument =
          ValueArgument(delegate.createArgument(expression, name, isSpread, reformat))

  override val String.argument: ValueArgument
    get() = ValueArgument(delegate.createArgument(trimMargin()))

  override val String.superTypeCallEntry: Scope<KtSuperTypeCallEntry>
    get() = Scope(delegate.createSuperTypeCallEntry(trimMargin()))

  override val String.superTypeEntry: Scope<KtSuperTypeEntry>
    get() = Scope(delegate.createSuperTypeEntry(trimMargin()))

  override val String.delegatedSuperTypeEntry: Scope<KtConstructorDelegationCall>
    get() = Scope(delegate.creareDelegatedSuperTypeEntry(trimMargin()))

  override val String.block: BlockExpression
    get() = BlockExpression(delegate.createBlock(trimMargin()))

  override fun singleStatementBlock(statement: KtExpression, prevComment: String?, nextComment: String?): BlockExpression =
    BlockExpression(delegate.createSingleStatementBlock(statement, prevComment, nextComment))

  override val String.comment: PsiComment
    get() = delegate.createComment(trimMargin())

  override val String.`for`: ForExpression
    get() = ForExpression(expression.value as KtForExpression)

  override val String.`while`: WhileExpression
    get() = WhileExpression(expression.value as KtWhileExpression)

  override val String.`if`: IfExpression
    get() = IfExpression(expression.value as KtIfExpression)

  override val String.`when`: WhenExpression
    get() = WhenExpression(expression.value as KtWhenExpression)

  override val String.`try`: TryExpression
    get() = TryExpression(expression.value as KtTryExpression)
  
  override val String.catch: CatchClause
    get() = CatchClause(expression.value as KtCatchClause)

  override val String.finally: FinallySection
    get() = FinallySection(expression.value as KtFinallySection)

  override val String.`throw`: ThrowExpression
    get() = ThrowExpression(expression.value as KtThrowExpression)

  override val String.`is`: IsExpression
    get() = IsExpression(expression.value as KtIsExpression)

  override val String.`return`: ReturnExpression
    get() = ReturnExpression(expression.value as KtReturnExpression)

  override val String.annotatedExpression: AnnotatedExpression
    get() = AnnotatedExpression(expression.value as KtAnnotatedExpression)
}
