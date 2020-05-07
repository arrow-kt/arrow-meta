package arrow.meta.ide.plugins.initial.tooltip.formatting

import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.JBHtmlEditorKit
import javax.swing.text.AbstractDocument
import javax.swing.text.Element
import javax.swing.text.StyleConstants
import javax.swing.text.View
import javax.swing.text.ViewFactory
import javax.swing.text.html.HTML
import javax.swing.text.html.HTMLEditorKit

/**
 * The Swing JEditorPane text component supports different kinds
 * of content via a plug-in mechanism called an EditorKit.  Because
 * HTML is a very popular format of content, some support is provided
 * by default.  The default support is provided by this class, which
 * supports HTML version 3.2 (with some extensions), and is migrating
 * toward version 4.0.
 *
 * See [HTMLEditorKit] docs for more.
 **/
fun htmlEditorKit(): HTMLEditorKit = object : JBHtmlEditorKit() {
  val factory: HTMLFactory = object : HTMLFactory() {
    override fun create(elem: Element): View {
      val attrs = elem.attributes
      val elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute)
      val o = if (elementName != null) null else attrs.getAttribute(StyleConstants.NameAttribute)
      if (o is HTML.Tag) {
        if (o === HTML.Tag.HR) {
          val view = super.create(elem)
          try {
            val field = view.javaClass.getDeclaredField("size")
            field.isAccessible = true
            field[view] = JBUIScale.scale(1)
            return view
          } catch (ignored: Exception) { //ignore
          }
        }
      }
      return super.create(elem)
    }
  }

  override fun getViewFactory(): ViewFactory {
    return factory
  }
}