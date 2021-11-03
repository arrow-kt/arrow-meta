@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import com.sun.source.tree.CompilationUnitTree
import com.sun.source.tree.Tree
import com.sun.source.util.TreePath
import com.sun.source.util.Trees
import javax.lang.model.element.Element
import javax.tools.JavaCompiler

public class Resolver(task: JavaCompiler.CompilationTask, public val unit: CompilationUnitTree) {
  private val trees: Trees = Trees.instance(task)

  public fun Tree.path(): TreePath = TreePath.getPath(unit, this)
  public fun Tree.resolve(): Element = trees.getElement(this.path())
}
