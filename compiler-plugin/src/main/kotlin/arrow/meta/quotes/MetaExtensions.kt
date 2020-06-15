package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.quotes.classorobject.ClassDeclaration
import arrow.meta.quotes.classorobject.ObjectDeclaration
import arrow.meta.quotes.declaration.DestructuringDeclaration
import arrow.meta.quotes.declaration.PropertyAccessor
import arrow.meta.quotes.element.CatchClause
import arrow.meta.quotes.element.ClassBody
import arrow.meta.quotes.element.FinallySection
import arrow.meta.quotes.element.ImportDirective
import arrow.meta.quotes.element.PackageDirective
import arrow.meta.quotes.element.ValueArgument
import arrow.meta.quotes.element.WhenEntry
import arrow.meta.quotes.element.whencondition.WhenCondition
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
import arrow.meta.quotes.modifierlistowner.TypeReference
import arrow.meta.quotes.nameddeclaration.notstubbed.FunctionLiteral
import arrow.meta.quotes.nameddeclaration.stub.Parameter
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.NamedFunction
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.Property
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.TypeAlias
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtWhenCondition
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * @see [BinaryExpression]
 */
fun Meta.binaryExpression(
  ctx: CompilerContext,
  match: KtBinaryExpression.() -> Boolean,
  map: BinaryExpression.(KtBinaryExpression) -> Transform<KtBinaryExpression>
): ExtensionPhase =
  quote(ctx, match, map) { BinaryExpression(it) }

/**
 * @see [BlockExpression]
 */
fun Meta.blockExpression(
  ctx: CompilerContext,
  match: KtBlockExpression.() -> Boolean,
  map: BlockExpression.(KtBlockExpression) -> Transform<KtBlockExpression>
): ExtensionPhase =
  quote(ctx, match, map) { BlockExpression(it) }

/**
 * @see [BreakExpression]
 */
fun Meta.breakExpression(
  ctx: CompilerContext,
  match: KtBreakExpression.() -> Boolean,
  map: BreakExpression.(KtBreakExpression) -> Transform<KtBreakExpression>
): ExtensionPhase =
  quote(ctx, match, map) { BreakExpression(it) }

/**
 * @see [CatchClause]
 */
fun Meta.catchClause(
  ctx: CompilerContext,
  match: KtCatchClause.() -> Boolean,
  map: CatchClause.(KtCatchClause) -> Transform<KtCatchClause>
): ExtensionPhase =
  quote(ctx, match, map) { CatchClause(it) }

/**
 * @see [ClassBody]
 */
fun Meta.classBody(
  ctx: CompilerContext,
  match: KtClassBody.() -> Boolean,
  map: ClassBody.(KtClassBody) -> Transform<KtClassBody>
): ExtensionPhase =
  quote(ctx, match, map) { ClassBody(it) }

/**
 * @see [ClassDeclaration]
 */
fun Meta.classDeclaration(
  ctx: CompilerContext,
  match: KtClass.() -> Boolean,
  map: ClassDeclaration.(KtClass) -> Transform<KtClass>
): ExtensionPhase =
  quote(ctx, match, map) { ClassDeclaration(it) }

/**
 * @see [ContinueExpression]
 */
fun Meta.continueExpression(
  ctx: CompilerContext,
  match: KtContinueExpression.() -> Boolean,
  map: ContinueExpression.(KtContinueExpression) -> Transform<KtContinueExpression>
): ExtensionPhase =
  quote(ctx, match, map) { ContinueExpression(it) }

/**
 * @see [DotQualifiedExpression]
 */
fun Meta.dotQualifiedExpression(
  ctx: CompilerContext,
  match: KtDotQualifiedExpression.() -> Boolean,
  map: DotQualifiedExpression.(KtDotQualifiedExpression) -> Transform<KtDotQualifiedExpression>
): ExtensionPhase =
  quote(ctx, match, map) { DotQualifiedExpression(it) }

/**
 * @see [DestructuringDeclaration]
 */
fun Meta.destructuringDeclaration(
  ctx: CompilerContext,
  match: KtDestructuringDeclaration.() -> Boolean,
  map: DestructuringDeclaration.(KtDestructuringDeclaration) -> Transform<KtDestructuringDeclaration>
): ExtensionPhase =
  quote(ctx, match, map) { DestructuringDeclaration(it) }

/**
 * @see [File]
 */
