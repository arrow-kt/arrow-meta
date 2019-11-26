package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.quotes.classorobject.ClassDeclaration
import arrow.meta.quotes.classorobject.ObjectDeclaration
import arrow.meta.quotes.declaration.DestructuringDeclaration
import arrow.meta.quotes.element.CatchClause
import arrow.meta.quotes.element.FinallySection
import arrow.meta.quotes.element.ImportDirective
import arrow.meta.quotes.element.ParameterList
import arrow.meta.quotes.element.ValueArgument
import arrow.meta.quotes.element.WhenCondition
import arrow.meta.quotes.element.WhenEntry
import arrow.meta.quotes.expression.AnnotatedExpression
import arrow.meta.quotes.expression.BinaryExpression
import arrow.meta.quotes.expression.BlockExpression
import arrow.meta.quotes.expression.IfExpression
import arrow.meta.quotes.expression.IsExpression
import arrow.meta.quotes.expression.LambdaExpression
import arrow.meta.quotes.expression.ThrowExpression
import arrow.meta.quotes.expression.TryExpression
import arrow.meta.quotes.expression.WhenExpression
import arrow.meta.quotes.expression.expressionwithlabel.ReturnExpression
import arrow.meta.quotes.expression.loopexpression.ForExpression
import arrow.meta.quotes.expression.loopexpression.WhileExpression
import arrow.meta.quotes.filebase.File
import arrow.meta.quotes.modifierlist.ModifierList
import arrow.meta.quotes.modifierlist.TypeReference
import arrow.meta.quotes.nameddeclaration.notstubbed.FunctionLiteral
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.NamedFunction
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.Property
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.TypeAlias
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameterList
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReturnExpression
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
 * @see [AnnotatedExpression]
 */
fun Meta.annotatedExpression(
  match: KtAnnotatedExpression.() -> Boolean,
  map: AnnotatedExpression.(KtAnnotatedExpression) -> Transform<KtAnnotatedExpression>
): ExtensionPhase =
  quote(match, map) { AnnotatedExpression(it) }

/**
 * @see [BinaryExpression]
 */
fun Meta.binaryExpression(
  match: KtBinaryExpression.() -> Boolean,
  map: BinaryExpression.(KtBinaryExpression) -> Transform<KtBinaryExpression>
): ExtensionPhase =
  quote(match, map) { BinaryExpression(it) }

/**
 * @see [BlockExpression]
 */
fun Meta.blockExpression(
  match: KtBlockExpression.() -> Boolean,
  map: BlockExpression.(KtBlockExpression) -> Transform<KtBlockExpression>
): ExtensionPhase =
  quote(match, map) { BlockExpression(it) }

/**
 * @see [CatchClause]
 */
fun Meta.catchClause(
  match: KtCatchClause.() -> Boolean,
  map: CatchClause.(KtCatchClause) -> Transform<KtCatchClause>
): ExtensionPhase =
  quote(match, map) { CatchClause(it) }

/**
 * @see [ClassDeclaration]
 */
fun Meta.classDeclaration(
  match: KtClass.() -> Boolean,
  map: ClassDeclaration.(KtClass) -> Transform<KtClass>
): ExtensionPhase =
  quote(match, map) { ClassDeclaration(it) }

/**
 * @see [DestructuringDeclaration]
 */
fun Meta.destructuringDeclaration(
  match: KtDestructuringDeclaration.() -> Boolean,
  map: DestructuringDeclaration.(KtDestructuringDeclaration) -> Transform<KtDestructuringDeclaration>
): ExtensionPhase =
  quote(match, map) { DestructuringDeclaration(it) }

/**
 * @see [File]
 */
fun Meta.file(
  match: KtFile.() -> Boolean,
  map: File.(KtFile) -> Transform<KtFile>
): ExtensionPhase =
  quote(match, map) { File(it) }

/**
 * @see [FinallySection]
 */
fun Meta.finallySection(
  match: KtFinallySection.() -> Boolean,
  map: FinallySection.(KtFinallySection) -> Transform<KtFinallySection>
): ExtensionPhase =
  quote(match, map) { FinallySection(it) }

/**
 * @see [ForExpression]
 */
fun Meta.forExpression(
  match: KtForExpression.() -> Boolean,
  map: ForExpression.(KtForExpression) -> Transform<KtForExpression>
): ExtensionPhase =
  quote(match, map) { ForExpression(it) }

/**
 * @see [FunctionLiteral]
 */
fun Meta.functionLiteral(
  match: KtFunctionLiteral.() -> Boolean,
  map: FunctionLiteral.(KtFunctionLiteral) -> Transform<KtFunctionLiteral>
) : ExtensionPhase =
  quote(match, map) { FunctionLiteral(it) }

/**
 * @see [IfExpression]
 */
fun Meta.ifExpression(
  match: KtIfExpression.() -> Boolean,
  map: IfExpression.(KtIfExpression) -> Transform<KtIfExpression>
): ExtensionPhase =
  quote(match, map) { IfExpression(it) }

/**
 * @see [IsExpression]
 */
fun Meta.isExpression(
  match: KtIsExpression.() -> Boolean,
  map: IsExpression.(KtIsExpression) -> Transform<KtIsExpression>
): ExtensionPhase =
  quote(match, map) { IsExpression(it) }

