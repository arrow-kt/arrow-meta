package arrow.meta.ide.plugins.lens

import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import arrow.meta.ide.testing.resolves
import org.junit.Test

class LensTest : IdeTestSetUp(){
  @Test
  fun `Optics Test for LineMarkers`() =
    ideTest(
      IdeTest(
        code = LensTestCode.code1,
        myFixture = myFixture,
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.OPTICS)
        },
        result = resolves("LineMarkerTest for no LM "){
          it.takeIf { descriptor ->  descriptor.lineMarker.size == 3 && descriptor.slowLM.isEmpty()}
        }
      )
    )
}