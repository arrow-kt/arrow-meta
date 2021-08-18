package arrow.refinement.laws.kotlin.math

import arrow.refinement.Law
import arrow.refinement.post
import arrow.refinement.pre
import kotlin.math.nextDown
import kotlin.math.nextTowards
import kotlin.math.nextUp

@Law
@JvmName("nextDownLawFloat")
public inline fun Float.nextDownLaw(): Float  {
  pre(true) { "kotlin.math.nextDown pre-conditions" }
  return nextDown()
    .post({ true }, { "kotlin.math.nextDown post-conditions" })
}

@Law
@JvmName("nextTowardsLawFloatFloat")
public inline fun Float.nextTowardsLaw(to: Float): Float  {
  pre(true) { "kotlin.math.nextTowards pre-conditions" }
  return nextTowards(to)
    .post({ true }, { "kotlin.math.nextTowards post-conditions" })
}


@Law
@JvmName("nextUpLawFloat")
public inline fun Float.nextUpLaw(): Float  {
  pre(true) { "kotlin.math.nextUp pre-conditions" }
  return nextUp()
    .post({ true }, { "kotlin.math.nextUp post-conditions" })
}