package arrow.meta.plugins.proofs

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.dsl.fir.additionalCheckers
import arrow.meta.invoke

val Meta.typeProofs: CliPlugin
  get() = "@contextual plugin" {
    meta(
      fir(
        additionalCheckers = {
          additionalCheckers(

          )
        }
      ),
      irDumpKotlinLike()
    )
  }
