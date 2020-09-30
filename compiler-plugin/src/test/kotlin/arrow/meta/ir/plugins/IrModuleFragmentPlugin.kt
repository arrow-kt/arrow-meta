package arrow.meta.ir.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke

val Meta.irModuleFragmentPlugin: CliPlugin
  get() = "IrModuleFragmentPlugin" {
    meta(
      irModuleFragment { module ->
        assert(false){
          "IrModuleFragment is visited"
        }
        null
      }
    )
  }