package arrow.meta.ide.phases.resolve

import arrow.meta.phases.ExtensionPhase
import com.intellij.psi.search.SearchScope

sealed class ResolveScopeProvider : ExtensionPhase {

    data class RegisterSearchScope(val scope: SearchScope) : ResolveScopeProvider()

}