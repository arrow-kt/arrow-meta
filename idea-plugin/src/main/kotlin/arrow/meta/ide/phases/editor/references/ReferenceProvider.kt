package arrow.meta.ide.phases.editor.references

import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.Language
import com.intellij.psi.PsiReferenceContributor
import arrow.meta.ide.dsl.editor.references.ReferenceSyntax

sealed class ReferenceProvider : ExtensionPhase {

  /**
   * @see ReferenceSyntax
   */
  data class RegisterReferenceContributor(val lang: Language, val impl: PsiReferenceContributor) : ReferenceProvider()
}