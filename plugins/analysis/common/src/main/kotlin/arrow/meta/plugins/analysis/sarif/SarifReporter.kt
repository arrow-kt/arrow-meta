package arrow.meta.plugins.analysis.sarif

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CompilerMessageSourceLocation
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorIds
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.SeverityLevel
import io.github.detekt.sarif4k.ArtifactLocation
import io.github.detekt.sarif4k.Level
import io.github.detekt.sarif4k.Location
import io.github.detekt.sarif4k.Message
import io.github.detekt.sarif4k.MultiformatMessageString
import io.github.detekt.sarif4k.PhysicalLocation
import io.github.detekt.sarif4k.Region
import io.github.detekt.sarif4k.ReportingDescriptor
import io.github.detekt.sarif4k.ResultKind
import io.github.detekt.sarif4k.Run
import io.github.detekt.sarif4k.SarifSchema210
import io.github.detekt.sarif4k.SarifSerializer
import io.github.detekt.sarif4k.Tool
import io.github.detekt.sarif4k.ToolComponent
import io.github.detekt.sarif4k.Version
import kotlin.io.path.Path

fun sarifFileContent(
  baseDir: String,
  analysisVersion: String,
  errors: List<ReportedError>
): String {
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
                      errors
                        .map(ReportedError::errorsId)
                        .distinctBy(ErrorIds::id)
                        .map(ErrorIds::toDescriptor),
                    organization = "arrow-kt",
                    semanticVersion = analysisVersion,
                    version = analysisVersion
                  )
              ),
            results = toResults(baseDir, errors)
          )
        )
    )
  return SarifSerializer.toJson(sarifSchema210)
}

private fun ErrorIds.toDescriptor(): ReportingDescriptor =
  ReportingDescriptor(
    id = "arrow.analysis.$id",
    name = name,
    shortDescription = MultiformatMessageString(text = shortDescription),
    fullDescription =
      when (val d = fullDescription) {
        null -> MultiformatMessageString(text = shortDescription)
        else -> MultiformatMessageString(markdown = d, text = d)
      },
    helpURI = "https://arrow-kt.io/docs/meta/analysis/${id}.html"
  )

fun toResults(baseDir: String, errors: List<ReportedError>): List<io.github.detekt.sarif4k.Result> =
  errors.map { it.toResult(baseDir) }

private fun SeverityLevel.toResultLevel(): Level =
  when (this) {
    SeverityLevel.Error -> Level.Error
    SeverityLevel.Warning -> Level.Warning
    SeverityLevel.Info -> Level.Note
    SeverityLevel.Unsupported -> Level.None
  }

private fun SeverityLevel.toResultKind(): ResultKind =
  when (this) {
    SeverityLevel.Unsupported -> ResultKind.NotApplicable
    else -> ResultKind.Fail
  }

private fun ReportedError.toResult(baseDir: String) =
  io.github.detekt.sarif4k.Result(
    ruleID = "arrow.analysis.$id",
    kind = errorsId.level.toResultKind(),
    level = errorsId.level.toResultLevel(),
    locations =
      (listOf(element.location()) + references.map { it.location() })
        .mapNotNull { it?.toLocation(baseDir) }
        .toSet()
        .toList(),
    message = Message(text = msg)
  )

private fun rel(baseDir: String, path: String): String {
  val p = Path(path)
  return if (p.isAbsolute) Path(baseDir).relativize(p).toString() else path
}

private fun CompilerMessageSourceLocation.toLocation(baseDir: String): Location =
  Location(
    physicalLocation =
      PhysicalLocation(
        region =
          Region(
            startLine = line.toLong(),
            startColumn = column.toLong(),
          ),
        artifactLocation = ArtifactLocation(uri = rel(baseDir, path))
      )
  )
