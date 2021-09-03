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
import arrow.meta.plugins.liquid.smt.utils.NameProvider
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

internal fun Meta.liquidDataflowPhases(): ExtensionPhase =
  Composite(
    listOf(
      analysis(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          ensureSolverStateInitialization(module)
          null
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          // module.lawScaffoldGenerationForPackage(FqName("kotlin.math"))
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

private fun ModuleDescriptor.lawScaffoldGenerationForPackage(pck: FqName) {
  getPackage(FqName("kotlin.math")).memberScope.getContributedDescriptors { true }
    .filterIsInstance<SimpleFunctionDescriptor>().filter {
      it.visibility.isPublicAPI && !it.annotations.hasAnnotation(FqName("kotlin.Deprecated"))
    }.forEach {
      val rendered = it.toString()
        .substringBeforeLast("defined in")
        .replace("(@.*?) ".toRegex(), "")
        .replace("operator", "")
        .replace("kotlin.", "")
        .replace("collections.", "")
        .replace(" = ...", "")
        .replace(
          it.name.asString(),
          it.name.asString() + "Law"
        )
      println(
        """
                  
                @Law  
                @JvmName("${it.name}Law${it.valueParameters.joinToString("") { it.type.toString() }}${it.returnType?.toString()}")
                $rendered {
                  pre(true) { "${it.fqNameSafe} pre-conditions" }
                  return ${it.name}(${it.valueParameters.joinToString { if (it.isVararg) "*" + it.name.asString() else it.name.asString() }})
                    .post({ true }, { "${it.fqNameSafe} post-conditions" })
                }
                """.trimIndent()
      )
    }

  TODO("stop")
}

internal fun CompilerContext.ensureSolverStateInitialization(
  module: ModuleDescriptor
) {
  val solverState = get<SolverState>(SolverState.key(module))
  if (solverState == null) {
    val state = SolverState(NameProvider())
    set(SolverState.key(module), state)
  }
}
