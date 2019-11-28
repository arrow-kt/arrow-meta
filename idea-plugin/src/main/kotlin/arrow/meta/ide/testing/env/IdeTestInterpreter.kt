package arrow.meta.ide.testing.env

import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.IdeTestEnvironment
import org.junit.Assert

/**
 * [runTest] executes the tests with a costume [interpreter], which allows users to run their test in every environment.
 * This allows user's to use the Testing DSL for IntelliJ in a pure or impure Test environment and compose it with a variety of
 * testing methods such as property-based testing, unit tests and many more.
 */
fun <A> IdeTest<A>.runTest(interpreter: (test: IdeTest<A>) -> Unit = ::interpreter): Unit =
  interpreter(this)

/**
 * [testResult] evaluates the actual result within the [IdeTestEnvironment]
 */
fun <A> IdeTest<A>.testResult(): A =
  test(IdeTestEnvironment, code, myFixture)

/**
 * The default [interpreter] evaluates and prints the actual result in an impure Environment.
 * In addition, it throws an [AssertionError] with the [result.messsage],
 * whenever the expected [result] doesn't match the actual result from [testResult].
 */
fun <A> interpreter(ideTest: IdeTest<A>): Unit =
  ideTest.run {
    val a = testResult()
    println("IdeTest results in $a")
    Assert.assertNotNull(result.message, result.transform(a))
  }

/**
 * Runs each ide test for selected editor features.
 * Each Test class has to extend [IdeTestSetUp] to spin-up the underlying IntelliJ Testing Platform.
 * ```kotlin
 * class ExampleTest: IdeTestSetUp() {
 *   /** test features **/
 * }
 * ```
 * One IdeTest bundles the necessary components for a testSuit and the general schema looks like this:
 * ```kotlin
 * @Test
 * fun `test feature`(): Unit =
 *  ideTest(
 *   IdeTest(
 *     myFixture, // the IntelliJ test environment spins up this value automatically
 *     code = "val exampleCode = 2",
 *     test = { code, myFixture ->
 *       /**
 *       * In addition to the parameters above the [IdeTestEnvironment] is in Scope, which bundles test operations
 *       * over the [IdeSyntax] and implements [IdeTestSyntax]. The latter composes a symmetric API over [IdeSyntax] in respect to tests.
 *       * That means an interface such as [LineMarkerSyntax] from the [IdeSyntax] is tested with [LineMarkerTestSyntax] from the [IdeTestSyntax].
 *       **/
 *     },
 *     result = // here we define what behavior is expected
 *    ),
 *    ... // you may add more test suit's for the same feature with different code examples and test's
 *  )
 * ```
 *
 * Given the example ide plugin from [TODO: Add URL] one concrete example may look like this:
 * ```kotlin
 * @Test
 * fun `test if lineMarker is displayed`(): Unit =
 *   ideTest(
 *     IdeTest(
 *       myFixture = myFixture,
 *       code = """
 *       | fun hello(): String =
 *       |   "Hello world!"
 *       """.trimIndent(),
 *       test = { code, myFixture ->
 *         collectLM(code, myFixture, {TODO: Add Icon from Isra})
 *         // this collect's all visible LineMarkers in the editor for a given Icon
 *       },
 *       result = resolves("LineMarker Test for helloWorld") {
 *         it.takeIf { collected ->
 *           collected.lineMarker.size == 1 // we expect that there is only one linemarker in our example code
 *         }
 *       }
 *     )
 *   )
 * ```
 */
fun <A> ideTest(vararg tests: IdeTest<A>): Unit =
  tests.toList().forEach { it.runTest() }