package arrow.meta.ide.phases.resolve

import arrow.meta.ide.phases.config.buildFolders
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

/**
 * This project component controls the cache update of MetaSyntheticPackageFragmentProvider
 * It initializes the cache when the project is opened and triggers an update
 * when necessary.
 *
 * fixme: Vfs events are application-wide, this component has project-scope
 *   Therefore vfs events of files in project A also clear the cache of project B
 */
class FragmentProviderComponent(val project: Project) : ProjectComponent, AsyncFileListener, AsyncFileListener.ChangeApplier {

  override fun getComponentName(): String = "arrow.meta.fragmentInitializer"

  override fun initComponent() {
    // dispose listener when project is disposed
    VirtualFileManager.getInstance().addAsyncFileListener(this, project)
  }

  override fun projectOpened() {
    LOG.debug("Initializing cache of MetaSyntheticPackageFragmentProvider")
    MetaSyntheticPackageFragmentProvider.getInstance(project)?.run {
      computeCacheAsync()
    } ?: LOG.error("Could not get MetaSyntheticPackageFragmentProvider instance, while opening the project")
  }

  /**
   *  we only care about changes to .class files inside of build folders
   */
  override fun prepareChange(events: MutableList<out VFileEvent>): FragmentProviderComponent? =
    this.takeIf {
      project.buildFolders().toHashSet().let { buildFolders: HashSet<VirtualFile> ->
        events.any { e: VFileEvent ->
          e.file?.let { file: VirtualFile ->
            e.isValid && file.extension == "class" && VfsUtilCore.isUnder(file, buildFolders)
          } ?: false
        }
      }
    }

  override fun afterVfsChange() {
    LOG.debug("MetaSyntheticPackageFragmentProvider.afterVfsChange")
    MetaSyntheticPackageFragmentProvider.getInstance(project)?.run {
      computeCacheAsync()
    } ?: LOG.error("Could not get MetaSyntheticPackageFragmentProvider instance, afterVfsChanges")
  }
}
