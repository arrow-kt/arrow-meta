package arrow.meta.plugins.analysis.phases.ir

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import java.io.File
import java.util.UUID

fun CompilerContext.hintsFile(
  parentPath: String,
  descriptor: ModuleDescriptor,
  packages: Set<FqName>
): File =
  File(
    parentPath,
    "/AnalysisHints.kt",
  ).also {
    it.createNewFile()
    it.writeText(hints(descriptor, packages))
  }

fun hints(descriptor: ModuleDescriptor, packages: Set<FqName>): String {
  val hintPackageName =
    descriptor.stableName?.asString()?.replace('.', '_')
      ?: "unknown_${UUID.randomUUID().toString().replace('-', '_')}"
  val packageList = packages.joinToString() { name ->
    "\"${name.name}\""
  }
  return """
         |package arrow.analysis.hints
         |
         |@arrow.analysis.PackagesWithLaws([$packageList])
         |class hints_for_$hintPackageName { }
         |""".trimMargin()
}
