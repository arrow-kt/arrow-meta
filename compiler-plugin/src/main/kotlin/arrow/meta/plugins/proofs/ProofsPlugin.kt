package arrow.meta.plugins.proofs

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.phases.analysis.bodySourceAsExpression
import arrow.meta.plugins.proofs.phases.config.enableProofCallResolver
import arrow.meta.plugins.proofs.phases.ir.ProofsIrCodegen
import arrow.meta.plugins.proofs.phases.ir.validateConstructorCall
import arrow.meta.plugins.proofs.phases.ir.validateRefinementExpression
import arrow.meta.plugins.proofs.phases.quotes.generateGivenExtensionsFile
import arrow.meta.plugins.proofs.phases.quotes.isRefined
import arrow.meta.plugins.proofs.phases.quotes.objectWithSerializedRefinement
import arrow.meta.plugins.proofs.phases.resolve.ProofTypeChecker
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressConstantExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressProvenTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressTypeInferenceExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.refinementExpressionFor
import arrow.meta.plugins.proofs.phases.resolve.scopes.provenSyntheticScope
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classorobject.ObjectDeclaration
import arrow.meta.quotes.objectDeclaration
import arrow.meta.quotes.orEmpty
import org.jetbrains.kotlin.cli.common.CLICompiler
import org.jetbrains.kotlin.cli.common.extensions.ScriptEvaluationExtension
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.resolve.constants.evaluate.ConstantExpressionEvaluator
import org.jetbrains.kotlin.types.KotlinType
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
        objectDeclaration(KtObjectDeclaration::isRefined) { objectWithSerializedRefinement(ctx) },
        irConstructorCall { validateConstructorCall(it); it },
        irTypeOperator { ProofsIrCodegen(this) { proveTypeOperator(it) } },
        irCall { ProofsIrCodegen(this) { proveNestedCalls(it) } },
        irProperty { ProofsIrCodegen(this) { proveProperty(it) } },
        irVariable { ProofsIrCodegen(this) { proveVariable(it) } },
        irReturn { ProofsIrCodegen(this) { proveReturn(it) } },
        irDump()
      )
    }
