package arrow.meta.plugins.liquid.phases

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.liquid.phases.analysis.solver.SolverState
import arrow.meta.plugins.liquid.phases.analysis.solver.checkDeclarationConstraints
import arrow.meta.plugins.liquid.phases.analysis.solver.collectDeclarationsConstraints
import arrow.meta.plugins.liquid.phases.analysis.solver.finalizeConstraintsCollection
import arrow.meta.plugins.liquid.phases.ir.annotateWithConstraints
import org.jetbrains.kotlin.descriptors.ModuleDescriptor

internal fun Meta.liquidDataflowPhases(): ExtensionPhase =
  Composite(
    listOf(
      analysis(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          ensureSolverStateInitialization(module)
          null
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          finalizeConstraintsCollection(module, bindingTrace)
        },
      ),
      declarationChecker { declaration, descriptor, context ->
        collectDeclarationsConstraints(context, declaration, descriptor)
      },
      declarationChecker { declaration, descriptor, context ->
        checkDeclarationConstraints(context, declaration, descriptor)
      },
      irFunction { fn ->
        annotateWithConstraints(fn)
        null
      },
      irDumpKotlinLike()
    )
  )


internal fun CompilerContext.ensureSolverStateInitialization(
  module: ModuleDescriptor
) {
  val solverState = get<SolverState>(SolverState.key(module))
  if (solverState == null) {
    val state = SolverState()
    set(SolverState.key(module), state)
  }
}
