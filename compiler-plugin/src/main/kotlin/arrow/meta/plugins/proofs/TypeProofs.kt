package arrow.meta.plugins.proofs

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.plugins.proofs.phases.quotes.generateGivenExtensionsFile
import arrow.meta.plugins.proofs.phases.resolve.ProofTypeChecker
import arrow.meta.plugins.proofs.phases.resolve.proofsPackageFragmentProvider
import arrow.meta.plugins.proofs.phases.resolve.registerProofSyntheticScope
import arrow.meta.plugins.proofs.phases.resolve.registerProofTypeChecker
import arrow.meta.plugins.proofs.phases.resolve.suppressConstantExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.suppressProvenTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.suppressTypeInferenceExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.suppressUpperboundViolated

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs" {
      meta(
        enableIr(),
        generateGivenExtensionsFile(this@typeProofs, this),
        proofsPackageFragmentProvider(),
        registerProofSyntheticScope(),
        registerProofTypeChecker(),
        suppressDiagnostic { ctx.suppressProvenTypeMismatch(it, module.proofs) },
        suppressDiagnostic { ctx.suppressConstantExpectedTypeMismatch(it, module.proofs) },
        suppressDiagnostic { ctx.suppressTypeInferenceExpectedTypeMismatch(it, module.proofs) },
        suppressDiagnostic { ctx.suppressUpperboundViolated(it, module.proofs) },
        typeChecker { ProofTypeChecker(ctx) },
        irCall { proveNestedCalls(module.proofs, it) },
        irProperty { proveProperty(module.proofs, it) },
        irVariable { proveVariable(module.proofs, it) },
        irReturn { proveReturn(module.proofs, it) },
        irDump()
      )
    }
