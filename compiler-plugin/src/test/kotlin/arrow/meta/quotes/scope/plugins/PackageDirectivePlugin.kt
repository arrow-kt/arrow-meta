package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.packageDirective

open class PackageDirectivePlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    packageDirectivePlugin,
    packageDirectivePackageNames,
    packageDirectiveLastPackageName
  )
}

private val Meta.packageDirectivePlugin
  get() = "Package Directive Scope Plugin" {
    meta(
      packageDirective({ packageNames.last().text == "test" }) { e ->
        Transform.replace(
          replacing = e,
          newDeclaration = identity()
        )
      }
    )
  }

private val Meta.packageDirectivePackageNames
  get() = "Package Directive Package Names Scope Plugin" {
    meta(
      packageDirective({ packageNames.last().text == "package_names" }) { e ->
        Transform.replace(
          replacing = e,
          newDeclaration = identity()
        )
      }
    )
  }

private val Meta.packageDirectiveLastPackageName
  get() = "Package Directive Last Package Name Scope Plugin" {
    meta(
      packageDirective({ packageNames.last().text == "package_last_name" }) { e ->
        Transform.replace(
          replacing = e,
          newDeclaration = identity()
        )
      }
    )
  }