package arrow.meta.quotes.pbt

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.templates.BinaryExpressionTest.Companion.binaryExpression
import arrow.meta.quotes.scope.templates.BlockExpressionTest.Companion.blockExpression
import arrow.meta.quotes.scope.templates.BreakExpressionTest.Companion.breakExpression
import arrow.meta.quotes.scope.templates.CatchClauseTest.Companion.catchClauseExpression
import arrow.meta.quotes.scope.templates.ClassBodyTest.Companion.classBodyExpressions
import arrow.meta.quotes.scope.templates.ContinueExpressionTest.Companion.continueExpressions
import arrow.meta.quotes.scope.templates.DotQualifiedExpressionTest.Companion.dotQualifiedExpressions
import arrow.meta.quotes.scope.templates.ForExpressionTest.Companion.forExpressions
import arrow.meta.quotes.scope.templates.FunctionLiteralTest.Companion.functionalLiteralExpressions
import arrow.meta.quotes.scope.templates.IfExpressionTest.Companion.ifExpressions
import arrow.meta.quotes.scope.templates.ImportDirectiveTest.Companion.importDirectiveExpression
import arrow.meta.quotes.scope.templates.IsExpressionTest.Companion.isExpressions
import arrow.meta.quotes.scope.templates.ObjectDeclarationTest.Companion.objectDeclarationExpression
import arrow.meta.quotes.scope.templates.PackageDirectiveTest.Companion.packageExpressions
import arrow.meta.quotes.scope.templates.ReturnExpressionTest.Companion.returnExpressions
import arrow.meta.quotes.scope.templates.ThrowExpressionTest.Companion.throwExpression
import arrow.meta.quotes.scope.templates.TryExpressionTest.Companion.tryExpression
import arrow.meta.quotes.scope.templates.TypeAliasTest.Companion.typeAliasExpressions
import arrow.meta.quotes.scope.templates.TypeReferenceTest.Companion.typeReferenceExpression
import arrow.meta.quotes.scope.templates.WhenConditionTest.Companion.whenConditionExpression
import arrow.meta.quotes.scope.templates.WhenEntryTest.Companion.whenEntryExpression
import arrow.meta.quotes.scope.templates.WhenExpressionTest.Companion.whenExpressions
import arrow.meta.quotes.scope.templates.WhileExpressionTest.Companion.whileExpression
import org.junit.Test

class QuoteTest {

  @Test
  fun `Check identity law`() {

    listOf(
//      binaryExpression, // Todo: make this fail
      blockExpression,
      breakExpression,
      *forExpressions,
      catchClauseExpression,
      *classBodyExpressions,
      *continueExpressions,
//     destructuringDeclarationExpression, // TODO: data class
      *dotQualifiedExpressions,
//      // finallySectionExpression, // TODO implement convertFinally in Converter to support FINALLY in AST
      *functionalLiteralExpressions,
      *ifExpressions,
      importDirectiveExpression,
      *isExpressions,
//      // *lambdaExpressions, // TODO: init
      objectDeclarationExpression,
      *packageExpressions,
      *returnExpressions,
//      // *thisExpressions, // TODO: inner class
      throwExpression,
      tryExpression,
      *typeAliasExpressions,
      typeReferenceExpression,
//      // *valueArgumentExpressions, // TODO: init
      whenConditionExpression,
      whenEntryExpression,
      *whenExpressions,
      whileExpression
    ).forEach { expression: Code.Source ->
      assertThis(CompilerTest(
        config = { listOf(addMetaPlugins(GenericPlugin())) },
        code = { expression },
        assert = { quoteOutputMatches(expression) }
      ))
    }
  }
}