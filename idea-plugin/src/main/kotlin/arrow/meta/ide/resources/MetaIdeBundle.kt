package arrow.meta.ide.resources

import com.intellij.AbstractBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE = "localization.MetaIdeBundle"

object MetaIdeBundle : AbstractBundle(BUNDLE) {

  @JvmStatic
  fun message(@NonNls @PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String =
    getMessage(key, *params)

  @JvmStatic
  fun htmlMessage(@NonNls @PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String =
    getMessage(key, *params).withHtml()

  private fun String.withHtml(): String = "<html>$this</html>"
}