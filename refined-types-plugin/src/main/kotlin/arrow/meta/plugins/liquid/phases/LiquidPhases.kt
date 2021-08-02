package arrow.meta.plugins.liquid.phases

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.plugins.liquid.phases.analysis.solver.DeclarationConstraints
import arrow.meta.plugins.liquid.phases.analysis.solver.checkDeclarationConstraints
import arrow.meta.plugins.liquid.phases.analysis.solver.collectDeclarationsConstraints
import arrow.meta.plugins.liquid.phases.analysis.solver.finalizeConstraintsCollection
import arrow.meta.plugins.liquid.phases.analysis.solver.SolverState
import arrow.meta.plugins.liquid.phases.analysis.solver.addClassPathConstraintsToSolverState
import arrow.meta.plugins.liquid.phases.analysis.solver.constraintsFromSolverState
import arrow.meta.plugins.liquid.phases.analysis.solver.declarationsWithConstraints
import arrow.meta.plugins.liquid.phases.ir.annotateWithConstraints
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrMutableAnnotationContainer
import org.jetbrains.kotlin.ir.descriptors.toIrBasedDescriptor
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.name.ClassId
import org.sosy_lab.java_smt.api.BooleanFormula

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
