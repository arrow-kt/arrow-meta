package arrow.meta

typealias Cont<R, A> = (cont: (x: A) -> R) -> R

fun <A> Cont<A, A>.runCont(): A = this { it }
fun <R, A> Cont<R, A>.runCont(cont: (x: A) -> R): R = this(cont)
fun <R, A> reifyCont(f: ((x: A) -> R) -> R): Cont<R, A> = f

fun <R, A> continueWith(x: A): Cont<R, A> = { cont -> cont(x) }
fun <R, A> abortWith(r: R): Cont<R, A> = { _ -> r }

fun <R, A, B> Cont<R, A>.then(f: ((a: A) -> Cont<R, B>)): Cont<R, B> =
  { cont: (r: B) -> R -> this { a -> f(a)(cont) } }
fun <R, A, B> Cont<R, A>.par(cont: () -> Cont<R, B>): Cont<R, Pair<A, B>> =
  this.then { a -> cont().then { b -> continueWith(Pair(a, b)) } }

fun <R, A> Cont<R, A>.forget(): Cont<R, Unit> =
  this.then { continueWith(Unit) }

fun <R, A, B> List<A>.contEach(f: ((a: A) -> Cont<R, B>)): Cont<R, List<B>> =
  if (this.isEmpty())
    continueWith<R, List<B>>(emptyList())
  else
    f(this.first()).par {
      this.drop(1).contEach(f)
    }.then { (b, bs) ->
      continueWith(listOf(b) + bs)
    }
fun <R, A> Int.contEach(f: (n: Int) -> Cont<R, A>): Cont<R, List<A>> =
  List(this, { it }).contEach { n -> f(n) }

fun main(args: Array<String>): Unit {
  3.contEach<Unit, Unit> {
    println("Hello $it")
    continueWith(Unit)
  }.forget().runCont()

  3.contEach<Unit, Unit> { n ->
    println("Bye $n")
    if (n >= 1) abortWith(Unit)
    else continueWith(Unit)
  }.forget().runCont()

  reifyCont<Unit, Int> { cont ->
    cont(1)
    cont(2)
  }.then { n ->
    println("Hello $n")
    continueWith(Unit)
  }.runCont()

}