package arrow.refinement


@JvmInline
value class PositiveInt private constructor(val value: Int) {
  companion object : Refined<Int, PositiveInt>(::PositiveInt, {
    ensure((it > 0) to "$it should be > 0")
  })
}

@JvmInline
value class Even private constructor(val value: Int) {
  companion object : Refined<Int, Even>(::Even, {
    ensure((it % 2 == 0) to "$it should be even")
  })
}

@JvmInline
value class ContentType private constructor(val value: String) {
  companion object : Refined<String, ContentType>(::ContentType, {
    ensure((it.equals("content-type", ignoreCase = true)) to "$it should be named Content-Type")
  })
}