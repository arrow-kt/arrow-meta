package arrow.meta.ide.dsl.editor.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.util.actualsForExpected
import org.jetbrains.kotlin.idea.util.liftToExpected
import org.jetbrains.kotlin.psi.KtDeclaration

interface InspectionUtilitySyntax {
  fun KtDeclaration.withExpectedActuals(): List<KtDeclaration> {
    val expect = liftToExpected() ?: return listOf(this)
    val actuals = expect.actualsForExpected()
    return listOf(expect) + actuals
  }

  val ArrowPath: Array<String>
    get() = arrayOf("Kotlin", "Λrrow")

  val ProofPath: Array<String>
    get() = ArrowPath + arrayOf("Type Proofs")

  fun PsiElement.inspectionManager(): InspectionManager =
    InspectionManager.getInstance(project)
}
