package arrow.meta.ide.phases.integration

import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.resolve.PackageProvider
import arrow.meta.phases.resolve.synthetics.SyntheticResolver
import com.intellij.openapi.project.Project

interface SyntheticResolver : ExtensionPhase {
  fun syntheticResolver(project: Project): SyntheticResolver?
}

interface PackageProvider : ExtensionPhase {
  fun packageFragmentProvider(project: Project): PackageProvider?
}
