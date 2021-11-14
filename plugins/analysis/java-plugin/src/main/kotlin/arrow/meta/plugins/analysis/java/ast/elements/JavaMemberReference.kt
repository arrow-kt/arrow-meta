@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CallableReferenceExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import com.sun.source.tree.MemberReferenceTree

public class JavaMemberReference(
  private val ctx: AnalysisContext,
  private val impl: MemberReferenceTree
) : CallableReferenceExpression, JavaElement(ctx, impl) {
  override val callableReference: SimpleNameExpression
    get() = JavaFakeReference(impl.name.toString(), this)
  override val receiverExpression: Expression?
    get() = impl.qualifierExpression?.model(ctx)
  override val hasQuestionMarks: Boolean = false
  override val isEmptyLHS: Boolean
    get() = impl.qualifierExpression == null
}
