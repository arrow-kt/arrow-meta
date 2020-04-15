/*
package arrow.meta.ide.testing.env.github

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vcs.AbstractVcsHelper
import com.intellij.openapi.vcs.VcsConfiguration
import com.intellij.openapi.vcs.VcsShowConfirmationOption
import com.intellij.openapi.vcs.changes.Change
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.testFramework.RunAll
import com.intellij.testFramework.replaceService
import com.intellij.testFramework.vcs.AbstractVcsTestCase
import com.intellij.util.ThrowableRunnable
import com.intellij.vcs.log.VcsFullCommitDetails
import com.intellij.vcs.log.util.VcsLogUtil
import git4idea.DialogManager
import git4idea.GitUtil
import git4idea.GitVcs
import git4idea.commands.Git
import git4idea.commands.GitHandler
import git4idea.config.GitExecutableManager
import git4idea.config.GitVcsApplicationSettings
import git4idea.config.GitVcsSettings
import git4idea.log.GitLogProvider
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import git4idea.test.GitPlatformTest.ConfigScope.GLOBAL
import git4idea.test.GitPlatformTest.ConfigScope.SYSTEM
import java.io.File

internal abstract class GitPlatformTest : VcsPlatformTest() {

  lateinit var repositoryManager: GitRepositoryManager
  lateinit var settings: GitVcsSettings
  lateinit var appSettings: GitVcsApplicationSettings
  lateinit var git: TestGitImpl
  lateinit var vcs: GitVcs
  lateinit var commitContext: CommitContext
  lateinit var dialogManager: TestDialogManager
  lateinit var vcsHelper: MockVcsHelper
  lateinit var logProvider: GitLogProvider

  private lateinit var credentialHelpers: Map<ConfigScope, List<String>>
  private var globalSslVerify: Boolean? = null

  @Throws(Exception::class)
  override fun setUp() {
    super.setUp()

    dialogManager = service<DialogManager>() as TestDialogManager
    vcsHelper = MockVcsHelper(myProject)
    project.replaceService(AbstractVcsHelper::class.java, vcsHelper, testRootDisposable)

    repositoryManager = GitUtil.getRepositoryManager(project)
    git = TestGitImpl()
    ApplicationManager.getApplication().replaceService(Git::class.java, git, testRootDisposable)
    vcs = GitVcs.getInstance(project)
    vcs.doActivate()
    commitContext = CommitContext()

    settings = GitVcsSettings.getInstance(project)
    appSettings = GitVcsApplicationSettings.getInstance()
    appSettings.setPathToGit(gitExecutable())
    GitExecutableManager.getInstance().testGitExecutableVersionValid(project)

    logProvider = findGitLogProvider(project)

    assumeSupportedGitVersion(vcs)
    addSilently()
    removeSilently()

    credentialHelpers = if (hasRemoteGitOperation()) readAndResetCredentialHelpers() else emptyMap()
    globalSslVerify = if (hasRemoteGitOperation()) readAndDisableSslVerifyGlobally() else null
  }

  @Throws(Exception::class)
  override fun tearDown() {
    RunAll()
      .append(ThrowableRunnable { restoreCredentialHelpers() })
      .append(ThrowableRunnable { restoreGlobalSslVerify() })
      .append(ThrowableRunnable { if (::dialogManager.isInitialized) dialogManager.cleanup() })
      .append(ThrowableRunnable { if (::git.isInitialized) git.reset() })
      .append(ThrowableRunnable { if (::settings.isInitialized) settings.appSettings.setPathToGit(null) })
      .append(ThrowableRunnable { super.tearDown() })
      .run()
  }

  override fun getDebugLogCategories(): Collection<String> {
    return super.getDebugLogCategories().plus(listOf("#" + Executor::class.java.name,
      "#git4idea",
      "#output." + GitHandler::class.java.name))
  }

  open fun hasRemoteGitOperation() = false

  open fun createRepository(rootDir: String): GitRepository {
    return createRepository(project, rootDir)
  }

  */
/**
   * Clones the given source repository into a bare parent.git and adds the remote origin.
   *//*

  fun prepareRemoteRepo(source: GitRepository, target: File = File(testRoot, "parent.git"), remoteName: String = "origin"): File {
    cd(testRoot)
    git("clone --bare '${source.root.path}' ${target.path}")
    cd(source)
    git("remote add ${remoteName} '${target.path}'")
    return target
  }

  */
