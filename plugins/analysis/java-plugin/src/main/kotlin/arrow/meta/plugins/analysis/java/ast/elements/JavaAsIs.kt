@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.IsExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import com.sun.source.tree.InstanceOfTree

/* missing
public class JavaTypeCast(private val ctx: AnalysisContext, private val impl: TypeCastTree)
  : {

}
*/

public class JavaInstanceOf(private val ctx: AnalysisContext, private val impl: InstanceOfTree) :
  IsExpression, JavaElement(ctx, impl) {
  override val leftHandSide: Expression
    get() = impl.expression.model(ctx)
  override val typeReference: TypeReference?
    get() = JavaTypeReference(ctx, impl.type)
  override val operationReference: SimpleNameExpression
    get() = JavaFakeReference("instanceof", this)

  // Java does not have negated instanceof
  override val isNegated: Boolean = false
}
