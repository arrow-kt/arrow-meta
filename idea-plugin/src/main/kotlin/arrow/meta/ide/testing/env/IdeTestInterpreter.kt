package arrow.meta.ide.testing.env

import arrow.meta.ide.dsl.IdeSyntax
import arrow.meta.ide.plugins.helloworld.helloWorld
import arrow.meta.ide.testing.IdeEnvironment
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.dsl.IdeTestSyntax
import arrow.meta.ide.testing.dsl.lineMarker.LineMarkerTestSyntax
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.junit.Assert

/**
 * [runTest] executes the test with a custom [interpreter], which facilitates to run a test in any environment, based on a plugin context [F].
 * Hence, the Testing DSL for IntelliJ supports pure or impure Test environments and allows to compose them in a variety of
 * testing methods such as property-based testing, unit tests and many more.
 * @see [arrow.meta.ide.testing.env.interpreter]
 */
fun <F : IdeSyntax, A> IdeTest<F, A>.runTest(
  fixture: CodeInsightTestFixture,
  ctx: F,
  interpreter: (test: IdeTest<F, A>, ctx: F, fixture: CodeInsightTestFixture) -> Unit = ::interpreter
): Unit =
  interpreter(this, ctx, fixture)

/**
 * [testResult] evaluates the actual result within the [IdeEnvironment]
 * @param ctx plugin context
 */
fun <F : IdeSyntax, A> IdeTest<F, A>.testResult(ctx: F, fixture: CodeInsightTestFixture): A =
  test(IdeEnvironment, code, fixture, ctx)

/**
 * The default [interpreter] evaluates and prints the actual result [A] on the console.
 * In addition, it throws an [AssertionError] with the [ideTest.result.message],
 * whenever the expected [ideTest.result] doesn't match the actual result from [testResult].
 */
fun <F : IdeSyntax, A> interpreter(ideTest: IdeTest<F, A>, ctx: F, fixture: CodeInsightTestFixture): Unit =
  ideTest.run {
    val a = testResult(ctx, fixture)
    println("IdeTest results in $a")
    Assert.assertNotNull(result.message, result.transform(ctx, a))
  }

/**
 * This extension runs each test for selected editor features.
 * Each Test class has to extend [IdeTestSetUp], in order to spin-up the underlying Intellij Testing Platform at runtime.
 * That also insures that the property [myFixture] is properly instantiated.
 * ```kotlin
 * class ExampleTest: IdeTestSetUp() {
 *   /** test features **/
 * }
 * ```
 * One IdeTest is composed by the source code, a test described with algebras in [IdeTestSyntax], and the expected result.
 * Based on a dummy IdePlugin
 * ```kotlin
 * class MyIdePlugin : IdeSyntax
 * ```
 * a general schema looks like this:
 * ```kotlin:ank:playground
 * import arrow.meta.ide.dsl.IdeSyntax
 * import arrow.meta.ide.testing.IdeTest
 * import arrow.meta.ide.testing.Source
 * import arrow.meta.ide.testing.env.IdeTestSetUp
 * import arrow.meta.ide.testing.env.ideTest
 * import com.intellij.testFramework.fixtures.CodeInsightTestFixture
 * import org.junit.Test
 * //sampleStart
 * class ExampleTest : IdeTestSetUp() {
 *   @Test
 *   fun `general test schema`(): Unit =
 *     ideTest(
 *       myFixture = myFixture, // the IntelliJ test environment spins up [myFixture] automatically at runtime.
 *       ctx = MyIdePlugin() // add here your ide plugin, to get dependencies and created features in the scope of [test] and [result].
 *     ) {
 *       listOf<IdeTest<MyIdePlugin, Unit>>( // type inference is not able to resolve the types here
 *         IdeTest(
 *           "val exampleCode = 2",
 *           test = { code: Source, myFixture: CodeInsightTestFixture, ctx: MyIdePlugin ->
 *             /**
 *              * In addition to the parameters above the [IdeEnvironment] is in Scope, which bundles test operations in [IdeTestSyntax]
 *              * over the [IdeSyntax]. [IdeTestSyntax] composes a symmetric API over [IdeSyntax] in respect to tests.
 *              * That means an interface such as [LineMarkerSyntax] from the [IdeSyntax] is tested with [LineMarkerTestSyntax] from the [IdeTestSyntax].
 *              * Furthermore, one can use the scope of [ctx] to use declared dependencies and features in the test.
 *              **/
 *           },
 *           result = resolves("Any result is accepted") // here we define what behavior is expected
 *         ) // ... you may add more test suit's for the same or related ide feature with different code examples and test's
 *       )
 *     }
 * }
 * //sampleEnd
 * class MyIdePlugin : IdeSyntax
 * ```
 * Given the [helloWorld] ide plugin, one concrete example may look like this:
 * ```kotlin
 * @Test
 * fun `hello World LineMarker is displayed`(): Unit =
 *   ideTest(
 *     myFixture = myFixture,
 *     ctx = IdeMetaPlugin()
 *   ) {
 *     listOf<IdeTest<IdeMetaPlugin, LineMarkerDescription>>(
 *       IdeTest(
 *         code = """
 *         | fun helloWorld(): String =
 *         |   "Hello world!"
 *         """.trimIndent(),
 *         test = { code: Source, myFixture: CodeInsightTestFixture, ctx ->
 *           collectLM(code, myFixture, ArrowIcons.ICON1) // this collect's all visible LineMarkers in the editor for the given Icon
 *         },
 *         result = resolvesWhen("LineMarker Test for helloWorld") {
 *           it.lineMarker.size == 1 // we expect that there is only one lineMarker in our example code
 *         }
 *       )
 *     )
 *   }
 * ```
 * The key in `result` is to define the shape of the expected outcome, whether the test represents a valid or invalid representation of [A].
 * @see [runTest], [IdeTest], [LineMarkerTestSyntax]
 * @param myFixture is a key component of the underlying Intellij Testing API.
 * @param ctx is the plugin context if unspecified it will use the [IdeEnvironment]
 */
fun <F : IdeSyntax, A> ideTest(
  myFixture: CodeInsightTestFixture,
  ctx: F = IdeEnvironment as F,
  tests: IdeEnvironment.() -> List<IdeTest<F, A>>
): Unit =
  tests(IdeEnvironment).forEach { it.runTest(myFixture, ctx) }
