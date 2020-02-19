package arrow.meta.ide.plugins.helloworld

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.lineMarker.LineMarkerSyntax
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.invoke
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.util.hasInlineModifier
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

/**
 * The following section exemplifies a Hello World IDE Plugin
 *
 * The Hello World plugin registers a LineMarker on every `helloWorld` function.
 *
 * ```kotlin
 * val IdeMetaPlugin.helloWorld: Plugin
 *    get() = "Hello World" {
 *      meta(
 *        addLineMarkerProvider(
 *          icon = ArrowIcons.ICON1,
 *          composite = KtNamedFunction::class.java,
 *          message = { f: KtNamedFunction -> "Teach your users about this feature in function $f" },
 *          transform = {
 *            it.safeAs<KtNamedFunction>()?.takeIf { f ->
 *              f.name == "helloWorld"
 *            }
 *          }
 *        )
 *      )
 *    }
 * ```
 *
 * For every function with the name `helloWorld`, our IDE plugin will register a lineMarker with our custom icon. And whenever
 * the user hovers over the Icon, it will display the message.
 * @see [LineMarkerSyntax]
 */
val IdeMetaPlugin.helloWorld: Plugin // TODO: Add Animation or example picture
  get() = "Hello World" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.ICON1,
        composite = KtClass::class.java,
        message = { ktClass: KtClass ->
          HTML.renderMessage("Teach your users about this feature for inline classes") + "<br/>" +
            ktClass.resolveToDescriptorIfAny()?.let(HTML::render)
        },
        transform = {
          it.safeAs<KtClass>()?.takeIf { ktClass ->
            ktClass.hasInlineModifier()
          }
        }
      )
    )
  }
