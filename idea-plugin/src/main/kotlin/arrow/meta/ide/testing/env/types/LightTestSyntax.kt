package arrow.meta.ide.testing.env.types

import arrow.meta.ide.dsl.utils.toNotNullable
import arrow.meta.ide.testing.Source
import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SyntaxTraverser
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl
import com.intellij.util.containers.TreeTraversal
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

/**
 * [LightTestSyntax] utilises common patterns in test environments for headless ide instances.
 */
object LightTestSyntax {
  /**
   * traverses the Source code and deconstructs the KtFile into a List, based on [traversal], which has several forms, such as
   * [TreeTraversal.POST_ORDER_DFS], [TreeTraversal.PRE_ORDER_DFS], [TreeTraversal.TRACING_BFS], ...
   */
  fun Source.ktFileToList(myFixture: CodeInsightTestFixture, traversal: TreeTraversal = TreeTraversal.PLAIN_BFS): List<PsiElement> =
    toKtFile(myFixture)?.let { SyntaxTraverser.psiTraverser(it).traverse(traversal).toList() } ?: emptyList()

  /**
   * transforms [Source] string to a [KtFile].
   */
  fun Source.toKtFile(myFixture: CodeInsightTestFixture): KtFile? =
    myFixture.configureByText(KotlinFileType.INSTANCE, this).safeAs()

  /**
   * traverses each PsiElement marked by [match] with function [f].
   * Look up is case-insensitive.
   */
  fun <A> Source.traverse(match: String = "<caret>", myFixture: CodeInsightTestFixture, f: (PsiElement) -> A): List<A> =
    StringBuilder(this).run {
      val psiFile: PsiFile? = myFixture.configureByText(KotlinFileType.INSTANCE, toString())
      filterFold(emptyList(), match, { acc: List<Int>, i: Int -> acc + i })
        .map { psiFile?.findElementAt(it)?.let(f) }.toNotNullable()
    }

  /**
   * filters through a StringBuilder for a specific [str] and returns all collected instances within container [F].
   * @param acc is a container like [List], [Array], etc.
   */
  tailrec fun <F> StringBuilder.filterFold(acc: F, str: String, f: (acc: F, index: Int) -> F): F =
    if (!contains(str)) acc
    else {
      val i: Int = indexOf(str)
      delete(i, i + str.length).filterFold(f(acc, i), str, f)
    }

  /**
   * instantiates the KtFile and returns highlighting information, filtering out [toIgnore].
   * @param toIgnore specifies which information can be ignored. Please refer to [Pass] for instances.
   * @param changes specifies if a file changes its highlighting
   */
  fun KtFile.highlighting(myFixture: CodeInsightTestFixture, toIgnore: List<Int>, changes: (KtFile) -> Boolean = { it.isScript() }): List<HighlightInfo> =
    CodeInsightTestFixtureImpl.instantiateAndRun(this, myFixture.editor, toIgnore.toIntArray(), changes(this)).filterNotNull()



}