package arrow.meta.idea.test.syntax.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.jetbrains.kotlin.idea.KotlinFileType

object LightTestSyntax : LightPlatformCodeInsightFixture4TestCase() {
  /**
   * Parses the code defined by in it's [receiver] and traverses each
   * PsiElement marked by [match] with function [f]
   * Look up is case-insensitive.
   */
  fun <R> String.sequence(match: String = "<caret>", f: (PsiElement) -> R): Unit =
    StringBuilder(this).run {
      filterFold(emptyList(), match, { acc: List<Int>, i: Int -> acc + i }).forEach { index ->
        // reparse file for each offset to allow side-effects in tests
        // and to keep myFixture up-to-date
        val psiFile: PsiFile? = myFixture.configureByText(KotlinFileType.INSTANCE, toString())
        psiFile?.findElementAt(index)?.let(f)
      }
    }

  tailrec fun <R> StringBuilder.filterFold(
    acc: R,
    str: String,
    f: (acc: R, index: Int) -> R): R =
    if (!contains(str)) acc
    else {
      val i: Int = indexOf(str)
      delete(i, i + str.length).filterFold(f(acc, i), str, f)
    }
}