@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.modelCautious
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnonymousInitializer
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Class
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ClassBody
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.EnumEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ModifierList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NamedFunction
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ObjectDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.PrimaryConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Property
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SecondaryConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SuperTypeListEntry
import com.sun.source.tree.ClassTree
import com.sun.source.tree.MethodTree
import com.sun.source.tree.Tree

public class JavaClass(private val ctx: AnalysisContext, private val impl: ClassTree) :
  Class, JavaElement(ctx, impl) {

  override val parents: List<Element>
    get() = ctx.resolver.parentTrees(impl).mapNotNull { it.modelCautious(ctx) }
  override val name: String
    get() = impl.name.toString()
  override val nameAsSafeName: Name
    get() = Name(name)
  override val fqName: FqName
    get() = FqName(impl.fqName(ctx))
  override val nameAsName: Name
    get() = nameAsSafeName

  override val superTypeListEntries: List<SuperTypeListEntry>
    get() = (listOfNotNull(impl.extendsClause) + impl.implementsClause).map { it.model(ctx) }
  override val declarations: List<Declaration>
    get() = impl.members.map { it.model(ctx) }

  override val body: ClassBody
    get() =
      object : ClassBody, JavaElement(ctx, impl) {
        override val anonymousInitializers: List<AnonymousInitializer> = emptyList()
        override val properties: List<Property> = emptyList()
        override val functions: List<NamedFunction> =
          this@JavaClass.declarations.mapNotNull { it as? JavaMethod }.filter {
            it !is JavaConstructor
          }
        override val enumEntries: List<EnumEntry> = emptyList() // TODO
        override val allCompanionObjects: List<ObjectDeclaration> = emptyList()
        override val declarations: List<Declaration> = this@JavaClass.declarations
      }

  // Java does not have many Kotlin niceties
  override fun getProperties(): List<Property> = emptyList()
  override fun getAnonymousInitializers(): List<AnonymousInitializer> = emptyList()
  override val companionObjects: List<ObjectDeclaration?> = emptyList()

  // we map all Java constructors as "secondary"
  override fun hasExplicitPrimaryConstructor(): Boolean = false
  override fun hasPrimaryConstructor(): Boolean = false
  override val primaryConstructor: PrimaryConstructor? = null
  override val primaryConstructorModifierList: ModifierList? = null
  override val primaryConstructorParameters: List<Parameter> = emptyList()

  override val secondaryConstructors: List<SecondaryConstructor?>
    get() = declarations.filterIsInstance<JavaConstructor>()

  override fun isAnnotation(): Boolean = impl.kind == Tree.Kind.ANNOTATION_TYPE
  override fun isInterface(): Boolean = impl.kind == Tree.Kind.INTERFACE
  override fun isEnum(): Boolean = impl.kind == Tree.Kind.ENUM
  override fun isData(): Boolean = false
  override fun isSealed(): Boolean = false
  override fun isInner(): Boolean = ctx.resolver.parentTrees(impl).any { it is ClassTree }
  override fun isInline(): Boolean = false
  override fun isValue(): Boolean = false
  override fun isTopLevel(): Boolean = !isInner()
  override val isLocal: Boolean
    get() = ctx.resolver.parentTrees(impl).any { it is MethodTree }
}
