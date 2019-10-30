package arrow.meta.ide.testing.env

import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.IdeTestEnvironment
import org.junit.Assert

fun <A> IdeTest<A>.runTest(interpreter: (IdeTest<A>) -> Unit = ::interpreter): Unit =
  interpreter(this)

fun <A> interpreter(test: IdeTest<A>): Unit {
  val result: A = test.test(IdeTestEnvironment, test.code)
  Assert.assertNotNull(test.result.message, test.result.transform(result))
}
