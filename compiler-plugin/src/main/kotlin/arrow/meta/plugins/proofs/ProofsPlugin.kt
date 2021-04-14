package arrow.meta.plugins.proofs

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.plugins.proofs.phases.config.enableProofCallResolver
import arrow.meta.plugins.proofs.phases.ir.ProofsIrCodegen
import arrow.meta.plugins.proofs.phases.quotes.generateGivenExtensionsFile
import arrow.meta.plugins.proofs.phases.quotes.isRefined
import arrow.meta.plugins.proofs.phases.quotes.objectWithSerializedRefinement
import arrow.meta.plugins.proofs.phases.resolve.ProofTypeChecker
import arrow.meta.plugins.proofs.phases.resolve.cliValidateRefinedCalls
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressConstantExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressProvenTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressTypeInferenceExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.proofResolutionRules
import arrow.meta.plugins.proofs.phases.resolve.scopes.provenSyntheticScope
import arrow.meta.quotes.objectDeclaration
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.psi.KtObjectDeclaration

@ObsoleteDescriptorBasedAPI
val Meta.typeProofs: CliPlugin
  get() =
    "Type Proofs CLI" {
      meta(
        enableIr(),
        enableProofCallResolver(),
        typeChecker { ProofTypeChecker(ctx) },
        provenSyntheticScope(),
        objectDeclaration(this, KtObjectDeclaration::isRefined) { objectWithSerializedRefinement(scope, ctx) },
        cliValidateRefinedCalls(),
        proofResolutionRules(),
        generateGivenExtensionsFile(this@typeProofs),
        suppressDiagnostic { ctx.suppressProvenTypeMismatch(it) },
        suppressDiagnostic { ctx.suppressConstantExpectedTypeMismatch(it) },
        suppressDiagnostic { ctx.suppressTypeInferenceExpectedTypeMismatch(it) },
        irTypeOperator { ProofsIrCodegen(this) { proveTypeOperator(it) } },
        irCall { ProofsIrCodegen(this) { proveNestedCalls(it) } },
        irProperty { ProofsIrCodegen(this) { proveProperty(it) } },
        irVariable { ProofsIrCodegen(this) { proveVariable(it) } },
        irReturn { ProofsIrCodegen(this) { proveReturn(it) } },
        irDump()
      )
    }
