package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.namedFunction
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

open class NamedFunctionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf(namedFunctionPlugin, namedFunctionDescriptorPlugin)
}

val Meta.namedFunctionPlugin: CliPlugin
  get() = "Named Function Scope Plugin" {
    meta(
      namedFunction(this, { true }) { (namedFunction, _) ->
        Transform.replace(replacing = namedFunction, newDeclaration = identity(descriptor))
      }
    )
  }

val Meta.namedFunctionDescriptorPlugin: CliPlugin
  get() = "Transform Replace Function Descriptor" {
    meta(
      namedFunction(
        this,
        {
          descriptor?.returnType.subtypeOf("CharSequence") && element.name == "descriptorEvaluation"
        }
      ) { (f, descriptor) ->
        Transform.replace(
          replacing = f,
          newDeclaration =
            """
            $modifiers fun $receiver $name $`(params)` $returnType {
              println(${descriptor != null})
              return ""
            }
          """.function(
              descriptor
            )
        )
      }
    )
  }

private fun KotlinType?.subtypeOf(type: String): Boolean =
  this?.supertypes()?.any { it.toString() == type } == true
