package arrow.meta.plugins.analysis.phases.ir

import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.filebase.File
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName
import java.util.UUID

fun CompilerContext.hintsFile(descriptor: ModuleDescriptor, packages: Set<FqName>): File =
  hints(descriptor, packages).file("AnalysisHints.kt")

fun hints(descriptor: ModuleDescriptor, packages: Set<FqName>): String {
  val hintPackageName =
    descriptor.stableName?.asString()?.replace('.', '_') ?:
      "unknown.id${UUID.randomUUID().toString().replace('-', '_')}"
  val packageList = packages.joinToString() { name ->
    "\"${name.asString()}\""
  }
  return """
         |@file:arrow.analysis.DeclaresLawsIn(${packageList}")
         |package arrow.analysis.hints.${hintPackageName}
         |""".trimMargin()
}
