package arrow.meta.ide.testing.env.github

import arrow.meta.internal.Noop
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitCommandResult
import git4idea.commands.GitLineHandler

interface GithubSyntax {
  fun gitClone(project: Project, url: String): GitCommandResult =
    gitCmd(
      project,
      GitCommand.CLONE,
      f = {
        setUrl(url)
        addParameters(url)
      }
    )

  fun gitCmd(
    project: Project,
    cmd: GitCommand,
    f: GitLineHandler.() -> Unit = Noop.effect1
  ): GitCommandResult =
    gitCmd(gitHandler(project, project.baseDir, cmd, f))

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
    val handler = GitLineHandler(project, project.baseDir, getGitCommandInstance(command))
    handler.setWithMediator(false)
    // handler.addParameters(split.subList(1, split.size))

    val result = Git.getInstance().runCommand(handler)
    if (result.exitCode != 0 && !ignoreNonZeroExitCode) {
      throw IllegalStateException("Command [$command] failed with exit code ${result.exitCode}\n${result.output}\n${result.errorOutput}")
    }
    return result.errorOutputAsJoinedString + result.outputAsJoinedString
  }

  fun getGitCommandInstance(commandName: String): GitCommand {
    return try {
      val fieldName = commandName.toUpperCase().replace('-', '_')
      GitCommand::class.java.getDeclaredField(fieldName).get(null) as GitCommand
    } catch (e: NoSuchFieldException) {
      val constructor = GitCommand::class.java.getDeclaredConstructor(String::class.java)
      constructor.isAccessible = true
      constructor.newInstance(commandName) as GitCommand
    }
  }
}