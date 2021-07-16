package arrow.refinement

import arrow.core.*

fun <V,R> Refined<V,R>.toEither(value:V): Either<String, R> =
    this.fold(value,
        { Left(renderMessages(it))},
        { Right(f(value)) }
    )

fun <V,R> Refined<V,R>.toValidated(value:V): Validated<String, R> =
    this.fold(value,
        { Invalid(renderMessages(it))},
        { Valid(f(value)) }
    )