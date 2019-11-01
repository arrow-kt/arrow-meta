package arrow.meta.ide.testing.env

import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.IdeTestEnvironment
import org.junit.Assert

fun <A> IdeTest<A>.runTest(interpreter: (IdeTest<A>) -> Unit = ::interpreter): Unit =
  interpreter(this)

fun <A> IdeTest<A>.testResult(): A =
  test(IdeTestEnvironment, code, myFixture)

fun <A> interpreter(ideTest: IdeTest<A>): Unit =
  ideTest.run {
    val a = testResult()
    println("IdeTest results in $a")
    Assert.assertNotNull(result.message, result.transform(a))
  }

fun <A> ideTest(vararg tests: IdeTest<A>): Unit =
  tests.toList().forEach { it.runTest() }