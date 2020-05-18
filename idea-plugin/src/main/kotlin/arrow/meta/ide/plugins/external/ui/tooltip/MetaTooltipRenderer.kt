package arrow.meta.ide.plugins.external.ui.tooltip

import arrow.meta.ide.plugins.external.ui.tooltip.style.applyStylesFromCDN
import arrow.meta.ide.plugins.external.ui.tooltip.util.removeMetaTags
import com.intellij.codeInsight.daemon.impl.createActionLabel
import com.intellij.codeInsight.daemon.impl.runActionCustomShortcutSet
import com.intellij.codeInsight.daemon.impl.tooltips.TooltipActionProvider
import com.intellij.codeInsight.hint.HintManagerImpl
import com.intellij.codeInsight.hint.HintManagerImpl.ActionToIgnore
import com.intellij.codeInsight.hint.LineTooltipRenderer
import com.intellij.codeInsight.hint.LineTooltipRenderer.TooltipReloader
import com.intellij.codeInsight.hint.TooltipController
import com.intellij.codeInsight.hint.TooltipGroup
import com.intellij.codeInsight.hint.TooltipLinkHandlerEP
import com.intellij.codeInsight.hint.TooltipRenderer
import com.intellij.icons.AllIcons
import com.intellij.ide.BrowserUtil
import com.intellij.ide.IdeTooltipManager
import com.intellij.ide.TooltipEvent
import com.intellij.ide.ui.UISettings
import com.intellij.internal.statistic.service.fus.collectors.TooltipActionsLogger
import com.intellij.internal.statistic.service.fus.collectors.TooltipActionsLogger.logShowDescription
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.actionSystem.PopupAction
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.TooltipAction
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.GraphicsConfig
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.registry.Registry
import com.intellij.ui.BalloonImpl
import com.intellij.ui.ComponentWithMnemonics
import com.intellij.ui.HintHint
import com.intellij.ui.JBColor
import com.intellij.ui.LightweightHint
import com.intellij.ui.ListenerUtil
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.WidthBasedLayout
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.GridBag
import com.intellij.util.ui.Html
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBHtmlEditorKit
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.accessibility.AccessibleContextDelegate
import com.intellij.util.ui.accessibility.ScreenReader
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Point
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.RoundRectangle2D
import java.util.*
import javax.accessibility.AccessibleContext
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants
import javax.swing.SwingUtilities
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener

/**
 * This is the tooltip renderer implementation used in Meta. Open API instantiates different implementations of
 * [TooltipRenderer] interface like [LineTooltipRenderer], DaemonTooltipRenderer or DaemonTooltipWithActionRenderer and
 * a few more depending on the needs. All those implementations are internal, but makes sense that Meta provides its
 * own one to take care of rendering tooltips.
 *
 * All the Meta features rely on this renderer to render tooltips now.
 *
 * What is a tooltip? It's the visual popup window you see when hovering a line marker, or an inspection for example.
 */
