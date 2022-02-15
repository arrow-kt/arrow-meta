package arrow.meta.dsl.fir

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirAnonymousFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirAnonymousInitializerChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirAnonymousObjectChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirBackingFieldChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirBasicDeclarationChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirCallableDeclarationChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassLikeChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirConstructorChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirDeclarationChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirEnumEntryChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFileChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyAccessorChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirSimpleFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirTypeAliasChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirTypeParameterChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirValueParameterChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirAnnotationCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirAnnotationChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirArrayOfCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirBasicExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirBlockChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirCallableReferenceAccessChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirCheckNotNullCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirClassReferenceExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirConstExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirDoWhileLoopChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirElvisExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirEqualityOperatorCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirGetClassCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirLogicExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirLoopExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirLoopJumpChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirQualifiedAccessChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirQualifiedAccessExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirResolvedQualifierChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirReturnExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirSafeCallExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirStringConcatenationCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirThisReceiverExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirTryExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirTypeOperatorCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirVariableAssignmentChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirWhenExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirWhileLoopChecker
import org.jetbrains.kotlin.fir.analysis.checkers.type.FirTypeChecker
import org.jetbrains.kotlin.fir.analysis.checkers.type.FirTypeRefChecker
import org.jetbrains.kotlin.fir.analysis.checkers.type.TypeCheckers
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.types.FirTypeRef

