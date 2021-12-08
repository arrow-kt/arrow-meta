@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NameReferenceExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SuperExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ThisExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import com.sun.source.tree.IdentifierTree

public class JavaIdentifier(ctx: AnalysisContext, private val impl: IdentifierTree) :
  NameReferenceExpression, JavaElement(ctx, impl) {
  init {
    require(impl.name != ctx.names._this)
  }
  override fun getReferencedName(): String = impl.name.toString()
  override fun getReferencedNameAsName(): Name = impl.name.name()
}

public class JavaThis(ctx: AnalysisContext, impl: IdentifierTree) :
  ThisExpression, JavaElement(ctx, impl) {
  init {
    require(impl.name == ctx.names._this)
  }

  override fun getTargetLabel(): SimpleNameExpression? = null
  override fun getLabelName(): String? = null
  override fun getLabelNameAsName(): Name? = null
}

public class JavaSuper(ctx: AnalysisContext, impl: IdentifierTree) :
  SuperExpression, JavaElement(ctx, impl) {
  init {
    require(impl.name == ctx.names._super)
  }

  override val superTypeQualifier: TypeReference? = null
  override fun getTargetLabel(): SimpleNameExpression? = null
  override fun getLabelName(): String? = null
  override fun getLabelNameAsName(): Name? = null
}
