package arrowx

import arrow.Proof
import arrow.TypeProof

interface Show<in A> {
  fun A.show(): String
  interface ShowSyntax<A> {
    val value: A
    fun show(): String
  }
}

object ShowInt: Show<Int> {
  override fun Int.show(): String = this.toString()
}

internal class ShowSyntax(override val value: Int): Show.ShowSyntax<Int> {
  override fun show(): String = value.toString()
}

@Proof(TypeProof.Extension)
fun Int.Companion.show(): Show<Int> = ShowInt

@Proof(TypeProof.Extension)
fun Int.showSyntax(): Show.ShowSyntax<Int> = ShowSyntax(this)