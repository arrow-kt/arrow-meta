package arrow.refinement.tests

import arrow.refinement.collections.Contains
import arrow.refinement.collections.Count
import arrow.refinement.collections.Empty
import arrow.refinement.collections.First
import arrow.refinement.collections.MaxSize
import arrow.refinement.collections.MinSize
import arrow.refinement.collections.NotEmpty
import arrow.refinement.collections.Tail
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list

class CollectionTests :
  RefinedLaws<Iterable<*>>(
    Arb.list(Arb.int()),
    Contains.Element(0),
    Count.N(0u),
    Empty,
    First.Element(0),
    MaxSize.N(0u),
    MinSize.N(0u),
    NotEmpty,
    Tail.Elements(emptyList())
  )
