package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Scope
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classorobject.*
import arrow.meta.quotes.declaration.DestructuringDeclaration
import arrow.meta.quotes.element.*
import arrow.meta.quotes.element.ValueArgument
import arrow.meta.quotes.element.whencondition.WhenCondition
import arrow.meta.quotes.expression.*
import arrow.meta.quotes.expression.expressionwithlabel.*
import arrow.meta.quotes.expression.expressionwithlabel.instanceexpressionwithlabel.ThisExpression
import arrow.meta.quotes.expression.loopexpression.*
import arrow.meta.quotes.modifierlist.TypeReference
import arrow.meta.quotes.nameddeclaration.notstubbed.FunctionLiteral
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.TypeAlias
import arrow.meta.quotes.quote
import org.jetbrains.kotlin.psi.*

open class GenericPlugin : Meta {

  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    "Generic Plugin" {
      meta(
        quote(
          { implemented(this) },
          { element: KtElement ->
            Transform.replace(
              replacing = element,
              newDeclaration = identity() // TODO: remove default implementation
            )
          },
          { transform(it) }
        )
      )
    }
  )

  // TODO: remove at the end
  private fun implemented(ktElement: KtElement): Boolean = (
    ktElement is KtBinaryExpression
      || ktElement is KtBlockExpression
      || ktElement is KtBreakExpression
      || ktElement is KtCatchClause
      || ktElement is KtClassBody
      || ktElement is KtContinueExpression
      || ktElement is KtDestructuringDeclaration
      || ktElement is KtDotQualifiedExpression
      || ktElement is KtFinallySection
      || ktElement is KtForExpression
      || ktElement is KtFunctionLiteral
      || ktElement is KtIfExpression
      || ktElement is KtImportDirective
      || ktElement is KtIsExpression
      || ktElement is KtLambdaExpression
      || ktElement is KtObjectDeclaration
      || ktElement is KtPackageDirective
      || ktElement is KtReturnExpression
      || ktElement is KtThisExpression
      || ktElement is KtThrowExpression
      || ktElement is KtTryExpression
      || ktElement is KtTypeAlias
      || ktElement is KtTypeReference
      || ktElement is KtValueArgument
      || ktElement is KtWhenCondition
      || ktElement is KtWhenEntry
      || ktElement is KtWhenExpression
      || ktElement is KtWhileExpression
    )
}


// TODO: remove at the end
fun transform(ktElement: KtElement): Scope<KtElement> {
  return when (ktElement) {
    is KtBinaryExpression -> BinaryExpression(ktElement)
    is KtBlockExpression -> BlockExpression(ktElement)
    is KtBreakExpression -> BreakExpression(ktElement)
    is KtCatchClause -> CatchClause(ktElement)
    is KtClassBody -> ClassBody(ktElement)
    is KtContinueExpression -> ContinueExpression((ktElement))
    is KtDestructuringDeclaration -> DestructuringDeclaration(ktElement)
    is KtDotQualifiedExpression -> DotQualifiedExpression(ktElement)
    is KtFinallySection -> FinallySection(ktElement)
    is KtForExpression -> ForExpression(ktElement)
    is KtFunctionLiteral -> FunctionLiteral(ktElement)
    is KtIfExpression -> IfExpression(ktElement)
    is KtImportDirective -> ImportDirective(ktElement)
    is KtIsExpression -> IsExpression(ktElement)
    is KtLambdaExpression -> LambdaExpression(ktElement)
    is KtObjectDeclaration -> ObjectDeclaration(ktElement)
    is KtPackageDirective -> PackageDirective(ktElement)
    is KtReturnExpression -> ReturnExpression(ktElement)
    is KtThisExpression -> ThisExpression(ktElement)
    is KtThrowExpression -> ThrowExpression(ktElement)
    is KtTryExpression -> TryExpression(ktElement)
    is KtTypeAlias -> TypeAlias(ktElement)
    is KtTypeReference -> TypeReference(ktElement)
    is KtValueArgument -> ValueArgument(ktElement)
    is KtWhenCondition -> WhenCondition(ktElement)
    is KtWhenEntry -> WhenEntry(ktElement)
    is KtWhenExpression -> WhenExpression(ktElement)
    is KtWhileExpression -> WhileExpression(ktElement)
    else -> Scope<KtElement>(ktElement)
  }
}