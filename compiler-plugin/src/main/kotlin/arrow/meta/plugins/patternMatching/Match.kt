package arrow.meta.plugins.patternMatching

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList

val KtCallExpression.isConstructorPattern: Boolean
  get() = (this as KtExpression).isConstructorPattern

val KtExpression.isConstructorPattern: Boolean
  get() =
    this is KtCallExpression
      && this.firstChild is KtReferenceExpression
      && this.text.first().isUpperCase()
      && this.firstChild.nextSibling is KtValueArgumentList
      && (this.firstChild.nextSibling as KtValueArgumentList).arguments.any { it.textMatches("_") }
