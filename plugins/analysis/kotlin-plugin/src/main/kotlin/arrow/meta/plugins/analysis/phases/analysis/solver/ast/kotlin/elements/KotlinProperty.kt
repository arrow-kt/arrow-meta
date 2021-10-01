package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Property
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.PropertyAccessor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.PropertyDelegate
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtProperty

class KotlinProperty(val impl: KtProperty) : Property, KotlinVariableDeclaration {
  override fun impl(): KtProperty = impl
  override val isLocal: Boolean
    get() = impl().isLocal
  override val isMember: Boolean
    get() = impl().isMember
  override val isTopLevel: Boolean
    get() = impl().isTopLevel
  override val accessors: List<PropertyAccessor?>
    get() = impl().accessors.map { it.model() }
  override val getter: PropertyAccessor?
    get() = impl().getter?.model()
  override val setter: PropertyAccessor?
    get() = impl().setter?.model()

  override fun hasDelegate(): Boolean =
    impl().hasDelegate()

  override val delegate: PropertyDelegate?
    get() = impl().delegate?.model()

  override fun hasDelegateExpression(): Boolean =
    impl().hasDelegateExpression()

  override val delegateExpression: Expression?
    get() = impl().delegateExpression?.model()

  override fun hasDelegateExpressionOrInitializer(): Boolean =
    impl().hasDelegateExpressionOrInitializer()

  override val delegateExpressionOrInitializer: Expression?
    get() = impl().delegateExpressionOrInitializer?.model()

  override fun hasBody(): Boolean =
    impl().hasBody()

  override val isVar: Boolean
    get() = impl().isVar
}
