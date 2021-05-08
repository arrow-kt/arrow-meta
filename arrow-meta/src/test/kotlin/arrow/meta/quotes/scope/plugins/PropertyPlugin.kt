package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.property
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

open class PropertyPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    propertyPlugin,
    propertyDescriptorPlugin
  )
}

val Meta.propertyPlugin
  get() = "Property scope plugin" {
    meta(
      property(this, { true }) { (property, _) ->
        Transform.replace(
          replacing = property,
          newDeclaration = identity(descriptor)
        )
      }
    )
  }

val Meta.propertyDescriptorPlugin
  get() = "Property Descriptor" {
    meta(
      property(this, { descriptor?.returnType.subtypeOf("CharSequence") && element.name == "descriptorEvaluation" }) { (property, descriptor) ->
        Transform.replace(
          replacing = property,
          newDeclaration = """ 
            val $name: Boolean = ${descriptor != null}
          """.property(descriptor)
        )
      }
    )
  }

private fun KotlinType?.subtypeOf(type: String): Boolean = this?.supertypes()?.any {
  it.toString() == type
} == true