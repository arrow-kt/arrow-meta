package arrow.meta.phases.analysis

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtElement

interface ScopeSyntax {

    /**
     * Transform a scope of [A] into a scope of [B].
     */
    fun <A : KtElement, B : KtElement> Scope<A>.map(f: (A) -> B): Scope<B>

    /**
     * Replaces a value [A] of a scope with [B].
     */
    fun <A : KtElement, B : KtElement> Scope<A>.`as`(b: B): Scope<B>

    /**
     * Given [A] is a sub type of [B], re-type this value from Scope<A> to Scope<B>.
     */
    fun <A : B, B : KtElement> Scope<A>.widen(): Scope<B>

    /**
     * fold on Scope using the provided function f.
     */
    fun <A : KtElement, B : KtElement> Scope<A>.fold(b: B, f: (B, A) -> B): B
}
