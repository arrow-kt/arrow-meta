package arrow

import arrow.TypeProof.*
import kotlin.contracts.contract

inline class PositiveInt(val value: Int)

/**
 * Here we are saying that Int extends PositiveInt?
 *
 * val x: PositiveInt = 0 //ok verified at compile time
 * val y: PositiveInt = -1 //fails to compile
 * val z: PositiveInt? = n //runtime values auto coherces to null
 * val
 */
@Proof(Subtyping)
fun Int.toPositiveInt(): PositiveInt? =
  if (this >= 0) PositiveInt(this) else null

@Proof(Subtyping)
fun PositiveInt.toPositiveInt(): Int =
  value

@Proof(Subtyping)
fun PositiveInt?.toPositiveInt(): Int? =
  this?.value