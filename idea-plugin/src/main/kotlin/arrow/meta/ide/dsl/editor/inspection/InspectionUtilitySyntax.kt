package arrow.meta.ide.dsl.editor.inspection

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.util.actualsForExpected
import org.jetbrains.kotlin.idea.util.liftToExpected
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtPsiFactory

interface InspectionUtilitySyntax {
  fun KtDeclaration.withExpectedActuals(): List<KtDeclaration> {
    val expect = liftToExpected() ?: return listOf(this)
    val actuals = expect.actualsForExpected()
    return listOf(expect) + actuals
  }

  val ArrowPath: Array<String>
    get() = arrayOf("Kotlin", "Λrrow")

  val Project.ktPsiFactory: KtPsiFactory
    get() = KtPsiFactory(this)
}
