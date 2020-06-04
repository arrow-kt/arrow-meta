package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
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
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import javax.swing.Icon

inline fun <reified A : KtDeclaration> IdeMetaPlugin.proofRelatedLineMarkers(icon: Icon, crossinline filter: A.() -> Boolean): ExtensionPhase =
  addRelatedLineMarkerProvider(
    icon = icon,
    transform = {
      it.safeAs<A>()?.takeIf(filter)
    },
    composite = KtDeclaration::class.java,
    targets = {
      it.proof { proof ->
        proof.targets()
      }.orEmpty()
    }
  )

fun Proof.targets(): List<PsiElement> =
  when (this) {
    is ClassProof -> targets()
    is ObjectProof -> targets()
    is CallableMemberProof -> targets()
    is CoercionProof -> targets()
    is ProjectionProof -> targets()
    is RefinementProof -> targets()
  }

fun GivenProof.targets(): List<PsiElement> =
  listOfNotNull(to.constructor.declarationDescriptor?.findPsi(), through.findPsi())

fun ExtensionProof.targets(): List<PsiElement> =
  listOf(from, to).psi() + listOfNotNull(through.findPsi())

fun RefinementProof.targets(): List<PsiElement> =
  listOf(from, to).psi() + listOfNotNull(through.findPsi())

fun List<KotlinType>.psi(): List<PsiElement> =
  mapNotNull { it.constructor.declarationDescriptor?.findPsi() }