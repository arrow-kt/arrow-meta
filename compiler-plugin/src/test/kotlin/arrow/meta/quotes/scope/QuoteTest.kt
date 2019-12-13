package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.QuoteFactory
//import arrow.meta.quotes.scope.expressions.blockExpression
//import arrow.meta.quotes.scope.expressions.breakExpression
//import arrow.meta.quotes.scope.expressions.catchClauseExpression
//import arrow.meta.quotes.scope.expressions.forExpression
//import arrow.meta.quotes.scope.expressions.whileExpression
import arrow.meta.quotes.scope.plugins.GenericPlugin
import arrow.meta.quotes.scope.plugins.transform
import io.kotlintest.data.forall
import io.kotlintest.tables.row
import org.junit.Test

// TODO:
// This test will be able to replace all the quote testing at the end
//

class QuoteTest {

//  @Test
//  fun `Validate expression scope properties`() {
//    forall(
//      row(blockExpression),
//      row(breakExpression),
//      row(forExpression),
//      row(catchClauseExpression),
//      row(whileExpression)
//    ) { expression: String ->
//      assertThis(CompilerTest(
//        config = { listOf(addMetaPlugins(GenericPlugin(QuoteFactory { ktElement -> transform(ktElement) }))) },
//        code = { expression.source },
//        assert = { quoteOutputMatches(expression.source) }
//      ))
//    }
//  }
}