@file:OptIn(SymbolInternals::class)

package arrow.meta.plugins.proofs

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.dsl.fir.additionalCheckers
import arrow.meta.dsl.fir.expressionChecker
import arrow.meta.invoke
import org.jetbrains.kotlin.diagnostics.AbstractSourceElementPositioningStrategy
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.PsiSourceNavigator.getRawName
import org.jetbrains.kotlin.fir.declarations.FirAnonymousFunction
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirLambdaArgumentExpression
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.argument
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.name.FqName

val resolveFn = FqName("test.resolve")
val contextualFn = FqName("test.contextual")

val Meta.typeProofs: CliPlugin
  get() = "@contextual plugin" {
    meta(
      fir(
        additionalCheckers = {
          additionalCheckers(
            callCheckers = setOf(
              expressionChecker { expression, context, reporter ->
                if (expression is FirFunctionCall) {
                  val ref = expression.calleeReference as? FirResolvedNamedReference
                  val calleeFn = ref?.resolvedSymbol?.fir
                  val sym = calleeFn?.symbol
                  if (sym is FirFunctionSymbol<*> && sym.callableId.asSingleFqName() == resolveFn) {
                    val lambda = expression.argument as? FirLambdaArgumentExpression
                    val anonFun = lambda?.expression as? FirAnonymousFunctionExpression
                    val returnEx = anonFun?.anonymousFunction?.body?.statements?.first() as FirReturnExpression
                    val targetFnCall = returnEx.result as FirFunctionCall
                    val targetRef = targetFnCall.calleeReference as? FirResolvedNamedReference
                    val targetFn = targetRef?.resolvedSymbol?.fir
                    val targetSymbol = targetFn?.symbol as FirFunctionSymbol<FirFunction>
                    val receiverType = targetSymbol.fir.receiverTypeRef?.coneType
                    // here in need to know the most specific `candidate` from `receiverType`
                    val candidates = proofs.find(receiverType)
                    if (proof == null) {
                      // if no candidate is found report it
                    }
                  }
                }
              }
            )
          )
        }
      ),
      irDumpKotlinLike()
    )
  }
