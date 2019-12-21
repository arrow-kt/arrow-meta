package arrow

import arrow.Proof
import arrow.TypeProof

/**
 * For All Union<A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> First<A>.firstN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Second<A>.secondN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Third<A>.thirdN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Fourth<A>.fourthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Fifth<A>.fifthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Sixth<A>.sixthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Seventh<A>.seventhN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Eighth<A>.eighthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Ninth<A>.ninthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Tenth<A>.tenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Eleventh<A>.eleventhN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Twelfth<A>.twelfthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Thirteenth<A>.thirteenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Fourteenth<A>.fourteenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Fifteenth<A>.fifteenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Sixteenth<A>.sixteenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Seventeenth<A>.seventeenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Eighteenth<A>.eighteenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Nineteenth<A>.nineteenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> Twentieth<A>.twentiethN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> TwentyFirst<A>.twentyFirstN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> TwentySecond<A>.twentySecondN(): A? =
  (this as Union).value as A