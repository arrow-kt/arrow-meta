package arrow.meta.idea.test.syntax

import io.kotlintest.TestCase
import io.kotlintest.TestContext
import io.kotlintest.TestType
import io.kotlintest.specs.AbstractStringSpec

abstract class IdeTestSyntax : AbstractStringSpec() {
  private val lawTestCases = mutableListOf<TestCase>()

  fun testLaws(vararg laws: List<IdeLaw>): List<TestCase> = laws
    .flatMap { list: List<IdeLaw> -> list.asIterable() }
    .distinctBy { law: IdeLaw -> law.name }
    .map { law: IdeLaw ->
      val lawTestCase = createTestCase(law.name, law.test, defaultTestCaseConfig, TestType.Test)
      lawTestCases.add(lawTestCase)
      lawTestCase
    }

  override fun testCases(): List<TestCase> = super.testCases() + lawTestCases
}


data class IdeLaw(val name: String, val test: suspend TestContext.() -> Unit)