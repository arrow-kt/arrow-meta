package arrow.meta.ide.plugins.quotes

import arrow.meta.ide.phases.resolve.QuoteSystemCache
import com.intellij.codeHighlighting.Pass
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtFile

class ArrowHighlightingPassFactory : TextEditorHighlightingPassFactoryRegistrar {
  override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
    registrar.registerTextEditorHighlightingPass({ file: PsiFile, _: Editor ->
      if (file is KtFile) {
        QuoteSystemCache.getInstance(project)?.waitForInitialize()
      }
      null
    }, TextEditorHighlightingPassRegistrar.Anchor.FIRST, Pass.UPDATE_FOLDING, false, false)
  }
}