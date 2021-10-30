package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ClassBody
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ModifierList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ObjectDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.PrimaryConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.PureClassOrObject
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SecondaryConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SuperTypeListEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtPureClassOrObject

fun interface KotlinPureClassOrObject : PureClassOrObject, KotlinDeclarationContainer {
  override fun impl(): KtPureClassOrObject

  override val name: String?
    get() = impl().name
  override val isLocal: Boolean
    get() = impl().isLocal
  override val superTypeListEntries: List<SuperTypeListEntry>
    get() = impl().superTypeListEntries.map { it.model() }
  override val companionObjects: List<ObjectDeclaration?>
    get() = impl().companionObjects.map { it.model() }

  override fun hasExplicitPrimaryConstructor(): Boolean = impl().hasExplicitPrimaryConstructor()

  override fun hasPrimaryConstructor(): Boolean = impl().hasPrimaryConstructor()

  override val primaryConstructor: PrimaryConstructor?
    get() = impl().primaryConstructor?.model()
  override val primaryConstructorModifierList: ModifierList?
    get() = impl().primaryConstructorModifierList?.model()
  override val primaryConstructorParameters: List<Parameter>
    get() = impl().primaryConstructorParameters.map { it.model() }
  override val secondaryConstructors: List<SecondaryConstructor?>
    get() = impl().secondaryConstructors.map { it.model() }
  override val body: ClassBody?
    get() = impl().body?.model()
  override val psiOrParent: Element
    get() = impl().psiOrParent.model()
}
