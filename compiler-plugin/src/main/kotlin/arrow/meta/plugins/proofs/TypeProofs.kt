package arrow.meta.plugins.proofs

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.proofs.Proof
import arrow.meta.proofs.ProofTypeChecker
import arrow.meta.proofs.suppressProvenTypeMismatch

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs" {
      val proofs: MutableList<Proof> = arrayListOf()
      meta(
        enableIr(),
        proofs.initializeProofs(),
        suppressDiagnostic { it.suppressProvenTypeMismatch(proofs) },
        typeChecker { ProofTypeChecker(proofs) },
        irVariable { insertProof(proofs, it) },
        irProperty { insertProof(proofs, it) },
        irReturn { insertProof(proofs, it) },
        irDump()
      )
    }



















