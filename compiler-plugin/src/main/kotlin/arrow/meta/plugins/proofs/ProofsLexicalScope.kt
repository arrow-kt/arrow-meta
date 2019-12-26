package arrow.meta.plugins.proofs

import org.jetbrains.kotlin.resolve.scopes.LexicalScope

class ProofsLexicalScope(val delegate: LexicalScope) : LexicalScope by delegate {

}