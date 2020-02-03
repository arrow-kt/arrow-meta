package arrow.meta.ide.dsl.editor.mdhtml

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JCEFHtmlPanel
import org.cef.browser.CefBrowser
import org.intellij.plugins.markdown.ui.preview.MarkdownHtmlPanel
import org.intellij.plugins.markdown.ui.preview.MarkdownHtmlPanelProvider
import org.jetbrains.annotations.ApiStatus

/**
 * https://youtrack.jetbrains.com/issue/IDEA-231833?p=JBR-1603#comment=27-3913289
 */
@ApiStatus.Experimental
interface MarkdownHtmlPanelSyntax {

  /**
   * @param available use [MarkdownHtmlPanelProvider.AvailabilityInfo.AVAILABLE] or [MarkdownHtmlPanelProvider.AvailabilityInfo.UNAVAILABLE]
   * See this [example](https://github.com/JetBrains/intellij-community/blob/032a215bfe2782d8f764e3bc6ed259fda4bc50ee/plugins/markdown/src/org/intellij/plugins/markdown/ui/preview/jcef/JCEFHtmlPanelProvider.java)
   */
  fun IdeMetaPlugin.addMdHtmlPanel(
    name: String,
    available: MarkdownHtmlPanelProvider.AvailabilityInfo,
    setCss: (inlineCss: String?, fileUris: List<String>) -> Unit,
    scrollToMdSrcOffset: CefBrowser.(offset: Int) -> Unit,
    render: Unit = Unit
  ): ExtensionPhase =
    extensionProvider(
      MarkdownHtmlPanelProvider.EP_NAME,
      object : MarkdownHtmlPanelProvider() { //JCEFHtmlPanel(), MarkdownHtmlPanel
        override fun getProviderInfo(): ProviderInfo = ProviderInfo(name, this::class.qualifiedName.orEmpty())
        override fun isAvailable(): AvailabilityInfo = available

        override fun createHtmlPanel(): MarkdownHtmlPanel =
          object : MarkdownHtmlPanel, JCEFHtmlPanel() {
            override fun render(): Unit = render

            override fun setCSS(inlineCss: String?, vararg fileUris: String): Unit =
              setCss(inlineCss, fileUris.toList())

            // TODO: add JBCefBrowser().cefBrowser, when dependency is resolved
            override fun scrollToMarkdownSrcOffset(offset: Int): Unit =
              JBCefBrowser().cefBrowser.scrollToMdSrcOffset(offset)
          }
      }
    )
}