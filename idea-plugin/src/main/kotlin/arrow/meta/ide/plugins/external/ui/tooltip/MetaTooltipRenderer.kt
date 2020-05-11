package arrow.meta.ide.plugins.external.ui.tooltip

import com.intellij.codeInsight.hint.HintManagerImpl.ActionToIgnore
import com.intellij.codeInsight.hint.LineTooltipRenderer
import com.intellij.codeInsight.hint.LineTooltipRenderer.TooltipReloader
import com.intellij.codeInsight.hint.TooltipController
import com.intellij.codeInsight.hint.TooltipGroup
import com.intellij.codeInsight.hint.TooltipLinkHandlerEP
import com.intellij.ide.BrowserUtil
import com.intellij.ide.TooltipEvent
import com.intellij.internal.statistic.service.fus.collectors.TooltipActionsLogger.logShowDescription
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.*
import com.intellij.util.ui.GridBag
import com.intellij.util.ui.Html
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.accessibility.AccessibleContextDelegate
import com.intellij.util.ui.accessibility.ScreenReader
import com.intellij.xml.util.XmlStringUtil
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.accessibility.AccessibleContext
import javax.swing.*
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener

@Suppress("UnstableApiUsage")
internal class MetaTooltipRenderer : LineTooltipRenderer {

  @Volatile
  private var myActiveLink = false

  constructor(text: String, comparable: Array<out Any> = arrayOf()) : super(text, comparable)
  constructor(text: String, width: Int, comparable: Array<out Any> = arrayOf()) : super(text, width, comparable)

