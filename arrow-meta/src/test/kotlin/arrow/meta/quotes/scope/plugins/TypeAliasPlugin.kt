package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.typeAlias
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

open class TypeAliasPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    typeAliasPlugin,
    typeAliasDescriptorPlugin
  )
}

val Meta.typeAliasPlugin
  get() =
    "Type Alias Expression Scope Plugin" {
      meta(
        typeAlias(this, { true }) { (element, _) ->
          Transform.replace(
            replacing = element,
            newDeclaration = identity(descriptor)
          )
        }
      )
    }

val Meta.typeAliasDescriptorPlugin
  get() =
    "Type Alias Descriptor Plugin" {
      meta(
        typeAlias(this, { element.name == "DescriptorEvaluation" }) { (element, descriptor) ->
          Transform.replace(
            replacing = element,
            newDeclaration = typeAlias("""$name""", `(typeParams)`.toStringList() , """${descriptor?.let { "Boolean" } ?: type}""", descriptor)
          )
        }
      )
    }