package arrow.meta.ide.plugins.initial.tooltip

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.hint.LineTooltipRenderer
import com.intellij.codeInsight.hint.TooltipController
import com.intellij.codeInsight.hint.TooltipGroup
import com.intellij.codeInsight.hint.TooltipRenderer
import com.intellij.ide.IdeTooltipManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.Comparing
import com.intellij.ui.HintHint
import com.intellij.ui.LightweightHint
import com.intellij.ui.awt.RelativePoint
import java.awt.Point
import java.awt.event.MouseEvent

val IdeMetaPlugin.toolTipController: ExtensionPhase
  get() = addAppService(TooltipController::class.java) {
    controller // hijack TooltipController and replace it with ours
  }

private val controller: TooltipController
  get() = object : TooltipController() {

    private var myCurrentTooltip: LightweightHint? = null
    private var myCurrentTooltipObject: TooltipRenderer? = null
    private var myCurrentTooltipGroup: TooltipGroup? = null

    override fun cancelTooltips() {
      hideCurrentTooltip()
    }

    override fun cancelTooltip(groupId: TooltipGroup, mouseEvent: MouseEvent?, forced: Boolean) {
      if (groupId == myCurrentTooltipGroup) {
        if (!forced && myCurrentTooltip != null && myCurrentTooltip!!.canControlAutoHide()) return
        cancelTooltips()
      }
    }

    /**
     * Returns newly created hint, or already existing (for the same renderer)
     */
    override fun showTooltipByMouseMove(
      editor: Editor,
      point: RelativePoint,
      tooltipObject: TooltipRenderer?,
      alignToRight: Boolean,
      group: TooltipGroup,
      hintHint: HintHint): LightweightHint? {

      val currentTooltip = myCurrentTooltip
      if (currentTooltip == null || !currentTooltip.isVisible) {
        if (currentTooltip != null) {
          if (!IdeTooltipManager.getInstance().isQueuedToShow(currentTooltip.currentIdeTooltip)) {
            myCurrentTooltipObject = null
          }
        } else {
          myCurrentTooltipObject = null
        }
      }

      if (Comparing.equal(tooltipObject, myCurrentTooltipObject)) {
        IdeTooltipManager.getInstance().cancelAutoHide()
        return myCurrentTooltip
      }

      hideCurrentTooltip()

      if (tooltipObject != null) {
        val p = point.getPointOn(editor.component.rootPane.layeredPane).point

        if (!hintHint.isAwtTooltip) {
          p.x += if (alignToRight) -10 else 10
        }

        val project = editor.project
        if (project != null && !project.isOpen) return null
        if (editor.contentComponent.isShowing) {
          return doShowTooltip(editor, p, tooltipObject, alignToRight, group, hintHint)
        }
      }
      return null
    }

    private fun hideCurrentTooltip() {
      if (myCurrentTooltip != null) {
        val currentTooltip: LightweightHint? = myCurrentTooltip
        myCurrentTooltip = null
        currentTooltip?.hide()
        myCurrentTooltipGroup = null
        IdeTooltipManager.getInstance().hide(null)
      }
    }

    override fun showTooltip(editor: Editor,
                             p: Point,
                             tooltipRenderer: TooltipRenderer,
                             alignToRight: Boolean,
                             group: TooltipGroup,
                             hintInfo: HintHint) {
      doShowTooltip(editor, p, tooltipRenderer, alignToRight, group, hintInfo)
    }

    private fun doShowTooltip(editor: Editor,
                              p: Point,
                              tooltipRenderer: TooltipRenderer,
                              alignToRight: Boolean,
                              group: TooltipGroup,
                              hintInfo: HintHint): LightweightHint? {
      if (myCurrentTooltip == null || !myCurrentTooltip!!.isVisible) {
        myCurrentTooltipObject = null
      }

      if (Comparing.equal(tooltipRenderer, myCurrentTooltipObject)) {
        IdeTooltipManager.getInstance().cancelAutoHide()
        return null
      }

      if (myCurrentTooltipGroup != null && group < myCurrentTooltipGroup) return null
      val point = Point(p)
      hideCurrentTooltip()

      val renderer = if (isAnArrowMetaTooltip() &&
        tooltipRenderer is LineTooltipRenderer &&
        tooltipRenderer.text != null) {
        MetaTooltipRenderer(tooltipRenderer.text!!, arrayOf())
      } else {
        tooltipRenderer
      }
      val hint = renderer.show(editor, point, alignToRight, group, hintInfo)
      myCurrentTooltipGroup = group
      myCurrentTooltip = hint
      myCurrentTooltipObject = tooltipRenderer
      return hint
    }

    override fun shouldSurvive(e: MouseEvent?): Boolean {
      if (myCurrentTooltip != null) {
        if (myCurrentTooltip!!.canControlAutoHide()) return true
      }
      return false
    }

    override fun hide(lightweightHint: LightweightHint) {
      if (myCurrentTooltip != null && myCurrentTooltip == lightweightHint) {
        hideCurrentTooltip()
      }
    }

    override fun resetCurrent() {
      myCurrentTooltip = null
      myCurrentTooltipGroup = null
      myCurrentTooltipObject = null
    }
  }

private fun isAnArrowMetaTooltip(): Boolean = true