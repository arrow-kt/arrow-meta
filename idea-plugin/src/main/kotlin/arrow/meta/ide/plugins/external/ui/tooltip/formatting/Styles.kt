package arrow.meta.ide.plugins.external.ui.tooltip.formatting

import com.intellij.util.ui.Html
import com.intellij.util.ui.StartupUiUtil
import com.intellij.util.ui.UIUtil

internal const val cdnStyleLight = "https://47deg-academy.s3.amazonaws.com/css/edutools-style-light.css"
internal const val cdnStyleDark = "https://47deg-academy.s3.amazonaws.com/css/edutools-style-dark.css"

internal fun Html.applyStylesFromCDN(): String {
  val htmlBody = UIUtil.getHtmlBody(this)
  val stylesToUse = if (StartupUiUtil.isUnderDarcula()) {
    cdnStyleLight
  } else {
    cdnStyleDark
  }

  // <link rel="stylesheet" type="text/css" href="$stylesToUse">
  return """
      <html>
        <head>
          <style>body { background-color: red; }</style>
        </head>
        <body>
          $htmlBody
        </body>
      </html>
    """.trimIndent()
}
