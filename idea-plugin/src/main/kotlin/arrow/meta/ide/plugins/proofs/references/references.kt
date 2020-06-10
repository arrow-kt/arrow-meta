package arrow.meta.ide.plugins.proofs.references

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.references.ReferenceSyntax
import arrow.meta.phases.ExtensionPhase
import com.intellij.patterns.ElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import org.jetbrains.kotlin.psi.KtNamedFunction

class DuplicatedFunReferences : RefProvider() {
  override val pattern: ElementPattern<KtNamedFunction>
    get() = psiElement(KtNamedFunction::class.java)

  override val priority: Double
    get() = PsiReferenceRegistrar.DEFAULT_PRIORITY

  override fun PsiElement.references(ctx: ProcessingContext): List<PsiReference> {
    println("this instance is Registered !")
    return this.references.filterNotNull()
  }
}


/**
 * registration is still WIP - Meanwhile use the example above
 */
val IdeMetaPlugin.proofReferences: ExtensionPhase
  get() = addRefContributor(
    pattern = psiElement(KtNamedFunction::class.java)
      .with(
        transform("kotlinFunctionPattern-withParameters") { ctx: ProcessingContext? ->
          takeIf { it.name == "helloWorld" }
        }
      ),
    references = {
      println("LAA")
      emptyList()
    }
  )

/**
 * only here as a temporary type - this won't be necessary once the registry is implemented
 * check out subtypes of [PsiReferenceContributor]
 * and to explore how to create PsiReferences check subtypes in Kotlin plugin and this [org.jetbrains.kotlin.psi.KotlinReferenceProvidersService]
 */
abstract class RefProvider : ReferenceSyntax, PsiReferenceContributor() {

  abstract val pattern: ElementPattern<out PsiElement>

  /**
   * dafault : [PsiReferenceRegistrar.DEFAULT_PRIORITY]
   */
  abstract val priority: Double

  abstract fun PsiElement.references(ctx: ProcessingContext): List<PsiReference>

  override fun registerReferenceProviders(registrar: PsiReferenceRegistrar): Unit =
    registrar.register(pattern, { this.references(it) }, priority)
}
