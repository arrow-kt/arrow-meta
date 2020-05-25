package arrow.meta.plugins.patternMatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.*
import org.jetbrains.kotlin.psi.*

val Meta.patternMatching: CliPlugin
  get() = "pattern matching" {
    meta(
      namedFunction({ name == "patmat" }) { c ->
        Transform.replace(
          replacing = c,
          newDeclaration = transformFunction(c).function.syntheticScope
        )
      }
    )
  }

fun transformFunction(function: KtNamedFunction) =
  """|fun ${function.name}(): ${function.typeReference?.text ?: "Unit"} {
     ${transformBlockExpression(function.bodyBlockExpression!!)}
     |}"""

fun transformBlockExpression(blockExpression: KtBlockExpression) =
  """|${transformStatements(blockExpression).joinToString("\n|")}"""

fun transformStatements(blockExpression: KtBlockExpression) =
  blockExpression.statements.map { transformStatement(it) }

fun transformStatement(statementExpression: KtExpression) =
  when {
    statementExpression is KtProperty && isConstructorPattern(statementExpression) ->
      desugarConstructorPattern(statementExpression)
    else -> "${statementExpression.text}"
  }

fun isConstructorPattern(property: KtProperty) =
  property.name!!.first().isUpperCase()

fun desugarConstructorPattern(property: KtProperty) =
  "${property.valOrVarKeyword.text} ${property.node.treeNext.text}"
