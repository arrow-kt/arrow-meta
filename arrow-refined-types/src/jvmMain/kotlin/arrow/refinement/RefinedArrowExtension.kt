package arrow.refinement

import arrow.core.Either
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.invalidNel

fun <V,R> Refined<V,R>.toEither(value:V): Either<String, R> =
    this.fold(value,
        { Either.Left(renderMessages(it))},
        { Either.Right(f(value)) }
    )

fun <V,R> Refined<V,R>.toValidated(value:V): Validated<String, R> =
    this.fold(value,
        { Validated.Invalid(renderMessages(it))},
        { Validated.Valid(f(value)) }
    )

fun <V,R> Refined<V,R>.toValidatedNel(value:V): ValidatedNel<String, R> =
    this.fold(value,
        { renderMessages(it).invalidNel() },
        { Validated.Valid(f(value)) }
    )
