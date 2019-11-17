package arrow

inline class PositiveInt(val value: Int)

@proof(conversion = true)
fun Int.toPositiveInt(): PositiveInt? =
  if (this >= 0) PositiveInt(this) else null

@proof(conversion = true)
fun PositiveInt.toPositiveInt(): Int =
  value

@proof(conversion = true)
fun PositiveInt?.toPositiveInt(): Int? =
  this?.value