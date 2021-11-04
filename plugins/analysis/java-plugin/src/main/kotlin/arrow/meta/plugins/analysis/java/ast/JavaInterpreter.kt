@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.elements.JavaClassDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaConstructorDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaEnumConstantDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaFieldDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaLocalVariableDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaModuleDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaPackageDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaParameterDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaSimpleFunctionDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaTypeParameterDescriptor
import arrow.meta.plugins.analysis.java.ast.types.JavaType
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.ModuleElement
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.TypeParameterElement
import javax.lang.model.element.VariableElement
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

public fun <A : TypeMirror> A.model(ctx: AnalysisContext): JavaType = JavaType(ctx, this)

public fun javax.lang.model.element.Name.name(): Name = Name(this.toString())

public fun javax.lang.model.element.Name.fqName(): FqName = FqName(this.toString())
