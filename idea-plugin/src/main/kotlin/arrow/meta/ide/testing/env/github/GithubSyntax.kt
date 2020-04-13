package arrow.meta.ide.testing.env.github

import arrow.meta.ide.testing.dsl.IdeTestSyntax
import arrow.meta.internal.Noop
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitCommandResult
import git4idea.commands.GitLineHandler

interface GithubSyntax {
  fun IdeTestSyntax.

  fun <A> git(f: Git.() -> A): A =
    Git.getInstance().f()

  fun gitCmd(handler: GitLineHandler): GitCommandResult =
    git { runCommand(handler) }

  fun gitHandler(
    project: Project,
    vcsRoot: VirtualFile,
    cmd: GitCommand,
    f: GitLineHandler.() -> Unit = Noop.effect1,
    configs: List<String> = emptyList()): GitLineHandler =
    GitLineHandler(project, vcsRoot, cmd, configs).apply { this.f() }

  fun git(project: Project, command: String, ignoreNonZeroExitCode: Boolean = false): String {
    /*val handler = GitLineHandler(project, project.baseDir, getGitCommandInstance())
    handler.setWithMediator(false)
    handler.addParameters(split.subList(1, split.size))*/

    val result = Git.getInstance().runCommand(handler)
    if (result.exitCode != 0 && !ignoreNonZeroExitCode) {
      throw IllegalStateException("Command [$command] failed with exit code ${result.exitCode}\n${result.output}\n${result.errorOutput}")
    }
    return result.errorOutputAsJoinedString + result.outputAsJoinedString
  }
}