package arrow.meta.plugins.proofs

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.plugins.proofs.phases.config.enableProofCallResolver
import arrow.meta.plugins.proofs.phases.ir.ProofsIrCodegen
import arrow.meta.plugins.proofs.phases.quotes.generateGivenExtensionsFile
import arrow.meta.plugins.proofs.phases.quotes.isRefined
import arrow.meta.plugins.proofs.phases.quotes.objectWithSerializedRefinement
import arrow.meta.plugins.proofs.phases.resolve.proofResolutionRules
import arrow.meta.quotes.objectDeclaration

val Meta.typeProofs: CliPlugin
  get() =
    "Type Proofs CLI" {
      meta(
        enableIr(),
        enableProofCallResolver(),
        objectDeclaration(this, { element.isRefined() }) { objectWithSerializedRefinement(scope, ctx) },
        proofResolutionRules(),
        generateGivenExtensionsFile(this@typeProofs),
        irCall { ProofsIrCodegen(this) { proveNestedCalls(it) } },
        irDumpKotlinLike()
      )
    }
