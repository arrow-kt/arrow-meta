package arrow.meta.quotes.transform.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
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
      classDeclaration(this, { element.name == "ManyRemove" }) { c ->
        removeFooPrint(c.element, this) + removeBarPrint(c.element, this) + cleanMethods("ManyRemove", c.element, this)
      }
    )
  }

private val Meta.transformManyReplace: CliPlugin
  get() = "Transform Many" {
    meta(
      classDeclaration(this, { element.name == "ManyReplace" }) { c ->
        createPrints("ManyReplace", c.element, this) + cleanMethods("ManyReplace", c.element, this)
      }
    )
  }

private val Meta.transformManyCustomCase: CliPlugin
  get() = "Transform Many" {
    meta(
      classDeclaration(this, { element.name == "ManyCustomCase" }) { c ->
        createPrints("ManyCustomCase", c.element, this) + removeFooPrint(c.element, this)
      }
    )
  }

private val Meta.transformManySimpleCase: CliPlugin
  get() = "Transform Many" {
    meta(
      classDeclaration(this, { element.name == "ManySimpleCase" }) { c ->
        changeClassVisibility("ManySimpleCase", c.element, this) + removeFooPrint(c.element, this)
      }
    )
  }

private fun CompilerContext.cleanMethods(className: String, context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run {
  Transform.replace(
    replacing = context,
    newDeclaration = """ private class $className {} """.`class`
  )
}

private fun CompilerContext.createPrints(className: String, context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run {
  Transform.replace(
    replacing = context,
    newDeclaration = """
  | private class $className {
  |   fun printFirst() = println("Foo")
  |   fun printSecond() = println("Bar")
  | } """.`class`
  )
}

private fun CompilerContext.changeClassVisibility(className: String, context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run {
  Transform.replace(
    replacing = context,
    newDeclaration = """
    | private class $className {
    |   $body
    | } """.`class`
  )
}

private fun CompilerContext.removeFooPrint(context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run {
  Transform.remove(
    removeIn = context,
    declaration = """ fun printFirst() = println("Foo") """.expressionIn(context)
  )
}

private fun CompilerContext.removeBarPrint(context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run {
  Transform.remove(
    removeIn = context,
    declaration = """ fun printSecond() = println("Bar") """.expressionIn(context)
  )
}