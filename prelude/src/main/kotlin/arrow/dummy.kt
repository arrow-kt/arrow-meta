package arrow

@proof(implicitConversion = true)
fun String.safeToInt(): Int? =
  try {
    toInt()
  } catch (e: NumberFormatException) {
    null
  }