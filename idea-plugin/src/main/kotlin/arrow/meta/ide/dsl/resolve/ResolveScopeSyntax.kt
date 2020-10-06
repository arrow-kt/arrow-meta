package arrow.meta.ide.dsl.resolve

import arrow.meta.ide.MetaIde
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.ResolveScopeEnlarger
import com.intellij.psi.search.SearchScope

interface ResolveScopeSyntax {
  fun MetaIde.addResolveScopeEnlarger(
    additionalResolveScope: (file: VirtualFile, project: Project) -> SearchScope
  ): ExtensionPhase =
    extensionProvider(
      ResolveScopeEnlarger.EP_NAME,
      object : ResolveScopeEnlarger() {
        override fun getAdditionalResolveScope(file: VirtualFile, project: Project): SearchScope =
          additionalResolveScope(file, project)
      }
    )

  fun ResolveScopeSyntax.searchScope(
    containsFile: (file: VirtualFile) -> Boolean,
    intersectWithScope: (scope: SearchScope) -> SearchScope,
    unionScope: (scope: SearchScope) -> SearchScope
  ): SearchScope =
    object : SearchScope() {
      override fun contains(file: VirtualFile): Boolean = containsFile(file)

      override fun intersectWith(scope: SearchScope): SearchScope = intersectWithScope(scope)

      override fun union(scope: SearchScope): SearchScope = unionScope(scope)
    }
}