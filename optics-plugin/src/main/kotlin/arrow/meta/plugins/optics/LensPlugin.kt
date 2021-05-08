package arrow.meta.plugins.optics

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classDeclaration
import arrow.meta.quotes.classorobject.ClassDeclaration
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.Property
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.source.KotlinSourceElement
import org.jetbrains.kotlin.resolve.source.toSourceElement
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val Meta.lenses: CliPlugin
  get() =
    "lenses" {
      meta(
        classDeclaration(this, { isProductType(element) }) { c ->
          validateMaxArityAllowed(this)
          Transform.replace<KtClass>(
            replacing = c.element,
            newDeclaration =
            if (c.element.companionObjects.isEmpty())
              """|
                 |$kind $name $`(params)` {
                 |  
                 |  companion object {
                 |    ${lenses(this)}
                 |    ${iso(this)}
                 |  }
                 |}""".`class`
            else
              """
                 |$kind $name $`(params)` {
                 |  ${body.value?.addDeclarationToBody(lenses = lenses(this))}
                 |  
                 |}""".`class`
          )
        }
      )
    }

private fun CompilerContext.validateMaxArityAllowed(classScope: ClassDeclaration) {
  if (classScope.`(params)`.value.size > 10)
  // Question: error message file location
    messageCollector?.report(
      CompilerMessageSeverity.WARNING,
      "Iso cannot be generated for product type with ${classScope.`(params)`.value.size}. Maximum support is $maxArity"
    )
}

private const val maxArity: Int = 10

private fun ElementScope.lenses(classScope: ClassDeclaration): ScopedList<KtProperty> =
  classScope.run {
    ScopedList(
      separator = "\n",
      value = `(params)`.value.mapNotNull { param: KtParameter ->
        lens(source = value, focus = param).value
      }
    )
  }

private fun ElementScope.lens(source: KtClass, focus: KtParameter): Property =
  """|val ${focus.name}: arrow.optics.Lens<${source.name}, ${focus.typeReference!!.text}> = arrow.optics.Lens(
     |  get = { ${source.name!!.toLowerCase()} -> ${source.name!!.toLowerCase()}.${focus.name} },
     |  set = { ${source.name!!.toLowerCase()}, ${focus.name} -> ${source.name!!.toLowerCase()}.copy(${focus.name} = ${focus.name}) }
     |)""".property(null).syntheticElement

private fun ElementScope.iso(classScope: ClassDeclaration): Property =
  classScope.run {
    """|val iso: arrow.optics.Iso<${value.name}, ${`(params)`.tupledType}> = arrow.optics.Iso(
       |  get = { (${`(params)`.destructured}) -> ${`(params)`.tupled} },
       |  reverseGet = { (${`(params)`.destructured}) -> ${value.name}(${`(params)`.destructured}) }
       |)""".property(null).syntheticElement
  }

val ScopedList<KtParameter>.tupledType: String
  // get() = "Tuple${value.size}<${value.joinToString { it.typeReference!!.text }}>"
  get() = "Pair<${value.joinToString { it.typeReference!!.text }}>"

val ScopedList<KtParameter>.tupled: String
  // get() = "Tuple${value.size}($destructured)"
  get() = "Pair($destructured)"

val ScopedList<KtParameter>.destructured: String
  get() = value.joinToString { it.name!! }

fun isProductType(ktClass: KtClass): Boolean =
  ktClass.isData() &&
    ktClass.primaryConstructorParameters.isNotEmpty() &&
    ktClass.primaryConstructorParameters.all { !it.isMutable } &&
    ktClass.typeParameters.isEmpty()

fun KtClassBody.addDeclarationToBody(lenses: ScopedList<KtProperty>): String =
  declarations.joinToString("\n") { declaration ->
    if (declaration is KtObjectDeclaration && declaration.isCompanion()) lenses.toString()
    else declaration.text
  }