  override fun createHint(
    editor: Editor,
    p: Point,
    alignToRight: Boolean,
    group: TooltipGroup,
    hintHint: HintHint,
    newLayout: Boolean,
    highlightActions: Boolean,
    limitWidthToScreen: Boolean,
    tooltipReloader: TooltipReloader?
  ): LightweightHint? {

    val currentText: String = requireNotNull(myText)

    //setup text
    val tooltipPreText = currentText.replace(UIUtil.MNEMONIC.toString().toRegex(), "")
    val dressedText = dressDescription(editor, tooltipPreText, myCurrentWidth > 0)

    val expanded = myCurrentWidth > 0 && dressedText != tooltipPreText

    val contentComponent = editor.contentComponent
    val editorComponent = editor.component

    if (!editorComponent.isShowing) return null

    val textToDisplay = if (newLayout) colorizeSeparators(dressedText) else dressedText

    val rootPane = editorComponent.rootPane.layeredPane
    val availableWidthToTheRightOfMarker = rootPane.width - p.x
    val preferredTooltipWidth = (availableWidthToTheRightOfMarker * 0.8).toInt()

    val editorPane = MetaTooltipJPane(Html(textToDisplay), hintHint, preferredTooltipWidth)

    hintHint.isContentActive = true

    val scrollPane = ScrollPaneFactory.createScrollPane(editorPane, true)
    with(scrollPane) {
      horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
      verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
      isOpaque = hintHint.isOpaqueAllowed
      viewport.isOpaque = hintHint.isOpaqueAllowed
      background = hintHint.textBackground
      viewport.background = hintHint.textBackground
      viewportBorder = null
    }

    if (!newLayout) editorPane.border = JBUI.Borders.emptyBottom(2)
    if (hintHint.isRequestFocus) {
      editorPane.isFocusable = true
    }

    val actions: MutableList<AnAction> = ArrayList()
    val grid = createMainPanel(hintHint, scrollPane, editorPane, newLayout, highlightActions, textToDisplay != dressedText)

    if (ScreenReader.isActive()) {
      grid.isFocusTraversalPolicyProvider = true
      grid.focusTraversalPolicy = object : LayoutFocusTraversalPolicy() {
        override fun getDefaultComponent(aContainer: Container): Component {
          return editorPane
        }

        override fun getImplicitDownCycleTraversal(): Boolean {
          return true
        }
      }
    }

    val hint: LightweightHint = object : LightweightHint(grid) {
      override fun hide() {
        onHide(editorPane)
        super.hide()
        for (action in actions) {
          action.unregisterCustomShortcutSet(contentComponent)
        }
      }

      override fun canAutoHideOn(event: TooltipEvent): Boolean {
        return if (!this@MetaTooltipRenderer.canAutoHideOn(event)) {
          false
        } else super.canAutoHideOn(event)
      }
    }

    val reloader = tooltipReloader ?: TooltipReloader { toExpand: Boolean ->
      reloadFor(hint, editor, p, editorPane, alignToRight, group, hintHint, toExpand)
    }

    val reloadAction = ReloadHintAction(hintHint, reloader, expanded)
    // an action to expand description when tooltip was shown after mouse move; need to unregister from editor component
    // an action to expand description when tooltip was shown after mouse move; need to unregister from editor component
    reloadAction.registerCustomShortcutSet(KeymapUtil.getActiveKeymapShortcuts(IdeActions.ACTION_SHOW_ERROR_DESCRIPTION), contentComponent)
    actions.add(reloadAction)

    editorPane.addHyperlinkListener(HyperlinkListener { e ->
      myActiveLink = true

      when (e.eventType) {
        HyperlinkEvent.EventType.EXITED -> {
          myActiveLink = false
          return@HyperlinkListener
        }
        HyperlinkEvent.EventType.ACTIVATED -> {
          val url = e.url
          if (url != null) {
            BrowserUtil.browse(url)
            hint.hide()
            return@HyperlinkListener
          } else {
            val description = e.description
            if (description != null && TooltipLinkHandlerEP.handleLink(description, editor)) {
              hint.hide()
              return@HyperlinkListener
            } else {
              logShowDescription(editor.project, "more.link", e.inputEvent, null)
              reloader.reload(!expanded)
            }
          }
        }
      }
    })

    fillPanel(editor, grid, hint, hintHint, actions, reloader, newLayout, highlightActions)

    if (!newLayout) {
      grid.addMouseListener(object : MouseAdapter() {
        // This listener makes hint transparent for mouse events. It means that hint is closed
        // by MousePressed and this MousePressed goes into the underlying editor component.
        override fun mouseReleased(e: MouseEvent) {
          if (!myActiveLink) {
            val newMouseEvent = SwingUtilities.convertMouseEvent(e.component, e, contentComponent)
            hint.hide()
            contentComponent.dispatchEvent(newMouseEvent)
          }
        }
      })

      ListenerUtil.addMouseListener(grid, object : MouseAdapter() {
        override fun mouseExited(e: MouseEvent) {
          if (expanded) return
          var parentContainer: Container = grid
          //ComponentWithMnemonics is top balloon component
          while (parentContainer !is ComponentWithMnemonics) {
            val candidate = parentContainer.parent ?: break
            parentContainer = candidate
          }
          val newMouseEvent = SwingUtilities.convertMouseEvent(e.component, e, parentContainer)
          if (parentContainer.contains(newMouseEvent.point)) {
            return
          }
          hint.hide()
        }
      })
    }

    return hint
  }

  private fun colorizeSeparators(html: String): String {
    val body = UIUtil.getHtmlBody(html)
    val parts = StringUtil.split(body, UIUtil.BORDER_LINE, true, false)
    if (parts.size <= 1) return html
    val b = StringBuilder()
    for (part in parts) {
      val addBorder = b.isNotEmpty()
      b.append("<div")
      if (addBorder) {
        b.append(" style='margin-top:6; padding-top:6; border-top: thin solid #")
        b.append(ColorUtil.toHex(UIUtil.getTooltipSeparatorColor()))
        b.append("'")
      }
      b.append("'>").append(part).append("</div>")
    }
    return XmlStringUtil.wrapInHtml(b.toString())
  }

