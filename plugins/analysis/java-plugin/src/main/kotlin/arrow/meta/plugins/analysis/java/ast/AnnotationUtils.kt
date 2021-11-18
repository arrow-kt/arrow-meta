@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast

import arrow.meta.plugins.analysis.java.AnalysisContext
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.TreeMaker

public fun JCTree.JCModifiers.addAnn(ann: JCTree.JCAnnotation) {
  this.annotations = this.annotations.prepend(ann)
}

public fun JCTree.JCModifiers.addAnn(ann: () -> JCTree.JCAnnotation) {
  addAnn(ann())
}

public fun AnalysisContext.annStrings(type: String, vararg args: String): JCTree.JCAnnotation {
  val factory = TreeMaker.instance(context)
  return factory.Annotation(ty(type), args.map { factory.Literal(it) }.javac())
}

public fun AnalysisContext.annArrays(type: String, vararg args: List<String>): JCTree.JCAnnotation {
  val factory = TreeMaker.instance(context)
  return factory.Annotation(
    ty(type),
    args
      .map { strings -> factory.NewArray(null, null, strings.map { factory.Literal(it) }.javac()) }
      .javac()
  )
}

private fun AnalysisContext.ty(type: String): JCTree.JCExpression {
  val factory = TreeMaker.instance(context)
  return factory.Type(symbolTable.getClass(modules.defaultModule, names.fromString(type)).type)
}

private inline fun <reified A : Any> Collection<A>.javac(): com.sun.tools.javac.util.List<A> =
  javacList(this)

private inline fun <reified A : Any> javacList(
  elements: Collection<A>
): com.sun.tools.javac.util.List<A> = javacList(*elements.toTypedArray<A>())

private fun <A> javacList(vararg elements: A): com.sun.tools.javac.util.List<A> =
  com.sun.tools.javac.util.List.from(elements)
