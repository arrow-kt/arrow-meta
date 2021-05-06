package arrow.meta.quotes.transform.plugins

import arrow.meta.Meta
import arrow.meta.CliPlugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classDeclaration
import arrow.meta.quotes.classorobject.ClassDeclaration
import arrow.meta.quotes.plus
import arrow.meta.quotes.transform.TransformNewSourceTest.Companion.CUSTOM_GENERATED_SRC_PATH_1
import arrow.meta.quotes.transform.TransformNewSourceTest.Companion.CUSTOM_GENERATED_SRC_PATH_2
import org.jetbrains.kotlin.psi.KtClass

val Meta.transformNewSource: List<CliPlugin>
  get() = listOf(
    transformNewSourceSingleGeneration,
    transformNewSourceWithManyTransformation,
    transformNewSourceMultipleGeneration,
    transformNewSourceSingleGenerationWithBaseDir,
    transformNewSourceSingleGenerationWithCustomPath,
    transformNewSourceSingleGenerationWithBaseDirAndCustomPath,
    transformNewSourceMultipleGenerationWithBaseDir,
    transformNewSourceMultipleGenerationWithCustomPath,
    transformNewSourceMultipleGenerationWithBaseDirAndCustomPath
  )

private val Meta.transformNewSourceSingleGeneration: CliPlugin
  get() = "Transform New Source" {
    meta(
      classDeclaration(this, { element.name == "NewSource" }) {
        Transform.newSources(
          """
            package arrow
            
            class ${name}_Generated {
             fun sayHi() = println("Hi!")
            }
          """.file("${name}_Generated.kt")
        )
      }
    )
  }

private val Meta.transformNewSourceMultipleGeneration: CliPlugin
  get() = "Transform New Multiple Source" {
    meta(
      classDeclaration(this, { element.name == "NewMultipleSource" }) {
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

private val Meta.transformNewSourceWithManyTransformation: CliPlugin
  get() = "Transform New Source With Many Transformation" {
    meta(
      classDeclaration(this, { element.name == "NewSourceMany" }) { c ->
        (
          changeClassVisibility("NewSourceMany", c.element, this)
          + removeFooPrint(c.element, this)
          + removeBarPrint(c.element, this)
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

private fun CompilerContext.generateSupplierClass(declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.newSources(
  """
    package arrow
    
    class ${name}_Generated {
      fun sayHello() = println("Hello!")
      fun say(name: String) = println(name)
    }
  """.file("${name}_Generated")
)}

private val Meta.transformNewSourceSingleGenerationWithCustomPath: CliPlugin
  get() = "Transform New Source" {
    meta(
      classDeclaration(this, { element.name == "NewSourceWithCustomPath" }) {
        Transform.newSources(
          """
            package arrow
            
            class ${name}_Generated
          """.file("${name}_Generated.kt", CUSTOM_GENERATED_SRC_PATH_1.toString())
        )
      }
    )
  }

private val Meta.transformNewSourceSingleGenerationWithBaseDir: CliPlugin
  get() = "Transform New Source" {
    meta(
      classDeclaration(this, { element.name == "NewSourceWithBaseDir" }) {
        Transform.newSources(
          """
            package arrow
            
            class ${name}_Generated
          """.file("${name}_Generated.kt")
        )
      }
    )
  }

private val Meta.transformNewSourceSingleGenerationWithBaseDirAndCustomPath: CliPlugin
  get() = "Transform New Source" {
    meta(
      classDeclaration(this, { element.name == "NewSourceWithBaseDirAndCustomPath" }) {
        Transform.newSources(
          """
            package arrow
            
            class ${name}_Generated
          """.file("${name}_Generated.kt", CUSTOM_GENERATED_SRC_PATH_1.toString())
        )
      }
    )
  }

private val Meta.transformNewSourceMultipleGenerationWithCustomPath: CliPlugin
  get() = "Transform New Multiple Source" {
    meta(
      classDeclaration(this, { element.name == "NewMultipleSourceWithCustomPath" }) {
        Transform.newSources(
          """
            package arrow
            
            class ${name}_Generated 
          """.file("${name}_Generated", CUSTOM_GENERATED_SRC_PATH_1.toString()),
          """
            package arrow
            
            class ${name}_Generated_2
          """.file("${name}_Generated_2", CUSTOM_GENERATED_SRC_PATH_2.toString())
        )
      }
    )
  }

private val Meta.transformNewSourceMultipleGenerationWithBaseDir: CliPlugin
  get() = "Transform New Multiple Source" {
    meta(
      classDeclaration(this, { element.name == "NewMultipleSourceWithBaseDir" }) {
        Transform.newSources(
          """
            package arrow
            
            class ${name}_Generated 
          """.file("${name}_Generated"),
          """
            package arrow
            
            class ${name}_Generated_2
          """.file("${name}_Generated_2")
        )
      }
    )
  }

private val Meta.transformNewSourceMultipleGenerationWithBaseDirAndCustomPath: CliPlugin
  get() = "Transform New Multiple Source" {
    meta(
      classDeclaration(this, { element.name == "NewMultipleSourceWithBaseDirAndCustomPath" }) {
        Transform.newSources(
          """
            package arrow
            
            class ${name}_Generated 
          """.file("${name}_Generated", CUSTOM_GENERATED_SRC_PATH_1.toString()),
          """
            package arrow
            
            class ${name}_Generated_2
          """.file("${name}_Generated_2", CUSTOM_GENERATED_SRC_PATH_2.toString())
        )
      }
    )
  }
