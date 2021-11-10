@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeElement
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import com.sun.source.tree.ArrayTypeTree
import com.sun.source.tree.ParameterizedTypeTree
import com.sun.source.tree.Tree

public class JavaTypeReference
private constructor(private val ctx: AnalysisContext, private val impl: Tree) :
  TypeReference, JavaElement(ctx, impl) {
  override val typeElement: TypeElement
    get() = JavaTypeElement(ctx, impl)

  public val underlyingTree: Tree
    get() = impl

  public companion object {
    public operator fun invoke(ctx: AnalysisContext, impl: Tree): JavaTypeReference? =
      when (impl.kind) {
        Tree.Kind.ARRAY_TYPE,
        Tree.Kind.PARAMETERIZED_TYPE,
        Tree.Kind.TYPE_PARAMETER,
        Tree.Kind.PRIMITIVE_TYPE,
        Tree.Kind.ANNOTATED_TYPE,
        Tree.Kind.ANNOTATION_TYPE,
        Tree.Kind.UNION_TYPE,
        Tree.Kind.INTERSECTION_TYPE,
        Tree.Kind.EXTENDS_WILDCARD,
        Tree.Kind.SUPER_WILDCARD,
        Tree.Kind.UNBOUNDED_WILDCARD -> JavaTypeReference(ctx, impl)
        else -> null
      }
  }
}

public class JavaTypeElement(private val ctx: AnalysisContext, private val impl: Tree) :
  TypeElement, JavaElement(ctx, impl) {
  override val typeArgumentsAsTypes: List<TypeReference>
    get() =
      when (impl) {
        is ArrayTypeTree -> listOfNotNull(JavaTypeReference(ctx, impl.type))
        is ParameterizedTypeTree -> impl.typeArguments.mapNotNull { JavaTypeReference(ctx, it) }
        else -> emptyList()
      }
}
