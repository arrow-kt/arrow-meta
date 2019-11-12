package arrow

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.EXPRESSION, AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS,
  AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.LOCAL_VARIABLE,
  AnnotationTarget.FUNCTION, AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.TYPE)
internal annotation class synthetic
