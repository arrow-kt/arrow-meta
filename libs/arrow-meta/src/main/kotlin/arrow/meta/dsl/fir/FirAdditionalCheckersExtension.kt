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
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.type.FirTypeChecker
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
    override val expressionCheckers: ExpressionCheckers
      get() = super.expressionCheckers
    override val typeCheckers: TypeCheckers
      get() = super.typeCheckers
  }

fun <D : FirDeclaration> declarationChecker(
  check: (declaration: D, context: CheckerContext, reporter: DiagnosticReporter) -> Unit,
): FirDeclarationChecker<D> =
  object : FirDeclarationChecker<D>() {

    override fun check(declaration: D, context: CheckerContext, reporter: DiagnosticReporter) {
      check(declaration, context, reporter)
    }
  }

fun <E : FirStatement> firExpressionChecker(
  check: (expression: E, context: CheckerContext, reporter: DiagnosticReporter) -> Unit,
): FirExpressionChecker<E> =
  object : FirExpressionChecker<E>() {

    override fun check(expression: E, context: CheckerContext, reporter: DiagnosticReporter) {
      check(expression, context, reporter)
    }
  }

fun <T : FirTypeRef> firTypeChecker(
  check: (typeRef: T, context: CheckerContext, reporter: DiagnosticReporter) -> Unit,
): FirTypeChecker<T> =
  object : FirTypeChecker<T>() {

    override fun check(typeRef: T, context: CheckerContext, reporter: DiagnosticReporter) {
      check(typeRef, context, reporter)
    }
  }
