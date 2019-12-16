package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.expressions.binaryExpression
import arrow.meta.quotes.scope.expressions.blockExpression
import arrow.meta.quotes.scope.expressions.breakExpression
import arrow.meta.quotes.scope.expressions.catchClauseExpression
import arrow.meta.quotes.scope.expressions.classBodyExpressions
import arrow.meta.quotes.scope.expressions.continueExpressions
import arrow.meta.quotes.scope.expressions.dotQualifiedExpressions
import arrow.meta.quotes.scope.expressions.forExpressions
import arrow.meta.quotes.scope.expressions.functionalLiteralExpressions
import arrow.meta.quotes.scope.expressions.ifExpressions
import arrow.meta.quotes.scope.expressions.importDirectiveExpression
import arrow.meta.quotes.scope.expressions.isExpressions
import arrow.meta.quotes.scope.expressions.objectDeclarationExpression
import arrow.meta.quotes.scope.expressions.packageExpression
import arrow.meta.quotes.scope.expressions.returnExpressions
import arrow.meta.quotes.scope.expressions.throwExpression
import arrow.meta.quotes.scope.expressions.tryExpression
import arrow.meta.quotes.scope.expressions.typeAliasExpressions
import arrow.meta.quotes.scope.expressions.typeReferenceExpression
import arrow.meta.quotes.scope.expressions.whenConditionExpression
import arrow.meta.quotes.scope.expressions.whenEntryExpression
import arrow.meta.quotes.scope.expressions.whileExpression
import arrow.meta.quotes.scope.plugins.GenericPlugin
import io.kotlintest.specs.AnnotationSpec

class QuoteTest: AnnotationSpec() {

  @Test
  fun `Validate expression scope properties`() {

    listOf(
      binaryExpression,
      blockExpression,
      breakExpression,
      *forExpressions,
      catchClauseExpression,
      *classBodyExpressions,
      *continueExpressions,
      // destructuringDeclarationExpression, // TODO: data class
      *dotQualifiedExpressions,
      // finallySectionExpression, // TODO implement convertFinally in Converter to support FINALLY in AST
      *functionalLiteralExpressions,
      *ifExpressions,
      importDirectiveExpression,
      *isExpressions,
      // *lambdaExpressions, // TODO: init
      objectDeclarationExpression,
      packageExpression,
      *returnExpressions,
      // *thisExpressions, // TODO: inner class
      throwExpression,
      tryExpression,
      *typeAliasExpressions,
      typeReferenceExpression,
      // *valueArgumentExpressions, // TODO: init
      whenConditionExpression,
      whenEntryExpression,
      // whenExpression, // TODO failing compilation:  Expecting a when-condition, Expecting an expression, is-condition or in-condition
      whileExpression
    ).forEach { expression: String ->
      val source = Code.Source(text = expression)

      assertThis(CompilerTest(
        config = { listOf(addMetaPlugins(GenericPlugin())) },
        code = { source },
        assert = { quoteOutputMatches(source) }
      ))
    }
  }
}