package arrow.meta.ide.plugins.helloworld

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.invoke
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import arrow.meta.ide.dsl.editor.lineMarker.LineMarkerSyntax

/**
 * The following section exemplifies a Hello World Ide Plugin
 *
 * The Hello World plugin registers a LineMarker on every `helloWorld` function.
 *
 * ```kotlin
 * val IdeMetaPlugin.helloWorld: Plugin
 *    get() = "Hello World" {
 *      meta(
 *        addLineMarkerProvider(
 *          icon = ArrowIcons.PURE,
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
 * For every function with the name `helloWorld` our ide plugin will register a lineMarker with our costume icon and whenever
 * the user hovers over the Icon it will display the message.
 * // TODO: Add Animation or example picture
 * Take a look at [LineMarkerSyntax] for more details.
 */
val IdeMetaPlugin.helloWorld: Plugin
  get() = "Hello World" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.PURE,
        composite = KtNamedFunction::class.java,
        message = { f: KtNamedFunction -> "Teach your users about this feature in function $f" },
        transform = {
          it.safeAs<KtNamedFunction>()?.takeIf { f ->
            f.name == "helloWorld"
          }
        }
      )
    )
  }