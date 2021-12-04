package arrow.meta.plugins.analysis.sarif

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CompilerMessageSourceLocation
import io.github.detekt.sarif4k.ArtifactLocation
import io.github.detekt.sarif4k.Level
import io.github.detekt.sarif4k.Location
import io.github.detekt.sarif4k.Message
import io.github.detekt.sarif4k.MultiformatMessageString
import io.github.detekt.sarif4k.PhysicalLocation
import io.github.detekt.sarif4k.Region
import io.github.detekt.sarif4k.ReportingDescriptor
import io.github.detekt.sarif4k.Run
import io.github.detekt.sarif4k.SarifSchema210
import io.github.detekt.sarif4k.SarifSerializer
import io.github.detekt.sarif4k.Tool
import io.github.detekt.sarif4k.ToolComponent
import io.github.detekt.sarif4k.Version

fun sarifFileContent(analysisVersion: String, errors: List<ReportedError>): String {
  val sarifSchema210 =
    SarifSchema210(
      schema =
        "https://raw.githubusercontent.com/oasis-tcs/sarif-spec/master/Schemata/sarif-schema-2.1.0.json",
      version = Version.The210,
      runs =
        listOf(
          Run(
            tool =
              Tool(
                driver =
                  ToolComponent(
                    downloadURI = "https://arrow-kt.io/docs/meta/analysis-quickstart.html",
                    fullName = "Arrow Analysis",
                    informationURI = "https://arrow-kt.io/docs/meta/analysis-quickstart.html",
                    language = "en",
                    name = "Arrow Analysis",
                    rules =
                      errors.map {
                        ReportingDescriptor(
                          id = it.errorsId.id,
                          name = it.errorsId.name,
                          shortDescription =
                            MultiformatMessageString(text = it.errorsId.shortDescription),
                          helpURI = "https://arrow-kt.io/docs/meta/analysis/${it.errorsId.id}.html"
                        )
                      },
                    organization = "arrow-kt",
                    semanticVersion = analysisVersion,
                    version = analysisVersion
                  )
              ),
            results = toResults(errors)
          )
        )
    )
  return SarifSerializer.toJson(sarifSchema210)
}

enum class SeverityLevel {
  Error,
  Warning,
  Info
}

fun toResults(errors: List<ReportedError>): List<io.github.detekt.sarif4k.Result> =
  errors.map(ReportedError::toResult)

private fun SeverityLevel.toResultLevel(): Level =
  when (this) {
    SeverityLevel.Error -> Level.Error
    SeverityLevel.Warning -> Level.Warning
    SeverityLevel.Info -> Level.Note
  }

private fun ReportedError.toResult() =
  io.github.detekt.sarif4k.Result(
    ruleID = "arrow.analysis.$id",
    level = severity.toResultLevel(),
    locations =
      (listOf(element.location()) + references.map { it.location() })
        .mapNotNull { it?.toLocation() }
        .toSet()
        .toList(),
    message = Message(text = msg)
  )

private fun CompilerMessageSourceLocation.toLocation() =
  Location(
    physicalLocation =
      PhysicalLocation(
        region =
          Region(
            startLine = line.toLong(),
            startColumn = column.toLong(),
          ),
        artifactLocation = ArtifactLocation(uri = path)
      )
  )
