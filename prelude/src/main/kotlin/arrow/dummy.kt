package arrow

@proof(conversion = true)
fun String.safeToInt(): Int? =
  try {
    toInt()
  } catch (e: NumberFormatException) {
    null
  }