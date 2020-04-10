package arrowx

import arrow.Proof
import arrow.TypeProof

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE)
@MustBeDocumented
annotation class given

object Env

@Proof(TypeProof.Given)
fun defaultString() : String = ""