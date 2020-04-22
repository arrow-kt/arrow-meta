package arrow.meta.plugins.fv

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke

val Meta.fv: CliPlugin
  get() =
    "SMT Plugin" {
      meta(
        /*addDiagnostics { trace ->
          trace.report()


        },
        storageComponent(
          registerModuleComponents = Noop.effect3,
          check = { declaration, descriptor, context ->

            declaration.dfs { it is KtBinaryExpression }.mapNotNull {
              it.safeAs<KtBinaryExpression>()?.operationToken
            }
          })*/
      )
    }