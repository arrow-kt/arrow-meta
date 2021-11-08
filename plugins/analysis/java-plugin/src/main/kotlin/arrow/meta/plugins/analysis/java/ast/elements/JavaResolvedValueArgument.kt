@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgumentName
import com.sun.source.tree.Tree

public class JavaResolvedValueArgument(
  private val ctx: AnalysisContext,
  private val impl: Tree,
  private val descr: ValueParameterDescriptor
) : ResolvedValueArgument {
  override val arguments: List<ValueArgument>
    get() = listOf(JavaValueArgument(ctx, impl, descr))
}

public class JavaValueArgument(
  private val ctx: AnalysisContext,
  private val impl: Tree,
  private val descr: ValueParameterDescriptor
) : ValueArgument {
  override val argumentExpression: Expression
    get() = impl.model(ctx)
  override fun getArgumentName(): ValueArgumentName = JavaValueArgumentName(descr)

  override fun isNamed(): Boolean = false
  override fun isExternal(): Boolean = false
  override val isSpread: Boolean = false
}

public class JavaValueArgumentName(private val descr: ValueParameterDescriptor) :
  ValueArgumentName {
  override val asName: Name
    get() = descr.name
  override val referenceExpression: SimpleNameExpression? = null
}
