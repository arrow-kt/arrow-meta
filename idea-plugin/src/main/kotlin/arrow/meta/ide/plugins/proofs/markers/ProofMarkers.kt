package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.internal.Noop
import arrow.meta.phases.CompilerContext
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
import com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import javax.swing.Icon

inline fun <reified A : KtNamedDeclaration> IdeMetaPlugin.proofLineMarkers(icon: Icon, crossinline filter: A.() -> Boolean): ExtensionPhase =
  addLineMarkerProvider(
    icon = icon,
    transform = {
      it.safeAs<A>()?.takeIf(filter)
    },
    composite = KtNamedDeclaration::class.java,
    message = { decl: KtNamedDeclaration ->
      decl.ctx()?.let { ctx: CompilerContext ->
        decl.markerMessage(ctx)
      } ?: "TODO"
    }
  )

fun KtDeclaration.markerMessage(ctx: CompilerContext): String =
  proof(ctx) {
    StringUtil.escapeXmlEntities(it.description().trimIndent())
  }.orEmpty()

fun <A> KtDeclaration.proof(ctx: CompilerContext, f: (Proof) -> A): A? = null
  // FIXME: fix shadows so KtDeclaration.proof(ctx: CompilerContext, f: (Proof) -> ???): ??? can be resolved correctly
  //scope().value?.resolveToDescriptorIfAny(bodyResolveMode = BodyResolveMode.PARTIAL)?.proof(ctx)?.let(f)

fun Proof.description(): String =
  fold(
    given = {
      when (this) {
        is ObjectProof -> """$to is available in all given<$to>() as a singleton value"""
        is ClassProof -> """$to is available in all given<$to>() as a new instance of this class"""
        is CallableMemberProof -> """$to is available in all given<$to>() as a call to this member"""
      }
    },
    refinement = {
      null
    },
    projection = {
      """All members of $to are available as members of $from"""
    },
    coercion = {
      """$from can be used in place of $to as if $to : $from, all members of $to are available as members of $from"""
    }
  ).orEmpty()
