package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.psi.proof
import arrow.meta.ide.plugins.proofs.psi.returnTypeCallableMembers
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.ExtensionProof
import arrow.meta.plugins.proofs.phases.GivenProof
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.ProofStrategy
import arrow.meta.plugins.proofs.phases.RefinementProof
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.NamedFunction
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.refactoring.pullUp.renderForConflicts
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import javax.swing.Icon


fun ExtensionProof.extensionMarkerMessage(name: Name?): String {
  return """
  All members of <code lang="kotlin">$to</code> become available in <code lang="kotlin">$from</code> :
  <ul>
  ${through.returnTypeCallableMembers().joinToString("\n -") {
    """
            <li>${it.renderForConflicts()}</li>
            """.trimIndent()
  }}
  </ul>
  <code lang="kotlin">$from</code> does not need to explicitly extend <code lang="kotlin">$to</code>, instead <code lang="kotlin">$name</code>
  is used as proof to support the intersection of <code lang="kotlin">$from & $to</code>.
  """.trimIndent()
}

fun GivenProof.extensionMarkerMessage(name: Name?): String {
  return """
  TODO() Given proof
  """.trimIndent()
}

fun Proof.negationMarkerMessage(name: Name?): String {
  return "TODO"
}

fun RefinementProof.refinementMarkerMessage(name: Name?): String {
  return """
        <code lang="kotlin">$from</code> is a refined type that represents a set of values of the type <code lang="kotlin">$to</code>
        that meet a certain type-level predicate.
        ```
  """.trimIndent()
}

fun ExtensionProof.subtypingMarkerMessage(name: Name?): String {
  return """
        <code lang="kotlin">$from</code> is seen as subtype of <code lang="kotlin">$to</code> :
        
        <code lang="kotlin">
        $from : $to
        </code>
        ```
        
        <code lang="kotlin">$from</code> does not need to explicitly extend <code lang="kotlin">$to</code>, instead <code lang="kotlin">$name</code> 
        is used as injective function proof to support all subtype associations of <code lang="kotlin">$from : $to</code>.
        
        <code lang="kotlin">
        val a: $from = TODO()
        val b: $to = a //ok
        </code>
        
        In the example above compiling <code lang="kotlin">val b: $to = a</code> would have failed to compile but because we have proof of 
        <code lang="kotlin">$from : $to</code> this becomes a valid global ad-hoc synthetic subtype relationship.
        """.trimIndent()
}

fun KtNamedFunction.markerMessage(): String =
  NamedFunction(this).run {
    value.resolveToDescriptorIfAny(bodyResolveMode = BodyResolveMode.PARTIAL)?.proof()?.let { proof ->
      val message = when (proof) {
        is GivenProof -> GivenProof::extensionMarkerMessage.invoke(proof, this.name)
        is ExtensionProof -> ExtensionProof::extensionMarkerMessage.invoke(proof, this.name)
        is RefinementProof -> RefinementProof::refinementMarkerMessage.invoke(proof, this.name)
      }
      """
      <code lang="kotlin">${text}</code> 
      $message
      
    <a href="">More info on Type Proofs</a>: ${ProofStrategy.values().joinToString { """<code lang="kotlin">${it.name}</code>""" }}
    """.trimIndent()
    }.orEmpty()
  }

fun IdeMetaPlugin.proofLineMarkers(icon: Icon, filter: KtNamedFunction.() -> Boolean): ExtensionPhase =
  addLineMarkerProvider(
    icon = icon,
    composite = KtNamedFunction::class.java,
    transform = {
      it.safeAs<KtNamedFunction>()?.takeIf(filter)
    },
    message = {
      it.markerMessage()
    }
  )

fun ExtensionProof.coercionMessage(): String {
  return """
        Coercion happening by proof:
        <code lang="kotlin">$from</code> is not a subtype of <code lang="kotlin">$to</code>.. but there is a proof to go from: <code lang="kotlin">$from</code> to <code lang="kotlin">$to</code> :
        Link to proof declaration:
        <code lang="kotlin">$through</code>
        """.trimIndent()
}