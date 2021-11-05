@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import com.sun.source.tree.BinaryTree
import com.sun.source.tree.ExpressionTree
import com.sun.source.tree.MethodInvocationTree
import com.sun.source.tree.NewClassTree
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
