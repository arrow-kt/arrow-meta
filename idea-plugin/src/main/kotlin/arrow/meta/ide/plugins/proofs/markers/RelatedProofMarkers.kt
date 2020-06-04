package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.psi.givenAnnotation
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
import com.intellij.core.CoreJavaFileManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.jvm.KotlinCliJavaFileManager
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
        proof.targets(it.project)
      }.orEmpty()
    },
    popUpTitle = { decl, targets ->
      ""
    }
  )

fun Proof.targets(project: Project): List<PsiElement> =
  when (this) {
    is ClassProof -> targets(project)
    is ObjectProof -> targets(project)
    is CallableMemberProof -> targets(project)
    is CoercionProof -> targets(project)
    is ProjectionProof -> targets(project)
    is RefinementProof -> targets(project)
  }

/**
 *
 */
fun GivenProof.targets(project: Project): List<PsiElement> =
  listOfNotNull(to).psi(project)

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
      ""
    },
    projection = {
      ""
    },
    coercion = {
      ""
    }
  )

fun ExtensionProof.targets(project: Project): List<PsiElement> =
  listOf(from, to).psi(project)

fun RefinementProof.targets(project: Project): List<PsiElement> =
  listOf(from, to).psi(project)

fun List<KotlinType>.psi(project: Project): List<PsiElement> =
  mapNotNull {
    ServiceManager.getService(project, CoreJavaFileManager::class.java).safeAs<KotlinCliJavaFileManager>()?.findClass(
      it.constructor.declarationDescriptor?.fqNameSafe?.asString() ?: "ERROR", GlobalSearchScope.allScope(project)
    )
  }