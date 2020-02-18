package arrow.meta.plugins.proofs

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.analysis.bodySourceAsExpression
import arrow.meta.plugins.proofs.phases.config.enableProofCallResolver
import arrow.meta.plugins.proofs.phases.ir.ProofsIrCodegen
import arrow.meta.plugins.proofs.phases.quotes.generateGivenExtensionsFile
import arrow.meta.plugins.proofs.phases.resolve.ProofTypeChecker
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressConstantExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressProvenTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressTypeInferenceExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.refinementExpressionFor
import arrow.meta.plugins.proofs.phases.resolve.scopes.provenSyntheticScope
import arrow.meta.quotes.Transform
import arrow.meta.quotes.objectDeclaration
import arrow.meta.quotes.orEmpty
import org.jetbrains.kotlin.cli.common.CLICompiler
import org.jetbrains.kotlin.cli.common.extensions.ScriptEvaluationExtension
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.resolve.constants.evaluate.ConstantExpressionEvaluator
import javax.script.ScriptEngineFactory
import javax.script.ScriptEngineManager

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs CLI" {
      meta(
        enableIr(),
        enableProofCallResolver(),
        typeChecker { ProofTypeChecker(ctx) },
        provenSyntheticScope(),
        generateGivenExtensionsFile(this@typeProofs),
        suppressDiagnostic { ctx.suppressProvenTypeMismatch(it) },
        suppressDiagnostic { ctx.suppressConstantExpectedTypeMismatch(it) },
        suppressDiagnostic { ctx.suppressTypeInferenceExpectedTypeMismatch(it) },
        objectDeclaration({
          isCompanion() && superTypeListEntries.any { it.text.matches("Refined<(.*?)>".toRegex()) }
        }) {
          val predicateAsExpression = body.properties.value.find { it.name == "validate" }?.delegateExpressionOrInitializer?.text
          if (predicateAsExpression == null)  Transform.empty
          else Transform.replace(
            value,
            "@arrow.Refinement(\"\"\"\n$predicateAsExpression\n\"\"\") $this".`object`
          )
        },
        irConstructorCall { call ->
          Log.Verbose({ "irConstructorCall: ${call.dump()}" }) {
            val maybeConstant = call.getValueArgument(0) as? IrConst<*>
            if (maybeConstant != null) {
              val targetType = call.descriptor.returnType
              val currentModule = module
              if (currentModule != null) {
                val refinementSource = module.proofs.refinementExpressionFor(targetType)
                val refinementExpression = refinementSource?.expression.orEmpty().value
                if (refinementExpression != null) {
                  val engine = ScriptEngineManager().getEngineByExtension("kts")
                  val source: String? =
                    when (maybeConstant.value) {
                      is String -> """"${maybeConstant.value}""""
                      else -> null
                    }
                  if (source != null) {
                    val constantChecker = """
                      ${source}.run ${refinementExpression.text}
                    """.trimIndent()
                    val expressionResult = engine.eval(constantChecker) as? Map<Any?, Any?>
                    if (expressionResult != null) {
                      val validationKeys = expressionResult.keys.filterIsInstance<String>()
                      val validation = validationKeys.map {
                        it to expressionResult[it] as Boolean
                      }.toMap()
                      val isValid = validation.all { it.value }
                      if (!isValid) {
                        validation.forEach { (msg, valid) ->
                          if (!valid) {
                            messageCollector?.report(CompilerMessageSeverity.ERROR, "Predicate for $targetType(`$source`) failed: \n$msg")
                          }
                        }
                      }
                    }
                  }

                }
              }
            }
          }
          call
        },
        irTypeOperator { ProofsIrCodegen(this) { proveTypeOperator(it) } },
        irCall { ProofsIrCodegen(this) { proveNestedCalls(it) } },
        irProperty { ProofsIrCodegen(this) { proveProperty(it) } },
        irVariable { ProofsIrCodegen(this) { proveVariable(it) } },
        irReturn { ProofsIrCodegen(this) { proveReturn(it) } },
        irDump()
      )
    }

