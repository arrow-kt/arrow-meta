package arrow.refinement.tests

import arrow.refinement.digests.MD5
import arrow.refinement.digests.SHA1
import arrow.refinement.digests.SHA224
import arrow.refinement.digests.SHA256
import arrow.refinement.digests.SHA384
import arrow.refinement.digests.SHA512
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string

class DigestsTests :
  RefinedLaws<String>(
    Arb.string(),
    listOf(
      "MD5" to MD5,
      "SHA1" to SHA1,
      "SHA224" to SHA224,
      "SHA256" to SHA256,
      "SHA384" to SHA384,
      "SHA512" to SHA512
    )
  )
