package arrow.meta.ide.testing.env.types

import arrow.meta.ide.dsl.utils.toNotNullable
import arrow.meta.ide.testing.Source
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.jetbrains.kotlin.idea.KotlinFileType


object LightTestSyntax : LightPlatformCodeInsightFixture4TestCase() {
  /**
   * Parses the code defined by in it's [receiver] and traverses each
   * PsiElement marked by [match] with function [f]
   * Look up is case-insensitive.
   * Please check if the PsiFile has to be configured in each loop check out PR #6
   */
  fun <R> Source.traverse(match: String = "<caret>", f: (PsiElement) -> R): List<R> =
    StringBuilder(this).run {
      val psiFile: PsiFile? = myFixture.configureByText(KotlinFileType.INSTANCE, toString())
      filterFold(emptyList(), match, { acc: List<Int>, i: Int -> acc + i }).map { index ->
        // reparse file for each offset keep myFixture up-to-date
        psiFile?.findElementAt(index)?.let(f)
      }.toNotNullable()
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