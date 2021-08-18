package arrow.refinement.tests

import arrow.refinement.network.URI
import arrow.refinement.network.URL
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string

class JvmStringsTests :
  RefinedLaws<String>(
    Arb.string(),
    URI,
    URL
  )

