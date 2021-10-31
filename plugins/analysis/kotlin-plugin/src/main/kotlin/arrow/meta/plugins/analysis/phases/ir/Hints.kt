package arrow.meta.plugins.analysis.phases.ir

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import java.io.File
import java.util.UUID
import org.jetbrains.kotlin.descriptors.ModuleDescriptor

fun CompilerContext.hintsFile(
  parentPath: String,
  descriptor: ModuleDescriptor,
  packages: Set<FqName>
): File =
  File(
      parentPath,
      "/AnalysisHints.kt",
    )
    .also {
      it.createNewFile()
      it.writeText(hints(descriptor, packages))
    }

fun hints(descriptor: ModuleDescriptor, packages: Set<FqName>): String {
  val hintPackageName =
    descriptor.stableName?.asString()?.replace('.', '_')
      ?: "unknown_${UUID.randomUUID().toString().replace('-', '_')}"
  val packageList = packages.joinToString() { name -> "\"${name.name}\"" }
  return """
         |package arrow.analysis.hints
         |
         |import arrow.analysis.PackagesWithLaws
         |
         |@PackagesWithLaws([$packageList])
         |class hints_$hintPackageName { }
         |""".trimMargin()
}

enum class HintState {
  NeedsProcessing,
  Processed
}
