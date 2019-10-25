package arrow.meta

import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.jetbrains.kotlin.idea.KotlinFileType

abstract class KotlinIdeBaseTest : LightPlatformCodeInsightFixture4TestCase() {
    /**
     * Parses the code defined by parameter 'code' and calls 'block' for each
     * PsiElement marked by 'caretMarker`. The caret marker is looked up case-insensitive.
     */
    internal fun withEachCaret(code: String, caretMarker: String = "<CARET>", block: (PsiElement) -> Unit): Unit {
        val offsets = mutableListOf<Int>()

        val codeBuilder = StringBuilder(code)
        while (codeBuilder.contains(caretMarker, true)) {
            val offset = codeBuilder.indexOf(caretMarker, ignoreCase = true)
            offsets += offset
            codeBuilder.delete(offset, offset + caretMarker.length)
        }

        offsets.forEach { offset ->
            // reparse file for each offset to allow side-effects in tests
            // and to keep myFixture up-to-date
            val psiFile = myFixture.configureByText(KotlinFileType.INSTANCE, codeBuilder.toString())
            assertNotNull(psiFile)

            val element = psiFile.findElementAt(offset)
            assertNotNull(element)

            block(element!!)
        }
    }
}
