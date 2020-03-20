package arrow.meta.ide.plugins.quotes

import arrow.meta.ide.phases.resolve.QuoteSystemComponent
import com.intellij.codeHighlighting.Pass
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtFile
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

class ArrowHighlightingPassFactory : TextEditorHighlightingPassFactoryRegistrar {
  // TODO: register this via Meta
  override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
    registrar.registerTextEditorHighlightingPass({ file: PsiFile, _: Editor ->
      if (file is KtFile) {
        project.getComponent(QuoteSystemComponent::class.java)?.waitForInitialize()
      }
      null
    }, TextEditorHighlightingPassRegistrar.Anchor.FIRST, Pass.UPDATE_FOLDING, false, false)
  }
}

internal val initialized = AtomicBoolean(false)
internal val initializedLatch = CountDownLatch(1)