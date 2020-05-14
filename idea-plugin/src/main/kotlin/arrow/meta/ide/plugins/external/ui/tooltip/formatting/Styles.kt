package arrow.meta.ide.plugins.external.ui.tooltip.formatting

import com.intellij.util.ui.Html
import com.intellij.util.ui.StartupUiUtil
import com.intellij.util.ui.UIUtil

internal const val cdnStyleLight = "https://47deg-academy.s3.amazonaws.com/css/edutools/edutools-style-light.css"
internal const val cdnStyleDark = "https://47deg-academy.s3.amazonaws.com/css/edutools/edutools-style-dark.css"

internal fun Html.applyStylesFromCDN(): String {
  val htmlBody = UIUtil.getHtmlBody(this)
  val stylesToUse = if (StartupUiUtil.isUnderDarcula()) {
    cdnStyleLight
  } else {
    cdnStyleDark
  }

  return """
      <html>
        <head>
          <link rel="stylesheet" type="text/css" href="$stylesToUse">
        </head>
        <body>
          $htmlBody
        </body>
      </html>
    """.trimIndent()
}
