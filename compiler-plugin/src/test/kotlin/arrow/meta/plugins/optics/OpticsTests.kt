package arrow.meta.plugins.optics

import arrow.meta.phases.analysis.DefaultElementScope.Companion.DEFAULT_BASE_DIR
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.transform.TransformNewSourceTest
import org.junit.jupiter.api.Test

class OpticsTests {

  companion object {
    const val model =
      """
      import arrow.Optics
      //metadebug
      
      @Optics data class Street(val number: Int, val name: String) {
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
      }
      """
  }

  @Test
  fun `simple case`() {
    val codeSnippet =
      """
      $model
      |
      | 
      """
    val arrowVersion = System.getProperty("ARROW_VERSION")
    val arrowAnnotations = Dependency("arrow-annotations:$arrowVersion")
    val arrowCore = Dependency("arrow-core-data:$arrowVersion")
    val arrowOptics = Dependency("arrow-optics:$arrowVersion")
    assertThis(CompilerTest(
      config = { metaDependencies + addDependencies(arrowAnnotations, arrowCore, arrowOptics) },
      code = { codeSnippet.source },
      assert = {
        quoteFileMatches("NewSource_Generated.kt",
          """
           noop
        """.source,
          sourcePath = DEFAULT_BASE_DIR.resolve(TransformNewSourceTest.CUSTOM_GENERATED_SRC_PATH_1)
        )
      }
    ))
  }
}