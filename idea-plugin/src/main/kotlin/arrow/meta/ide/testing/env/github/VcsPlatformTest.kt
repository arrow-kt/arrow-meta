package arrow.meta.ide.testing.env.github

import arrow.meta.ide.testing.env.github.Executor.cd

// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import com.intellij.ide.highlighter.ProjectFileType
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.VcsNotifier
import com.intellij.openapi.vcs.changes.ChangeListManagerImpl
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.RunAll
import com.intellij.testFramework.TestLoggerFactory
import com.intellij.testFramework.replaceService
import com.intellij.testFramework.runInEdtAndWait
import com.intellij.util.ArrayUtil
import com.intellij.util.ThrowableRunnable
import com.intellij.vfs.AsyncVfsEventsPostProcessorImpl
import java.io.File
import java.nio.file.Path
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.random.Random

internal abstract class VcsPlatformTest : HeavyPlatformTestCase() {
  lateinit var testRoot: File
  lateinit var testRootFile: VirtualFile
  lateinit var projectRoot: VirtualFile
  lateinit var projectPath: String

  private lateinit var testStartedIndicator: String
  private val asyncTasks = mutableSetOf<AsyncTask>()

  lateinit var changeListManager: ChangeListManagerImpl
  lateinit var vcsManager: ProjectLevelVcsManagerImpl
  lateinit var vcsNotifier: TestVcsNotifier

  @Throws(Exception::class)
  override fun setUp() {
    testRoot = createTempDir("root-${Integer.toHexString(Random.nextInt())}", false)
    checkTestRootIsEmpty(testRoot)

    runInEdtAndWait { super@VcsPlatformTest.setUp() }
    testRootFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(testRoot)!!
    refresh()

    testStartedIndicator = enableDebugLogging()

    projectRoot = project.baseDir
    projectPath = projectRoot.path

    changeListManager = ChangeListManagerImpl.getInstanceImpl(project)
    vcsManager = ProjectLevelVcsManager.getInstance(project) as ProjectLevelVcsManagerImpl

    vcsNotifier = TestVcsNotifier(myProject)
    project.replaceService(VcsNotifier::class.java, vcsNotifier, testRootDisposable)
    cd(testRoot)
  }

  @Throws(Exception::class)
  override fun tearDown() {
    RunAll()
      .append(ThrowableRunnable { selfTearDownRunnable() })
      .append(ThrowableRunnable { clearFields(this) })
      .append(ThrowableRunnable { runInEdtAndWait { super@VcsPlatformTest.tearDown() } })
      .run()

  }

  private fun selfTearDownRunnable() {
    var tearDownErrorDetected = false
    try {
      RunAll()
        .append(ThrowableRunnable { AsyncVfsEventsPostProcessorImpl.waitEventsProcessed() })
        .append(ThrowableRunnable { changeListManager.waitEverythingDoneInTestMode() })
        .append(ThrowableRunnable { if (::vcsNotifier.isInitialized) vcsNotifier.cleanup() })
        .append(ThrowableRunnable { waitForPendingTasks() })
        .run()
    } catch (e: Throwable) {
      tearDownErrorDetected = true
      throw e
    } finally {
      if (myAssertionsInTestDetected || tearDownErrorDetected) {
        TestLoggerFactory.dumpLogToStdout(testStartedIndicator)
      }
    }
  }

  /**
   * Returns log categories which will be switched to DEBUG level.
   * Implementations must add theirs categories to the ones from super class,
   * not to erase log categories from the super class.
   * (e.g. by calling `super.getDebugLogCategories().plus(additionalCategories)`.
   */
  open fun getDebugLogCategories(): Collection<String> = emptyList()

  override fun getProjectDirOrFile(): Path {
    val projectRoot = File(testRoot, "project")
    val file: File = FileUtil.createTempFile(projectRoot, name + "_", ProjectFileType.DOT_DEFAULT_EXTENSION)
    return file.toPath()
  }

  override fun setUpModule() {
    // we don't need a module in Git tests
  }

  override fun runInDispatchThread(): Boolean {
    return false
  }

  override fun getTestName(lowercaseFirstLetter: Boolean): String {
    var name = super.getTestName(lowercaseFirstLetter)
    name = StringUtil.shortenTextWithEllipsis(name.trim { it <= ' ' }.replace(" ", "_"), 12, 6, "_")
    if (name.startsWith("_")) {
      name = name.substring(1)
    }
    return name
  }

  @JvmOverloads
  open fun refresh(dir: VirtualFile = testRootFile) {
    VfsUtil.markDirtyAndRefresh(false, true, false, dir)
  }

  fun updateChangeListManager() {
    VcsDirtyScopeManager.getInstance(project).markEverythingDirty()
    changeListManager.ensureUpToDate()
  }

  private fun waitForPendingTasks() {
    for ((name, indicator, future) in asyncTasks) {
      if (!future.isDone) {
        LOG.error("Task $name didn't finish within the test")
        indicator.cancel()
        future.get(10, TimeUnit.SECONDS)
      }
    }
  }

  fun executeOnPooledThread(runnable: () -> Unit) {
    val indicator = EmptyProgressIndicator()
    val future = ApplicationManager.getApplication().executeOnPooledThread {
      ProgressManager.getInstance().executeProcessUnderProgress({ runnable() }, indicator)
    }
    asyncTasks.add(AsyncTask(super.getTestName(false), indicator, future))
  }

  private fun checkTestRootIsEmpty(testRoot: File) {
    val files: List<File> = testRoot.listFiles().orEmpty().filterNotNull()
    if (files.isNotEmpty()) {
      LOG.warn("Test root was not cleaned up during some previous test run. " + "testRoot: " + testRoot +
        ", files: " + files)
      for (file in files) {
        LOG.assertTrue(FileUtil.delete(file))
      }
    }
  }

  private fun enableDebugLogging(): String {
    TestLoggerFactory.enableDebugLogging(testRootDisposable, *ArrayUtil.toStringArray(getDebugLogCategories()))
    val testStartedIndicator = createTestStartedIndicator()
    LOG.info(testStartedIndicator)
    return testStartedIndicator
  }

  private fun createTestStartedIndicator(): String {
    return "Starting " + javaClass.name + "." + super.getTestName(false) + Math.random()
  }


  fun assertSuccessfulNotification(title: String, message: String): Notification {
    //return assertNotification(NotificationType.INFORMATION, title, message, vcsNotifier.lastNotification)
    TODO()
  }

  fun assertSuccessfulNotification(message: String): Notification {
    return assertSuccessfulNotification("", message)
  }

  fun assertWarningNotification(title: String, message: String) {
    //assertNotification(NotificationType.WARNING, title, message, vcsNotifier.lastNotification)
  }

  fun assertErrorNotification(title: String, message: String): Notification {
    val notification = vcsNotifier.lastNotification
    assertNotNull("No notification was shown", notification)
    //assertNotification(NotificationType.ERROR, title, message, notification)
    return notification
  }

  fun assertNoNotification() {
    val notification = vcsNotifier.lastNotification
    if (notification != null) {
      fail("No notification is expected here, but this one was shown: ${notification.title}/${notification.content}")
    }
  }

  fun assertNoErrorNotification() {
    vcsNotifier.notifications.find { it.type == NotificationType.ERROR }?.let { notification ->
      fail("No error notification is expected here, but this one was shown: ${notification.title}/${notification.content}")
    }
  }

  data class AsyncTask(val name: String, val indicator: ProgressIndicator, val future: Future<*>)
}