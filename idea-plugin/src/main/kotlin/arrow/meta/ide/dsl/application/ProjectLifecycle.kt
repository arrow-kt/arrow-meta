package arrow.meta.ide.dsl.application

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.impl.ProjectLifecycleListener

interface ProjectLifecycle : ProjectLifecycleListener, Disposable
