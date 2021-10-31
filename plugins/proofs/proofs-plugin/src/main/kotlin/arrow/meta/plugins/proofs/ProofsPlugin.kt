package arrow.meta.plugins.proofs

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.plugins.proofs.phases.config.enableProofCallResolver
import arrow.meta.plugins.proofs.phases.ir.ProofsIrCodegen
import arrow.meta.plugins.proofs.phases.ir.removeCompileTimeDeclarations
import arrow.meta.plugins.proofs.phases.quotes.generateGivenPreludeFile
import arrow.meta.plugins.proofs.phases.resolve.proofResolutionRules

val Meta.typeProofs: CliPlugin
  get() = "Type Proofs CLI" {
    meta(
      enableIr(),
      enableProofCallResolver(),
      proofResolutionRules(),
      generateGivenPreludeFile(),
      irCall { ProofsIrCodegen(this) { proveNestedCalls(it) } },
      removeCompileTimeDeclarations(),
      irDumpKotlinLike()
    )
  }
