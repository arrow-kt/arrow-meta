package arrow.refinement.digests

import arrow.refinement.Refined
import arrow.refinement.and
import arrow.refinement.strings.HexString
import arrow.refinement.strings.Size

@JvmInline
value class MD5 private constructor(val value: String) {
  companion object : Refined<String, MD5>(::MD5, HexString and Size.N(32u))
}

@JvmInline
value class SHA1 private constructor(val value: String) {
  companion object : Refined<String, SHA1>(::SHA1, HexString and Size.N(40u))
}

@JvmInline
value class SHA224 private constructor(val value: String) {
  companion object : Refined<String, SHA224>(::SHA224, HexString and Size.N(56u))
}

@JvmInline
value class SHA256 private constructor(val value: String) {
  companion object : Refined<String, SHA256>(::SHA256, HexString and Size.N(64u))
}

@JvmInline
value class SHA384 private constructor(val value: String) {
  companion object : Refined<String, SHA384>(::SHA384, HexString and Size.N(96u))
}

@JvmInline
value class SHA512 private constructor(val value: String) {
  companion object : Refined<String, SHA512>(::SHA512, HexString and Size.N(128u))
}
