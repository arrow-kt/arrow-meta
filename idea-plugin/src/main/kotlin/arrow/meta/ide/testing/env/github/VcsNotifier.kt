package arrow.meta.ide.testing.env.github

import com.intellij.notification.Notification
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsNotifier
import com.intellij.util.containers.ContainerUtil
import java.util.*

// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

class TestVcsNotifier(project: Project) : VcsNotifier(project) {
  private val myNotifications: MutableList<Notification?> = ArrayList()
  val lastNotification: Notification
    get() = ContainerUtil.getLastItem(myNotifications)!!

  fun findExpectedNotification(expectedNotification: Notification): Notification? {
    return myNotifications
      .stream()
      .filter { notification: Notification? -> expectedNotification.type == notification!!.type && expectedNotification.title == notification.title && expectedNotification.content == notification.content }
      .findAny().orElse(null)
  }

  override fun notify(notification: Notification): Notification {
    myNotifications.add(notification)
    return notification
  }

  @Suppress("UNCHECKED_CAST")
  val notifications: List<Notification>
    get() = ContainerUtil.unmodifiableOrEmptyList(myNotifications) as List<Notification>

  fun cleanup() {
    myNotifications.clear()
  }

  companion object {
    private const val TEST_NOTIFICATION_GROUP = "Test"
  }
}