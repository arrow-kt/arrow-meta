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
  fun <A> Source.traverse(match: String = "<caret>", f: (PsiElement) -> A): List<A> =
    StringBuilder(this).run {
      val psiFile: PsiFile? = myFixture.configureByText(KotlinFileType.INSTANCE, toString())
      filterFold(emptyList(), match, { acc: List<Int>, i: Int -> acc + i }).map { index ->
        // reparse file for each offset keep myFixture up-to-date
        psiFile?.findElementAt(index)?.let(f)
      }.toNotNullable()
    }

  tailrec fun <A> StringBuilder.filterFold(
    acc: A,
    str: String,
    f: (acc: A, index: Int) -> A): A =
    if (!contains(str)) acc
    else {
      val i: Int = indexOf(str)
      delete(i, i + str.length).filterFold(f(acc, i), str, f)
    }
}