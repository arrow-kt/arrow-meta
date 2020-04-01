package arrow.meta.ide.plugins.quotes.resolve

import arrow.meta.ide.dsl.application.services.Id
import arrow.meta.ide.dsl.application.services.IdService
import arrow.meta.ide.testing.unavailableServices
import com.intellij.codeHighlighting.Pass
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtFile
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class QuoteHighlightingPassFactory : TextEditorHighlightingPassFactoryRegistrar {
  // TODO: register this via Meta
  override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
    registrar.registerTextEditorHighlightingPass({ file: PsiFile, _: Editor ->
      if (file is KtFile) {
        project.getService(QuoteHighlightingCache::class.java)?.waitToInitialize()
          ?: unavailableServices(QuoteHighlightingCache::class.java)
      }
      null
    }, TextEditorHighlightingPassRegistrar.Anchor.FIRST, Pass.UPDATE_FOLDING, false, false)
  }
}

/**
 * default are the initial values when project opens. They have to be reset, when it is closed.
 * Please note, that this cache is immutable, but it has to be used in a thread-safe way to make "waitToInitialize" work.
 * Therefore, you have to make values of type "HighlightingCache" volatile.
 */
internal data class HighlightingCache(val initialized: Boolean = false, val latch: CountDownLatch = CountDownLatch(1))

/**
 * TODO: transfer this to a lifecycle extension
 */
internal class QuoteHighlightingCache private constructor() : IdService<HighlightingCache> {
  @Volatile
  override var value: Id<HighlightingCache> =
    Id.just(HighlightingCache())

  /**
   * waits until the initial transformation, which is started after the project was initialized,
   * is finished. This is necessary to implement fully working highlighting of .kt files, which
   * access data from the Quote transformations during resolving.
   */
  fun waitToInitialize(): Unit =
    value.extract().let { cache ->
      if (!cache.initialized) { // this is not executed anymore
        cache.latch.await(5, TimeUnit.SECONDS)
        //println("BOOOOOOMMMMMMM")
      } else {
        //println("NOOO BOOOOM")
      }
    }
}