/**
 * @see [ImportDirective]
 */
fun Meta.importDirective(
  match: KtImportDirective.() -> Boolean,
  map: ImportDirective.(KtImportDirective) -> Transform<KtImportDirective>
) : ExtensionPhase =
  quote(match, map) { ImportDirective(it) }

/**
 * @see [LambdaExpression]
 */
fun Meta.lambdaExpression(
  match: KtLambdaExpression.() -> Boolean,
  map: LambdaExpression.(KtLambdaExpression) -> Transform<KtLambdaExpression>
) : ExtensionPhase =
  quote(match, map) { LambdaExpression(it) }

/**
 * @see [ModifierList]
 */
fun Meta.modifierList(
  match: KtModifierList.() -> Boolean,
  map: ModifierList.(KtModifierList) -> Transform<KtModifierList>
): ExtensionPhase =
  quote(match, map) { ModifierList(it) }

/**
 * @see [NamedFunction]
 */
fun Meta.namedFunction(
  match: KtNamedFunction.() -> Boolean,
  map: NamedFunction.(KtNamedFunction) -> Transform<KtNamedFunction>
): ExtensionPhase =
  quote(match, map) { NamedFunction(it) }

/**
 * @see [ObjectDeclaration]
 */
fun Meta.objectDeclaration(
  match: KtObjectDeclaration.() -> Boolean,
  map: ObjectDeclaration.(KtObjectDeclaration) -> Transform<KtObjectDeclaration>
): ExtensionPhase =
  quote(match, map) { ObjectDeclaration(it) }

/**
 * @see [ParameterList]
 */
fun Meta.parameterList(
  match: KtParameterList.() -> Boolean,
  map: ParameterList.(KtParameterList) -> Transform<KtParameterList>
): ExtensionPhase =
  quote(match, map) { ParameterList(it) }

/**
 * @see [Property]
 */
fun Meta.property(
  match: KtProperty.() -> Boolean,
  map: Property.(KtProperty) -> Transform<KtProperty>
): ExtensionPhase =
  quote(match, map) { Property(it) }

/**
 * @see [ReturnExpression]
 */
fun Meta.returnExpression(
  match: KtReturnExpression.() -> Boolean,
  map: ReturnExpression.(KtReturnExpression) -> Transform<KtReturnExpression>
): ExtensionPhase =
  quote(match, map) { ReturnExpression(it) }

/**
 * @see [ThrowExpression]
 */
fun Meta.throwExpression(
  match: KtThrowExpression.() -> Boolean,
  map: ThrowExpression.(KtThrowExpression) -> Transform<KtThrowExpression>
): ExtensionPhase =
  quote(match, map) { ThrowExpression(it) }

/**
 * @see [TypeReference]
 */
fun Meta.typeReference(
  match: KtTypeReference.() -> Boolean,
  map: TypeReference.(KtTypeReference) -> Transform<KtTypeReference>
): ExtensionPhase =
  quote(match, map) { TypeReference(it) }

/**
 * @see [WhenCondition]
 */
fun Meta.whenCondition(
  match: KtWhenCondition.() -> Boolean,
  map: WhenCondition.(KtWhenCondition) -> Transform<KtWhenCondition>
): ExtensionPhase =
  quote(match, map) { WhenCondition(it) }

/**
 * @see [WhenEntry]
 */
fun Meta.whenEntry(
  match: KtWhenEntry.() -> Boolean,
  map: WhenEntry.(KtWhenEntry) -> Transform<KtWhenEntry>
): ExtensionPhase =
  quote(match, map) { WhenEntry(it) }

/**
 * @see [WhenExpression]
 */
fun Meta.whenExpression(
  match: KtWhenExpression.() -> Boolean,
  map: WhenExpression.(KtWhenExpression) -> Transform<KtWhenExpression>
): ExtensionPhase =
  quote(match, map) { WhenExpression(it) }

/**
 * @see [WhileExpression]
 */
fun Meta.whileExpression(
  match: KtWhileExpression.() -> Boolean,
  map: WhileExpression.(KtWhileExpression) -> Transform<KtWhileExpression>
): ExtensionPhase =
  quote(match, map) { WhileExpression(it) }

/**
 * @see [TryExpression]
 */
fun Meta.tryExpression(
  match: KtTryExpression.() -> Boolean,
  map: TryExpression.(KtTryExpression) -> Transform<KtTryExpression>
): ExtensionPhase =
  quote(match, map) { TryExpression(it) }

/**
 * @see [TypeAlias]
 */
fun Meta.typeAlias(
  match: KtTypeAlias.() -> Boolean,
  map: TypeAlias.(KtTypeAlias) -> Transform<KtTypeAlias>
): ExtensionPhase =
  quote(match, map) { TypeAlias(it) }

/**
 * """someObject.add(${argumentName = argumentExpression}.valueArgument)""""
 * @see [ValueArgument]
 */
fun Meta.valueArgument(
  match: KtValueArgument.() -> Boolean,
  map: ValueArgument.(KtValueArgument) -> Transform<KtValueArgument>
): ExtensionPhase =
  quote(match, map) { ValueArgument(it) }
