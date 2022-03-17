@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import com.sun.tools.javac.code.Flags
import com.sun.tools.javac.code.Scope
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.comp.AttrContext
import com.sun.tools.javac.comp.Env
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.TreeMaker
import java.util.UUID

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

public fun AnalysisContext.hintsPackage(
  hints: List<FqName>
): Pair<Env<AttrContext>, JCTree.JCClassDecl> {
  val factory = TreeMaker.instance(context)

  // we need to set up the symbols correctly
  // this has been hacked by inspecting the debug trace
  symbolTable.unnamedModule.opens = emptyJavacList()
  val pkgName =
    Symbol.PackageSymbol(names.fromString("arrow.analysis.hints"), symbolTable.unnamedModule)
  val pkg = factory.PackageDecl(emptyJavacList(), factory.Ident(pkgName))
  val klass = hintsClass(hints)
  val klassName = Symbol.ClassSymbol(Flags.PUBLIC.toLong(), klass.simpleName, pkgName)
  klassName.members_field = Scope.WriteableScope.create(klassName)
  klass.sym = klassName
  val unit = factory.TopLevel(javacList(pkg, klass))

  // hack taken from [com.sun.tools.javac.comp.Enter]
  val predefClassDef =
    factory.ClassDef(
      factory.Modifiers(Flags.PUBLIC.toLong()),
      symbolTable.predefClass.name,
      emptyJavacList(),
      null,
      emptyJavacList(),
      emptyJavacList()
    )
  predefClassDef.sym = symbolTable.predefClass

  val unitEnv = Env(unit, AttrContext())
  val pkgEnv = Env(pkg, AttrContext())
  pkgEnv.toplevel = unit
  pkgEnv.outer = unitEnv
  pkgEnv.enclClass = predefClassDef
  val klassEnv = Env(klass, AttrContext())
  klassEnv.toplevel = unit
  klassEnv.outer = unitEnv
  klassEnv.enclClass = predefClassDef

  return Pair(klassEnv, klass)
}

public fun AnalysisContext.hintsClass(hints: List<FqName>): JCTree.JCClassDecl {
  val factory = TreeMaker.instance(context)
  val annotations =
    annStrings("arrow.analysis.PackagesWithLaws", *hints.map { it.name }.toTypedArray())
  val modifiers = factory.Modifiers(Flags.PUBLIC.toLong(), javacList(annotations))
  val uuid = UUID.randomUUID().toString().replace('-', '_')
  val className = "hints_$uuid"
  return factory.ClassDef(
    modifiers,
    names.fromString(className),
    emptyJavacList(),
    null,
    emptyJavacList(),
    emptyJavacList()
  )
}

private fun <A> emptyJavacList(): com.sun.tools.javac.util.List<A> = javacList()

private inline fun <reified A : Any> Collection<A>.javac(): com.sun.tools.javac.util.List<A> =
  javacList(this)

private inline fun <reified A : Any> javacList(
  elements: Collection<A>
): com.sun.tools.javac.util.List<A> = javacList(*elements.toTypedArray())

private fun <A> javacList(vararg elements: A): com.sun.tools.javac.util.List<A> =
  com.sun.tools.javac.util.List.from(elements)
