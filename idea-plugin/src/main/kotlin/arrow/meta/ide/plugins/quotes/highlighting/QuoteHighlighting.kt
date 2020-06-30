package arrow.meta.ide.plugins.quotes.highlighting

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.quotes.lifecycle.quoteConfigs
import arrow.meta.ide.testing.unavailableServices
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeHighlighting.Pass
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtFile

val IdeMetaPlugin.quoteHighlighting: ExtensionPhase
  get() = addTextEditorHighlighting(
    factory = { project ->
      // TODO: Error viewed and temporarily fixed in PR #641 --
      //  reproducible by uncommenting quote service description
      //  behavior: Program freezes indefinitely as the picocontainer is not able to timeout and resolve with null. Instead it waits indefinitely for the instance.
      project.quoteConfigs()?.let { configs ->
        project.getService(QuoteHighlightingCache::class.java)?.run {
          registerTextEditorHighlightingPass({ file: PsiFile, _: Editor ->
            if (file is KtFile) {
              configs.waitToInitialize()
            }
            null
          }, TextEditorHighlightingPassRegistrar.Anchor.FIRST, Pass.UPDATE_FOLDING, false, false)
          Unit
        } ?: unavailableServices(QuoteHighlightingCache::class.java)
      } ?: Unit
    }
  )