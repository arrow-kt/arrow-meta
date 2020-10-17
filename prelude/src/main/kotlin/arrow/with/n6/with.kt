package arrow.with.n6

@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER
)
annotation class with6<out A, out B, out C, out D, out E, out F>