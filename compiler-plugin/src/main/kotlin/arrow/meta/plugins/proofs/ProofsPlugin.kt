package arrow.meta.plugins.proofs

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.plugins.proofs.phases.config.enableProofCallResolver
import arrow.meta.plugins.proofs.phases.ir.ProofsIrCodegen
import arrow.meta.plugins.proofs.phases.quotes.generateGivenExtensionsFile
import arrow.meta.plugins.proofs.phases.resolve.ProofTypeChecker
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressConstantExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressProvenTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressTypeInferenceExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.proofsPackageFragmentProvider
import arrow.meta.plugins.proofs.phases.resolve.registerArgumentTypeResolver
import arrow.meta.plugins.proofs.phases.resolve.scopes.registerProofSyntheticScope

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs CLI" {
      meta(
        enableIr(),
        enableProofCallResolver(),
        registerArgumentTypeResolver(),
        typeChecker { ProofTypeChecker(ctx) },
        registerProofSyntheticScope(),
        generateGivenExtensionsFile(this@typeProofs, this),
        suppressDiagnostic { ctx.suppressProvenTypeMismatch(it, module.proofs) },
        suppressDiagnostic { ctx.suppressConstantExpectedTypeMismatch(it, module.proofs) },
        suppressDiagnostic { ctx.suppressTypeInferenceExpectedTypeMismatch(it, module.proofs) },
        irTypeOperator { ProofsIrCodegen(this) { proveTypeOperator(it) } },
        irCall { ProofsIrCodegen(this) { proveNestedCalls(it) } },
        irProperty { ProofsIrCodegen(this) { proveProperty(it) } },
        irVariable { ProofsIrCodegen(this) { proveVariable(it) } },
        irReturn { ProofsIrCodegen(this) { proveReturn(it) } },
        irDump()
      )
    }

