package arrow.meta.ide.plugins.initial.tooltip.formatting

import com.intellij.util.ui.Html
import com.intellij.util.ui.UIUtil

const val cdnStyle = "https://47deg-academy.s3.amazonaws.com/css/edutools-style-light.css"

fun Html.applyStylesFromCDN(): String {
  val htmlBody = UIUtil.getHtmlBody(this)
  return """
      <html>
        <head>
          <link rel="stylesheet" type="text/css" href="$cdnStyle">
        </head>
        <body>
          $htmlBody
        </body>
      </html>
    """.trimIndent()
}