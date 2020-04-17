package arrow.meta.ide.plugins.proofs.folding

object FoldingBuilderTestCode {
  val unionCode =
    """
    package arrow
    
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
    interface Union22<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V> { val value: Any? }
    inline class Union(override val value: Any?) : Union22<`🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`>
    
    fun f(): Union2<String, Union2<Int, Double>> = 2
    fun g(): Union3<String, Int, Double> = 2.0
    fun h(): Union4<String, Int, Double, Long> = 2L
    """.trimIndent()

  val tupleCode =
    """
    package arrow.tuples

    interface TupleN {
      val value: Array<Any?>
    }
    
    interface Tuple1<out A> : TupleN {
      operator fun component1(): A = value[0] as A
    }
    
    interface Tuple2<out A, out B> : Tuple1<A> {
      operator fun component2(): B = value[1] as B
    }
    
    interface Tuple3<out A, out B, out C> : Tuple2<A, B> {
      operator fun component3(): C = value[2] as C
    }
    
    interface Tuple4<out A, out B, out C, out D> : Tuple3<A, B, C> {
      operator fun component4(): D = value[3] as D
    }
    
    inline class tupleOf private constructor(override val value: Array<Any?>) : Tuple4<Nothing, Nothing, Nothing, Nothing> {
      companion object {
        operator fun <A, B> invoke(a: A, b: B): Tuple2<A, B> =
          tupleOf(arrayOf(a, b))
    
        operator fun <A, B, C> invoke(a: A, b: B, c: C): Tuple3<A, B, C> =
          tupleOf(arrayOf(a, b, c))
    
        operator fun <A, B, C, D> invoke(a: A, b: B, c: C, d: D): Tuple4<A, B, C, D> =
          tupleOf(arrayOf(a, b, c, d))
      }
    }

    val tuple2: Tuple2<Int, Int> = tupleOf(2, 2)
    val tuple3: Tuple3<Int, Int, String> = tupleOf(2, 2, "47Degrees")
    val tuple4: Tuple4<Int, Int, String, Double> = tupleOf(2, 2, "47Degrees", 5.0)
    """.trimIndent()

  val unionCode2 =
    """
    package prelude
    
    import arrow.Union2
    import arrow.Union3
    import arrow.Union4
    
    fun f(): Union2<String, Union2<Int, Double>> = 2
    fun g(): Union3<String, Int, Double> = 2.0
    fun h(): Union4<String, Int, Double, Long> = 2L
    """.trimIndent()

  val tupleCode2 =
    """
    package prelude
    
    import arrow.tuples.Tuple2
    import arrow.tuples.Tuple3
    import arrow.tuples.Tuple4
    import arrow.tuples.tupleOf
    
    val tuple2: Tuple2<Int, Int> = tupleOf(2, 2)
    val tuple3: Tuple3<Int, Int, String> = tupleOf(2, 2, "47Degrees")
    val tuple4: Tuple4<Int, Int, String, Double> = tupleOf(2, 2, "47Degrees", 5.0)
    """.trimIndent()
}