package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.psi.proof
import arrow.meta.ide.plugins.proofs.psi.returnTypeCallableMembers
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.CallableMemberProof
import arrow.meta.plugins.proofs.phases.ClassProof
import arrow.meta.plugins.proofs.phases.CoercionProof
import arrow.meta.plugins.proofs.phases.ExtensionProof
import arrow.meta.plugins.proofs.phases.GivenProof
import arrow.meta.plugins.proofs.phases.ObjectProof
import arrow.meta.plugins.proofs.phases.ProjectionProof
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.RefinementProof
import arrow.meta.quotes.scope
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.refactoring.pullUp.renderForConflicts
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import javax.swing.Icon

fun Proof.markerMessage(renderer: DescriptorRenderer): String? =
  when (this) {
    is ClassProof -> "${renderer.renderType(to)} is available in all given() as a new instance of ${renderer.renderType(to)}"
    is ObjectProof -> "${renderer.renderType(to)} is available in all given() as a singleton value"
    is CallableMemberProof -> "${renderer.renderType(to)} is available in all given() as a call to this member"
    is CoercionProof -> """$from can be used in place of $to as if $to : $from, all members of $to are available as members of $from"""
    is ProjectionProof -> """all members of $to are available as members of $from"""
    is RefinementProof -> null
  }

fun ExtensionProof.markerMessage(): String {
  return """
  All members of <code lang="kotlin">$to</code> become available in <code lang="kotlin">$from</code> :
  <ul>
  ${through.returnTypeCallableMembers().joinToString("\n -") {
    """
            <li>${it.renderForConflicts()}</li>
            """.trimIndent()
  }}
  </ul>
  <code lang="kotlin">$from</code> does not need to explicitly extend <code lang="kotlin">$to</code>, instead <code lang="kotlin">${through.name}</code>
  is used as proof to support the intersection of <code lang="kotlin">$from & $to</code>.
  """.trimIndent()
}

fun GivenProof.markerMessage(): String {
  return """
  TODO() Given proof
  """.trimIndent()
}

fun RefinementProof.markerMessage(): String {
  return """
        <code lang="kotlin">$from</code> is a refined type that represents a set of values of the type <code lang="kotlin">$to</code>
        that meet a certain type-level predicate.
        ```
  """.trimIndent()
}

fun ExtensionProof.subtypingMarkerMessage(): String {
  return """
        <code lang="kotlin">$from</code> is seen as subtype of <code lang="kotlin">$to</code> :
        
        <code lang="kotlin">
        $from : $to
        </code>
        ```
        
        <code lang="kotlin">$from</code> does not need to explicitly extend <code lang="kotlin">$to</code>, instead <code lang="kotlin">${through.name}</code> 
        is used as injective function proof to support all subtype associations of <code lang="kotlin">$from : $to</code>.
        
        <code lang="kotlin">
        val a: $from = TODO()
        val b: $to = a //ok
        </code>
        
        In the example above compiling <code lang="kotlin">val b: $to = a</code> would have failed to compile but because we have proof of 
        <code lang="kotlin">$from : $to</code> this becomes a valid global ad-hoc synthetic subtype relationship.
        """.trimIndent()
}

fun KtDeclaration.markerMessage(renderer: DescriptorRenderer): String =
  scope().run {
    value?.resolveToDescriptorIfAny(bodyResolveMode = BodyResolveMode.PARTIAL)?.proof()?.let { proof ->
      """
      ${proof.markerMessage(renderer)}
    """.trimIndent()
    }.orEmpty()
  }

inline fun <reified B : PsiNameIdentifierOwner>
  IdeMetaPlugin.proofLineMarkers(icon: Icon, crossinline transform: (PsiElement) -> B?): ExtensionPhase =
  addLineMarkerProvider(
    icon = icon,
    composite = B::class.java,
    transform = { transform(it) },
    message = {
      it.safeAs<KtDeclaration>()?.markerMessage(this.HTML) ?: ""
    }
  )

fun CoercionProof.coercionMessage(): String =
  """
    Coercion happening by proof:
    <code lang="kotlin">$from</code> is not a subtype of <code lang="kotlin">$to</code>.. but there is a proof to go from: <code lang="kotlin">$from</code> to <code lang="kotlin">$to</code> :
    Link to proof declaration:
    <code lang="kotlin">$through</code>
  """.trimIndent()
