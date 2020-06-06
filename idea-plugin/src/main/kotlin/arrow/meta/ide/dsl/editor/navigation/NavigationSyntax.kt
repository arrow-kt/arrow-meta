package arrow.meta.ide.dsl.editor.navigation

import arrow.meta.ide.MetaIde
import arrow.meta.phases.ExtensionPhase
import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project

interface NavigationSyntax {
  fun MetaIde.addChooseByNameContributorForFile(
    itemsByName: (name: String?, pattern: String?, project: Project?, includeNonProjectItems: Boolean) -> Array<NavigationItem>,
    names: (project: Project?, includeNonProjectItems: Boolean) -> Array<String>
  ): ExtensionPhase =
    extensionProvider(
      ChooseByNameContributor.FILE_EP_NAME,
      chooseByNameContributor(itemsByName, names)
    )

  fun MetaIde.addChooseByNameContributorForClass(
    itemsByName: (name: String?, pattern: String?, project: Project?, includeNonProjectItems: Boolean) -> Array<NavigationItem>,
    names: (project: Project?, includeNonProjectItems: Boolean) -> Array<String>
  ): ExtensionPhase =
    extensionProvider(
      ChooseByNameContributor.CLASS_EP_NAME,
      chooseByNameContributor(itemsByName, names)
    )

  fun MetaIde.addChooseByNameContributorForSymbol(
    itemsByName: (name: String?, pattern: String?, project: Project?, includeNonProjectItems: Boolean) -> Array<NavigationItem>,
    names: (project: Project?, includeNonProjectItems: Boolean) -> Array<String>
  ): ExtensionPhase =
    extensionProvider(
      ChooseByNameContributor.SYMBOL_EP_NAME,
      chooseByNameContributor(itemsByName, names)
    )

  fun NavigationSyntax.chooseByNameContributor(
    itemsByName: (name: String?, pattern: String?, project: Project?, includeNonProjectItems: Boolean) -> Array<NavigationItem>,
    names: (project: Project?, includeNonProjectItems: Boolean) -> Array<String>
  ): ChooseByNameContributor =
    object : ChooseByNameContributor {
      override fun getItemsByName(name: String?, pattern: String?, project: Project?, includeNonProjectItems: Boolean): Array<NavigationItem> =
        itemsByName(name, pattern, project, includeNonProjectItems)

      override fun getNames(project: Project?, includeNonProjectItems: Boolean): Array<String> =
        names(project, includeNonProjectItems)
    }
}
