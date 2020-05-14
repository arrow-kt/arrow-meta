package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.external.ui.tooltip.util.applyMetaStyles
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
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.refactoring.pullUp.renderForConflicts
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import javax.swing.Icon

fun Proof.markerMessage(): String =
  when (this) {
    is ClassProof -> markerMessage()
    is ObjectProof -> markerMessage()
    is CallableMemberProof -> markerMessage()
    is CoercionProof -> markerMessage()
    is ProjectionProof -> markerMessage()
    is RefinementProof -> markerMessage()
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

fun KtDeclaration.markerMessage(): String =
  scope().run {
    value?.resolveToDescriptorIfAny(bodyResolveMode = BodyResolveMode.PARTIAL)?.proof()?.let { proof ->
      val message = proof.markerMessage()
      """
      <code lang="kotlin">${text}</code> 
      $message
    """.trimIndent()
    }.orEmpty()
  }

inline fun <reified A : KtDeclaration> IdeMetaPlugin.proofLineMarkers(icon: Icon, crossinline filter: A.() -> Boolean): ExtensionPhase =
  addLineMarkerProvider(
    icon = icon,
    transform = {
      it.safeAs<A>()?.takeIf(filter)
    },
    message = {
      it.markerMessage()
    }
  )

fun CoercionProof.coercionMessage(): String =
  """
    <h3>Coercion</h3>
    Coercion happening by proof:
    <p>
      <code lang="kotlin">$from</code> is not a subtype of <code lang="kotlin">$to</code>.. but there is a proof to go from: <code lang="kotlin">$from</code> to <code lang="kotlin">$to</code> :
    </p>
    <p>Link to proof declaration: <code lang="kotlin">$through</code></p>
  """.trimIndent()
