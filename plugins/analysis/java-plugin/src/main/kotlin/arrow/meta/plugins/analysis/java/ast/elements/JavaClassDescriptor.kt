@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ConstructorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.MemberScope
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.NestingKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind

public class JavaClassDescriptor(private val ctx: AnalysisContext, private val impl: TypeElement) :
  ClassDescriptor, JavaMemberDescriptor(ctx, impl) {

  private fun memberScope(predicate: (Element) -> Boolean): JavaMemberScope =
    JavaMemberScope(ctx, impl.enclosedElements.filter(predicate))

  override val unsubstitutedMemberScope: MemberScope = memberScope {
    it !is TypeElement && !it.modifiers.contains(Modifier.STATIC)
  }
  override val staticScope: MemberScope = memberScope {
    it !is TypeElement && it.modifiers.contains(Modifier.STATIC)
  }
  override val unsubstitutedInnerClassesScope: MemberScope = memberScope { it is TypeElement }

  override val constructors: Collection<JavaConstructorDescriptor> =
    impl.enclosedElements.filter { it.kind == ElementKind.CONSTRUCTOR }.map { it.model(ctx) }

  override val kind: ClassDescriptor.ClassKind =
    when {
      impl.nestingKind == NestingKind.ANONYMOUS -> ClassDescriptor.ClassKind.OBJECT
      impl.kind == ElementKind.ENUM -> ClassDescriptor.ClassKind.ENUM_CLASS
      impl.kind == ElementKind.INTERFACE -> ClassDescriptor.ClassKind.INTERFACE
      ctx.types.isSubtype(impl.asType(), ctx.symbolTable.annotationType) ->
        ClassDescriptor.ClassKind.ANNOTATION_CLASS
      else -> ClassDescriptor.ClassKind.CLASS
    }

  override val superTypes: Collection<Type> =
    (listOfNotNull(impl.superclass.takeIf { it.kind != TypeKind.NONE }) + impl.interfaces).map {
      it.model(ctx)
    }
  override val declaredTypeParameters: List<TypeParameterDescriptor> =
    impl.typeParameters.map { it.model(ctx) }

  override val typeConstructor: TypeConstructor?
    get() = TODO("Not yet implemented")
  override val defaultType: Type?
    get() = TODO("Not yet implemented")
  override val thisAsReceiverParameter: ReceiverParameterDescriptor =
    JavaReceiverParameterDescriptor(ctx, impl.asType(), impl.enclosingElement)

  override val isCompanionObject: Boolean = false
  override val isData: Boolean = false
  override val isInline: Boolean = false
  override val isFun: Boolean =
    ctx.types.isSubtype(impl.asType(), ctx.symbolTable.functionalInterfaceType)
  override val isValue: Boolean = false
  override val isEnumEntry: Boolean = false
  override val isInner: Boolean = impl.nestingKind == NestingKind.MEMBER

  override val companionObjectDescriptor: ClassDescriptor? = null
  override val sealedSubclasses: Collection<ClassDescriptor> = emptyList()
  override val unsubstitutedPrimaryConstructor: ConstructorDescriptor? = null
}
