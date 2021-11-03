@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import java.util.ListResourceBundle

@Suppress("unused")
public class AnalysisMessages : ListResourceBundle() {
  override fun getContents(): Array<Array<Any>> =
    arrayOf(arrayOf("arrow-analysis.warn.hello", "Hello from {0}"))
}
