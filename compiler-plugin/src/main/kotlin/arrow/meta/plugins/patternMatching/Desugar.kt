package arrow.meta.plugins.patternMatching

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtWhenEntry

val KtCallExpression.desugar: String
  get() = """${text.replace("_", "person.firstName")}"""

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
    this matches constructorPattern -> desugar
    else -> text
  }

val KtProperty.desugar: String
  get() = "${valOrVarKeyword.text} ${node.treeNext.text}"

val KtWhenEntry.desugar: String
  get() = """is ${conditions.first().children.first().children[1].text} -> ${expression?.text}"""
