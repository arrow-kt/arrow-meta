package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.QuoteFactory
import arrow.meta.quotes.Scope
import arrow.meta.quotes.Transform
import arrow.meta.quotes.element.CatchClause
import arrow.meta.quotes.expression.BlockExpression
import arrow.meta.quotes.expression.expressionwithlabel.BreakExpression
import arrow.meta.quotes.expression.loopexpression.ForExpression
import arrow.meta.quotes.expression.loopexpression.WhileExpression
import arrow.meta.quotes.quote
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

open class GenericPlugin(private val quoteFactory: QuoteFactory<KtElement, Scope<KtElement>>) : Meta {

  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    "Generic Plugin" {
      meta(
        quote(
          quoteFactory,
          { true },
          { element ->
            Transform.replace(
              replacing = element,
              newDeclaration = identity()
            )
          })
      )
    }
  )
}

// TODO:
//  - Finish!
//  - Choose a better location for this function
fun transform(ktElement: KtElement): Scope<KtElement> {
  return when (ktElement) {
    is KtBlockExpression -> BlockExpression(ktElement)
    is KtBreakExpression -> BreakExpression(ktElement)
    is KtCatchClause -> CatchClause(ktElement)
    is KtForExpression -> ForExpression(ktElement)
    is KtWhileExpression -> WhileExpression(ktElement)
    else -> Scope<KtElement>(ktElement)
  }
}