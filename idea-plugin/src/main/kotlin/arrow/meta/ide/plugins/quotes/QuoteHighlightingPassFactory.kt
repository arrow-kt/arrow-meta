package arrow.meta.ide.plugins.quotes

import arrow.meta.ide.dsl.application.cache.Id
import arrow.meta.ide.dsl.application.cache.PersistentService
import arrow.meta.ide.testing.UnavailableService
import com.intellij.codeHighlighting.Pass
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtFile
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class QuoteHighlightingPassFactory : TextEditorHighlightingPassFactoryRegistrar {
  // TODO: register this via Meta
  override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
    registrar.registerTextEditorHighlightingPass({ file: PsiFile, _: Editor ->
      if (file is KtFile) {
        project.getService(QuoteHighlightingCache::class.java)?.waitToInitialize()
          ?: throw UnavailableService(QuoteHighlightingCache::class.java)
      }
      null
    }, TextEditorHighlightingPassRegistrar.Anchor.FIRST, Pass.UPDATE_FOLDING, false, false)
  }
}

/**
 * default are the initial values when project opens. They have to be reset, when it is closed.
 */
internal data class HighlightingCache(val initialized: AtomicBoolean = AtomicBoolean(false), val latch: CountDownLatch = CountDownLatch(1))

/**
 * TODO: transfer this to a lifecycle extension
 */
@State(name = "QuoteHighlightingCache", storages = [Storage(StoragePathMacros.CACHE_FILE)])
internal object QuoteHighlightingCache : PersistentService<HighlightingCache> {
  override var value: Id<HighlightingCache> =
    Id.just(HighlightingCache())

  /**
   * waits until the initial transformation, which is started after the project was initialized,
   * is finished. This is necessary to implement fully working highlighting of .kt files, which
   * access data from the Quote transformations during resolving.
   */
  fun waitToInitialize(): Unit =
    value.extract().let { cache ->
      if (!cache.initialized.get()) {
        cache.latch.await(5, TimeUnit.SECONDS)
        println("BOOOOOOMMMMMMM")
      }
      println("NOOO BOOOOM")
    }
}