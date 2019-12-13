package arrow.meta.quotes.transform.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classDeclaration
import arrow.meta.quotes.classorobject.ClassDeclaration
import arrow.meta.quotes.plus
import org.jetbrains.kotlin.psi.KtClass

val Meta.transformNewSource: List<Plugin>
  get() = listOf(transformNewSourceSingleGeneration, transformNewSourceWithManyTransformation, transformNewSourceMultipleGeneration)

private val Meta.transformNewSourceSingleGeneration: Plugin
  get() = "Transform New Source" {
    meta(
      classDeclaration({ name == "NewSource" }) {
        Transform.newSources(
          """
            package arrow
            
            class ${name}_Generated {
             fun sayHi() = println("Hi!")
            }
          """.file("${name}_Generated")
        )
      }
    )
  }

private val Meta.transformNewSourceMultipleGeneration: Plugin
  get() = "Transform New Multiple Source" {
    meta(
      classDeclaration({ name == "NewMultipleSource" }) {
        Transform.newSources(
          """
            package arrow
            
            class ${name}_Generated {
             fun sayHi() = println("Hi!")
            }
          """.file("${name}_Generated"),
          """
            package arrow
            
            class ${name}_Generated_2 {
             fun say(name: String) = println(name)
            }
          """.file("${name}_Generated_2")
        )
      }
    )
  }

private val Meta.transformNewSourceWithManyTransformation: Plugin
  get() = "Transform New Source With Many Transformation" {
    meta(
      classDeclaration({ name == "NewSourceMany" }) { c ->
        (
          changeClassVisibility("NewSourceMany", c, this)
          + removeFooPrint(c, this)
          + removeBarPrint(c, this)
          + generateSupplierClass(this)
        )
      }
    )
  }

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

private fun CompilerContext.generateSupplierClass(declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.newSources(
  """
    package arrow
    
    class ${name}_Generated {
      fun sayHello() = println("Hello!")
      fun say(name: String) = println(name)
    }
  """.file("${name}_Generated")
)}