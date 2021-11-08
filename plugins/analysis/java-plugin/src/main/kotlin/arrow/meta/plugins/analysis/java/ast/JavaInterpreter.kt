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
import arrow.meta.plugins.analysis.java.ast.elements.JavaBlock
import arrow.meta.plugins.analysis.java.ast.elements.JavaBreak
import arrow.meta.plugins.analysis.java.ast.elements.JavaCase
import arrow.meta.plugins.analysis.java.ast.elements.JavaClass
import arrow.meta.plugins.analysis.java.ast.elements.JavaConstructor
import arrow.meta.plugins.analysis.java.ast.elements.JavaContinue
import arrow.meta.plugins.analysis.java.ast.elements.JavaDoWhile
import arrow.meta.plugins.analysis.java.ast.elements.JavaElement
import arrow.meta.plugins.analysis.java.ast.elements.JavaEmptyBlock
import arrow.meta.plugins.analysis.java.ast.elements.JavaEnhancedFor
import arrow.meta.plugins.analysis.java.ast.elements.JavaFor
import arrow.meta.plugins.analysis.java.ast.elements.JavaIf
import arrow.meta.plugins.analysis.java.ast.elements.JavaLiteral
import arrow.meta.plugins.analysis.java.ast.elements.JavaMethod
import arrow.meta.plugins.analysis.java.ast.elements.JavaNull
import arrow.meta.plugins.analysis.java.ast.elements.JavaParenthesized
import arrow.meta.plugins.analysis.java.ast.elements.JavaReturn
import arrow.meta.plugins.analysis.java.ast.elements.JavaSingleBlock
import arrow.meta.plugins.analysis.java.ast.elements.JavaSwitch
import arrow.meta.plugins.analysis.java.ast.elements.JavaSynchronized
import arrow.meta.plugins.analysis.java.ast.elements.JavaTernaryConditional
import arrow.meta.plugins.analysis.java.ast.elements.JavaTypeReference
import arrow.meta.plugins.analysis.java.ast.elements.JavaVariable
import arrow.meta.plugins.analysis.java.ast.elements.JavaWhile
import arrow.meta.plugins.analysis.java.ast.types.JavaType
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import com.sun.source.tree.AnnotatedTypeTree
import com.sun.source.tree.ArrayTypeTree
import com.sun.source.tree.BlockTree
import com.sun.source.tree.BreakTree
import com.sun.source.tree.CaseTree
import com.sun.source.tree.ClassTree
import com.sun.source.tree.CompilationUnitTree
import com.sun.source.tree.ConditionalExpressionTree
import com.sun.source.tree.ContinueTree
import com.sun.source.tree.DirectiveTree
import com.sun.source.tree.DoWhileLoopTree
import com.sun.source.tree.EmptyStatementTree
import com.sun.source.tree.EnhancedForLoopTree
import com.sun.source.tree.ErroneousTree
import com.sun.source.tree.ExpressionStatementTree
import com.sun.source.tree.ForLoopTree
import com.sun.source.tree.IfTree
import com.sun.source.tree.ImportTree
import com.sun.source.tree.IntersectionTypeTree
import com.sun.source.tree.LiteralTree
import com.sun.source.tree.MethodTree
import com.sun.source.tree.ModifiersTree
import com.sun.source.tree.ModuleTree
import com.sun.source.tree.PackageTree
import com.sun.source.tree.ParameterizedTypeTree
import com.sun.source.tree.ParenthesizedTree
import com.sun.source.tree.PrimitiveTypeTree
import com.sun.source.tree.ReturnTree
import com.sun.source.tree.SwitchTree
import com.sun.source.tree.SynchronizedTree
import com.sun.source.tree.Tree
import com.sun.source.tree.TypeParameterTree
import com.sun.source.tree.UnionTypeTree
import com.sun.source.tree.VariableTree
import com.sun.source.tree.WhileLoopTree
import com.sun.source.tree.WildcardTree
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
    is CompilationUnitTree, is PackageTree, is ModuleTree, is DirectiveTree, is ImportTree -> null
    is ModifiersTree -> null
    else -> JavaElement(ctx, this) as B
  }

public fun <
  A : Tree,
  B : arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element> A.model(
  ctx: AnalysisContext
): B =
  when (this) {
    is ArrayTypeTree,
    is ParameterizedTypeTree,
    is TypeParameterTree,
    is PrimitiveTypeTree,
    is IntersectionTypeTree,
    is UnionTypeTree,
    is AnnotatedTypeTree,
    is WildcardTree -> JavaTypeReference(ctx, this) as B
    is MethodTree ->
      if (ctx.resolver.resolve(this).kind == ElementKind.CONSTRUCTOR)
        JavaConstructor(ctx, this) as B
      else JavaMethod(ctx, this) as B
    is VariableTree -> JavaVariable(ctx, this) as B
    // expressions
    is ParenthesizedTree -> JavaParenthesized(ctx, this) as B
    is ConditionalExpressionTree -> JavaTernaryConditional(ctx, this) as B
    is ErroneousTree -> JavaElement(ctx, this) as B // nothing special
    is LiteralTree ->
      when (this.value) {
        null -> JavaNull(ctx, this) as B
        else -> JavaLiteral(ctx, this) as B
      }
    // statements
    is ReturnTree -> JavaReturn(ctx, this) as B
    is ContinueTree -> JavaContinue(ctx, this) as B
    is BreakTree -> JavaBreak(ctx, this) as B
    is DoWhileLoopTree -> JavaDoWhile(ctx, this) as B
    is WhileLoopTree -> JavaWhile(ctx, this) as B
    is ForLoopTree -> JavaFor(ctx, this) as B
    is EnhancedForLoopTree -> JavaEnhancedFor(ctx, this) as B
    is IfTree -> JavaIf(ctx, this) as B
    is SwitchTree -> JavaSwitch(ctx, this) as B
    is CaseTree -> JavaCase(ctx, this) as B
    is ClassTree -> JavaClass(ctx, this) as B
    is SynchronizedTree -> JavaSynchronized(ctx, this) as B
    is EmptyStatementTree -> JavaEmptyBlock(ctx, this) as B
    is ExpressionStatementTree -> JavaSingleBlock(ctx, this) as B
    is BlockTree -> JavaBlock(ctx, this) as B
    // compilation elements
    is CompilationUnitTree ->
      throw IllegalArgumentException("compilation unit trees cannot be converted")
    is PackageTree -> throw IllegalArgumentException("package trees cannot be converted")
    is ModuleTree -> throw IllegalArgumentException("module trees cannot be converted")
    is DirectiveTree -> throw IllegalArgumentException("module directive trees cannot be converted")
    is ImportTree -> throw IllegalArgumentException("import trees cannot be converted")
    is ModifiersTree ->
      throw IllegalArgumentException(
        "modifiers trees cannot be converted, annotations should be handled in elements"
      )
    else -> JavaElement(ctx, this) as B
  }

public fun <A : TypeMirror> A.model(ctx: AnalysisContext): JavaType = JavaType(ctx, this)

public fun <A : TypeMirror> A.modelCautious(ctx: AnalysisContext): JavaType? =
  if (this.kind == TypeKind.NONE) null else this.model(ctx)

public fun javax.lang.model.element.Name.name(): Name = Name(this.toString())

public fun javax.lang.model.element.Name.fqName(): FqName = FqName(this.toString())
