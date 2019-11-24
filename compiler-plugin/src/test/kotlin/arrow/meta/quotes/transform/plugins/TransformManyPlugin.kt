package arrow.meta.quotes.transform.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.ClassDeclaration
import arrow.meta.quotes.Transform
import arrow.meta.quotes.`class`
import arrow.meta.quotes.plus
import org.jetbrains.kotlin.psi.KtClass

val Meta.transformMany: List<Plugin>
  get() = listOf(transformManySimpleCase, transformManyCustomCase, transformManyReplace, transformManyRemove)

private val Meta.transformManyRemove: Plugin
  get() = "Transform Many" {
    meta(
      `class`({ name == "ManyRemove" }) { c ->
        removeFooPrint(c, this) + removeBarPrint(c, this)
      }
    )
  }

private val Meta.transformManyReplace: Plugin
  get() = "Transform Many" {
    meta(
      `class`({ name == "ManyReplace" }) { c ->
        createPrints("ManyReplace", c, this) + cleanMethods("ManyReplace", c, this)
      }
    )
  }

private val Meta.transformManyCustomCase: Plugin
  get() = "Transform Many" {
    meta(
      `class`({ name == "ManyCustomCase" }) { c ->
        createPrints("ManyCustomCase", c, this) + removeFooPrint(c, this)
      }
    )
  }

private val Meta.transformManySimpleCase: Plugin
  get() = "Transform Many" {
    meta(
      `class`({ name == "ManySimpleCase" }) { c ->
        changeClassVisibility("ManySimpleCase", c, this) + removeFooPrint(c, this)
      }
    )
  }

private fun CompilerContext.cleanMethods(className: String, context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.replace(
  replacing = context,
  newDeclaration = """ private class $className {} """.`class`.synthetic
)}

private fun CompilerContext.createPrints(className: String, context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.replace(
  replacing = context,
  newDeclaration = """
  | private class $className {
  |   fun printFirst() = println("Foo")
  |   fun printSecond() = println("Bar")
  | } """.`class`.synthetic
)}

private fun CompilerContext.changeClassVisibility(className: String, context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.replace(
  replacing = context,
  newDeclaration = """
    | private class $className {
    |   $body
    | } """.`class`.synthetic
)}

private fun CompilerContext.removeFooPrint(context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.remove(
  removeIn = context,
  declaration = """ fun printFirst() = println("Foo") """.expressionIn(context)
)}

private fun CompilerContext.removeBarPrint(context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.remove(
  removeIn = context,
  declaration = """ fun printSecond() = println("Bar") """.expressionIn(context)
)}