package arrow.meta.quotes.transform.plugins

import arrow.meta.Meta
import arrow.meta.CliPlugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classDeclaration
import arrow.meta.quotes.classorobject.ClassDeclaration
import arrow.meta.quotes.plus
import org.jetbrains.kotlin.psi.KtClass

val Meta.transformMany: List<CliPlugin>
  get() = listOf(transformManySimpleCase, transformManyCustomCase, transformManyReplace, transformManyRemove)

private val Meta.transformManyRemove: CliPlugin
  get() = "Transform Many" {
    meta(
      classDeclaration({ name == "ManyRemove" }) { c ->
        removeFooPrint(c, this) + removeBarPrint(c, this) + cleanMethods("ManyRemove", c, this)
      }
    )
  }

private val Meta.transformManyReplace: CliPlugin
  get() = "Transform Many" {
    meta(
      classDeclaration({ name == "ManyReplace" }) { c ->
        createPrints("ManyReplace", c, this) + cleanMethods("ManyReplace", c, this)
      }
    )
  }

private val Meta.transformManyCustomCase: CliPlugin
  get() = "Transform Many" {
    meta(
      classDeclaration({ name == "ManyCustomCase" }) { c ->
        createPrints("ManyCustomCase", c, this) + removeFooPrint(c, this)
      }
    )
  }

private val Meta.transformManySimpleCase: CliPlugin
  get() = "Transform Many" {
    meta(
      classDeclaration({ name == "ManySimpleCase" }) { c ->
        changeClassVisibility("ManySimpleCase", c, this) + removeFooPrint(c, this)
      }
    )
  }

private fun CompilerContext.cleanMethods(className: String, context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.replace(
  replacing = context,
  newDeclaration = """ private class $className {} """.`class`.syntheticScope
)}

private fun CompilerContext.createPrints(className: String, context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.replace(
  replacing = context,
  newDeclaration = """
  | private class $className {
  |   fun printFirst() = println("Foo")
  |   fun printSecond() = println("Bar")
  | } """.`class`.syntheticScope
)}

private fun CompilerContext.changeClassVisibility(className: String, context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.replace(
  replacing = context,
  newDeclaration = """
    | private class $className {
    |   $body
    | } """.`class`.syntheticScope
)}

private fun CompilerContext.removeFooPrint(context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.remove(
  removeIn = context,
  declaration = """ fun printFirst() = println("Foo") """.expressionIn(context)
)}

private fun CompilerContext.removeBarPrint(context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.remove(
  removeIn = context,
  declaration = """ fun printSecond() = println("Bar") """.expressionIn(context)
)}