fun Meta.file(
  ctx: CompilerContext,
  match: KtFile.() -> Boolean,
  map: File.(KtFile) -> Transform<KtFile>
): ExtensionPhase =
  quote(ctx, match, map) { File(it) }

/**
 * @see [FinallySection]
 */
fun Meta.finallySection(
  ctx: CompilerContext,
  match: KtFinallySection.() -> Boolean,
  map: FinallySection.(KtFinallySection) -> Transform<KtFinallySection>
): ExtensionPhase =
  quote(ctx, match, map) { FinallySection(it) }

/**
 * @see [ForExpression]
 */
fun Meta.forExpression(
  ctx: CompilerContext,
  match: KtForExpression.() -> Boolean,
  map: ForExpression.(KtForExpression) -> Transform<KtForExpression>
): ExtensionPhase =
  quote(ctx, match, map) { ForExpression(it) }

/**
 * @see [FunctionLiteral]
 */
fun Meta.functionLiteral(
  ctx: CompilerContext,
  match: KtFunctionLiteral.() -> Boolean,
  map: FunctionLiteral.(KtFunctionLiteral) -> Transform<KtFunctionLiteral>
): ExtensionPhase =
  quote(ctx, match, map) { FunctionLiteral(it) }

/**
 * @see [IfExpression]
 */
fun Meta.ifExpression(
  ctx: CompilerContext,
  match: KtIfExpression.() -> Boolean,
  map: IfExpression.(KtIfExpression) -> Transform<KtIfExpression>
): ExtensionPhase =
  quote(ctx, match, map) { IfExpression(it) }

/**
 * @see [IsExpression]
 */
fun Meta.isExpression(
  ctx: CompilerContext,
  match: KtIsExpression.() -> Boolean,
  map: IsExpression.(KtIsExpression) -> Transform<KtIsExpression>
): ExtensionPhase =
  quote(ctx, match, map) { IsExpression(it) }

/**
 * @see [ImportDirective]
 */
fun Meta.importDirective(
  ctx: CompilerContext,
  match: KtImportDirective.() -> Boolean,
  map: ImportDirective.(KtImportDirective) -> Transform<KtImportDirective>
): ExtensionPhase =
  quote(ctx, match, map) { ImportDirective(it) }

/**
 * @see [LambdaExpression]
 */
fun Meta.lambdaExpression(
  ctx: CompilerContext,
  match: KtLambdaExpression.() -> Boolean,
  map: LambdaExpression.(KtLambdaExpression) -> Transform<KtLambdaExpression>
): ExtensionPhase =
  quote(ctx, match, map) { LambdaExpression(it) }

/**
 * @see [NamedFunction]
 */
fun Meta.namedFunction(
  ctx: CompilerContext,
  match: TypedQuoteTemplate<KtNamedFunction, FunctionDescriptor>.() -> Boolean,
  map: NamedFunction.(KtNamedFunction) -> Transform<KtNamedFunction>
): ExtensionPhase =
  typedQuote(ctx, match, map) { (element, descriptor) -> NamedFunction(element, descriptor) }

/**
 * @see [ObjectDeclaration]
 */
fun Meta.objectDeclaration(
  ctx: CompilerContext,
  match: KtObjectDeclaration.() -> Boolean,
  map: ObjectDeclaration.(KtObjectDeclaration) -> Transform<KtObjectDeclaration>
): ExtensionPhase =
  quote(ctx, match, map) { ObjectDeclaration(it) }

/**
 * @see [PackageDirective]
 */
fun Meta.packageDirective(
  ctx: CompilerContext,
  match: KtPackageDirective.() -> Boolean,
  map: PackageDirective.(KtPackageDirective) -> Transform<KtPackageDirective>
): ExtensionPhase =
  quote(ctx, match, map) { PackageDirective(it) }

/**
 * @see [Parameter]
 */
fun Meta.parameter(
  ctx: CompilerContext,
  match: KtParameter.() -> Boolean,
  map: Parameter.(KtParameter) -> Transform<KtParameter>
): ExtensionPhase =
  quote(ctx, match, map) { Parameter(it) }

/**
 * @see [Property]
 */
fun Meta.property(
  ctx: CompilerContext,
  match: TypedQuoteTemplate<KtProperty, PropertyDescriptor>.() -> Boolean,
  map: Property.(KtProperty) -> Transform<KtProperty>
): ExtensionPhase =
  typedQuote(ctx, match, map) { (element, descriptor) -> Property(element, descriptor) }

