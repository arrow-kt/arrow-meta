package arrow.meta.ide.testing.env.gradle

import arrow.meta.internal.Noop
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListenerAdapter
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import org.assertj.core.api.AbstractThrowableAssert
import org.assertj.core.api.Assertions.assertThatCode
import org.jetbrains.plugins.gradle.GradleManager
import org.jetbrains.plugins.gradle.service.task.GradleTaskManager
import org.jetbrains.plugins.gradle.settings.GradleExecutionSettings
import org.jetbrains.plugins.gradle.util.GradleConstants

interface GradleSyntax {
  /**
   * runs a gradle task.
   * This extension throws an exception if the gradle task fails or throws an exception.
   * To recover from this, please use [assertThatCode].
   */
  fun GradleSyntax.gradle(
    project: Project,
    rootPath: String,
    settings: GradleManager.() -> GradleExecutionSettings? = Noop.nullable1(),
    jvmSetupParams: String? = null,
    listener: ExternalSystemTaskNotificationListener,
    tasks: List<String>
  ): Unit =
    GradleTaskManager().executeTasks(
      ExternalSystemTaskId.create(GradleConstants.SYSTEM_ID, ExternalSystemTaskType.EXECUTE_TASK, project),
      tasks,
      rootPath,
      settings(GradleManager()),
      jvmSetupParams,
      listener
    )

  /**
   * returns the logs with the each taskOutput
   */
  fun GradleSyntax.gradle(
    project: Project,
    rootPath: String,
    settings: GradleManager.() -> GradleExecutionSettings? = {
      executionSettingsProvider.`fun`(Pair.create(project, rootPath))
    },
    jvmSetupParams: String? = null,
    tasks: List<String>,
    logs: MutableList<String>
  ): List<String> {
    val listener = listener(
      taskOutput = { _, text, _ ->
        logs.add(text.trim('\r', '\n', ' '))
      },
      failure = { _, e ->
        logs.addAll(e.stackTrace.map { it.toString() }.toList())
      })
    gradle(project, rootPath, settings, jvmSetupParams, listener, tasks)
    return logs.toList()
  }

  fun GradleSyntax.assertGradle(
    project: Project,
    rootPath: String,
    settings: GradleManager.() -> GradleExecutionSettings? = {
      executionSettingsProvider.`fun`(Pair.create(project, rootPath))
    },
    jvmSetupParams: String? = null,
    tasks: List<String>
  ): kotlin.Pair<AbstractThrowableAssert<*, out Throwable>, List<String>> {
    val logs = mutableListOf<String>()
    val taskAssert = assertThatCode {
      gradle(project, rootPath, settings, jvmSetupParams, tasks, logs)
    }
    return taskAssert to logs.toList()
  }


  fun GradleSyntax.listener(
    taskOutput: (id: ExternalSystemTaskId, text: String, stdOut: Boolean) -> Unit = Noop.effect3,
    failure: (id: ExternalSystemTaskId, e: Exception) -> Unit = Noop.effect2
  ): ExternalSystemTaskNotificationListenerAdapter =
    object : ExternalSystemTaskNotificationListenerAdapter() {
      override fun onTaskOutput(id: ExternalSystemTaskId, text: String, stdOut: Boolean): Unit =
        taskOutput(id, text, stdOut)

      override fun onFailure(id: ExternalSystemTaskId, e: Exception): Unit =
        failure(id, e)
    }
}