package arrow.with.n4

@Target(
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER
)
annotation class with<out A, out B, out C, out D>