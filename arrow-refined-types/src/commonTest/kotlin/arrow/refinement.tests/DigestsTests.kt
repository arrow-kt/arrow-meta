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
    MD5,
    SHA1,
    SHA224,
    SHA256,
    SHA384,
    SHA512
  )

