package arrow.union

import arrow.Proof
import arrow.TypeProof


/**
 * For All A : Union<A,...>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.first(): First<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.second(): Second<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.third(): Third<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.fourth(): Fourth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.fifth(): Fifth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.sixth(): Sixth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.seventh(): Seventh<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.eighth(): Eighth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.ninth(): Ninth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.tenth(): Tenth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.eleventh(): Eleventh<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.twelfth(): Twelfth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.thirteenth(): Thirteenth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.fourteenth(): Fourteenth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.fifteenth(): Fifteenth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.sixteenth(): Sixteenth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.seventeenth(): Seventeenth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.eighteenth(): Eighteenth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.nineteenth(): Nineteenth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.twentieth(): Twentieth<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.twentyFirst(): TwentyFirst<A> =
  Union(this)

/**
 * For All A : Union<.,A,.>
 */
@Proof(TypeProof.Subtyping)
fun <A> A.twentySecond(): TwentySecond<A> =
  Union(this)