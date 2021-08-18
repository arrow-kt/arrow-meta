package arrow.refinement.tests

import arrow.refinement.network.PrivateNetwork
import arrow.refinement.network.Rfc1918ClassAPrivateNetwork
import arrow.refinement.network.Rfc1918ClassBPrivateNetwork
import arrow.refinement.network.Rfc1918ClassCPrivateNetwork
import arrow.refinement.network.Rfc1918PrivateNetwork
import arrow.refinement.network.Rfc2544BenchmarkNetwork
import arrow.refinement.network.Rfc3927LocalLinkNetwork
import arrow.refinement.network.Rfc5737Testnet1Network
import arrow.refinement.network.Rfc5737Testnet2Network
import arrow.refinement.network.Rfc5737Testnet3Network
import arrow.refinement.network.Rfc5737TestnetNetwork
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string

class NetworkTests :
  RefinedLaws<String>(
    Arb.string(),
    PrivateNetwork,
    Rfc1918ClassAPrivateNetwork,
    Rfc1918ClassBPrivateNetwork,
    Rfc1918ClassCPrivateNetwork,
    Rfc1918PrivateNetwork,
    Rfc2544BenchmarkNetwork,
    Rfc3927LocalLinkNetwork,
    Rfc5737Testnet1Network,
    Rfc5737Testnet2Network,
    Rfc5737Testnet3Network,
    Rfc5737TestnetNetwork
  )

