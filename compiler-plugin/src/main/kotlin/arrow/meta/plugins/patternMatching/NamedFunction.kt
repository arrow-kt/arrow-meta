package arrow.meta.plugins.patternMatching

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty

val KtNamedFunction.desugar: String
  get() = """
     |fun ${name}${valueParameterList?.text ?: "()"}: ${typeReference?.text ?: "Unit"}
     ${bodyBlockExpression?.desugar}
     |"""

val KtBlockExpression.desugar: String
  get() = """
    |{
    |  ${statements.map { it.desugar }.joinToString { "\n  |" }}
    |}
    |"""

val KtExpression.desugar: String
  get() = when {
    this is KtProperty && isConstructorPattern -> desugar
    else -> text
  }

val KtProperty.isConstructorPattern: Boolean
  get() = name!!.first().isUpperCase()

val KtProperty.desugar: String
  get() = "${valOrVarKeyword.text} ${node.treeNext.text}"
