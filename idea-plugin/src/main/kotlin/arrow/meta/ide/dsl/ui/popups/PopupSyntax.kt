package arrow.meta.ide.dsl.ui.popups

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.action.AnActionSyntax
import arrow.meta.ide.dsl.editor.inspection.InspectionSyntax
import arrow.meta.internal.Noop
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.BalloonBuilder
import com.intellij.openapi.ui.popup.ComponentPopupBuilder
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.ui.popup.ListSeparator
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.PopupStep.FINAL_CHOICE
import com.intellij.openapi.ui.popup.TreePopup
import com.intellij.openapi.ui.popup.TreePopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import java.awt.Color
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.event.HyperlinkEvent

/**
 * Popup's can be created at any time.
 * Either in [AnActionSyntax], [InspectionSyntax] or [IntentionExtensionProviderSyntax] and many other places.
 * Once the PopUp is created the user needs to call a `show()` method like [com.intellij.openapi.ui.popup.JBPopup.showUnderneathOf], this is an effectfull operation.
 */
interface PopupSyntax {
  fun <P : JBPopup> IdeMetaPlugin.popUp(f: JBPopupFactory.() -> P): P = f(JBPopupFactory.getInstance())
  fun IdeMetaPlugin.balloon(f: JBPopupFactory.() -> BalloonBuilder): BalloonBuilder = f(JBPopupFactory.getInstance())
  fun IdeMetaPlugin.componentPopUp(f: JBPopupFactory.() -> ComponentPopupBuilder): ComponentPopupBuilder = f(JBPopupFactory.getInstance())

  fun <A> IdeMetaPlugin.listPopUp(
    popUps: List<IdeListPopup<A>>,
    title: String? = null,
    sameIcon: Icon? = null,
    canceled: () -> Unit = {}
  ): ListPopup =
    popUp {
      popUps.run {
        createListPopup(object : BaseListPopupStep<A>(title, map { it.element }, sameIcon) {
          override fun isSelectable(value: A): Boolean =
            this@run.firstOrNull { it == value }?.isSelectable ?: false

          override fun hasSubstep(selectedValue: A): Boolean =
            this@run.firstOrNull { it == selectedValue }?.hasSubStep ?: false

          override fun onChosen(selectedValue: A, finalChoice: Boolean): PopupStep<*> =
            this@run.firstOrNull { it == selectedValue }?.let { a -> a.onChosen(selectedValue, finalChoice) }
              ?: PopupStep.FINAL_CHOICE

          override fun getTextFor(value: A): String =
            this@run.firstOrNull { it == value }?.let { a -> a.text(a.element) } ?: value.toString()

          override fun getSeparatorAbove(value: A): ListSeparator? =
            this@run.firstOrNull { it == value }?.let { a -> a.listSeparator(a.element) }

          override fun getForegroundFor(value: A): Color? =
            this@run.firstOrNull { it == value }?.forGround

          override fun canceled(): Unit = canceled()

          override fun getIconFor(value: A): Icon? =
            this@run.firstOrNull { it == value }?.iconForElement
        })
      }
    }

  fun <A> IdeMetaPlugin.treePopUp(root: TreePopupStep<A>, tree: JBPopupFactory.(root: TreePopupStep<A>) -> TreePopup = { createTree(root) }): TreePopup =
    popUp { tree(this, root) }

  fun IdeMetaPlugin.message(text: String): JBPopup =
    popUp { createMessage(text) }

  fun IdeMetaPlugin.componentPopUp(content: JComponent, focus: JComponent, transform: ComponentPopupBuilder.() -> ComponentPopupBuilder): ComponentPopupBuilder =
    componentPopUp { transform(createComponentPopupBuilder(content, focus)) }

  fun IdeMetaPlugin.balloonBuilder(content: JComponent, transform: BalloonBuilder.() -> BalloonBuilder): BalloonBuilder =
    balloon { transform(createBalloonBuilder(content)) }

  fun IdeMetaPlugin.balloonBuilder(content: JComponent, title: String, transform: BalloonBuilder.() -> BalloonBuilder): BalloonBuilder =
    balloon { transform(createDialogBalloonBuilder(content, title)) }

  fun IdeMetaPlugin.balloonBuilder(html: String, fillColor: Color, transform: BalloonBuilder.() -> BalloonBuilder, icon: Icon? = null, textColor: Color? = null, link: (HyperlinkEvent) -> Unit = Noop.effect1): BalloonBuilder =
    balloon { transform(createHtmlTextBalloonBuilder(html, icon, textColor, fillColor, link)) }

  fun IdeMetaPlugin.balloonBuilder(html: String, messageType: MessageType, transform: BalloonBuilder.() -> BalloonBuilder, link: (HyperlinkEvent) -> Unit = Noop.effect1): BalloonBuilder =
    balloon { transform(createHtmlTextBalloonBuilder(html, messageType, link)) }
}

/**
 * [IdeListPopup] resembles one item of a [ListPopup]
 * Default values are from [com.intellij.openapi.ui.popup.util.BaseListPopupStep]
 */
data class IdeListPopup<A>(
  val element: A,
  val text: (element: A) -> String = { it.toString() },
  val iconForElement: Icon? = null,
  val forGround: Color? = null,
  val backGround: Color? = null,
  val hasSubStep: Boolean = false,
  val isSelectable: Boolean = true,
  val onChosen: (element: A, finalChoice: Boolean) -> PopupStep<*> = { _, _ -> FINAL_CHOICE },
  val listSeparator: (A) -> ListSeparator? = Noop.nullable1()
)
