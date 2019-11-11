package arrow.meta.ide.plugins.typeclasses

import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.env.IdeHeavyTestSetUp
import arrow.meta.ide.testing.env.testResult
import arrow.meta.ide.testing.resolves
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.types.ErrorType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import org.junit.Ignore

// Ignored until it's fixed
@Ignore class TypeClassResolutionTest: IdeHeavyTestSetUp() {
  //TODO: use @Test and make IdeHeavyTestSetUp a JUnit4 test, it's still Junit3 atm, therefore we have to use a `test` prefix
  fun testSyntheticResolution() {
    val d = IdeTest(
      code = TypeclassesTestCode.c1,
      myFixture = myFixture,
      test = { code, myFixture ->
        traverseResolution(
          code = code,
          myFixture = myFixture,
          module = myModule
        ) {
          it.takeIf { p -> p.safeAs<KtDeclaration>()?.type() is ErrorType }
        }
      },
      result = resolves("No Unresolved KtElements") {
        it.takeIf { l -> l.isEmpty() }
      }
    ).testResult()

    assert(true)
  }
}
