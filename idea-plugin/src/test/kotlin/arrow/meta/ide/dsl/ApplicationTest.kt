package arrow.meta.ide.dsl

import arrow.meta.ide.testing.env.IdeHeavyTestSetUp
import com.intellij.openapi.project.guessProjectDir
import org.junit.Test

class ApplicationTest : IdeHeavyTestSetUp() {
  @Test
  fun `Woow`() {
    project.baseDir
  }
}