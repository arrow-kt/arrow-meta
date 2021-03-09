package arrow.meta.quotes

import arrow.meta.quotes.filebase.File
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpressionCodeFragment

/**
 * The resulting action from matching on transformation
 */
sealed class Transform<out K> {

  /**
   * A Transform that replaces some element in AST. See below:
   *
   * ```kotlin:ank:silent
   * import arrow.meta.Meta
   * import arrow.meta.CliPlugin
   * import arrow.meta.invoke
   * import arrow.meta.quotes.Transform
   * import arrow.meta.quotes.namedFunction
   *
   * val Meta.replacePlugin: CliPlugin
   *  get() =
   *   "Replace Transform" {
   *     meta(
   *      namedFunction(this, { element.name == "helloWorld" }) { (c, d) ->
   *        Transform.replace(
   *          replacing = c,
   *          newDeclaration =
   *          """|fun helloWorld(): Unit =
   *             |  println("Hello ΛRROW Meta!")
   *             |""".function(d).syntheticScope
   *        )
   *      }
   *     )
   *   }
   * ```
   *
   * @param replacing the element to be replaced
   * @param newDeclarations are the elements that will replace
   */
  data class Replace<out K : KtElement>(
    val replacing: PsiElement,
    val newDeclarations: List<Scope<KtElement>>,
    val replacementId: String? = null
  ) : Transform<K>()

  /**
   * A Transform that removes declarations from a specific element in the AST. See below:
   *
   * ```kotlin:ank:silent
   * import arrow.meta.Meta
   * import arrow.meta.CliPlugin
   * import arrow.meta.invoke
   * import arrow.meta.quotes.Transform
   * import arrow.meta.quotes.namedFunction
   *
   * val Meta.replacePlugin: CliPlugin
   *  get() =
   *   "Remove Transform" {
   *     meta(
   *      namedFunction(this, { element.name == "helloWorld" }) { (c, _) ->
   *        Transform.remove(
   *          removeIn = c,
   *          declaration = """ println("") """.expressionIn(c)
   *        )
   *      }
   *     )
   *   }
   * ```
   *
   * @param removing is the element context
   * @param declarations are the elements that should be removed
   */
  data class Remove<out K : KtElement>(
    val removing: PsiElement,
    val declarations: List<Scope<KtExpressionCodeFragment>> = listOf()
  ) : Transform<K>()

  /**
   * A Transform that allows transformations combining. See below:
   *
   * ```kotlin:ank:silent
   * import arrow.meta.Meta
   * import arrow.meta.CliPlugin
   * import arrow.meta.invoke
   * import arrow.meta.phases.CompilerContext
   * import arrow.meta.quotes.classorobject.ClassDeclaration
   * import arrow.meta.quotes.Transform
   * import arrow.meta.quotes.classDeclaration
   * import arrow.meta.quotes.plus
   * import org.jetbrains.kotlin.psi.KtClass
   *
   * val Meta.transformManySimpleCase: CliPlugin
   *  get() = "Transform Many" {
   *   meta(
   *      classDeclaration(this, { name == "ManySimpleCase" }) { c ->
   *       changeClassVisibility("ManySimpleCase", c, this) + removeFooPrint(c, this)
   *     }
   *    )
   *   }
   *
   * fun CompilerContext.changeClassVisibility(className: String, context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.replace(
   *   replacing = context,
   *   newDeclaration = """
   *   | private class $className {
   *   |   $body
   *   | } """.`class`.syntheticScope
   * )}
   *
   * fun CompilerContext.removeFooPrint(context: KtClass, declaration: ClassDeclaration): Transform<KtClass> = declaration.run { Transform.remove(
   *   removeIn = context,
   *   declaration = """ fun printFirst() = println("Foo") """.expressionIn(context)
   * )}
   * ```
   *
   * @param transforms list of transformations
   */
  data class Many<K : KtElement>(
    val transforms: ArrayList<Transform<K>>
  ) : Transform<K>()

  /**
   * A Transform that allows code generation. See below:
   *
   * ```kotlin:ank:silent
   * import arrow.meta.Meta
   * import arrow.meta.CliPlugin
   * import arrow.meta.invoke
   * import arrow.meta.quotes.Transform
   * import arrow.meta.quotes.classDeclaration
   *
   * val Meta.transformNewSource: CliPlugin
   *  get() = "Transform New Source" {
   *   meta(
   *    classDeclaration(this, { name == "NewSource" }) {
   *     Transform.newSources(
   *      """
   *      package arrow
   *      //metadebug
   *      class ${name}_Generated {
   *       fun sayHi() = println("Hi!")
   *      }
   *      """.file("${name}_Generated")
   *     )
   *    }
   *   )
   *  }
   *```
   *
   * @param files list of files to be generated
   */
  data class NewSource<K : KtElement>(
    val files: List<File>
  ) : Transform<K>()

  object Empty : Transform<Nothing>()

  companion object {
    fun <K : KtElement> replace(
      replacing: PsiElement,
      newDeclarations: List<Scope<KtElement>>
    ): Transform<K> =
      Replace(replacing, newDeclarations)

    fun <K : KtElement> replace(
      replacing: PsiElement,
      newDeclaration: Scope<KtElement>
    ): Transform<K> =
      replace(replacing, listOf(newDeclaration))

    fun <K : KtElement> remove(remove: PsiElement): Transform<K> = Replace(remove, emptyList())

    fun <K : KtElement> remove(
      removeIn: PsiElement,
      declaration: Scope<KtExpressionCodeFragment>
    ): Transform<K> = Remove(removeIn, listOf(declaration))

    fun <K : KtElement> remove(
      removeIn: PsiElement,
      declarations: List<Scope<KtExpressionCodeFragment>>
    ): Transform<K> = Remove(removeIn, declarations)

    fun <K : KtElement> newSources(
      vararg files: File
    ): Transform<K> = NewSource(files.toList())

    val empty: Transform<Nothing> = Empty
  }
}

operator fun <K : KtElement> Transform<K>.plus(transform: Transform<K>): Transform.Many<K> =
  Transform.Many(arrayListOf(this, transform))

operator fun <K : KtElement> Transform.Many<K>.plus(transform: Transform<K>): Transform.Many<K> =
  Transform.Many(this.transforms.also { it.add(transform) })
