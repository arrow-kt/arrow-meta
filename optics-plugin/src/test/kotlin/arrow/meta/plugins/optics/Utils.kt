package arrow.meta.plugins.optics

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.AssertSyntax
import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis

const val imports =
  """
      import arrow.Optics
      import arrow.optics.*
      import arrow.core.Tuple2
      //metadebug
      """

const val dslModel =
  """@Optics data class Street(val number: Int, val name: String) {
            companion object
          }
          @Optics data class Address(val city: String, val street: Street) {
            companion object
          }
          @Optics data class Company(val name: String, val address: Address) {
            companion object
          }
          @Optics data class Employee(val name: String, val company: Company?) {
            companion object
          }"""

operator fun String.invoke(assert: AssertSyntax.() -> Assert) {
  val arrowVersion = System.getProperty("ARROW_VERSION")
  val currentVersion = System.getProperty("CURRENT_VERSION")
  val opticsCompilerPlugin =
    CompilerPlugin("Arrow Meta Optics", listOf(Dependency("arrow-optics-plugin:$currentVersion")))
  val arrowAnnotations = Dependency("arrow-annotations:$arrowVersion")
  val arrowCore = Dependency("arrow-core-data:$arrowVersion")
  val arrowOptics = Dependency("arrow-optics:$arrowVersion")
  assertThis(CompilerTest(
    config = {
      metaDependencies +
        addCompilerPlugins(opticsCompilerPlugin) +
        addDependencies(arrowAnnotations, arrowCore, arrowOptics)
    },
    code = { this@invoke.source },
    assert = { assert() }
  ))
}
