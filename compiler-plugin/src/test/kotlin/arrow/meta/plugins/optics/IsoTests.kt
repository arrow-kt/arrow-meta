package arrow.meta.plugins.optics

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.AssertSyntax
import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.optics.internals.isoErrorMessage
import arrow.meta.plugins.optics.internals.isoTooBigErrorMessage
import arrow.meta.plugins.optics.internals.noCompanion
import org.junit.jupiter.api.Test

class OpticsTests {

  companion object {
    const val model =
      """
      import arrow.Optics
      //metadebug
      """

    private fun dslModel() =
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
  }

  @Test
  fun `Iso generation requires companion object declaration`() {
    """
      |$model
      |@Optics
      |data class IsoNoCompanion(
      |  val field1: String
      |)
      """ { failsWith { it.contains("IsoNoCompanion".noCompanion) } }
  }

  @Test
  fun `Isos cannot be generated for huge classes`() {
    """
      |$model
      |@Optics
      |data class IsoXXL(
      |  val field1: String,
      |  val field2: String,
      |  val field3: String,
      |  val field4: String,
      |  val field5: String,
      |  val field6: String,
      |  val field7: String,
      |  val field8: String,
      |  val field9: String,
      |  val field10: String,
      |  val field11: String,
      |  val field12: String,
      |  val field13: String,
      |  val field14: String,
      |  val field15: String,
      |  val field16: String,
      |  val field17: String,
      |  val field18: String,
      |  val field19: String,
      |  val field20: String,
      |  val field21: String,
      |  val field22: String,
      |  val field23: String
      |) {
      |  companion object
      |}
      """ { failsWith { it.contains("IsoXXL".isoTooBigErrorMessage) } }
  }
}

private operator fun String.invoke(assert: AssertSyntax.() -> Assert) {
  val arrowVersion = System.getProperty("ARROW_VERSION")
  val arrowAnnotations = Dependency("arrow-annotations:$arrowVersion")
  val arrowCore = Dependency("arrow-core-data:$arrowVersion")
  val arrowOptics = Dependency("arrow-optics:$arrowVersion")
  assertThis(CompilerTest(
    config = { metaDependencies + addDependencies(arrowAnnotations, arrowCore, arrowOptics) },
    code = { this@invoke.source },
    assert = { assert() }
  ))
}