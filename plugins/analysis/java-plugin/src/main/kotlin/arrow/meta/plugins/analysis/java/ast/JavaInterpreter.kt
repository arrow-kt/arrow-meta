@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaClassDescriptor
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaConstructorDescriptor
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaDescriptor
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaEnumConstantDescriptor
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaFieldDescriptor
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaLocalVariableDescriptor
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaModuleDescriptor
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaPackageDescriptor
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaParameterDescriptor
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaSimpleFunctionDescriptor
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaTypeParameterDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaElement
import arrow.meta.plugins.analysis.java.ast.types.JavaType
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import com.sun.source.tree.CompilationUnitTree
import com.sun.source.tree.Tree
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.ModuleElement
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.TypeParameterElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

public fun <A : Element, B : JavaDescriptor> A.model(ctx: AnalysisContext): B =
  when (this) {
    is ModuleElement -> JavaModuleDescriptor(ctx, this) as B
    is PackageElement -> JavaPackageDescriptor(ctx, this) as B
    is VariableElement ->
      when (this.kind) {
        ElementKind.PARAMETER, ElementKind.EXCEPTION_PARAMETER ->
          JavaParameterDescriptor(ctx, this) as B
        ElementKind.LOCAL_VARIABLE -> JavaLocalVariableDescriptor(ctx, this) as B
        ElementKind.FIELD -> JavaFieldDescriptor(ctx, this) as B
        ElementKind.ENUM_CONSTANT -> JavaEnumConstantDescriptor(ctx, this) as B
        else -> throw IllegalArgumentException("incorrect VariableElement case")
      }
    is ExecutableElement ->
      when (this.kind) {
        ElementKind.METHOD -> JavaSimpleFunctionDescriptor(ctx, this) as B
        ElementKind.CONSTRUCTOR -> JavaConstructorDescriptor(ctx, this) as B
        ElementKind.STATIC_INIT, ElementKind.INSTANCE_INIT -> TODO("not yet supported")
        else -> throw IllegalArgumentException("incorrect ExecutableElement case")
      }
    is TypeElement -> JavaClassDescriptor(ctx, this) as B
    is TypeParameterElement -> JavaTypeParameterDescriptor(ctx, this) as B
    else -> JavaDescriptor(ctx, this) as B
  }

public fun <
  A : Tree,
  B : arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element> A.modelCautious(
  ctx: AnalysisContext
): B? =
  when (this) {
    is CompilationUnitTree -> null
    else -> JavaElement(ctx, this) as B
  }

public fun <
  A : Tree,
  B : arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element> A.model(
  ctx: AnalysisContext
): B =
  when (this) {
    is CompilationUnitTree ->
      throw IllegalArgumentException("compilation unit trees cannot be converted")
    else -> JavaElement(ctx, this) as B
  }

public fun <A : TypeMirror> A.model(ctx: AnalysisContext): JavaType = JavaType(ctx, this)

public fun <A : TypeMirror> A.modelCautious(ctx: AnalysisContext): JavaType? =
  if (this.kind == TypeKind.NONE) null else this.model(ctx)

public fun javax.lang.model.element.Name.name(): Name = Name(this.toString())

public fun javax.lang.model.element.Name.fqName(): FqName = FqName(this.toString())
