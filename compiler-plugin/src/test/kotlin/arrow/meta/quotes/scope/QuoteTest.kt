package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.GenericPlugin
import org.junit.Test

class QuoteTest {

  @Test
  fun `Check identity law`() {

//    listOf(
//      binaryExpression,
//      blockExpression,
//      breakExpression,
//      *forExpressions,
//      catchClauseExpression,
//      *classBodyExpressions,
//      *continueExpressions,
//      // destructuringDeclarationExpression, // TODO: data class
//      *dotQualifiedExpressions,
//      // finallySectionExpression, // TODO implement convertFinally in Converter to support FINALLY in AST
//      *functionalLiteralExpressions,
//      *ifExpressions,
//      importDirectiveExpression,
//      *isExpressions,
//      // *lambdaExpressions, // TODO: init
//      objectDeclarationExpression,
//      packageExpression,
//      *returnExpressions,
//      // *thisExpressions, // TODO: inner class
//      throwExpression,
//      tryExpression,
//      *typeAliasExpressions,
//      typeReferenceExpression,
//      // *valueArgumentExpressions, // TODO: init
//      whenConditionExpression,
//      whenEntryExpression,
//      // whenExpression, // TODO failing compilation:  Expecting a when-condition, Expecting an expression, is-condition or in-condition
//      whileExpression
//    ).forEach { expression: String ->
//      assertThis(CompilerTest(
//        config = { listOf(addMetaPlugins(GenericPlugin())) },
//        code = { expression.source },
//        assert = { quoteOutputMatches(expression.source) }
//      ))
//    }
  }
}