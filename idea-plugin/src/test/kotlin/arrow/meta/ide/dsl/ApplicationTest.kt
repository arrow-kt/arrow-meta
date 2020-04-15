package arrow.meta.ide.dsl

import arrow.meta.ide.testing.IdeEnvironment
import arrow.meta.ide.testing.env.IdeHeavyTestSetUp
import arrow.meta.ide.testing.env.IdeTestSetUp
import git4idea.commands.GitCommand
import org.junit.Test

class ApplicationTest : IdeTestSetUp() {
  @Test
  fun `test`() {
    IdeEnvironment.gitCmd(myFixture.project, GitCommand.INIT)
    IdeEnvironment.gitClone(myFixture.project, "https://github.com/arrow-kt/arrow-typeproofs.git")

    assert(false)
  }
}