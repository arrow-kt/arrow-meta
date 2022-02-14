package arrow.meta.ir.plugin

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.dsl.fir.additionalCheckers
import arrow.meta.dsl.fir.declarationChecker
import arrow.meta.dsl.fir.declarationGeneration
import arrow.meta.dsl.fir.declarationStatus
import arrow.meta.dsl.fir.statusTransformer
import arrow.meta.dsl.fir.superTypeGeneration
import arrow.meta.dsl.fir.typeAttribute
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration

open class FirSyntaxPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf(
      "FirSyntaxPlugin" {
        meta(
          fir(
            additionalCheckers = {
              additionalCheckers(
                callableDeclarationCheckers =
                  setOf(
                    declarationChecker {
                      a: FirCallableDeclaration,
                      b: CheckerContext,
                      c: DiagnosticReporter ->
                    }
                  )
              )
            },
            declarationGeneration = {
              declarationGeneration(
                generateFunctions = { a, b -> emptyList() },
              )
            },
            statusTransformer = {
              statusTransformer(
                needTransformStatus = { true },
                transformStatus = { _, _ ->
                  declarationStatus(visibility = Visibilities.Public, modality = Modality.OPEN)
                }
              )
            },
            supertypeGeneration = { superTypeGeneration() },
            typeAttribute = { typeAttribute() }
          )
        )
      }
    )
}
