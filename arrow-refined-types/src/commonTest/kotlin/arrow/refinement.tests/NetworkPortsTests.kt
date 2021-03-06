package arrow.refinement.tests

import arrow.refinement.network.DynamicPortNumber
import arrow.refinement.network.NonSystemPortNumber
import arrow.refinement.network.PortNumber
import arrow.refinement.network.SystemPortNumber
import arrow.refinement.network.UserPortNumber
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

class NetworkPortsTests :
  RefinedLaws<Int>(
    Arb.int(),
    DynamicPortNumber,
    NonSystemPortNumber,
    PortNumber,
    SystemPortNumber,
    UserPortNumber
  )

