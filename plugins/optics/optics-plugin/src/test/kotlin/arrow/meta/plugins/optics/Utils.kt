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
      import arrow.core.None
      import arrow.optics.*
      import arrow.optics.dsl.*
      import arrow.optics.typeclasses.*
      //metadebug
      """

const val dslModel =
  """@optics data class Street(val number: Int, val name: String) {
            companion object
          }
          @optics data class Address(val city: String, val street: Street) {
            companion object
          }
          @optics data class Company(val name: String, val address: Address) {
            companion object
          }
          @optics data class Employee(val name: String, val company: Company?) {
            companion object
          }
          @optics data class Employees(val employees: List<Employee>) {
            companion object
          }
          sealed class Keys
          object One : Keys() {
            override fun toString(): String = "One"
          }
          object Two : Keys() {
            override fun toString(): String = "Two"
          }
          object Three : Keys() {
            override fun toString(): String = "Three"
          }
          object Four : Keys() {
            override fun toString(): String = "Four"
          }
          @optics data class Db(val content: Map<Keys, String>) {
            companion object
          }"""

const val dslValues =
  """      
      |val john = Employee("Audrey Tang",
      |       Company("Arrow",
      |               Address("Functional city",
      |                       Street(42, "lambda street"))))
      |val jane = Employee("Bestian Tang",
      |       Company("Arrow",
      |               Address("Functional city",
      |                       Street(42, "lambda street"))))
      |val employees = Employees(listOf(john, jane))
      |val db = Db(
      |  mapOf(
      |    One to "one",
      |    Two to "two",
      |    Three to "three",
      |    Four to "four"
      |  )
      |)"""

operator fun String.invoke(assert: AssertSyntax.() -> Assert) {
  val arrowVersion = System.getProperty("arrowVersion")
  val currentVersion = System.getProperty("CURRENT_VERSION")
  val opticsCompilerPlugin =
    CompilerPlugin("Arrow Meta Optics", listOf(Dependency("arrow-optics-plugin:$currentVersion")))
  val arrowAnnotations = Dependency("arrow-annotations:$arrowVersion")
  val arrowCore = Dependency("arrow-core:$arrowVersion")
  val arrowOptics = Dependency("arrow-optics:$arrowVersion")
  assertThis(
    CompilerTest(
      config = {
        metaDependencies +
          addCompilerPlugins(opticsCompilerPlugin) +
          addDependencies(arrowAnnotations, arrowCore, arrowOptics)
      },
      code = { this@invoke.source },
      assert = { assert() }
    )
  )
}