/**
   * Creates 3 repositories: a bare "parent" repository, and two clones of it.
   *
   * One of the clones - "bro" - is outside of the project.
   * Another one is inside the project, is registered as a Git root, and is represented by [GitRepository].
   *
   * Parent and bro are created just inside the [testRoot](myTestRoot).
   * The main clone is created at [repoRoot], which is assumed to be inside the project.
   *//*

  fun setupRepositories(repoRoot: String, parentName: String, broName: String): ReposTrinity {
    val parentRepo = createParentRepo(parentName)
    val broRepo = createBroRepo(broName, parentRepo)

    val repository = createRepository(project, repoRoot)
    cd(repository)
    git("remote add origin " + parentRepo.path)
    git("push --set-upstream origin master:master")

    cd(broRepo.path)
    git("pull")

    return ReposTrinity(repository, parentRepo, broRepo)
  }

  private fun createParentRepo(parentName: String): File {
    cd(testRoot)
    git("init --bare $parentName.git")
    return File(testRoot, "$parentName.git")
  }

  fun createBroRepo(broName: String, parentRepo: File): File {
    cd(testRoot)
    git("clone " + parentRepo.name + " " + broName)
    cd(broName)
    setupDefaultUsername(project)
    return File(testRoot, broName)
  }

  private fun doActionSilently(op: VcsConfiguration.StandardConfirmation) {
    AbstractVcsTestCase.setStandardConfirmation(project, GitVcs.NAME, op, VcsShowConfirmationOption.Value.DO_ACTION_SILENTLY)
  }

  private fun addSilently() {
    doActionSilently(VcsConfiguration.StandardConfirmation.ADD)
  }

  private fun removeSilently() {
    doActionSilently(VcsConfiguration.StandardConfirmation.REMOVE)
  }

  fun installHook(gitDir: File, hookName: String, hookContent: String) {
    val hookFile = File(gitDir, "hooks/$hookName")
    FileUtil.writeToFile(hookFile, hookContent)
    hookFile.setExecutable(true, false)
  }

  private fun readAndResetCredentialHelpers(): Map<ConfigScope, List<String>> {
    val system = readAndResetCredentialHelper(SYSTEM)
    val global = readAndResetCredentialHelper(GLOBAL)
    return mapOf(SYSTEM to system, GLOBAL to global)
  }

  private fun readAndResetCredentialHelper(scope: ConfigScope): List<String> {
    val values = git("config ${scope.param()} --get-all -z credential.helper", true).split("\u0000").filter { it.isNotBlank() }
    git("config ${scope.param()} --unset-all credential.helper", true)
    return values
  }

  private fun restoreCredentialHelpers() {
    credentialHelpers.forEach { (scope, values) ->
      values.forEach { git("config --add ${scope.param()} credential.helper ${it}", true) }
    }
  }

  private fun readAndDisableSslVerifyGlobally(): Boolean? {
    val value = git("config --global --get-all -z http.sslVerify", true)
      .split("\u0000")
      .singleOrNull { it.isNotBlank() }
      ?.let { it.toBoolean() }
    git("config --global http.sslVerify false", true)
    return value
  }

  private fun restoreGlobalSslVerify() {
    if (globalSslVerify != null) {
      git("config --global http.sslVerify ${globalSslVerify}", true)
    } else {
      git("config --global --unset http.sslVerify", true)
    }
  }

  fun readDetails(hashes: List<String>): List<VcsFullCommitDetails> = VcsLogUtil.getDetails(logProvider, projectRoot, hashes)

  fun readDetails(hash: String) = readDetails(listOf(hash)).first()

  fun commit(changes: Collection<Change>) {
    val exceptions = vcs.checkinEnvironment!!.commit(changes.toList(), "comment", commitContext, mutableSetOf())
    exceptions?.forEach { fail("Exception during executing the commit: " + it.message) }
    updateChangeListManager()
  }

  fun `do nothing on merge`() {
    vcsHelper.onMerge {}
  }

  fun `mark as resolved on merge`() {
    vcsHelper.onMerge { git("add -u .") }
  }

  fun `assert merge dialog was shown`() {
    assertTrue("Merge dialog was not shown", vcsHelper.mergeDialogWasShown())
  }

  fun `assert commit dialog was shown`() {
    assertTrue("Commit dialog was not shown", vcsHelper.commitDialogWasShown())
  }

  fun assertNoChanges() {
    changeListManager.assertNoChanges()
  }

  fun assertChanges(changes: ChangesBuilder.() -> Unit): List<Change> {
    return changeListManager.assertChanges(changes)
  }

  data class ReposTrinity(val projectRepo: GitRepository, val parent: File, val bro: File)


  private enum class ConfigScope {
    SYSTEM,
    GLOBAL;

    fun param() = "--${name.toLowerCase()}"
  }
}

*/
