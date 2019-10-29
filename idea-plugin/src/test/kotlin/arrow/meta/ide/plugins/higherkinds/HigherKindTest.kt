package arrow.meta.ide.plugins.higherkinds

import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.dsl.lineMarker.IconDescription
import arrow.meta.ide.testing.env.runTest
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.psi.PsiElement
import org.junit.Test

class HigherKindTest {
  @Test
  fun `LM Test`() =
    IdeTest<Int>(
      code = HigherKindsTestCode.code,
      test = { code ->
        testLineMarkers(code, IconDescription(ArrowIcons.HKT, "HKT LineMarker"),
          slowLM = { slow: List<LineMarkerInfo<PsiElement>> ->
            slow.takeIf { it.isEmpty() }
          },
          lm = { lm: List<LineMarkerInfo<PsiElement>> ->
            lm.takeIf { it.size == 1 }
          })
      },
      result = {
        resolves
      }
    ).runTest()


}