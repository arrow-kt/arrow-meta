@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import java.util.ListResourceBundle

@Suppress("unused")
public class AnalysisMessages : ListResourceBundle() {

  public companion object {
    public const val InconsistentBodyPre: String = "inconsistent_body_pre"
    public const val UnsatBodyPost: String = "unsat_body_post"
    public const val UnsatCallPre: String = "unsat_call_pre"
    public const val InconsistentCallPost: String = "inconsistent_call_post"
    public const val InconsistentConditions: String = "inconsistent_conditions"
    public const val InconsistentInvariants: String = "inconsistent_invariants"
    public const val UnsatInvariants: String = "unsat_invariants"
    public const val LiskovProblem: String = "liskov_problem"
    public const val ErrorParsingPredicate: String = "error_parsing_predicate"
    public const val UnsupportedElement: String = "unsupported_element"
    public const val AnalysisException: String = "analysis_exception"

    public val Errors: List<String> =
      listOf(
        InconsistentBodyPre,
        UnsatBodyPost,
        UnsatCallPre,
        InconsistentInvariants,
        UnsatInvariants,
        LiskovProblem,
        ErrorParsingPredicate,
        AnalysisException
      )

    public val Warnings: List<String> =
      listOf(InconsistentCallPost, InconsistentConditions, UnsupportedElement)
  }

  // "err" and "warn" come from [DiagnosticType.key]
  override fun getContents(): Array<Array<Any>> =
    (Errors.map<String, Array<Any>> { arrayOf("arrow-analysis.err.$it", "{0}") } +
        Warnings.map<String, Array<Any>> { arrayOf("arrow-analysis.warn.$it", "{0}") })
      .toTypedArray()
}
