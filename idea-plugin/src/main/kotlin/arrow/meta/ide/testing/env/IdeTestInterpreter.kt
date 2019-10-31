package arrow.meta.ide.testing.env

import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.IdeTestEnvironment
import org.junit.Assert

fun <A> IdeTest<A>.runTest(interpreter: (IdeTest<A>) -> Unit = ::interpreter): Unit =
  interpreter(this)

fun <A> IdeTest<A>.testResult(): A? =
  result.transform(test(IdeTestEnvironment, code))

fun <A> interpreter(ideTest: IdeTest<A>): Unit {
  Assert.assertNotNull(ideTest.result.message, ideTest.testResult())
}