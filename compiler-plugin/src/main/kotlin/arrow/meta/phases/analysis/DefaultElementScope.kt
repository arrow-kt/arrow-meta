package arrow.meta.phases.analysis

import arrow.meta.quotes.Scope
import arrow.meta.quotes.classorobject.ClassBody
import arrow.meta.quotes.classorobject.ClassDeclaration
import arrow.meta.quotes.classorobject.ObjectDeclaration
import arrow.meta.quotes.declaration.DestructuringDeclaration
import arrow.meta.quotes.declaration.PropertyAccessor
import arrow.meta.quotes.element.CatchClause
import arrow.meta.quotes.element.FinallySection
import arrow.meta.quotes.element.ImportDirective
import arrow.meta.quotes.element.PackageDirective
import arrow.meta.quotes.element.ParameterList
import arrow.meta.quotes.element.ValueArgument
import arrow.meta.quotes.element.WhenEntry
import arrow.meta.quotes.element.whencondition.WhenCondition
import arrow.meta.quotes.expression.AnnotatedExpression
import arrow.meta.quotes.expression.BinaryExpression
import arrow.meta.quotes.expression.BlockExpression
import arrow.meta.quotes.expression.DotQualifiedExpression
import arrow.meta.quotes.expression.IfExpression
import arrow.meta.quotes.expression.IsExpression
import arrow.meta.quotes.expression.LambdaExpression
import arrow.meta.quotes.expression.ThrowExpression
import arrow.meta.quotes.expression.TryExpression
import arrow.meta.quotes.expression.WhenExpression
import arrow.meta.quotes.expression.expressionwithlabel.BreakExpression
import arrow.meta.quotes.expression.expressionwithlabel.ContinueExpression
import arrow.meta.quotes.expression.expressionwithlabel.ReturnExpression
import arrow.meta.quotes.expression.expressionwithlabel.instanceexpressionwithlabel.ThisExpression
import arrow.meta.quotes.expression.loopexpression.ForExpression
import arrow.meta.quotes.expression.loopexpression.WhileExpression
import arrow.meta.quotes.filebase.File
import arrow.meta.quotes.modifierlist.ModifierList
import arrow.meta.quotes.modifierlist.TypeReference
import arrow.meta.quotes.nameddeclaration.notstubbed.FunctionLiteral
import arrow.meta.quotes.nameddeclaration.stub.Parameter
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.NamedFunction
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.Property
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.TypeAlias
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockCodeFragment
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtExpressionCodeFragment
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtFunctionTypeReceiver
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtInitializerList
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtLabeledExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtSimpleNameStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateEntryWithExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtSuperTypeEntry
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtTypeCodeFragment
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeParameterList
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.KtWhileExpression
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
    get() = Scope(delegate.createExpression(trimMargin().trim()))

  override val String.dotQualifiedExpression: DotQualifiedExpression
    get() = DotQualifiedExpression(expression.value as KtDotQualifiedExpression)

  override val String.expressionOrNull: Scope<KtExpression>
    get() = Scope(delegate.createExpressionIfPossible(trimMargin().trim()))

  override val String.callArguments: Scope<KtValueArgumentList>
    get() = Scope(delegate.createCallArguments(trimMargin().trim()))

  override val String.typeArguments: Scope<KtTypeArgumentList>
    get() = Scope(delegate.createTypeArguments(trimMargin().trim()))

  override val String.typeArgument: Scope<KtTypeProjection>
    get() = Scope(delegate.createTypeArgument(trimMargin().trim()))

  override val String.type: TypeReference
    get() = TypeReference(delegate.createType(trimMargin().trim()))

  override val KtTypeElement.type: TypeReference
    get() = TypeReference(delegate.createType(this))

  override val String.typeOrNull: Scope<KtTypeReference>
    get() = Scope(delegate.createTypeIfPossible(trimMargin()))

  override val KtTypeReference.functionTypeReceiver: Scope<KtFunctionTypeReceiver>
    get() = Scope(delegate.createFunctionTypeReceiver(this))

  override val KtTypeReference.functionTypeParameter: Parameter
    get() = Parameter(delegate.createFunctionTypeParameter(this))

  override fun typeAlias(name: String, typeParameters: List<String>, typeElement: KtTypeElement): TypeAlias =
    TypeAlias(delegate.createTypeAlias(name, typeParameters, typeElement))

  override fun typeAlias(name: String, typeParameters: List<String>, body: String): TypeAlias =
    TypeAlias(delegate.createTypeAlias(name, typeParameters, body))

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
  override val String.`object`: ObjectDeclaration
    get() = ObjectDeclaration(delegate.createObject(trimMargin()))
  override val companionObject: ObjectDeclaration
    get() = ObjectDeclaration(delegate.createCompanionObject())
  override val String.companionObject: ObjectDeclaration
    get() = ObjectDeclaration(delegate.createCompanionObject(trimMargin()))
  
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

  override fun property(modifiers: String?, name: String, type: String?, isVar: Boolean, initializer: String?): Property =
    Property(delegate.createProperty(modifiers, name, type, isVar, initializer))

  override fun property(name: String, type: String?, isVar: Boolean, initializer: String?): Property =
    Property(delegate.createProperty(name, type, isVar, initializer))

  override fun property(name: String, type: String?, isVar: Boolean): Property =
    Property(delegate.createProperty(name, type, isVar))

  override val String.property: Property
    get() = Property(delegate.createProperty(trimMargin().trim()))

  override fun propertyGetter(expression: KtExpression): PropertyAccessor =
    PropertyAccessor(delegate.createPropertyGetter(expression))

  override fun propertySetter(expression: KtExpression): PropertyAccessor =
    PropertyAccessor(delegate.createPropertyGetter(expression))

  override fun propertyDelegate(expression: KtExpression): Scope<KtPropertyDelegate> =
    Scope(delegate.createPropertyDelegate(expression))

  override val String.destructuringDeclaration: DestructuringDeclaration
    get() = DestructuringDeclaration(delegate.createDestructuringDeclaration(trimMargin().trim()))

  override fun <A : KtDeclaration> String.declaration(): Scope<A> =
    Scope(delegate.createDeclaration(trimMargin().trim()))

  override val String.nameIdentifier: PsiElement
    get() = delegate.createNameIdentifier(trimMargin().trim())

  override val String.nameIdentifierIfPossible: PsiElement?
    get() = delegate.createNameIdentifierIfPossible(trimMargin())

  override val String.simpleName: Scope<KtSimpleNameExpression>
    get() = Scope(delegate.createSimpleName(trimMargin().trim()))

  override val String.operationName: Scope<KtSimpleNameExpression>
    get() = Scope(delegate.createOperationName(trimMargin().trim()))

  override val String.identifier: PsiElement
    get() = delegate.createIdentifier(trimMargin().trim())

  override val String.function: NamedFunction
    get() = NamedFunction(delegate.createFunction(trimMargin().trim()))

  override val String.binaryExpression: BinaryExpression
    get() = BinaryExpression(expression.value as KtBinaryExpression)

  override val String.callableReferenceExpression: Scope<KtCallableReferenceExpression>
    get() = Scope(delegate.createCallableReferenceExpression(trimMargin().trim()))

  override val String.secondaryConstructor: Scope<KtSecondaryConstructor>
    get() = Scope(delegate.createSecondaryConstructor(trimMargin().trim()))

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

  override val emptyClassBody: ClassBody
    get() = ClassBody(delegate.createEmptyClassBody())

  override val String.classParameter: Parameter
    get() = Parameter(delegate.createParameter(trimMargin()))

  override val String.loopParameter: Parameter
    get() = Parameter(delegate.createLoopParameter(trimMargin()))

  override val String.destructuringParameter: Parameter
    get() = Parameter(delegate.createDestructuringParameter(trimMargin()))

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

  override fun lambdaExpression(parameters: String, body: String): LambdaExpression =
    LambdaExpression(delegate.createLambdaExpression(parameters, body))

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

  override val String.`package`: PackageDirective
    get() = PackageDirective(delegate.createPackageDirective(FqName(trimMargin())))

  override val String.packageDirectiveOrNull: PackageDirective
    get() = PackageDirective(delegate.createPackageDirectiveIfNeeded(FqName(trimMargin())))

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
    get() = BlockExpression(delegate.createBlock(trimMargin().trim()))

  override fun singleStatementBlock(statement: KtExpression, prevComment: String?, nextComment: String?): BlockExpression =
    BlockExpression(delegate.createSingleStatementBlock(statement, prevComment, nextComment))

  override val String.comment: PsiComment
    get() = delegate.createComment(trimMargin())

  override val String.`for`: ForExpression
    get() = ForExpression(expression.value as KtForExpression)

  override val String.`while`: WhileExpression
    get() = WhileExpression(expression.value as KtWhileExpression)

  override val String.`when`: WhenExpression
    get() = WhenExpression(expression.value as KtWhenExpression)

  override val String.`try`: TryExpression
    get() = TryExpression(expression.value as KtTryExpression)
  
  override val String.catch: CatchClause
    get() = CatchClause(
      """
      |try { } 
      |$this
      """.trimIndent().trim().`try`.catchClauses.value.first())

  override val String.finally: FinallySection
    get() = FinallySection(
      """
      |try { } 
      |$this
      """.trimIndent().trim().`try`.finallySection.value)

  override val String.`throw`: ThrowExpression
    get() = ThrowExpression(expression.value as KtThrowExpression)

  override val String.`is`: IsExpression
    get() = IsExpression(expression.value as KtIsExpression)

  override val String.`if`: IfExpression
    get() = IfExpression(expression.value as KtIfExpression)

  override val String.`return`: ReturnExpression
    get() = ReturnExpression(expression.value as KtReturnExpression)

  override val String.`break`: BreakExpression
    get() = BreakExpression(expression.value as KtBreakExpression)

  override val String.`continue`: ContinueExpression
    get() = ContinueExpression(expression.value as KtContinueExpression)

  override val String.`this`: ThisExpression
    get() = ThisExpression(expression.value as KtThisExpression)

  override fun String.expressionIn(context: PsiElement): Scope<KtExpressionCodeFragment> =
    Scope(delegate.createExpressionCodeFragment(trimMargin(), context))

  override val String.annotatedExpression: AnnotatedExpression
    get() = AnnotatedExpression(expression.value as KtAnnotatedExpression)
  
  override fun String.file(fileName: String): File = File(delegate.createFile(if(fileName.contains(".kt")) fileName else "$fileName.kt", this))

  override val String.functionLiteral: FunctionLiteral
    get() = FunctionLiteral((expression.value as KtLambdaExpression).functionLiteral)
  
  override val String.classBody: ClassBody
    get() = ClassBody(delegate.createClass("class _ClassBodyScopeArrowMeta ${trimMargin()}").body)
}

