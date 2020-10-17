package arrow.with.n7

@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER
)
annotation class with7<out A, out B, out C, out D, out E, out F, out G>