  private fun createMainPanel(hintHint: HintHint,
                              pane: JComponent,
                              editorPane: JEditorPane,
                              newLayout: Boolean,
                              highlightActions: Boolean,
                              hasSeparators: Boolean): JPanel {
    val leftBorder = if (newLayout) 10 else 8
    val rightBorder = 12

    class MyPanel : JPanel(GridBagLayout()), WidthBasedLayout {
      override fun getPreferredWidth(): Int {
        return preferredSize.width
      }

      override fun getPreferredHeight(width: Int): Int {
        val size = editorPane.size
        val sideComponentsWidth = sideComponentWidth
        editorPane.setSize(width - leftBorder - rightBorder - sideComponentsWidth, 1.coerceAtLeast(size.height))
        val height: Int
        height = try {
          preferredSize.height
        } finally {
          editorPane.size = size
        }
        return height
      }

      override fun getAccessibleContext(): AccessibleContext {
        return object : AccessibleContextDelegate(editorPane.accessibleContext) {
          override fun getDelegateParent(): Container {
            return parent
          }
        }
      }

      private val sideComponentWidth: Int
        get() {
          val layout = layout as GridBagLayout
          var sideComponent: Component? = null
          var sideComponentConstraints: GridBagConstraints? = null
          var unsupportedLayout = false

          for (component in components) {
            val c = layout.getConstraints(component)
            if (c.gridx > 0) {
              if (sideComponent == null && c.gridy == 0) {
                sideComponent = component
                sideComponentConstraints = c
              } else {
                unsupportedLayout = true
              }
            }
          }

          if (unsupportedLayout) {
            Logger.getInstance(LineTooltipRenderer::class.java).error("Unsupported tooltip layout")
          }

          return if (sideComponent == null) {
            0
          } else {
            val insets = sideComponentConstraints!!.insets
            sideComponent.preferredSize.width + if (insets == null) 0 else insets.left + insets.right
          }
        }
    }

    val grid: JPanel = MyPanel()
    val bag = GridBag()
      .anchor(GridBagConstraints.CENTER) //weight is required for correct working scrollpane inside gridbaglayout
      .weightx(1.0)
      .weighty(1.0)
      .fillCell()

    pane.border = JBUI.Borders.empty(if (newLayout) 10 else 6,
      leftBorder,
      if (newLayout) if (highlightActions) 10 else if (hasSeparators) 8 else 3 else 6,
      rightBorder)

    grid.add(pane, bag)
    grid.background = hintHint.textBackground
    grid.border = JBUI.Borders.empty()
    grid.isOpaque = hintHint.isOpaqueAllowed
    return grid
  }

  private fun reloadFor(hint: LightweightHint,
                        editor: Editor,
                        p: Point,
                        pane: JComponent,
                        alignToRight: Boolean,
                        group: TooltipGroup,
                        hintHint: HintHint,
                        expand: Boolean) { //required for immediately showing. Otherwise there are several concurrent issues
    hint.hide()
    hintHint.isShowImmediately = true
    val point = Point(p)
    if (!Registry.`is`("editor.new.mouse.hover.popups")) {
      point.translate(-3, -3)
    }
    TooltipController.getInstance().showTooltip(editor, point,
      createRenderer(myText, if (expand) pane.width else 0), alignToRight, group,
      hintHint)
  }

  private class ReloadHintAction constructor(
    private val myHintHint: HintHint,
    private val myReloader: TooltipReloader,
    private val myExpanded: Boolean
  ) : AnAction(), ActionToIgnore {

    override fun actionPerformed(e: AnActionEvent) { // The tooltip gets the focus if using a screen reader and invocation through a keyboard shortcut.
      myHintHint.isRequestFocus = ScreenReader.isActive() && e.inputEvent is KeyEvent
      logShowDescription(e.project, "shortcut", e.inputEvent, e.place)
      myReloader.reload(!myExpanded)
    }
  }
}
