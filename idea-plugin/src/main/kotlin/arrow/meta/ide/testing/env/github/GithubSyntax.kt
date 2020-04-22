package arrow.meta.ide.testing.env.github

import arrow.meta.internal.Noop
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitCommandResult
import git4idea.commands.GitLineHandler

interface GithubSyntax {
  fun gitClone(project: Project, url: String, vcsRoot: VirtualFile = project.baseDir): GitCommandResult =
    gitCmd(
      project,
      GitCommand.CLONE,
      vcsRoot,
      f = {
        setUrl(url)
        addParameters(url)
      }
    )

  fun gitCmd(
    project: Project,
    cmd: GitCommand,
    vcsRoot: VirtualFile = project.baseDir,
    f: GitLineHandler.() -> Unit = Noop.effect1
  ): GitCommandResult =
    gitCmd(gitHandler(project, vcsRoot, cmd, f))

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
}