package arrow.union

import arrow.Proof
import arrow.TypeProof


/**
 * A : Union<A, B, ...> for all A, B, ...
 */
@Proof(TypeProof.Subtyping)
fun <A> A.first(): First<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.second(): Second<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.third(): Third<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.fourth(): Fourth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.fifth(): Fifth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.sixth(): Sixth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.seventh(): Seventh<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.eighth(): Eighth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.ninth(): Ninth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.tenth(): Tenth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.eleventh(): Eleventh<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.twelfth(): Twelfth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.thirteenth(): Thirteenth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.fourteenth(): Fourteenth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.fifteenth(): Fifteenth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.sixteenth(): Sixteenth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.seventeenth(): Seventeenth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.eighteenth(): Eighteenth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.nineteenth(): Nineteenth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.twentieth(): Twentieth<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.twentyFirst(): TwentyFirst<A> =
  Union(this)

@Proof(TypeProof.Subtyping)
fun <A> A.twentySecond(): TwentySecond<A> =
  Union(this)