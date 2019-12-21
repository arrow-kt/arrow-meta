package arrow

import arrow.Proof
import arrow.TypeProof

/**
 * For All Union<A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> FirstN<A>.firstN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> SecondN<A>.secondN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> ThirdN<A>.thirdN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> FourthN<A>.fourthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> FifthN<A>.fifthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> SixthN<A>.sixthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> SeventhN<A>.seventhN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> EighthN<A>.eighthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> NinthN<A>.ninthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> TenthN<A>.tenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> EleventhN<A>.eleventhN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> TwelfthN<A>.twelfthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> ThirteenthN<A>.thirteenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> FourteenthN<A>.fourteenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> FifteenthN<A>.fifteenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> SixteenthN<A>.sixteenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> SeventeenthN<A>.seventeenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> EighteenthN<A>.eighteenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> NineteenthN<A>.nineteenthN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> TwentiethN<A>.twentiethN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> TwentyFirstN<A>.twentyFirstN(): A? =
  (this as Union).value as A

/**
 * For All Union<..A,..> : A?
 */
@Proof(TypeProof.Subtyping)
inline fun <reified A> TwentySecondN<A>.twentySecondN(): A? =
  (this as Union).value as A