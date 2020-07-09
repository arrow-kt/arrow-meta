package arrow.meta.plugins.patternMatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.plugins.patternMatching.phases.analysis.PatternExpression.Param.Captured
import arrow.meta.plugins.patternMatching.phases.analysis.PatternResolutionContext
import arrow.meta.plugins.patternMatching.phases.analysis.fillCapturedParameters
import arrow.meta.plugins.patternMatching.phases.analysis.patternExpressionResolution
import arrow.meta.plugins.patternMatching.phases.analysis.referPlaceholder
import arrow.meta.plugins.patternMatching.phases.analysis.resolvePatternExpression
import arrow.meta.plugins.patternMatching.phases.ir.patchIrWhen
import arrow.meta.plugins.patternMatching.phases.resolve.diagnostics.suppressUnresolvedReference
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrSymbolOwner
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrWhen
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.util.slicedMap.Slices

val Meta.patternMatching: CliPlugin
  get() =
    "Pattern Matching Plugin" {
      meta(
        enableIr(),
        analysis(
          doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
            null
          },
          analysisCompleted = { project, module, bindingTrace, files ->
            val context = PatternResolutionContext(this)

            val patternExpressions = files.flatMap { file ->
              context.resolvePatternExpression(file) { whenExpr ->
                patternExpressionResolution(whenExpr).map { (entry, expr) ->
                  expr.parameters.forEachIndexed { index, param ->
                    if (param is Captured) {
                      val nameExpression = expr.paramExpression(index) as KtSimpleNameExpression
                      bindingTrace.record(PATTERN_EXPRESSION_CAPTURED_PARAMS, nameExpression)
                      referPlaceholder(nameExpression, index)
                    }
                  }

                  if (expr.parameters.any { it is Captured && !it.isWildcard }) {
                    val params = fillCapturedParameters(entry, expr)
                    params.forEach {
                      bindingTrace.record(PATTERN_EXPRESSION_BODY_PARAMS, it)
                    }
                  }

                  expr
                }
              }
            }
            println("Resolved pattern expressions $patternExpressions")

            null
          }
        ),
        suppressDiagnostic { ctx.suppressUnresolvedReference(it) },
        IrGeneration { compilerContext, file, backendContext, bindingContext ->
          file.accept(
            object : IrElementTransformer<IrSymbolOwner?> {
              private val irUtils = IrUtils(backendContext, compilerContext)
              override fun visitDeclaration(declaration: IrDeclaration, data: IrSymbolOwner?): IrStatement =
                if (declaration is IrSymbolOwner) {
                  super.visitDeclaration(declaration, declaration)
                } else {
                  super.visitDeclaration(declaration, data)
                }

              override fun visitWhen(expression: IrWhen, data: IrSymbolOwner?): IrExpression {
                return super.visitWhen(expression, data).also {
                  val builder = DeclarationIrBuilder(
                    backendContext,
                    data!!.symbol,
                    expression.startOffset,
                    expression.endOffset
                  )
                  irUtils.patchIrWhen(expression, builder)
                }
              }
            },
            null
          )
        },
        irDump()
      )
    }

val PATTERN_EXPRESSION_CAPTURED_PARAMS = Slices.createCollectiveSetSlice<KtSimpleNameExpression>()
val PATTERN_EXPRESSION_BODY_PARAMS = Slices.createCollectiveSetSlice<KtSimpleNameExpression>()
