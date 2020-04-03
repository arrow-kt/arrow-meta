package arrow

/**
 * If A : C and B : C then Union2<A, B> : C
 */
@Proof(TypeProof.Extension, coerce = true)
inline fun <A : C, B : C, reified C> Union2<A, B>.widden(): C =
  value as C

@kotlin.jvm.JvmName("or1")
infix fun <A, B> A?.or(b: B): Union2<A, B> =
  if (this == null) Union(b) else Union(this)