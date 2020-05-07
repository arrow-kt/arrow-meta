package arrow.meta.ide.plugins.initial.tooltip

import arrow.meta.ide.plugins.initial.tooltip.formatting.applyStylesFromCDN
import arrow.meta.ide.plugins.initial.tooltip.formatting.htmlEditorKit
import com.intellij.ide.IdeTooltipManager
import com.intellij.openapi.util.Ref
import com.intellij.ui.HintHint
import com.intellij.util.ui.Html
import com.intellij.util.ui.JBInsets
import java.awt.Dimension
import javax.swing.JEditorPane
import kotlin.math.max

/**
 * JPane preconfigured to:
 *
 * * Render Html.
 * * Apply Arrow-Meta styles loaded from a remote CDN.
 * * Starts caret at position 0.
 * * Is not editable.
 * * Constrain its width to be the 80% of the available editors space, or the minimum imposed by parent in case it's
 * bigger.
 */
class MetaTooltipJPane(
  html: Html,
  private val hintHint: HintHint,
  private val preferredWidth: Int
) : JEditorPane() {

  private val prefSize = Ref<Dimension>(null)
  private var isSizeComputed = false

  init {
    editorKit = htmlEditorKit()
    text = html.applyStylesFromCDN()
    caretPosition = 0
    isEditable = false
    if (hintHint.isOwnBorderAllowed) {
      IdeTooltipManager.setBorder(this)
      IdeTooltipManager.setColors(this)
    } else {
      border = null
    }
    if (!hintHint.isAwtTooltip) {
      isSizeComputed = true
    }
    val opaque = hintHint.isOpaqueAllowed
    isOpaque = opaque
    background = hintHint.textBackground
  }

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