fun FirContext.additionalCheckers(
  basicDeclarationCheckers: Set<FirBasicDeclarationChecker> = emptySet(),
  callableDeclarationCheckers: Set<FirCallableDeclarationChecker> = emptySet(),
  functionCheckers: Set<FirFunctionChecker> = emptySet(),
  simpleFunctionCheckers: Set<FirSimpleFunctionChecker> = emptySet(),
  propertyCheckers: Set<FirPropertyChecker> = emptySet(),
  classLikeCheckers: Set<FirClassLikeChecker> = emptySet(),
  classCheckers: Set<FirClassChecker> = emptySet(),
  regularClassCheckers: Set<FirRegularClassChecker> = emptySet(),
  constructorCheckers: Set<FirConstructorChecker> = emptySet(),
  fileCheckers: Set<FirFileChecker> = emptySet(),
  typeParameterCheckers: Set<FirTypeParameterChecker> = emptySet(),
  typeAliasCheckers: Set<FirTypeAliasChecker> = emptySet(),
  anonymousFunctionCheckers: Set<FirAnonymousFunctionChecker> = emptySet(),
  propertyAccessorCheckers: Set<FirPropertyAccessorChecker> = emptySet(),
  backingFieldCheckers: Set<FirBackingFieldChecker> = emptySet(),
  valueParameterCheckers: Set<FirValueParameterChecker> = emptySet(),
  enumEntryCheckers: Set<FirEnumEntryChecker> = emptySet(),
  anonymousObjectCheckers: Set<FirAnonymousObjectChecker> = emptySet(),
  anonymousInitializerCheckers: Set<FirAnonymousInitializerChecker> = emptySet(),
  annotationCallCheckers: Set<FirAnnotationCallChecker> = emptySet(),
  annotationCheckers: Set<FirAnnotationChecker> = emptySet(),
  arrayOfCallCheckers: Set<FirArrayOfCallChecker> = emptySet(),
  basicExpressionCheckers: Set<FirBasicExpressionChecker> = emptySet(),
  blockCheckers: Set<FirBlockChecker> = emptySet(),
  callCheckers: Set<FirCallChecker> = emptySet(),
  callableReferenceAccessCheckers: Set<FirCallableReferenceAccessChecker> = emptySet(),
  checkNotNullCallCheckers: Set<FirCheckNotNullCallChecker> = emptySet(),
  classReferenceExpressionCheckers: Set<FirClassReferenceExpressionChecker> = emptySet(),
  constExpressionCheckers: Set<FirConstExpressionChecker> = emptySet(),
  doWhileLoopCheckers: Set<FirDoWhileLoopChecker> = emptySet(),
  elvisExpressionCheckers: Set<FirElvisExpressionChecker> = emptySet(),
  equalityOperatorCallCheckers: Set<FirEqualityOperatorCallChecker> = emptySet(),
  functionCallCheckers: Set<FirFunctionCallChecker> = emptySet(),
  getClassCallCheckers: Set<FirGetClassCallChecker> = emptySet(),
  logicExpressionCheckers: Set<FirLogicExpressionChecker> = emptySet(),
  loopExpressionCheckers: Set<FirLoopExpressionChecker> = emptySet(),
  loopJumpCheckers: Set<FirLoopJumpChecker> = emptySet(),
  qualifiedAccessCheckers: Set<FirQualifiedAccessChecker> = emptySet(),
  qualifiedAccessExpressionCheckers: Set<FirQualifiedAccessExpressionChecker> = emptySet(),
  resolvedQualifierCheckers: Set<FirResolvedQualifierChecker> = emptySet(),
  returnExpressionCheckers: Set<FirReturnExpressionChecker> = emptySet(),
  safeCallExpressionCheckers: Set<FirSafeCallExpressionChecker> = emptySet(),
  stringConcatenationCallCheckers: Set<FirStringConcatenationCallChecker> = emptySet(),
  thisReceiverExpressionCheckers: Set<FirThisReceiverExpressionChecker> = emptySet(),
  tryExpressionCheckers: Set<FirTryExpressionChecker> = emptySet(),
  typeOperatorCallCheckers: Set<FirTypeOperatorCallChecker> = emptySet(),
  variableAssignmentCheckers: Set<FirVariableAssignmentChecker> = emptySet(),
  whenExpressionCheckers: Set<FirWhenExpressionChecker> = emptySet(),
  whileLoopCheckers: Set<FirWhileLoopChecker> = emptySet(),
  typeRefCheckers: Set<FirTypeRefChecker> = emptySet(),
): FirAdditionalCheckersExtension =
  object : FirAdditionalCheckersExtension(firSession) {
    override val declarationCheckers: DeclarationCheckers =
      object : DeclarationCheckers() {
        override val basicDeclarationCheckers: Set<FirBasicDeclarationChecker> =
          basicDeclarationCheckers
        override val callableDeclarationCheckers: Set<FirCallableDeclarationChecker> =
          callableDeclarationCheckers
        override val functionCheckers: Set<FirFunctionChecker> = functionCheckers
        override val simpleFunctionCheckers: Set<FirSimpleFunctionChecker> = simpleFunctionCheckers
        override val propertyCheckers: Set<FirPropertyChecker> = propertyCheckers
        override val classLikeCheckers: Set<FirClassLikeChecker> = classLikeCheckers
        override val classCheckers: Set<FirClassChecker> = classCheckers
        override val regularClassCheckers: Set<FirRegularClassChecker> = regularClassCheckers
        override val constructorCheckers: Set<FirConstructorChecker> = constructorCheckers
        override val fileCheckers: Set<FirFileChecker> = fileCheckers
        override val typeParameterCheckers: Set<FirTypeParameterChecker> = typeParameterCheckers
        override val typeAliasCheckers: Set<FirTypeAliasChecker> = typeAliasCheckers
        override val anonymousFunctionCheckers: Set<FirAnonymousFunctionChecker> =
          anonymousFunctionCheckers
        override val propertyAccessorCheckers: Set<FirPropertyAccessorChecker> =
          propertyAccessorCheckers
        override val backingFieldCheckers: Set<FirBackingFieldChecker> = backingFieldCheckers
        override val valueParameterCheckers: Set<FirValueParameterChecker> = valueParameterCheckers
        override val enumEntryCheckers: Set<FirEnumEntryChecker> = enumEntryCheckers
        override val anonymousObjectCheckers: Set<FirAnonymousObjectChecker> =
          anonymousObjectCheckers
        override val anonymousInitializerCheckers: Set<FirAnonymousInitializerChecker> =
          anonymousInitializerCheckers
      }
    override val expressionCheckers: ExpressionCheckers =
      object : ExpressionCheckers() {
        override val annotationCallCheckers: Set<FirAnnotationCallChecker> = annotationCallCheckers
        override val annotationCheckers: Set<FirAnnotationChecker> = annotationCheckers
        override val arrayOfCallCheckers: Set<FirArrayOfCallChecker> = arrayOfCallCheckers
        override val basicExpressionCheckers: Set<FirBasicExpressionChecker> =
          basicExpressionCheckers
        override val blockCheckers: Set<FirBlockChecker> = blockCheckers
        override val callCheckers: Set<FirCallChecker> = callCheckers
        override val callableReferenceAccessCheckers: Set<FirCallableReferenceAccessChecker> =
          callableReferenceAccessCheckers
        override val checkNotNullCallCheckers: Set<FirCheckNotNullCallChecker> =
          checkNotNullCallCheckers
        override val classReferenceExpressionCheckers: Set<FirClassReferenceExpressionChecker> =
          classReferenceExpressionCheckers
        override val constExpressionCheckers: Set<FirConstExpressionChecker> =
          constExpressionCheckers
        override val doWhileLoopCheckers: Set<FirDoWhileLoopChecker> = doWhileLoopCheckers
        override val elvisExpressionCheckers: Set<FirElvisExpressionChecker> =
          elvisExpressionCheckers
        override val equalityOperatorCallCheckers: Set<FirEqualityOperatorCallChecker> =
          equalityOperatorCallCheckers
        override val functionCallCheckers: Set<FirFunctionCallChecker> = functionCallCheckers
        override val getClassCallCheckers: Set<FirGetClassCallChecker> = getClassCallCheckers
        override val logicExpressionCheckers: Set<FirLogicExpressionChecker> =
          logicExpressionCheckers
        override val loopExpressionCheckers: Set<FirLoopExpressionChecker> = loopExpressionCheckers
        override val loopJumpCheckers: Set<FirLoopJumpChecker> = loopJumpCheckers
        override val qualifiedAccessCheckers: Set<FirQualifiedAccessChecker> =
          qualifiedAccessCheckers
        override val qualifiedAccessExpressionCheckers: Set<FirQualifiedAccessExpressionChecker> =
          qualifiedAccessExpressionCheckers
        override val resolvedQualifierCheckers: Set<FirResolvedQualifierChecker> =
          resolvedQualifierCheckers
        override val returnExpressionCheckers: Set<FirReturnExpressionChecker> =
          returnExpressionCheckers
        override val safeCallExpressionCheckers: Set<FirSafeCallExpressionChecker> =
          safeCallExpressionCheckers
        override val stringConcatenationCallCheckers: Set<FirStringConcatenationCallChecker> =
          stringConcatenationCallCheckers
        override val thisReceiverExpressionCheckers: Set<FirThisReceiverExpressionChecker> =
          thisReceiverExpressionCheckers
        override val tryExpressionCheckers: Set<FirTryExpressionChecker> = tryExpressionCheckers
        override val typeOperatorCallCheckers: Set<FirTypeOperatorCallChecker> =
          typeOperatorCallCheckers
        override val variableAssignmentCheckers: Set<FirVariableAssignmentChecker> =
          variableAssignmentCheckers
        override val whenExpressionCheckers: Set<FirWhenExpressionChecker> = whenExpressionCheckers
        override val whileLoopCheckers: Set<FirWhileLoopChecker> = whileLoopCheckers
      }
    override val typeCheckers: TypeCheckers =
      object : TypeCheckers() {
        override val typeRefCheckers: Set<FirTypeRefChecker> = typeRefCheckers
      }
  }

fun <D : FirDeclaration> declarationChecker(
  check: (declaration: D, context: CheckerContext, reporter: DiagnosticReporter) -> Unit,
): FirDeclarationChecker<D> =
  object : FirDeclarationChecker<D>() {

    override fun check(declaration: D, context: CheckerContext, reporter: DiagnosticReporter) {
      check(declaration, context, reporter)
    }
  }

fun <E : FirStatement> expressionChecker(
  check: (expression: E, context: CheckerContext, reporter: DiagnosticReporter) -> Unit,
): FirExpressionChecker<E> =
  object : FirExpressionChecker<E>() {

    override fun check(expression: E, context: CheckerContext, reporter: DiagnosticReporter) {
      check(expression, context, reporter)
    }
  }

fun <T : FirTypeRef> typeChecker(
  check: (typeRef: T, context: CheckerContext, reporter: DiagnosticReporter) -> Unit,
): FirTypeChecker<T> =
  object : FirTypeChecker<T>() {

    override fun check(typeRef: T, context: CheckerContext, reporter: DiagnosticReporter) {
      check(typeRef, context, reporter)
    }
  }
