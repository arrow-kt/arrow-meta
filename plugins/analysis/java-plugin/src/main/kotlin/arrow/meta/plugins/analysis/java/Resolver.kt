@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import com.sun.source.tree.CompilationUnitTree
import com.sun.source.tree.Tree
import com.sun.source.util.TreePath
import com.sun.source.util.Trees
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror
import javax.tools.JavaCompiler

public class Resolver(task: JavaCompiler.CompilationTask, private val unit: CompilationUnitTree) {
  private val trees: Trees = Trees.instance(task)

  public val topElement: Element
    get() = trees.getElement(TreePath(unit))

  public fun path(tree: Tree): TreePath = TreePath.getPath(unit, tree)
  public fun resolve(tree: Tree): Element = trees.getElement(path(tree))
  public fun resolveType(tree: Tree): TypeMirror? = trees.getTypeMirror(path(tree))
  public fun tree(element: Element): Tree? = trees.getTree(element)

  public fun positionOf(tree: Tree): Pair<Long, Long> =
    trees.sourcePositions.getStartPosition(unit, tree) to
      trees.sourcePositions.getEndPosition(unit, tree)

  public fun parentTrees(tree: Tree): Iterable<Tree> {
    val result = mutableListOf<Tree>()
    var current = path(tree).parentPath
    while (current != null) {
      result.add(current.leaf)
      current = current.parentPath
    }
    return result.toList()
  }

  // this is faster than calling map over parentTrees,
  // because then we need to reconstruct each TreePath
  public fun parentElements(tree: Tree): Iterable<Element> {
    val result = mutableListOf<Element>()
    var current = path(tree).parentPath
    while (current != null) {
      result.add(trees.getElement(current))
      current = current.parentPath
    }
    return result.toList()
  }
}
