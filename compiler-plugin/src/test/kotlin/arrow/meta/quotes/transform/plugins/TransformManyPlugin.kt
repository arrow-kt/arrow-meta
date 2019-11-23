package arrow.meta.quotes.transform.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.`class`
import arrow.meta.quotes.plus
import org.jetbrains.kotlin.psi.KtClass

val Meta.transformMany: Plugin
  get() = "Transform Many" {
    meta(
      `class`({ name == "Many" }) { c ->
        createPrints(c) + removePrint(c)
      }
    )
  }

private fun CompilerContext.createPrints(context: KtClass): Transform<KtClass> = Transform.replace(
  replacing = context,
  newDeclaration = """
    | class ManyModified {
    |   fun printFirst() = println("Hello")
    |   fun printSecond() = println("World!")
    | } """.`class`.synthetic
)

private fun CompilerContext.removePrint(context: KtClass): Transform<KtClass> = Transform.remove(
  removeIn = context,
  declaration = """ fun printSecond() = println("World!") """.expressionIn(context)
)