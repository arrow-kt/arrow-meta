package arrow.meta.plugins.proofs

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.plugins.proofs.phases.config.enableProofCallResolver
import arrow.meta.plugins.proofs.phases.ir.ProofsIrCodegen
import arrow.meta.plugins.proofs.phases.quotes.generateGivenExtensionsFile
import arrow.meta.plugins.proofs.phases.resolve.ProofTypeChecker
import arrow.meta.plugins.proofs.phases.resolve.proofsPackageFragmentProvider
import arrow.meta.plugins.proofs.phases.resolve.scopes.registerProofSyntheticScope
import arrow.meta.plugins.proofs.phases.resolve.registerArgumentTypeResolver
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressConstantExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressProvenTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressTypeInferenceExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressUpperboundViolated

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs" {
      meta(
        enableIr(),
        enableProofCallResolver(),
        generateGivenExtensionsFile(this@typeProofs, this),
        proofsPackageFragmentProvider(),
        registerProofSyntheticScope(),
        registerArgumentTypeResolver(),
        suppressDiagnostic { ctx.suppressProvenTypeMismatch(it, module.proofs) },
        suppressDiagnostic { ctx.suppressConstantExpectedTypeMismatch(it, module.proofs) },
        suppressDiagnostic { ctx.suppressTypeInferenceExpectedTypeMismatch(it, module.proofs) },
        suppressDiagnostic { ctx.suppressUpperboundViolated(it, module.proofs) },
        typeChecker { ProofTypeChecker(ctx) },
        irCall { ProofsIrCodegen(this) { proveNestedCalls(module.proofs, it) } },
        irProperty { ProofsIrCodegen(this) { proveProperty(module.proofs, it) } },
        irVariable { ProofsIrCodegen(this) { proveVariable(module.proofs, it) } },
        irReturn { ProofsIrCodegen(this) { proveReturn(module.proofs, it) } },
        irDump()
      )
    }

