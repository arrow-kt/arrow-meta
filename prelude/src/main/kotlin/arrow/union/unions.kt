package arrow.union

import arrow.`🚫`

/**
 * A single interface the highest arity represents the most generic type of the Union
 */
interface Union22<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V> {
  val value: Any?
}

/**
 *
 *                    -'"".
 *                   \  |
 *                      :
 *                    |  \         ___
 * ASCII Skateboarding:  '._      /   '.
 * Big Ollie           \  / '._  /'    |
 *                      ''     '-.>   /
 * Lauri Kangas 11/99     \       '--.\_
 *                   .'"'. |            '.
 *                  /    /\|              \
 *                 /    /  '.    ___   _   \_
 *         .-._   /  _.'     '._/   \ | '__  '._ __
 *         |    \.  / .        |    | /    "'._ " _|
 *         '    ._.'   '.     /    / ;         ""'
 *          \     |      \   /    :  |
 *   .-..___ \__.'      __'/_   .' .'
 *  (_)     ""--..__   /  \  '.'..-'
 *   \\             "":.__ \ /lka
 *    \\            (_)   '''._
 *    (_)'-..___     \\        .
 *              ""-.__\\       |
 *                    (_).____.'
 */
typealias Union2<A, B> = Union3<A, B, `🚫`>
typealias Union3<A, B, C> = Union4<A, B, C, `🚫`>
typealias Union4<A, B, C, D> = Union5<A, B, C, D, `🚫`>
typealias Union5<A, B, C, D, E> = Union6<A, B, C, D, E, `🚫`>
typealias Union6<A, B, C, D, E, F> = Union7<A, B, C, D, E, F, `🚫`>
typealias Union7<A, B, C, D, E, F, G> = Union8<A, B, C, D, E, F, G, `🚫`>
typealias Union8<A, B, C, D, E, F, G, H> = Union9<A, B, C, D, E, F, G, H, `🚫`>
typealias Union9<A, B, C, D, E, F, G, H, I> = Union10<A, B, C, D, E, F, G, H, I, `🚫`>
typealias Union10<A, B, C, D, E, F, G, H, I, J> = Union11<A, B, C, D, E, F, G, H, I, J, `🚫`>
typealias Union11<A, B, C, D, E, F, G, H, I, J, K> = Union12<A, B, C, D, E, F, G, H, I, J, K, `🚫`>
typealias Union12<A, B, C, D, E, F, G, H, I, J, K, L> = Union13<A, B, C, D, E, F, G, H, I, J, K, L, `🚫`>
typealias Union13<A, B, C, D, E, F, G, H, I, J, K, L, M> = Union14<A, B, C, D, E, F, G, H, I, J, K, L, M, `🚫`>
typealias Union14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> = Union15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, `🚫`>
typealias Union15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> = Union16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, `🚫`>
typealias Union16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> = Union17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, `🚫`>
typealias Union17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> = Union18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, `🚫`>
typealias Union18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> = Union19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, `🚫`>
typealias Union19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> = Union20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, `🚫`>
typealias Union20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> = Union21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, `🚫`>
typealias Union21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> = Union22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, `🚫`>

/**
 * The Union runtime proof is inlined and erasable
 */
inline class Union(override val value: Any?) : First<Nothing>

/**
 * An easy encoding for arity positions
 */
typealias First<A> = Union22<A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Second<A> = Union22<Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Third<A> = Union22<Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Fourth<A> = Union22<Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Fifth<A> = Union22<Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Sixth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Seventh<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Eighth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Ninth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Tenth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Eleventh<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Twelfth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Thirteenth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Fourteenth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Fifteenth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Sixteenth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Seventeenth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing, Nothing>
typealias Eighteenth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing, Nothing>
typealias Nineteenth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing, Nothing>
typealias Twentieth<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing, Nothing>
typealias TwentyFirst<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A, Nothing>
typealias TwentySecond<A> = Union22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, A>
