package arrow.meta.ide.plugins.initial.tooltip

import arrow.meta.ide.plugins.initial.tooltip.formatting.applyStylesFromCDN
import arrow.meta.ide.plugins.initial.tooltip.formatting.htmlEditorKit
import com.intellij.ide.IdeTooltipManager
import com.intellij.openapi.util.Ref
import com.intellij.ui.HintHint
import com.intellij.util.ui.Html
import com.intellij.util.ui.JBInsets
import org.jetbrains.annotations.NonNls
import java.awt.Dimension
import javax.swing.JEditorPane
import kotlin.math.max

object IdeTooltipPaneUtils {

  fun initTooltipPane(
    @NonNls html: Html,
    hintHint: HintHint,
    preferredWidth: Int
  ): JEditorPane {
    val prefSize = Ref<Dimension>(null)
    var isSizeComputed = false

    val pane: JEditorPane = object : JEditorPane() {
      override fun getPreferredSize(): Dimension {
        if (!isSizeComputed && hintHint.isAwtTooltip) {
          isSizeComputed = true
          val prefSizeOriginal: Dimension = super.getPreferredSize()

          // Ensure Tooltip width is over the minimum allowed and make it be up to the 80% of the width left for the
          // editor.
          if (prefSizeOriginal.width > preferredWidth) {
            size = Dimension(preferredWidth, Integer.MAX_VALUE)
            val fixedWidthSize: Dimension = super.getPreferredSize()
            val minSize: Dimension = super.getMinimumSize()
            prefSize.set(Dimension(max(preferredWidth, minSize.width), fixedWidthSize.height))
          } else {
            prefSize.set(Dimension(prefSizeOriginal))
          }
        }
        val s: Dimension = if (prefSize.get() != null) Dimension(prefSize.get()) else super.getPreferredSize()
        if (border != null) {
          JBInsets.addTo(s, border.getBorderInsets(this))
        }
        return s
      }

      override fun setPreferredSize(preferredSize: Dimension) {
        super.setPreferredSize(preferredSize)
        prefSize.set(preferredSize)
      }
    }

    pane.editorKit = htmlEditorKit()
    pane.text = html.applyStylesFromCDN()
    pane.caretPosition = 0
    pane.isEditable = false
    if (hintHint.isOwnBorderAllowed) {
      IdeTooltipManager.setBorder(pane)
      IdeTooltipManager.setColors(pane)
    } else {
      pane.border = null
    }
    if (!hintHint.isAwtTooltip) {
      isSizeComputed = true
    }
    val opaque = hintHint.isOpaqueAllowed
    pane.isOpaque = opaque
    pane.background = hintHint.textBackground

    return pane
  }
}