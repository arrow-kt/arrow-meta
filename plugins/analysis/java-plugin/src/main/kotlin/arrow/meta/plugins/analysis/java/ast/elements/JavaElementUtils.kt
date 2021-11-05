@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import com.sun.source.tree.BinaryTree
import com.sun.source.tree.ClassTree
import com.sun.source.tree.ExpressionTree
import com.sun.source.tree.MethodInvocationTree
import com.sun.source.tree.MethodTree
import com.sun.source.tree.NewClassTree
import com.sun.source.tree.PackageTree
import com.sun.source.tree.Tree
import com.sun.source.tree.UnaryTree

public val ExpressionTree.argumentsFromEverywhere: List<Tree>
  get() =
    when (this) {
      is UnaryTree -> listOf(this.expression)
      is BinaryTree -> listOf(this.leftOperand, this.rightOperand)
      is MethodInvocationTree -> this.arguments
      is NewClassTree -> this.arguments
      else -> emptyList()
    }

public val ExpressionTree.typeArgumentsFromEverywhere: List<Tree>
  get() =
    when (this) {
      is MethodInvocationTree -> this.typeArguments
      is NewClassTree -> this.typeArguments
      else -> emptyList()
    }

public val Tree.name: String?
  get() =
    when (this) {
      is MethodTree -> this.name.toString()
      is ClassTree -> this.simpleName.toString()
      is PackageTree -> this.packageName.toString()
      else -> null
    }

public fun Tree.fqName(ctx: AnalysisContext): String {
  val elements = listOf(this) + ctx.resolver.parentTrees(this)
  return elements.reversed().mapNotNull { it.name }.joinToString(separator = ".")
}
