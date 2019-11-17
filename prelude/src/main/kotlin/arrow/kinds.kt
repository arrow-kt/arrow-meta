package arrowx

class Impossible

inline class Kinded(val value: Any?) :
  Kind22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing,
    Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

typealias Kind<F, A> =
  Kind2<F, A, Impossible>
typealias Kind2<F, A, B> =
  Kind3<F, A, B, Impossible>
typealias Kind3<F, A, B, C> =
  Kind4<F, A, B, C, Impossible>
typealias Kind4<F, A, B, C, D> =
  Kind5<F, A, B, C, D, Impossible>
typealias Kind5<F, A, B, C, D, E> =
  Kind6<F, A, B, C, D, E, Impossible>
typealias Kind6<F, A, B, C, D, E, G> =
  Kind7<F, A, B, C, D, E, G, Impossible>
typealias Kind7<F, A, B, C, D, E, G, H> =
  Kind8<F, A, B, C, D, E, G, H, Impossible>
typealias Kind8<F, A, B, C, D, E, G, H, I> =
  Kind9<F, A, B, C, D, E, G, H, I, Impossible>
typealias Kind9<F, A, B, C, D, E, G, H, I, J> =
  Kind10<F, A, B, C, D, E, G, H, I, J, Impossible>
typealias Kind10<F, A, B, C, D, E, G, H, I, J, K> =
  Kind11<F, A, B, C, D, E, G, H, I, J, K, Impossible>
typealias Kind11<F, A, B, C, D, E, G, H, I, J, K, L> =
  Kind12<F, A, B, C, D, E, G, H, I, J, K, L, Impossible>
typealias Kind12<F, A, B, C, D, E, G, H, I, J, K, L, M> =
  Kind13<F, A, B, C, D, E, G, H, I, J, K, L, M, Impossible>
typealias Kind13<F, A, B, C, D, E, G, H, I, J, K, L, M, N> =
  Kind14<F, A, B, C, D, E, G, H, I, J, K, L, M, N, Impossible>
typealias Kind14<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O> =
  Kind15<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, Impossible>
typealias Kind15<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P> =
  Kind16<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Impossible>
typealias Kind16<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q> =
  Kind17<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, Impossible>
typealias Kind17<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R> =
  Kind18<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, Impossible>
typealias Kind18<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S> =
  Kind19<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, Impossible>
typealias Kind19<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> =
  Kind20<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Impossible>
typealias Kind20<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> =
  Kind21<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Impossible>
typealias Kind21<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> =
  Kind22<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, Impossible>
interface Kind22<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J,
  out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V, out W>

class `Id(_)`
class Id<out A>(val value: A)

@arrow.proof(conversion = true)
fun <A> Kind<`Id(_)`, A>.fix(): Id<A> =
  (this as Kinded).value as Id<A>

@arrow.proof(conversion = true)
fun <A> Id<A>.unfix(): Kind<`Id(_)`, A> =
  Kinded(this)

class `List(_)`

@arrow.proof(conversion = true)
fun <A> Kind<`List(_)`, A>.fix(): List<A> =
  (this as Kinded).value as List<A>

@arrow.proof(conversion = true)
fun <A> List<A>.unfix(): Kind<`List(_)`, A> =
  Kinded(this)