@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import java.util.ListResourceBundle

@Suppress("unused")
public class AnalysisMessages : ListResourceBundle() {

  public companion object {
    public val InconsistentBodyPre: String = "inconsistent_body_pre"
    public val UnsatBodyPost: String = "unsat_body_post"
    public val UnsatCallPre: String = "unsat_call_pre"
    public val InconsistentCallPost: String = "inconsistent_call_post"
    public val InconsistentConditions: String = "inconsistent_conditions"
    public val InconsistentInvariants: String = "inconsistent_invariants"
    public val UnsatInvariants: String = "unsat_invariants"
    public val LiskovProblem: String = "liskov_problem"
    public val ErrorParsingPredicate: String = "error_parsing_predicate"
    public val UnsupportedElement: String = "unsupported_element"

    public val Errors: List<String> =
      listOf(
        InconsistentBodyPre,
        UnsatBodyPost,
        UnsatCallPre,
        InconsistentCallPost,
        InconsistentConditions,
        InconsistentInvariants,
        UnsatInvariants,
        LiskovProblem,
        ErrorParsingPredicate
      )

    public val Warnings: List<String> = listOf(UnsupportedElement)
  }

  // "err" and "warn" come from [DiagnosticType.key]
  override fun getContents(): Array<Array<Any>> =
    (Errors.map<String, Array<Any>> { arrayOf("arrow-analysis.err.$it", "{0}") } +
        Warnings.map<String, Array<Any>> { arrayOf("arrow-analysis.warn.$it", "{0}") })
      .toTypedArray()
}
