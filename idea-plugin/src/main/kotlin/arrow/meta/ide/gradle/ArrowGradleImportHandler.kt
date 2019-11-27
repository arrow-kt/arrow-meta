package arrow.meta.ide.gradle

import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ModuleData
import org.jetbrains.kotlin.idea.configuration.GradleProjectImportHandler
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.plugins.gradle.model.data.GradleSourceSetData

class ArrowGradleImportHandler : GradleProjectImportHandler {

  override fun importBySourceSet(facet: KotlinFacet, sourceSetNode: DataNode<GradleSourceSetData>) {
    println("ArrowGradleImportHandler.importBySourceSet")
  }

  override fun importByModule(facet: KotlinFacet, moduleNode: DataNode<ModuleData>) {
    println("ArrowGradleImportHandler.importByModule")
  }
}