@Suppress("UnstableApiUsage")
internal class MetaTooltipRenderer(
  text: String,
  comparable: Array<out Any> = arrayOf(),
  private val tooltipAction: TooltipAction? = null) : LineTooltipRenderer(text, comparable) {

  @Volatile
  private var myActiveLink = false

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
    if (!editor.component.isShowing) return null

    val sanitizedText = requireNotNull(myText).replace(UIUtil.MNEMONIC.toString().toRegex(), "").removeMetaTags()
    val htmlText = Html(sanitizedText)

    val layeredPane = editor.component.rootPane.layeredPane
    val editorPane = IdeTooltipManager.initPane(htmlText, hintHint, layeredPane, limitWidthToScreen)

    editorPane.editorKit = JBHtmlEditorKit()
    editorPane.text = htmlText.applyStylesFromCDN()
    hintHint.isContentActive = true

    val scrollPane = ScrollPaneFactory.createScrollPane(editorPane, true).apply {
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
    val grid = createMainPanel(hintHint, scrollPane, editorPane, newLayout, highlightActions, sanitizedText != sanitizedText)

    val hint: LightweightHint = object : LightweightHint(grid) {
      override fun hide() {
        onHide(editorPane)
        super.hide()
        for (action in actions) {
          action.unregisterCustomShortcutSet(editor.contentComponent)
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

    val reloadAction = ReloadHintAction(hintHint, reloader)
    // an action to expand description when tooltip was shown after mouse move; need to unregister from editor component
    // an action to expand description when tooltip was shown after mouse move; need to unregister from editor component
    reloadAction.registerCustomShortcutSet(KeymapUtil.getActiveKeymapShortcuts(IdeActions.ACTION_SHOW_ERROR_DESCRIPTION), editor.contentComponent)
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
              reloader.reload(false)
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
            val newMouseEvent = SwingUtilities.convertMouseEvent(e.component, e, editor.contentComponent)
            hint.hide()
            editor.contentComponent.dispatchEvent(newMouseEvent)
          }
        }
      })

      ListenerUtil.addMouseListener(grid, object : MouseAdapter() {
        override fun mouseExited(e: MouseEvent) {
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

  override fun fillPanel(editor: Editor, grid: JPanel, hint: LightweightHint, hintHint: HintHint, actions: MutableList<in AnAction>, tooltipReloader: TooltipReloader, newLayout: Boolean, highlightActions: Boolean) {
    super.fillPanel(editor, grid, hint, hintHint, actions, tooltipReloader, newLayout, highlightActions)
    val hasMore = isActiveHtml(requireNotNull(myText))
    if (tooltipAction == null && !hasMore) return

    val settingsComponent = createSettingsComponent(hintHint, tooltipReloader, hasMore, newLayout)

    val settingsConstraints = GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
      JBUI.insets(if (newLayout) 7 else 4, 7, if (newLayout) 0 else 4, if (newLayout) 2 else 4),
      0, 0)
    grid.add(settingsComponent, settingsConstraints)

    if (TooltipActionProvider.isShowActions()) {
      addActionsRow(hintHint, hint, editor, actions, grid, newLayout, highlightActions)
    }
  }

  private fun createSettingsComponent(hintHint: HintHint,
                                      reloader: TooltipReloader,
                                      hasMore: Boolean,
                                      newLayout: Boolean): JComponent {
    val presentation = Presentation()
    presentation.icon = AllIcons.Actions.More
    presentation.putClientProperty(ActionButton.HIDE_DROPDOWN_ICON, true)
    val actions = mutableListOf<AnAction>()
    actions.add(ShowActionsAction(reloader, tooltipAction != null))
    val docAction = ShowDocAction(reloader, hasMore)
    actions.add(docAction)
    val actionGroup = SettingsActionGroup(actions)
    val buttonSize = if (newLayout) 20 else 18
    val settingsButton = ActionButton(actionGroup, presentation, ActionPlaces.UNKNOWN, Dimension(buttonSize, buttonSize))
    settingsButton.setNoIconsInPopup(true)
    settingsButton.border = JBUI.Borders.empty()
    settingsButton.isOpaque = false

    val wrapper = JPanel(BorderLayout())
    wrapper.add(settingsButton, BorderLayout.EAST)
    wrapper.border = JBUI.Borders.empty()
    wrapper.background = hintHint.textBackground
    wrapper.isOpaque = false
    return wrapper
  }

  private fun addActionsRow(hintHint: HintHint,
                            hint: LightweightHint,
                            editor: Editor,
                            actions: MutableList<in AnAction>,
                            grid: JComponent,
                            newLayout: Boolean,
                            highlightActions: Boolean) {
    if (tooltipAction == null || !hintHint.isAwtTooltip) return


    val buttons = JPanel(GridBagLayout())
    val wrapper = createActionPanelWithBackground(highlightActions)
    wrapper.add(buttons, BorderLayout.WEST)

    buttons.border = JBUI.Borders.empty()
    buttons.isOpaque = false

    val runFixAction = { event: InputEvent? ->
      hint.hide()
      tooltipAction.execute(editor, event)
    }

    val shortcutRunActionText = KeymapUtil.getShortcutsText(runActionCustomShortcutSet.shortcuts)
    val shortcutShowAllActionsText = getKeymap(IdeActions.ACTION_SHOW_INTENTION_ACTIONS)

    val gridBag = GridBag()
      .fillCellHorizontally()
      .anchor(GridBagConstraints.WEST)

    val topInset = 5
    val bottomInset = if (newLayout) (if (highlightActions) 4 else 10) else 5
    buttons.add(createActionLabel(tooltipAction.text, runFixAction, hintHint.textBackground),
      gridBag.next().insets(topInset, if (newLayout) 10 else 8, bottomInset, 4))
    buttons.add(createKeymapHint(shortcutRunActionText),
      gridBag.next().insets(if (newLayout) topInset else 0, 4, if (newLayout) bottomInset else 0, 12))

    val showAllFixes = { _: InputEvent? ->
      hint.hide()
      tooltipAction.showAllActions(editor)
    }

    buttons.add(createActionLabel("More actions...", showAllFixes, hintHint.textBackground),
      gridBag.next().insets(topInset, 12, bottomInset, 4))
    buttons.add(createKeymapHint(shortcutShowAllActionsText),
      gridBag.next().fillCellHorizontally().insets(if (newLayout) topInset else 0, 4, if (newLayout) bottomInset else 0, 20))

    actions.add(object : AnAction() {
      override fun actionPerformed(e: AnActionEvent) {
        runFixAction(e.inputEvent)
      }

      init {
        registerCustomShortcutSet(runActionCustomShortcutSet, editor.contentComponent)
      }
    })

    actions.add(object : AnAction() {
      override fun actionPerformed(e: AnActionEvent) {
        showAllFixes(e.inputEvent)
      }

      init {
        registerCustomShortcutSet(KeymapUtil.getActiveKeymapShortcuts(IdeActions.ACTION_SHOW_INTENTION_ACTIONS), editor.contentComponent)
      }
    })

    val buttonsConstraints = GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
      JBUI.insetsTop(0), 0, 0)

    grid.add(wrapper, buttonsConstraints)
  }

  private fun createMainPanel(hintHint: HintHint,
                              scrollPane: JComponent,
                              editorPane: JEditorPane,
                              newLayout: Boolean,
                              highlightActions: Boolean,
                              hasSeparators: Boolean): JPanel {
    val leftBorder = if (newLayout) 10 else 8
    val rightBorder = 12

    class ConstrainedWidthGridBagLayout : JPanel(GridBagLayout()), WidthBasedLayout {
      override fun getPreferredWidth(): Int {
        return preferredSize.width
      }

      override fun getPreferredHeight(width: Int): Int {
        val size = editorPane.size
        val sideComponentsWidth = sideComponentWidth
        editorPane.setSize(width - leftBorder - rightBorder - sideComponentsWidth, Math.max(1, size.height))
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

    val grid: JPanel = ConstrainedWidthGridBagLayout()
    val bag = GridBag()
      .anchor(GridBagConstraints.CENTER) // weight required for correct working ScrollPane inside GridBadLayout
      .weightx(1.0)
      .weighty(1.0)
      .fillCell()

    scrollPane.border = JBUI.Borders.empty(if (newLayout) 10 else 6,
      leftBorder,
      if (newLayout) if (highlightActions) 10 else if (hasSeparators) 8 else 3 else 6,
      rightBorder)

    grid.add(scrollPane, bag)
    grid.background = hintHint.textBackground
    grid.border = JBUI.Borders.empty()
    grid.isOpaque = hintHint.isOpaqueAllowed
    return grid
  }

  private fun createActionPanelWithBackground(highlight: Boolean): JPanel {
    val wrapper: JPanel = if (highlight) object : JPanel(BorderLayout()) {
      override fun paint(g: Graphics?) {
        g!!.color = UIUtil.getToolTipActionBackground()
        if (JBPopupFactory.getInstance().getParentBalloonFor(this) == null) {
          g.fillRect(0, 0, width, height)
        } else {
          val graphics2D = g as Graphics2D
          val cfg = GraphicsConfig(g)
          cfg.setAntialiasing(true)

          graphics2D.fill(RoundRectangle2D.Double(1.0, 0.0, bounds.width - 2.5, (bounds.height / 2).toDouble(), 0.0, 0.0))

          val arc = BalloonImpl.ARC.get().toDouble()
          val double = RoundRectangle2D.Double(1.0, 0.0, bounds.width - 2.5, (bounds.height - 1).toDouble(), arc, arc)

          graphics2D.fill(double)

          cfg.restore()
        }
        super.paint(g)
      }
    } else JPanel(BorderLayout())

    wrapper.isOpaque = false
    wrapper.border = JBUI.Borders.empty()
    return wrapper
  }

  private fun getKeymap(key: String): String {
    val keymapManager = KeymapManager.getInstance()
    if (keymapManager != null) {
      val keymap = keymapManager.activeKeymap
      return KeymapUtil.getShortcutsText(keymap.getShortcuts(key))
    }

    return ""
  }

  private fun createKeymapHint(shortcutRunAction: String): JComponent {
    val fixHint = object : JBLabel(shortcutRunAction) {
      override fun getForeground(): Color {
        return getKeymapColor()
      }
    }
    fixHint.border = JBUI.Borders.empty()
    fixHint.font = getActionFont()
    return fixHint
  }

  private fun getKeymapColor(): Color {
    return JBColor.namedColor("ToolTip.Actions.infoForeground", JBColor(0x99a4ad, 0x919191))
  }

  private fun getActionFont(): Font? {
    val toolTipFont = UIUtil.getToolTipFont()
    if (toolTipFont == null || SystemInfo.isWindows) return toolTipFont

    //if font was changed from default we dont have a good heuristic to customize it
    if (JBFont.label() != toolTipFont || UISettings.instance.overrideLafFonts) return toolTipFont

    if (SystemInfo.isMac) {
      return toolTipFont.deriveFont(toolTipFont.size - 1f)
    }
    if (SystemInfo.isLinux) {
      return toolTipFont.deriveFont(toolTipFont.size - 1f)
    }

    return toolTipFont
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
    private val myReloader: TooltipReloader
  ) : AnAction(), ActionToIgnore {

    override fun actionPerformed(e: AnActionEvent) { // The tooltip gets the focus if using a screen reader and invocation through a keyboard shortcut.
      myHintHint.isRequestFocus = ScreenReader.isActive() && e.inputEvent is KeyEvent
      logShowDescription(e.project, "shortcut", e.inputEvent, e.place)
      myReloader.reload(false)
    }
  }

  private class SettingsActionGroup(actions: List<AnAction>) : DefaultActionGroup(actions), HintManagerImpl.ActionToIgnore, DumbAware {
    init {
      isPopup = true
    }
  }

  private inner class ShowActionsAction(val reloader: TooltipReloader, val isEnabled: Boolean) : ToggleAction(
    "Show Quick Fixes"), HintManagerImpl.ActionToIgnore {

    override fun isSelected(e: AnActionEvent): Boolean {
      return TooltipActionProvider.isShowActions()
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
      TooltipActionProvider.setShowActions(state)
      reloader.reload(myCurrentWidth > 0)
    }

    override fun update(e: AnActionEvent) {
      e.presentation.isEnabled = isEnabled
      super.update(e)
    }
  }

  private inner class ShowDocAction(val reloader: TooltipReloader, val isEnabled: Boolean) : ToggleAction(
    "Show Inspection Description"), HintManagerImpl.ActionToIgnore, DumbAware, PopupAction {

    init {
      shortcutSet = KeymapUtil.getActiveKeymapShortcuts(IdeActions.ACTION_SHOW_ERROR_DESCRIPTION)
    }

    override fun isSelected(e: AnActionEvent): Boolean {
      return myCurrentWidth > 0
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
      TooltipActionsLogger.logShowDescription(e.project, "gear", e.inputEvent, e.place)
      reloader.reload(state)
    }

    override fun update(e: AnActionEvent) {
      e.presentation.isEnabled = isEnabled
      super.update(e)
    }
  }
}
