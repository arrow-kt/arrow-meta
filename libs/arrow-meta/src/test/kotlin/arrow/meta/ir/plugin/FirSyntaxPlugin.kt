package arrow.meta.ir.plugin

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.psi

open class FirSyntaxPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf(
      "FirSyntaxPlugin" {
        meta(
          fir(
            firStatusTransformerExtension = {
              object : FirStatusTransformerExtension(it) {
                override fun needTransformStatus(declaration: FirDeclaration): Boolean {
                  println(declaration.psi?.text)
                  return false
                }
              }
            }
          )
        )
      }
    )
}