/**
 * @see [PropertyAccessor]
 */
fun Meta.propertyAccessor(
  ctx: CompilerContext,
  match: KtPropertyAccessor.() -> Boolean,
  map: PropertyAccessor.(KtPropertyAccessor) -> Transform<KtPropertyAccessor>
): ExtensionPhase =
  quote(ctx, match, map) { PropertyAccessor(it) }

/**
 * @see [ReturnExpression]
 */
fun Meta.returnExpression(
  ctx: CompilerContext,
  match: KtReturnExpression.() -> Boolean,
  map: ReturnExpression.(KtReturnExpression) -> Transform<KtReturnExpression>
): ExtensionPhase =
  quote(ctx, match, map) { ReturnExpression(it) }

/**
 * @see [ThrowExpression]
 */
fun Meta.throwExpression(
  ctx: CompilerContext,
  match: KtThrowExpression.() -> Boolean,
  map: ThrowExpression.(KtThrowExpression) -> Transform<KtThrowExpression>
): ExtensionPhase =
  quote(ctx, match, map) { ThrowExpression(it) }

/**
 * @see [TypeReference]
 */
fun Meta.typeReference(
  ctx: CompilerContext,
  match: KtTypeReference.() -> Boolean,
  map: TypeReference.(KtTypeReference) -> Transform<KtTypeReference>
): ExtensionPhase =
  quote(ctx, match, map) { TypeReference(it) }

/**
 * @see [WhenCondition]
 */
fun Meta.whenCondition(
  ctx: CompilerContext,
  match: KtWhenCondition.() -> Boolean,
  map: WhenCondition.(KtWhenCondition) -> Transform<KtWhenCondition>
): ExtensionPhase =
  quote(ctx, match, map) { WhenCondition(it) }

/**
 * @see [WhenEntry]
 */
fun Meta.whenEntry(
  ctx: CompilerContext,
  match: KtWhenEntry.() -> Boolean,
  map: WhenEntry.(KtWhenEntry) -> Transform<KtWhenEntry>
): ExtensionPhase =
  quote(ctx, match, map) { WhenEntry(it) }

/**
 * @see [WhenExpression]
 */
fun Meta.whenExpression(
  ctx: CompilerContext,
  match: KtWhenExpression.() -> Boolean,
  map: WhenExpression.(KtWhenExpression) -> Transform<KtWhenExpression>
): ExtensionPhase =
  quote(ctx, match, map) { WhenExpression(it) }

/**
 * @see [WhileExpression]
 */
fun Meta.whileExpression(
  ctx: CompilerContext,
  match: KtWhileExpression.() -> Boolean,
  map: WhileExpression.(KtWhileExpression) -> Transform<KtWhileExpression>
): ExtensionPhase =
  quote(ctx, match, map) { WhileExpression(it) }

/**
 * @see [ThisExpression]
 */
fun Meta.thisExpression(
  ctx: CompilerContext,
  match: KtThisExpression.() -> Boolean,
  map: ThisExpression.(KtThisExpression) -> Transform<KtThisExpression>
): ExtensionPhase =
  quote(ctx, match, map) { ThisExpression(it) }

/**
 * @see [TryExpression]
 */
fun Meta.tryExpression(
  ctx: CompilerContext,
  match: KtTryExpression.() -> Boolean,
  map: TryExpression.(KtTryExpression) -> Transform<KtTryExpression>
): ExtensionPhase =
  quote(ctx, match, map) { TryExpression(it) }

/**
 * @see [TypeAlias]
 */
fun Meta.typeAlias(
  ctx: CompilerContext,
  match: TypedQuoteTemplate<KtTypeAlias, TypeAliasDescriptor>.() -> Boolean,
  map: TypeAlias.(KtTypeAlias) -> Transform<KtTypeAlias>
): ExtensionPhase =
  typedQuote(ctx, match, map) { (element, descriptor) -> TypeAlias(element, descriptor) }

/**
 * """someObject.add(${argumentName = argumentExpression}.valueArgument)""""
 * @see [ValueArgument]
 */
fun Meta.valueArgument(
  ctx: CompilerContext,
  match: KtValueArgument.() -> Boolean,
  map: ValueArgument.(KtValueArgument) -> Transform<KtValueArgument>
): ExtensionPhase =
  quote(ctx, match, map) { ValueArgument(it) }
